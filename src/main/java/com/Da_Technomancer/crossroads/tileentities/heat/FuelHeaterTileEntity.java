package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class FuelHeaterTileEntity extends InventoryTE{

	private int burnTime;

	public FuelHeaterTileEntity(){
		super(1);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	protected boolean useRotary(){
		return false;
	}

	@Override
	public void update(){
		super.update();
		if(world.isRemote){
			return;
		}

		if(burnTime != 0){
			temp += 10D;
			if(--burnTime == 0){
				world.setBlockState(pos, ModBlocks.fuelHeater.getDefaultState().withProperty(Properties.ACTIVE, false), 18);
			}
			markDirty();
		}

		if(burnTime == 0 && TileEntityFurnace.isItemFuel(inventory[0])){
			burnTime = TileEntityFurnace.getItemBurnTime(inventory[0]);
			Item item = inventory[0].getItem();
			inventory[0].shrink(1);
			if(inventory[0].isEmpty() && item.hasContainerItem(inventory[0])){
				inventory[0] = item.getContainerItem(inventory[0]);
			}
			world.setBlockState(pos, ModBlocks.fuelHeater.getDefaultState().withProperty(Properties.ACTIVE, true), 18);
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		burnTime = nbt.getInteger("burn");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("burn", burnTime);
		return nbt;
	}

	private ItemHandler itemHandler = new ItemHandler(null);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null)){
			return (T) heatHandler;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && TileEntityFurnace.isItemFuel(stack);
	}

	@Override
	public int getField(int id){
		if(id == 0){
			return burnTime;
		}else{
			return 0;
		}
	}

	@Override
	public void setField(int id, int value){
		if(id == 0){
			burnTime = value;
		}
	}

	@Override
	public int getFieldCount(){
		return 1;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public String getName(){
		return "container.fuel_heater";
	}
}
