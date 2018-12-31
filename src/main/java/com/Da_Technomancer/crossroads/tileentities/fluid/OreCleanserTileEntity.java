package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.fluids.BlockDirtyWater;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class OreCleanserTileEntity extends InventoryTE{

	public static final int WATER_USE = 250;
	private int progress = 0;//Out of 100

	public OreCleanserTileEntity(){
		super(2);
		fluidProps[0] = new TankProperty(0, 1_000, true, false, (Fluid f) -> f == BlockSteam.getSteam());//Steam
		fluidProps[1] = new TankProperty(1, 1_000, false, true);//Water
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote){
			return;
		}

		if(fluids[0] != null && fluids[0].amount >= WATER_USE && fluidProps[1].getCapacity() - (fluids[1] == null ? 0 : fluids[1].amount) >= WATER_USE && !inventory[0].isEmpty()){
			ItemStack created = RecipeHolder.oreCleanserRecipes.get(inventory[0]);
			if(created.isEmpty()){
				created = inventory[0].copy();
				created.setCount(1);
			}else{
				created = created.copy();
			}

			if(!inventory[1].isEmpty() && (inventory[1].getMaxStackSize() - inventory[1].getCount() < created.getCount() || !ItemStack.areItemsEqual(created, inventory[1]) || !ItemStack.areItemStackTagsEqual(created, inventory[1]))){
				return;
			}

			progress++;
			markDirty();
			if(progress < 100){
				return;
			}

			progress = 0;

			if((fluids[0].amount -= WATER_USE) <= 0){
				fluids[0] = null;
			}

			if(fluids[1] == null){
				fluids[1] = new FluidStack(BlockDirtyWater.getDirtyWater(), WATER_USE);
			}else{
				fluids[1].amount += WATER_USE;
			}

			inventory[0].shrink(1);
			if(inventory[1].isEmpty()){
				inventory[1] = created;
			}else{
				inventory[1].grow(created.getCount());
			}
		}else{
			progress = 0;
		}
	}

	@Override
	public int getFieldCount(){
		return super.getFieldCount() + 1;
	}

	@Override
	public int getField(int id){
		if(id == getFieldCount() - 1){
			return progress;
		}
		return super.getField(id);
	}

	@Override
	public void setField(int id, int value){
		if(id == getFieldCount() - 1){
			progress = value;
		}
		super.setField(id, value);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		progress = nbt.getInteger("prog");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("prog", progress);
		return nbt;
	}

	private final ItemHandler itemHandler = new ItemHandler(null);
	private final FluidHandler innerFluidHandler = new FluidHandler(-1);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) innerFluidHandler;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return index == 1;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && !RecipeHolder.oreCleanserRecipes.get(stack).isEmpty();
	}

	@Override
	public String getName(){
		return "container.ore_cleanser";
	}
}
