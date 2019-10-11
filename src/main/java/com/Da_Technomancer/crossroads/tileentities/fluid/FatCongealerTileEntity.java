package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class FatCongealerTileEntity extends InventoryTE{

	public FatCongealerTileEntity(){
		super(0);
		fluidProps[0] = new TankProperty(0, 10_000, true, false, (Fluid f) -> BlockLiquidFat.getLiquidFat() == f);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	public static final double HUN_PER_SPD = 4D;
	public static final double SAT_PER_SPD = 4D;

	@Override
	public void tick(){
		super.tick();
		if(world.isRemote){
			return;
		}

		TileEntity adjTE;
		IAxleHandler topHandler;
		IAxleHandler bottomHandler;

		if((adjTE = world.getTileEntity(pos.offset(Direction.UP))) != null && (topHandler = adjTE.getCapability(Capabilities.AXLE_CAPABILITY, Direction.DOWN)) != null && (adjTE = world.getTileEntity(pos.down())) != null && (bottomHandler = adjTE.getCapability(Capabilities.AXLE_CAPABILITY, Direction.UP)) != null){
			int hun = (int) Math.min(Math.abs(topHandler.getMotionData()[0]) * HUN_PER_SPD, 20);
			int sat = (int) Math.min(Math.abs(bottomHandler.getMotionData()[0]) * SAT_PER_SPD, 20);
			if(hun == 0 && sat == 0 || fluids[0] == null){
				return;
			}
			int fluidUse = EnergyConverters.FAT_PER_VALUE * (hun + sat);
			if(fluidUse > fluids[0].amount){
				return;
			}
			topHandler.addEnergy(-hun, false, false);
			bottomHandler.addEnergy(-sat, false, false);
			if((fluids[0].amount -= fluidUse) <= 0){
				fluids[0] = null;
			}
			ItemStack stack = new ItemStack(CRItems.edibleBlob, 1, 0);
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("food", hun);
			nbt.putInt("sat", sat);
			stack.put(nbt);
			ItemEntity ent = new ItemEntity(world, pos.getX() + .5D, pos.getY() + .5D, pos.getZ() + .5D, stack);
			ent.motionX = 2D * Math.random() - 1D;
			ent.motionZ = 2D * Math.random() - 1D;
			world.addEntity(ent);
			markDirty();
		}
	}

	private final FluidHandler mainHandler = new FluidHandler(0);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != Direction.DOWN && facing != Direction.UP){
			return (T) mainHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public String getName(){
		return "Fat Congealer";
	}
}
