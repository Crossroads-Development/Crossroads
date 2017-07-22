package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class PrototypingTableContainer extends Container{

	private PrototypingTableTileEntity te;

	public PrototypingTableContainer(IInventory playerInv, PrototypingTableTileEntity te){
		this.te = te;

		//Copshowium ID 0
		addSlotToContainer(new Slot(te, 0, 134, 78){
			@Override
			public boolean isItemValid(ItemStack stack){
				return stack.getItem() == OreSetup.ingotCopshowium;
			}
		});

		//Template ID 1
		addSlotToContainer(new Slot(te, 1, 152, 78){
			@Override
			public boolean isItemValid(ItemStack stack){
				return stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == ModBlocks.prototype;
			}
		});

		//Output ID 2
		addSlotToContainer(new Slot(te, 2, 152, 108){
			@Override
			public boolean isItemValid(ItemStack stack){
				return false;
			}
		});

		//Trash ID 3
		addSlotToContainer(new Slot(te, 3, 134, 108){
			@Override
			public boolean isItemValid(ItemStack stack){
				return stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == ModBlocks.prototype;
			}
		});

		// Player Inventory, Slots 9-35, Slot IDs 3-30
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 132 + y * 18));
			}
		}

		// Player Inventory, Slot 0-8, Slot IDs 31-39
		for(int x = 0; x < 9; ++x){
			addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 190));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot){
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(fromSlot);

		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();

			if(fromSlot >= 0 && fromSlot <= 3){
				// From TE Inventory to Player Inventory
				if(!mergeItemStack(current, 4, 40, true)){
					return ItemStack.EMPTY;
				}
			}else{
				// From Player Inventory to TE Inventory
				if(!mergeItemStack(current, 0, 3, false)){
					return ItemStack.EMPTY;
				}
			}

			if(current.isEmpty()){
				slot.putStack(ItemStack.EMPTY);
			}else{
				slot.onSlotChanged();
			}
			if(current.getCount() == previous.getCount()){
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, current);
		}
		return previous;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn){
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
		ItemStack out = super.slotClick(slotId, dragType, clickTypeIn, player);
		detectAndSendChanges();
		return out;
	}
}
