package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class RotaryDrillTileEntity extends ModuleTE{

	@ObjectHolder("rotary_drill")
	public static TileEntityType<RotaryDrillTileEntity> type = null;

	private static final DamageSource DRILL = new DamageSource("drill");

	public RotaryDrillTileEntity(){
		super(type);
	}

	public RotaryDrillTileEntity(boolean golden){
		super(type);
		this.golden = golden;
	}

	private int ticksExisted = 0;
	private boolean golden;
	public static final double ENERGY_USE_IRON = 3D;
	public static final double ENERGY_USE_GOLD = 5D;
	private static final double SPEED_PER_HARDNESS = .2D;
	private static final float DAMAGE_PER_SPEED = 5F;
	public static final double[] INERTIA = {50, 100};

	public boolean isGolden(){
		return golden;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected AxleHandler createAxleHandler(){
		return new AngleAxleHandler();
	}

	@Override
	protected double getMoInertia(){
		return INERTIA[golden ? 1 : 0];
	}

	private Direction getFacing(){
		BlockState state = getBlockState();
		if(state.getBlock() instanceof RotaryDrill){
			return state.get(ESProperties.FACING);
		}
		remove();
		return Direction.UP;
	}

	@Override
	public void updateContainingBlockInfo(){
		super.updateContainingBlockInfo();
		axleOpt.invalidate();
		axleOpt = LazyOptional.of(this::createAxleHandler);
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}

		double powerDrain = golden ? ENERGY_USE_GOLD : ENERGY_USE_IRON;
		if(Math.abs(motData[1]) >= powerDrain && Math.abs(motData[0]) >= 0.05D){
			axleHandler.addEnergy(-powerDrain, false);
			if(++ticksExisted % 2 == 0){//Activate once every redstone tick
				Direction facing = getFacing();
				BlockPos targetPos = pos.offset(facing);
				BlockState targetState = world.getBlockState(targetPos);
				if(!targetState.isAir(world, targetPos)){
					float hardness = targetState.getBlockHardness(world, targetPos);
					if(hardness >= 0 && Math.abs(motData[0]) >= hardness * SPEED_PER_HARDNESS){
						world.destroyBlock(targetPos, true);
					}
				}

				List<LivingEntity> ents = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.offset(facing)), EntityPredicates.IS_ALIVE);
				for(LivingEntity ent : ents){
					ent.attackEntityFrom(golden ? new EntityDamageSource("drill", FakePlayerFactory.get((ServerWorld) world, new GameProfile(null, "drill_player_" + MiscUtil.getDimensionName(world)))) : DRILL, (float) Math.abs(motData[0]) * DAMAGE_PER_SPEED);
				}
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("gold", golden);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		golden = nbt.getBoolean("gold");
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putBoolean("gold", golden);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXLE_CAPABILITY && (side == null || side == getFacing().getOpposite())){
			return (LazyOptional<T>) axleOpt;
		}
		return super.getCapability(cap, side);
	}
}
