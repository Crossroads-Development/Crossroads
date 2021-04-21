package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

import java.awt.*;
import java.util.ArrayList;

import static net.minecraft.tileentity.TileEntity.INFINITE_EXTENT_AABB;

@ObjectHolder(Crossroads.MODID)
public class EntityFlameCore extends Entity{

	@ObjectHolder("flame_core")
	public static EntityType<EntityFlameCore> type = null;

	protected static final DataParameter<Integer> TIME_EXISTED = EntityDataManager.defineId(EntityFlameCore.class, DataSerializers.INT);
	protected static final DataParameter<Integer> COLOR = EntityDataManager.defineId(EntityFlameCore.class, DataSerializers.INT);
	protected static final float FLAME_VEL = 0.1F;//Flame interface speed, blocks per tick

	/**
	 * In order to avoid iterating over a large ReagentStack[] that is mostly empty several times a tick, this list is created to store all non-null reagent stacks
	 */
	private ArrayList<ReagentStack> reagList = new ArrayList<>();
	private Color col;
	private int ticksExisted = -1;
	private int maxRadius;

	public EntityFlameCore(EntityType<EntityFlameCore> type, World worldIn){
		super(type, worldIn);
		setNoGravity(true);
		noPhysics = true;
		noCulling = true;
	}


	public void setInitialValues(ReagentMap reags, int radius){
		this.reags = reags == null ? new ReagentMap() : reags;
		maxRadius = radius;
	}

	private ReagentMap reags = null;

	@Override
	public AxisAlignedBB getBoundingBoxForCulling(){
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public IPacket<?> getAddEntityPacket(){
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
	public void readAdditionalSaveData(CompoundNBT nbt){
		reags = new ReagentMap();
		maxRadius = nbt.getInt("rad");
		reags = ReagentMap.readFromNBT(nbt);
		setInitialValues(reags, maxRadius);
		ticksExisted = nbt.getInt("life");
		entityData.set(TIME_EXISTED, ticksExisted);
		if(nbt.contains("color")){
			col = new Color(nbt.getInt("color"), true);
			entityData.set(COLOR, col.getRGB());
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt){
		if(reags != null){
			reags.write(nbt);
		}
		if(col != null){
			nbt.putInt("color", col.getRGB());
		}

		nbt.putInt("rad", maxRadius);
		nbt.putInt("life", ticksExisted);
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
				remove();
				return;
			}

			col = new Color(r / amount, g / amount, b / amount, a / amount);
			entityData.set(COLOR, col.getRGB());
		}

		if(ticksExisted % 8 == 0){
			int radius = Math.round(FLAME_VEL * (float) ticksExisted);
			BlockPos pos = new BlockPos(getX(), getY(), getZ());
			boolean lastAction = maxRadius <= radius;

			double temp = reags.getTempC();

			for(int i = 0; i <= radius; i++){
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
				remove();
			}
		}
	}

	private static void act(ArrayList<ReagentStack> reagList, ReagentMap reags, double temp, World world, BlockPos pos, boolean lastAction){
		//Block destruction is disabled by alchemical salt
		if(reags.getQty(EnumReagents.ALCHEMICAL_SALT.id()) == 0){
			BlockState state = world.getBlockState(pos);

			if(!CRConfig.isProtected(world, pos, state) && state.getDestroySpeed(world, pos) >= 0){
				world.setBlock(pos, lastAction && Math.random() > 0.75D && Blocks.FIRE.defaultBlockState().canSurvive(world, pos) ? Blocks.FIRE.defaultBlockState() : Blocks.AIR.defaultBlockState(), lastAction ? 3 : 18);
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
	public boolean isMovementNoisy(){
		return false;
	}
}
