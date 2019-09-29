package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;

public class RotaryDrillTileEntity extends ModuleTE{

	private static final DamageSource DRILL = new DamageSource("drill");

	public RotaryDrillTileEntity(){
		super();
	}

	public RotaryDrillTileEntity(boolean golden){
		super();
		this.golden = golden;
	}

	private int ticksExisted = 0;
	private boolean golden;
	public static final double ENERGY_USE = 2D;
	private static final double SPEED_PER_HARDNESS = .2D;

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
		return golden ? 100 : 50;
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}

		if(!(world.getBlockState(pos).getBlock() instanceof RotaryDrill)){
			invalidate();
			return;
		}

		if(Math.abs(motData[1]) >= ENERGY_USE){
			axleHandler.addEnergy(-ENERGY_USE, false, false);
			if(++ticksExisted % 8 == 0){
				Direction facing = world.getBlockState(pos).get(EssentialsProperties.FACING);
				if(world.getBlockState(pos.offset(facing)).getBlock().canCollideCheck(world.getBlockState(pos.offset(facing)), false)){
					float hardness = world.getBlockState(pos.offset(facing)).getBlockHardness(world, pos.offset(facing));
					if(hardness >= 0 && Math.abs(motData[0]) >= hardness * SPEED_PER_HARDNESS){
						world.destroyBlock(pos.offset(facing), true);
					}
				}else{
					List<LivingEntity> ents = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.offset(facing)), EntityPredicates.IS_ALIVE);
					for(LivingEntity ent : ents){
						ent.attackEntityFrom(golden ? new EntityDamageSource("drill", FakePlayerFactory.get((ServerWorld) world, new GameProfile(null, "drill_player_" + world.provider.getDimension()))) : DRILL, (float) Math.abs(motData[0] / SPEED_PER_HARDNESS));
					}
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
	public void read(CompoundNBT nbt){
		super.read(nbt);
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
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXLE_CAPABILITY && (side == null || side == world.getBlockState(pos).get(EssentialsProperties.FACING).getOpposite())){
			return (T) axleHandler;
		}
		return super.getCapability(cap, side);
	}
}
