package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.fluids.BlockLiquidFat;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

public class FatFeederTileEntity extends ModuleTE{

	public FatFeederTileEntity(){
		super();
		fluidProps[0] = new TankProperty(0, 4_000, true, false, (Fluid f) -> f == BlockLiquidFat.getLiquidFat());
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	private static final double ENERGY_PER_VALUE = 1;

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		IAxleHandler upAxle = world.getTileEntity(pos.offset(EnumFacing.UP)) != null ? world.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN) : null;
		IAxleHandler downAxle = world.getTileEntity(pos.offset(EnumFacing.DOWN)) != null ? world.getTileEntity(pos.offset(EnumFacing.DOWN)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP) : null;

		if(upAxle != null && downAxle != null && fluids[0] != null){
			int range = (int) (downAxle.getMotionData()[0] == 0 ? 0 : Math.abs(upAxle.getMotionData()[0] / downAxle.getMotionData()[0]));
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.subtract(new Vec3i(range, range, range)), pos.add(new Vec3i(range, range, range))), EntitySelectors.IS_ALIVE);
			for(EntityPlayer play : players){
				FoodStats food = play.getFoodStats();
				int added = Math.min(fluids[0].amount / EnergyConverters.FAT_PER_VALUE, (int) Math.min(Math.abs(upAxle.getMotionData()[1]) / ENERGY_PER_VALUE, 40 - (food.getFoodLevel() + food.getSaturationLevel())));
				if(added <= 0){
					continue;
				}
				fluids[0].amount -= added * EnergyConverters.FAT_PER_VALUE;
				upAxle.addEnergy(-added * ENERGY_PER_VALUE, false, false);
				downAxle.addEnergy(added * ENERGY_PER_VALUE, false, false);
				int hungerAdded = Math.min(20 - food.getFoodLevel(), added);
				//The way saturation is coded is weird (defined relative to hunger), and the best way to do this is through nbt.
				NBTTagCompound nbt = new NBTTagCompound();
				food.writeNBT(nbt);
				nbt.setInteger("foodLevel", hungerAdded + food.getFoodLevel());
				nbt.setFloat("foodSaturationLevel", Math.min(20F - food.getSaturationLevel(), added - hungerAdded) + food.getSaturationLevel());
				food.readNBT(nbt);
				if(fluids[0].amount <= 0){
					fluids[0] = null;
					markDirty();
					return;
				}
			}
			markDirty();
		}
	}

	private final FluidHandler mainHandler = new FluidHandler(0);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP){
			return (T) mainHandler;
		}

		return super.getCapability(capability, facing);
	}
}
