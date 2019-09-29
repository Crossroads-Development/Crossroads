package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;

public class FluidCoolingChamberTileEntity extends InventoryTE{

	public static final int HEATING_RATE = 40;
	private double storedHeat = 0;

	public FluidCoolingChamberTileEntity(){
		super(1);
		fluidProps[0] = new TankProperty(0, 4_000, true, false, RecipeHolder.fluidCoolingRecipes::containsKey);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}

		double moved = Math.min(storedHeat, HEATING_RATE);
		if(moved > 0){
			storedHeat -= moved;
			temp += moved;
			markDirty();
		}

		Pair<Integer, Triple<ItemStack, Double, Double>> craft = fluids[0] == null ? null : RecipeHolder.fluidCoolingRecipes.get(fluids[0].getFluid());

		if(craft != null && fluids[0].amount >= craft.getLeft()){
			if(temp + storedHeat < craft.getRight().getMiddle() && (inventory[0].isEmpty() || ItemStack.areItemsEqual(craft.getRight().getLeft(), inventory[0]) && inventory[0].getMaxStackSize() - inventory[0].getCount() >= craft.getRight().getLeft().getCount())){
				storedHeat += craft.getRight().getRight();
				if((fluids[0].amount -= craft.getLeft()) <= 0){
					fluids[0] = null;
				}
				markDirty();
				if(inventory[0].isEmpty()){
					inventory[0] = craft.getRight().getLeft().copy();
				}else{
					inventory[0].grow(craft.getRight().getLeft().getCount());
				}
			}
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		storedHeat = nbt.getDouble("heat_stored");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putDouble("heat_stored", storedHeat);
		return nbt;
	}

	private final FluidHandler fluidHandler = new FluidHandler(0);
	private final ItemHandler itemHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) fluidHandler;
		}

		if(capability == Capabilities.HEAT_CAPABILITY && (facing == Direction.UP || facing == null)){
			return (T) heatHandler;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public String getName(){
		return "container.fluid_cooler";
	}
}