package com.Da_Technomancer.crossroads.tileentities.fluid;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.fluids.BlockLiquidFat;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FatCongealerTileEntity extends TileEntity implements ITickable{

	private FluidStack content = null;
	private final int CAPACITY = EnergyConverters.FAT_PER_VALUE * 40;
	private final double VALUE_PER_ENERGY = .1D;
	private final double SAT_UPPER_SPEED_BOUND = 2;

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(world.getTileEntity(pos.offset(EnumFacing.UP)) != null && world.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			IAxleHandler rot = world.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN);
			int value = Math.min((int) (Math.abs(rot.getMotionData()[1]) * VALUE_PER_ENERGY), 40);
			if(value == 0 || content == null){
				return;
			}
			int sat = (int) (((double) value) * MiscOp.findEfficiency(rot.getMotionData()[0], 0, SAT_UPPER_SPEED_BOUND));
			sat = Math.min(20, sat);
			value = Math.min(value, 20 + sat);
			if(value * EnergyConverters.FAT_PER_VALUE > content.amount){
				return;
			}
			rot.addEnergy(-1, false, false);
			if((content.amount -= value * EnergyConverters.FAT_PER_VALUE) <= 0){
				content = null;
			}
			ItemStack stack = new ItemStack(ModItems.edibleBlob, 1, 0);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("food", value - sat);
			nbt.setInteger("sat", sat);
			stack.setTagCompound(nbt);
			EntityItem ent = new EntityItem(world, pos.getX() + .5D, pos.getY() - .5D, pos.getZ() + .5D, stack);
			ent.motionX = 0;
			ent.motionZ = 0;
			world.spawnEntity(ent);
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
