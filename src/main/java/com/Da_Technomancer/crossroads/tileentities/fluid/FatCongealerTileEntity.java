package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.fluids.BlockLiquidFat;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class FatCongealerTileEntity extends ModuleTE{

	public FatCongealerTileEntity(){
		super();
		fluidProps[0] = new TankProperty(0, 4_000, true, false, (Fluid f) -> BlockLiquidFat.getLiquidFat() == f);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	private static final double VALUE_PER_ENERGY = 1D;//TODO These numbers need to be rebalanced
	private static final double SAT_UPPER_SPEED_BOUND = 2;

	@Override
	public void update(){
		super.update();
		if(world.isRemote){
			return;
		}

		if(world.getTileEntity(pos.offset(EnumFacing.UP)) != null && world.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			IAxleHandler rot = world.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN);
			int value = Math.min((int) (Math.abs(rot.getMotionData()[1]) * VALUE_PER_ENERGY), 40);
			if(value == 0 || fluids[0] == null){
				return;
			}
			int sat = (int) (((double) value) * RotaryUtil.findEfficiency(rot.getMotionData()[0], 0, SAT_UPPER_SPEED_BOUND));
			sat = Math.min(20, sat);
			value = Math.min(value, 20 + sat);
			if(value * EnergyConverters.FAT_PER_VALUE > fluids[0].amount){
				return;
			}
			rot.addEnergy(-1, false, false);
			if((fluids[0].amount -= value * EnergyConverters.FAT_PER_VALUE) <= 0){
				fluids[0] = null;
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
