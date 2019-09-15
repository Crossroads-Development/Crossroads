package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.fluids.BlockDirtyWater;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

public class WaterCentrifugeTileEntity extends InventoryTE{

	private static final double TIP_POINT = .5D;
	private boolean neg;

	public WaterCentrifugeTileEntity(){
		super(1);
		fluidProps[0] = new TankProperty(0, 10_000, true, false, (Fluid f) -> f == FluidRegistry.WATER || f == BlockDirtyWater.getDirtyWater());
		fluidProps[1] = new TankProperty(1, 10_000, false, true, null);
	}

	public boolean isNeg(){
		return neg;
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public boolean useRotary(){
		return true;
	}

	@Override
	public void update(){
		super.update();
		if(world.isRemote){
			return;
		}

		if(Math.abs(motData[0]) >= TIP_POINT && (Math.signum(motData[0]) == -1) == neg){
			neg = !neg;
			if(fluids[0] != null && fluids[0].amount >= 250){
				boolean dirty = fluids[0].getFluid() != FluidRegistry.WATER;
				ItemStack product = ItemStack.EMPTY;
				if(dirty){
					int choice = world.rand.nextInt(RecipeHolder.dirtyWaterWeights) + 1;
					for(Pair<Integer, ItemStack> entry : RecipeHolder.dirtyWaterRecipes){
						choice -= entry.getLeft();
						if(choice <= 0){
							product = entry.getRight();
							break;
						}
					}
				}else{
					product = MiscUtil.getOredictStack("dustSalt", 1);
				}
				if((fluids[0].amount -= 250) == 0){
					fluids[0] = null;
				}
				fluids[1] = new FluidStack(BlockDistilledWater.getDistilledWater(), Math.min(fluidProps[1].getCapacity(), 250 + (fluids[1] == null ? 0 : fluids[1].amount)));
				if(inventory[0].isEmpty() || inventory[0].isItemEqual(product)){
					inventory[0] = new ItemStack(product.getItem(), Math.min(64, 1 + inventory[0].getCount()), product.getMetadata());
				}
				markDirty();
			}
		}
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("neg", neg);

		return nbt;
	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);
		neg = nbt.getBoolean("neg");
	}

	private final IFluidHandler waterHandler = new FluidHandler(0);
	private final IFluidHandler dWaterHandler = new FluidHandler(1);
	private final IFluidHandler masterHandler = new FluidHandler(-1);
	private final IItemHandler saltHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction facing){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing == null){
			return (T) masterHandler;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() != world.getBlockState(pos).get(Properties.HORIZ_AXIS)){
			return (T) waterHandler;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() == world.getBlockState(pos).get(Properties.HORIZ_AXIS)){
			return (T) dWaterHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) saltHandler;
		}
		if(cap == Capabilities.AXLE_CAPABILITY && facing == Direction.UP){
			return (T) axleHandler;
		}

		return super.getCapability(cap, facing);
	}

	@Override
	public double getMoInertia(){
		return 115;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return index == 0;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public String getName(){
		return "container.water_centrifuge";
	}
}
