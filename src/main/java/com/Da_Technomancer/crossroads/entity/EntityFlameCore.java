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
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ObjectHolder;

import java.awt.*;
import java.util.ArrayList;

import static net.minecraft.tileentity.TileEntity.INFINITE_EXTENT_AABB;

@ObjectHolder(Crossroads.MODID)
public class EntityFlameCore extends Entity{

	@ObjectHolder("flame_core")
	public static EntityType<EntityFlameCore> type = null;

	protected static final DataParameter<Integer> TIME_EXISTED = EntityDataManager.createKey(EntityFlameCore.class, DataSerializers.VARINT);
	protected static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityFlameCore.class, DataSerializers.VARINT);
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
		noClip = true;
		ignoreFrustumCheck = true;
	}


	public void setInitialValues(ReagentMap reags, int radius){
		this.reags = reags == null ? new ReagentMap() : reags;
		maxRadius = radius;
	}

	private ReagentMap reags = null;

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public IPacket<?> createSpawnPacket(){
		return new SSpawnObjectPacket(this);
	}

	@Override
	protected void registerData(){
		dataManager.register(TIME_EXISTED, 0);
		dataManager.register(COLOR, Color.WHITE.getRGB());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z){
		return true;
	}

	@Override
	public void readAdditional(CompoundNBT nbt){
		reags = new ReagentMap();
		maxRadius = nbt.getInt("rad");
		reags = ReagentMap.readFromNBT(nbt);
		setInitialValues(reags, maxRadius);
		ticksExisted = nbt.getInt("life");
		dataManager.set(TIME_EXISTED, ticksExisted);
		if(nbt.contains("color")){
			col = new Color(nbt.getInt("color"), true);
			dataManager.set(COLOR, col.getRGB());
		}
	}

	@Override
	protected void writeAdditional(CompoundNBT nbt){
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

		if(world.isRemote || reags == null){
			return;
		}

		ticksExisted++;
		dataManager.set(TIME_EXISTED, ticksExisted);

		if(col == null){
			reagList.clear();
			int r = 0;
			int g = 0;
			int b = 0;
			int a = 0;
			int amount = 0;
			double temp = reags.getTempC();
			for(IReagent type : reags.keySet()){
				int qty = reags.getQty(type);
				if(qty > 0){
					Color color = type.getColor(type.getPhase(temp));
					r += qty * color.getRed();
					g += qty * color.getGreen();
					b += qty * color.getBlue();
					a += qty * color.getAlpha();
					amount += qty;

					if(!type.getId().equals(EnumReagents.PHELOSTOGEN.id()) && !type.getId().equals(EnumReagents.HELLFIRE.id())){
						reagList.add(reags.getStack(type));
					}
				}
			}
			if(amount <= 0){
				remove();
				return;
			}

			col = new Color(r / amount, g / amount, b / amount, a / amount);
			dataManager.set(COLOR, col.getRGB());
		}

		if(ticksExisted % 8 == 0){
			int radius = (int) Math.round(FLAME_VEL * (float) ticksExisted);
			BlockPos pos = new BlockPos(posX, posY, posZ);
			boolean lastAction = maxRadius <= radius;

			double temp = reags.getTempC();

			for(int i = 0; i <= radius; i++){
				for(int j = 0; j <= radius; j++){
					//x-z plane
					act(reagList, reags, temp, world, pos.add(i, -radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, -radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(i, -radius, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, -radius, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i, radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(i, radius, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, radius, j - radius), lastAction);
					//x-y plane
					act(reagList, reags, temp, world, pos.add(i, j, -radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, j, -radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i, j - radius, -radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, j - radius, -radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i, j, radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, j, radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i, j - radius, radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, j - radius, radius), lastAction);
					//y-z plane
					act(reagList, reags, temp, world, pos.add(-radius, i, j), lastAction);
					act(reagList, reags, temp, world, pos.add(-radius, i - radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(-radius, i, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(-radius, i - radius, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(radius, i, j), lastAction);
					act(reagList, reags, temp, world, pos.add(radius, i - radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(radius, i, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(radius, i - radius, j - radius), lastAction);
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

			if(!CRConfig.isProtected(world, pos, state) && state.getBlockHardness(world, pos) >= 0){
				world.setBlockState(pos, lastAction && Math.random() > 0.75D && Blocks.FIRE.getDefaultState().isValidPosition(world, pos) ? Blocks.FIRE.getDefaultState() : Blocks.AIR.getDefaultState(), lastAction ? 3 : 18);
			}
		}

		for(ReagentStack r : reagList){
			IAlchEffect effect;
			//reagList should never contain null
			if(r != null && (effect = r.getType().getEffect(r.getType().getPhase(temp))) != null){
				effect.doEffect(world, pos, r.getAmount(), EnumMatterPhase.FLAME, reags);
			}
		}
	}

	@Override
	public boolean canTriggerWalking(){
		return false;
	}
}
