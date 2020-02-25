package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class ReagentFilterContainer extends Container{

	@ObjectHolder("reagent_filter")
	private static ContainerType<ReagentFilterContainer> type = null;

	private final IInventory te;

	public ReagentFilterContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id);
		BlockPos pos = buf.readBlockPos();
		TileEntity wTe = playerInv.player.world.getTileEntity(pos);
		if(wTe instanceof IInventory){
			te = (IInventory) wTe;
		}else{
			te = new Inventory(1);
			Crossroads.logger.info("Reagent Filter UI created without TE- pos: " + pos.toString());
		}

		// Fuel slot, ID 0
		addSlot(new Slot(te, 0, 80, 53){
			@Override
			public boolean isItemValid(ItemStack stack){
				return inventory.isItemValidForSlot(getSlotIndex(), stack);
			}
		});

		//Hotbar
		for(int x = 0; x < 9; ++x){
			addSlot(new Slot(playerInv, x, 8 + x * 18, 84 + 58));
		}

		//Crossroads player inv
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int fromSlot){
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(fromSlot);

		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();

			//fromSlot < slotCount means TE -> Player, else Player -> TE input slots
			if(fromSlot < 1 ? !mergeItemStack(current, 1, 36 + 1, true) : !mergeItemStack(current, 0, 1, false)){
				return ItemStack.EMPTY;
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
	public boolean canInteractWith(PlayerEntity playerIn){
		return te.isUsableByPlayer(playerIn);
	}
}
