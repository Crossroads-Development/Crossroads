package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.AbstractInventory;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class HeatingChamberTileEntity extends AbstractInventory implements ITickable{

	// 0 = Input, 1 = Output
	private ItemStack[] inventory = new ItemStack[2];
	private int progress = 0;
	private double temp;
	private boolean init = false;
	public final static int REQUIRED = 100;
	private final static int MINTEMP = 200;

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		if(!init){
			temp = EnergyConverters.BIOME_TEMP_MULT * worldObj.getBiomeForCoordsBody(pos).getFloatTemperature(getPos());
			init = true;
		}

		if(inventory[0] != null && getOutput() != null && temp >= 2 + MINTEMP){
			temp -= 2;
			if((progress += 2) >= REQUIRED){
				progress = 0;
				markDirty();
				if(inventory[1] == null){
					inventory[1] = getOutput();
				}else{
					inventory[1].stackSize += getOutput().stackSize;
				}

				if(--inventory[0].stackSize == 0){
					inventory[0] = null;
				}
			}
		}
	}

	private ItemStack getOutput(){
		ItemStack stack = FurnaceRecipes.instance().getSmeltingResult(inventory[0]);

		if(stack == null){
			return null;
		}

		if(inventory[1] != null && !ItemStack.areItemsEqual(stack, inventory[1])){
			return null;
		}

		if(inventory[1] != null && getInventoryStackLimit() - inventory[1].stackSize < stack.stackSize){
			return null;
		}

		return stack.copy();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		progress = nbt.getInteger("prog");

		if(nbt.hasKey("inv")){
			inventory[0] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("inv"));
		}
		if(nbt.hasKey("invO")){
			inventory[1] = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("invO"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		nbt.setBoolean("init", this.init);
		nbt.setDouble("temp", this.temp);
		nbt.setInteger("prog", progress);

		if(inventory[0] != null){
			NBTTagCompound invTag = new NBTTagCompound();
			inventory[0].writeToNBT(invTag);
			nbt.setTag("inv", invTag);
		}
		if(inventory[1] != null){
			NBTTagCompound invTagO = new NBTTagCompound();
			inventory[1].writeToNBT(invTagO);
			nbt.setTag("invO", invTagO);
		}
		return nbt;
	}

	private IHeatHandler heatHandler = new HeatHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && (side == EnumFacing.UP || side == null)){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		return (capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.UP)) ? (T) heatHandler : super.getCapability(capability, facing);
	}

	@Override
	public int getSizeInventory(){
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int index){
		return index > 1 ? null : inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		return index > 1 ? null : inventory[index].splitStack(count);
	}

	@Override
	public String getName(){
		return "container.heatingChamber";
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		if(index > 1){
			return null;
		}

		ItemStack holder = inventory[index];
		inventory[index] = null;
		return holder;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index > 1){
			return;
		}
		inventory[index] = stack;
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0;
	}

	@Override
	public int getField(int id){
		return id == 0 ? progress : 0;
	}

	@Override
	public void setField(int id, int value){
		if(id == 0){
			progress = value;
		}
	}

	@Override
	public int getFieldCount(){
		return 1;
	}

	@Override
	public void clear(){
		inventory = new ItemStack[2];
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return side == EnumFacing.DOWN ? new int[] {1} : new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return index == 0 && direction != EnumFacing.DOWN;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return index == 1 && direction == EnumFacing.DOWN;
	}

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				temp = EnergyConverters.BIOME_TEMP_MULT * worldObj.getBiomeForCoordsBody(pos).getFloatTemperature(getPos());
				init = true;
			}
		}

		@Override
		public double getTemp(){
			init();
			return temp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			temp = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			temp += heat;
		}

	}
}
