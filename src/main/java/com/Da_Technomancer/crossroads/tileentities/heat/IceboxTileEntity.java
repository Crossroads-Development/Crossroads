package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class IceboxTileEntity extends InventoryTE{

	private int burnTime;
	private int maxBurnTime = 0;

	public IceboxTileEntity(){
		super(1);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void update(){
		super.update();
		if(world.isRemote){
			return;
		}

		if(burnTime != 0 && temp > -20){
			temp = Math.max(-20, temp - 10);
			if(--burnTime == 0){
				world.setBlockState(pos, CrossroadsBlocks.icebox.getDefaultState().with(Properties.ACTIVE, false), 18);
			}
			markDirty();
		}

		if(burnTime == 0 && RecipeHolder.coolingRecipes.get(inventory[0]) > 0){
			burnTime = RecipeHolder.coolingRecipes.get(inventory[0]);
			maxBurnTime = burnTime;
			Item item = inventory[0].getItem();
			inventory[0].shrink(1);
			if(inventory[0].isEmpty() && item.hasContainerItem(inventory[0])){
				inventory[0] = item.getContainerItem(inventory[0]);
			}
			world.setBlockState(pos, CrossroadsBlocks.icebox.getDefaultState().with(Properties.ACTIVE, true), 18);
			markDirty();
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		burnTime = nbt.getInt("burn");
		maxBurnTime = nbt.getInt("max_burn");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("burn", burnTime);
		nbt.putInt("max_burn", maxBurnTime);
		return nbt;
	}

	private ItemHandler itemHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY && (facing == Direction.UP || facing == null)){
			return (T) heatHandler;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && RecipeHolder.coolingRecipes.get(stack) > 0;
	}

	@Override
	public int getField(int id){
		if(id == getFieldCount() - 1){
			return world.isRemote || maxBurnTime == 0 ? burnTime : burnTime * 100 / maxBurnTime;
		}else{
			return super.getField(id);
		}
	}

	@Override
	public void setField(int id, int value){
		super.setField(id, value);

		if(id == getFieldCount() - 1){
			burnTime = value;
		}
	}

	@Override
	public int getFieldCount(){
		return super.getFieldCount() + 1;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public String getName(){
		return "container.icebox";
	}
}
