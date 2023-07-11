package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.alchemy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EntityFlameCore extends Entity{

	public static EntityType<EntityFlameCore> type;

	protected static final EntityDataAccessor<Integer> TIME_EXISTED = SynchedEntityData.defineId(EntityFlameCore.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(EntityFlameCore.class, EntityDataSerializers.INT);
	protected static final float FLAME_VEL = 0.08F;//Flame interface speed, blocks per tick

	/**
	 * In order to avoid iterating over a large ReagentStack[] that is mostly empty several times a tick, this list is created to store all non-null reagent stacks
	 */
	private ArrayList<ReagentStack> reagList = new ArrayList<>();
	private Color col;
	private int ticksExisted = -1;
	private int maxRadius;

	public EntityFlameCore(EntityType<EntityFlameCore> type, Level worldIn){
		super(type, worldIn);
		setNoGravity(true);
		noPhysics = true;
		noCulling = true;
		if(worldIn instanceof ServerLevel world){
			//Flame cores need to be registered
			FlameCoresSavedData.addFlameCore(world, this);
		}
	}

	public void setInitialValues(ReagentMap reags, int radius){
		this.reags = reags == null ? new ReagentMap() : reags;
		maxRadius = radius;
		CRSounds.playSoundServer(level, blockPosition(), CRSounds.FIRE_SWELL, SoundSource.BLOCKS, 2F, 1F);
	}

	private ReagentMap reags = null;

	@Override
	public AABB getBoundingBoxForCulling(){
		return BlockEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void defineSynchedData(){
		entityData.define(TIME_EXISTED, 0);
		entityData.define(COLOR, Color.WHITE.getRGB());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldRender(double x, double y, double z){
		return true;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt){
		maxRadius = nbt.getInt("rad");
		reags = ReagentMap.readFromNBT(nbt);
		ticksExisted = nbt.getInt("life");
		entityData.set(TIME_EXISTED, ticksExisted);
		if(nbt.contains("color")){
			col = new Color(nbt.getInt("color"), true);
			entityData.set(COLOR, col.getRGB());
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt){
		if(reags != null){
			reags.write(nbt);
		}
		if(col != null){
			nbt.putInt("color", col.getRGB());
		}

		nbt.putInt("rad", maxRadius);
		nbt.putInt("life", ticksExisted);
	}

	public int getRadius(){
		return getRadius(ticksExisted);
	}

	private static int getRadius(int ticksExisted){
		return Math.round(FLAME_VEL * (float) ticksExisted);
	}

	@Override
	public void tick(){
		super.tick();

		if(level.isClientSide || reags == null){
			return;
		}

		ticksExisted++;
		entityData.set(TIME_EXISTED, ticksExisted);

		if(col == null){
			reagList.clear();
			int r = 0;
			int g = 0;
			int b = 0;
			int a = 0;
			int amount = 0;
			double temp = reags.getTempC();
			for(IReagent type : reags.keySetReag()){
				int qty = reags.getQty(type);
				if(qty > 0){
					Color color = type.getColor(type.getPhase(temp));
					r += qty * color.getRed();
					g += qty * color.getGreen();
					b += qty * color.getBlue();
					a += qty * color.getAlpha();
					amount += qty;

					if(!type.getID().equals(EnumReagents.PHELOSTOGEN.id()) && !type.getID().equals(EnumReagents.HELLFIRE.id())){
						reagList.add(reags.getStack(type));
					}
				}
			}
			if(amount <= 0){
				remove(RemovalReason.DISCARDED);
				return;
			}

			col = new Color(r / amount, g / amount, b / amount, a / amount);
			entityData.set(COLOR, col.getRGB());
		}

		final int distributedTime = 8;//Action is distributed to do 1/8 the block changes every tick instead of all of them every 8 ticks
		int tickMod = ticksExisted % distributedTime;
		int radius = getRadius(ticksExisted - tickMod);
		BlockPos pos = new BlockPos((int) Math.round(getX()), (int) Math.round(getY()), (int) Math.round(getZ()));
		boolean lastAction = maxRadius <= radius && tickMod == (distributedTime - 1);

		double temp = reags.getTempC();

		for(int i = tickMod; i <= radius; i += distributedTime){
			for(int j = 0; j <= radius; j++){
				//x-z plane
				act(reagList, reags, temp, level, pos.offset(i, -radius, j), lastAction);
				act(reagList, reags, temp, level, pos.offset(i - radius, -radius, j), lastAction);
				act(reagList, reags, temp, level, pos.offset(i, -radius, j - radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i - radius, -radius, j - radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i, radius, j), lastAction);
				act(reagList, reags, temp, level, pos.offset(i - radius, radius, j), lastAction);
				act(reagList, reags, temp, level, pos.offset(i, radius, j - radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i - radius, radius, j - radius), lastAction);
				//x-y plane
				act(reagList, reags, temp, level, pos.offset(i, j, -radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i - radius, j, -radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i, j - radius, -radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i - radius, j - radius, -radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i, j, radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i - radius, j, radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i, j - radius, radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(i - radius, j - radius, radius), lastAction);
				//y-z plane
				act(reagList, reags, temp, level, pos.offset(-radius, i, j), lastAction);
				act(reagList, reags, temp, level, pos.offset(-radius, i - radius, j), lastAction);
				act(reagList, reags, temp, level, pos.offset(-radius, i, j - radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(-radius, i - radius, j - radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(radius, i, j), lastAction);
				act(reagList, reags, temp, level, pos.offset(radius, i - radius, j), lastAction);
				act(reagList, reags, temp, level, pos.offset(radius, i, j - radius), lastAction);
				act(reagList, reags, temp, level, pos.offset(radius, i - radius, j - radius), lastAction);
			}
		}

		if(lastAction){
			remove(RemovalReason.DISCARDED);
		}
	}

	private static void act(ArrayList<ReagentStack> reagList, ReagentMap reags, double temp, Level world, BlockPos pos, boolean lastAction){
		//Block destruction and ignition is disabled by alchemical salt
		if(reags.getQty(EnumReagents.ALCHEMICAL_SALT.id()) == 0){
			BlockState state = world.getBlockState(pos);

			if(!CRConfig.isProtected(world, pos, state) && state.getDestroySpeed(world, pos) >= 0){
				world.setBlock(pos, lastAction && Math.random() > 0.75D && Blocks.FIRE.defaultBlockState().canSurvive(world, pos) ? Blocks.FIRE.defaultBlockState() : Blocks.AIR.defaultBlockState(), lastAction ? 3 : 18);
			}

			//Set entities on fire
			List<LivingEntity> ents = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), EntitySelector.ENTITY_STILL_ALIVE);
			for(LivingEntity ent : ents){
				ent.setSecondsOnFire(15);
			}
		}

		for(ReagentStack r : reagList){
			IAlchEffect effect;
			//reagList should never contain null
			if(r != null){
				effect = r.getType().getEffect();
				effect.doEffect(world, pos, r.getAmount(), EnumMatterPhase.FLAME, reags);
			}
		}
	}

	@Override
	protected MovementEmission getMovementEmission(){
		return MovementEmission.NONE;
	}
}
