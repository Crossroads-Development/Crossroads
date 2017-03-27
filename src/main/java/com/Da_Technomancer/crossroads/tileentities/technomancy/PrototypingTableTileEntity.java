package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.gui.AbstractInventory;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetUp;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class PrototypingTableTileEntity extends AbstractInventory implements ITickable{

	private ItemStack copshowium = ItemStack.EMPTY;
	private ItemStack template = ItemStack.EMPTY;
	private ItemStack output = ItemStack.EMPTY;

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!template.isEmpty() && template.getItem() instanceof ItemBlock && ((ItemBlock) template.getItem()).getBlock() == ModBlocks.prototype && output.isEmpty()){
			//TODO copies the template into output using copshowium at a cost of 3 + (# of ports) ingots. 
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		if(nbt.hasKey("cop")){
			copshowium = new ItemStack(nbt.getCompoundTag("cop"));
		}
		if(nbt.hasKey("temp")){
			template = new ItemStack(nbt.getCompoundTag("temp"));
		}
		if(nbt.hasKey("out")){
			output = new ItemStack(nbt.getCompoundTag("out"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		if(!copshowium.isEmpty()){
			NBTTagCompound invTag = new NBTTagCompound();
			copshowium.writeToNBT(invTag);
			nbt.setTag("cop", invTag);
		}
		if(!template.isEmpty()){
			NBTTagCompound invTag = new NBTTagCompound();
			template.writeToNBT(invTag);
			nbt.setTag("temp", invTag);
		}
		if(!output.isEmpty()){
			NBTTagCompound invTag = new NBTTagCompound();
			output.writeToNBT(invTag);
			nbt.setTag("out", invTag);
		}
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}

		return super.hasCapability(capability, facing);
	}

	private final CopshowiumItemHandler copshowiumHandler = new CopshowiumItemHandler();
	private final OutputItemHandler outputHandler = new OutputItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			if(facing == EnumFacing.DOWN){
				return (T) outputHandler;
			}
			return (T) copshowiumHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public int getSizeInventory(){
		//The fake fourth slot is for destroying prototypes
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int index){
		switch(index){
			case 0:
				return copshowium;
			case 1:
				return template;
			case 2:
				return output;
			default:
				return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		ItemStack slot = getStackInSlot(index);
		if(slot.isEmpty()){
			return ItemStack.EMPTY;
		}
		markDirty();
		return slot.splitStack(count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		ItemStack stack = ItemStack.EMPTY;
		if(index < 3 && index >= 0){
			markDirty();
			switch(index){
				case 0:
					stack = copshowium;
					copshowium = ItemStack.EMPTY;
					break;
				case 1:
					stack = template;
					template = ItemStack.EMPTY;
					break;
				case 2:
					stack = output;
					output = ItemStack.EMPTY;
					break;
			}
		}

		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index == 0){
			copshowium = stack;
			markDirty();
		}else if(index == 1){
			template = stack;
			markDirty();
		}else if(index == 2){
			output = stack;
			markDirty();
		}else if(index == 3 && stack.getItem() == Item.getItemFromBlock(ModBlocks.prototype)){
			int ind = stack.getTagCompound().hasKey("index") ? stack.getTagCompound().getInteger("index") : -1;
			if(ind == -1){
				copshowium = new ItemStack(OreSetUp.ingotCopshowium, Math.min(copshowium.getCount() + 3, 64));
				markDirty();
				return;
			}
			ArrayList<PrototypeInfo> infoList = PrototypeWorldSavedData.get(DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID)).prototypes;
			if(infoList.size() <= ind){
				copshowium = new ItemStack(OreSetUp.ingotCopshowium, Math.min(copshowium.getCount() + 3, 64));
				markDirty();
				return;
			}
			int out = 3;
			PrototypePortTypes[] types = infoList.get(ind).ports;
			for(int i = 0; i < 6; i++){
				if(types[i] != null){
					out++;
				}
			}
			copshowium = new ItemStack(OreSetUp.ingotCopshowium, Math.min(copshowium.getCount() + out, 64));
			infoList.set(ind, null);
			markDirty();
		}
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && stack.getItem() == OreSetUp.ingotCopshowium;
	}

	@Override
	public int getField(int id){
		return 0;
	}

	@Override
	public void setField(int id, int value){

	}

	@Override
	public int getFieldCount(){
		return 0;
	}

	@Override
	public void clear(){
		copshowium = ItemStack.EMPTY;
		template = ItemStack.EMPTY;
		output = ItemStack.EMPTY;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return side == EnumFacing.DOWN ? new int[] {2} : new int[] {0};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction){
		return direction != EnumFacing.DOWN && isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return direction == EnumFacing.DOWN && stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == ModBlocks.prototype;
	}

	@Override
	public String getName(){
		return "container.prototype_table";
	}

	@Override
	public boolean isEmpty(){
		//This is used for automated item transport, so it doesn't need to know about template.
		return copshowium.isEmpty() && output.isEmpty();
	}

	private class CopshowiumItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? copshowium : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot == 0 && stack.getItem() == OreSetUp.ingotCopshowium && copshowium.getCount() < 64){
				ItemStack out = new ItemStack(stack.getItem(), stack.getCount() + copshowium.getCount() - 64);
				if(!simulate){
					copshowium = new ItemStack(stack.getItem(), Math.min(copshowium.getCount() + stack.getCount(), 64));
					markDirty();
				}
				if(out.getCount() <= 0){
					return ItemStack.EMPTY;
				}
				return out;
			}
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 64 : 0;
		}
	}
	
	private class OutputItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? output : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount <= 0 || output.isEmpty()){
				return ItemStack.EMPTY;
			}
			
			ItemStack extracted = output.copy();
			
			if(!simulate){
				output = ItemStack.EMPTY;
			}
			
			return extracted;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 1 : 0;
		}
	}
}
