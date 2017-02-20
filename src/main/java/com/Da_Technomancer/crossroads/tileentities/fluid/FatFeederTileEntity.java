package com.Da_Technomancer.crossroads.tileentities.fluid;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.fluids.BlockLiquidFat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FatFeederTileEntity extends TileEntity implements ITickable{

	private FluidStack content = null;
	private final int CAPACITY = EnergyConverters.FAT_PER_VALUE * 40;
	private final double ENERGY_PER_VALUE = 1;

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		
		IAxleHandler upAxle = world.getTileEntity(pos.offset(EnumFacing.UP)) != null ? world.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN) : null;
		IAxleHandler downAxle = world.getTileEntity(pos.offset(EnumFacing.DOWN)) != null ? world.getTileEntity(pos.offset(EnumFacing.DOWN)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP) : null;
		
		if(upAxle != null && downAxle != null && content != null){
			int range = (int) (downAxle.getMotionData()[0] == 0 ? 0 : Math.abs(upAxle.getMotionData()[0] / downAxle.getMotionData()[0]));
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.subtract(new Vec3i(range, range, range)), pos.add(new Vec3i(range, range, range))), EntitySelectors.IS_ALIVE);
			if(players != null){
				for(EntityPlayer play : players){
					FoodStats food = play.getFoodStats();
					int added = (int) Math.min(content.amount / EnergyConverters.FAT_PER_VALUE, Math.min(Math.abs(upAxle.getMotionData()[1]) / ENERGY_PER_VALUE, 40 - (food.getFoodLevel() + food.getSaturationLevel())));
					if(added <= 0){
						continue;
					}
					content.amount -= added * EnergyConverters.FAT_PER_VALUE;
					upAxle.addEnergy(-added * ENERGY_PER_VALUE, false, false);
					downAxle.addEnergy(added * ENERGY_PER_VALUE, false, false);
					int hungerAdded = Math.min(20 - food.getFoodLevel(), added);
					//The way saturation is coded is weird, and the best way to do this is through nbt.
					NBTTagCompound nbt = new NBTTagCompound();
					food.writeNBT(nbt);
					nbt.setInteger("foodLevel", hungerAdded + food.getFoodLevel());
					nbt.setFloat("foodSaturationLevel", Math.min(20F - food.getSaturationLevel(), added - hungerAdded) + food.getSaturationLevel());
					food.readNBT(nbt);
					if(content.amount <= 0){
						content = null;
						markDirty();
						return;
					}
				}
			}
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(content != null){
			content.writeToNBT(nbt);
		}

		return nbt;
	}

	private final IFluidHandler mainHandler = new MainHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP){
			return (T) mainHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	private class MainHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(content, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null || resource.getFluid() != BlockLiquidFat.getLiquidFat()){
				return 0;
			}
			int amount = Math.min(CAPACITY - (content == null ? 0 : content.amount), resource.amount);

			if(doFill && amount != 0){
				content = new FluidStack(BlockLiquidFat.getLiquidFat(), amount + (content == null ? 0 : content.amount));
			}

			return amount;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			return null;
		}
	}
}
