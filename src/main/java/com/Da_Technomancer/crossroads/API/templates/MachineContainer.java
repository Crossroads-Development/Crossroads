package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

public abstract class MachineContainer extends Container{

	protected final IInventory playerInv;
	protected final InventoryTE te;

	//The passed PacketBuffer must have the blockpos as the first encoded datum
	public MachineContainer(ContainerType<? extends MachineContainer> type, int windowId, PlayerInventory playerInv, PacketBuffer data){
		super(type, windowId);
		this.playerInv = playerInv;
		TileEntity rawTE = playerInv.player.world.getTileEntity(data.readBlockPos());
		if(rawTE instanceof InventoryTE){
			this.te = (InventoryTE) rawTE;
		}else{
			//Create a generic empty TE to avoid crashing and log an error.
			//Possible "legitimate" causes include network weirdness
			this.te = new InventoryTE(null, 0){
				@Override
				protected boolean useHeat(){
					return false;
				}

				@Override
				protected boolean useRotary(){
					return false;
				}

				@Override
				protected int fluidTanks(){
					return 0;
				}

				@Override
				protected TankProperty[] createFluidTanks(){
					return new TankProperty[0];
				}

				@Override
				public boolean canExtractItem(int index, ItemStack stack, Direction direction){
					return false;
				}
			};
			te.setWorld(playerInv.player.world);
			Crossroads.logger.warn("Null InventoryTE passed to gui!");
		}

		if(te.useRotary()){
			trackInt(te.rotaryReference);
		}
		if(te.useHeat()){
			trackInt(te.heatReference);
		}
		for(FluidSlotManager manager : te.fluidManagers){
			trackInt(manager.getFluidIdHolder());
			trackInt(manager.getFluidQtyHolder());
		}

		addSlots();
		int[] invStart = getInvStart();

		//Hotbar
		for(int x = 0; x < 9; x++){
			addSlot(new Slot(playerInv, x, invStart[0] + x * 18, invStart[1] + 58));
		}

		//Crossroads player inv
		for(int y = 0; y < 3; y++){
			for(int x = 0; x < 9; x++){
				addSlot(new Slot(playerInv, x + y * 9 + 9, invStart[0] + x * 18, invStart[1] + y * 18));
			}
		}
	}

	protected abstract void addSlots();

	protected int slotCount(){
		return te.inventory.length;
	}

	/**
	 * Gets the position the inventory menu slots start at
	 * @return A size two integer[] (x, y) where the player inventory slots start
	 */
	protected int[] getInvStart(){
		return new int[] {8, 84};
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int fromSlot){
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(fromSlot);

		if(slot != null && slot.getHasStack()){
			ItemStack current = slot.getStack();
			previous = current.copy();

			//fromSlot < slotCount means TE -> Player, else Player -> TE input slots
			if(fromSlot < slotCount() ? !mergeItemStack(current, slotCount(), 36 + slotCount(), true) : !mergeItemStack(current, 0, slotCount(), false)){
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

	@Override
	public void onContainerClosed(PlayerEntity playerIn){
		super.onContainerClosed(playerIn);

		if(!te.getWorld().isRemote){
			if(playerIn.isAlive() && !(playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity) playerIn).hasDisconnected())){
				for(Slot s : inventorySlots){
					if(s.inventory instanceof FluidSlotManager.FakeInventory){
						playerIn.inventory.placeItemBackInInventory(te.getWorld(), s.getStack());
					}
				}
			}else{
				for(Slot s : inventorySlots){
					if(s.inventory instanceof FluidSlotManager.FakeInventory){
						playerIn.dropItem(s.getStack(), false);
					}
				}
			}
		}
	}

	protected static class StrictSlot extends Slot{

		public StrictSlot(IInventory te, int index, int x, int y){
			super(te, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack){
			return inventory.isItemValidForSlot(getSlotIndex(), stack);
		}
	}

	protected static class OutputSlot extends Slot{

		public OutputSlot(IInventory te, int index, int x, int y){
			super(te, index, x, y);
		}

		@Override
		public boolean isItemValid(ItemStack stack){
			return false;
		}
	}
}
