package com.Da_Technomancer.crossroads.API.templates;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MachineContainer extends Container{

	protected final IInventory playerInv;
	protected final InventoryTE te;
	protected final int[] fields;

	public MachineContainer(IInventory playerInv, InventoryTE te){
		this.playerInv = playerInv;
		this.te = te;
		fields = new int[te.getFieldCount()];
		addSlots();
		int[] invStart = getInvStart();

		//Hotbar
		for(int x = 0; x < 9; ++x){
			addSlotToContainer(new Slot(playerInv, x, invStart[0] + x * 18, invStart[1] + 58));
		}

		//Crossroads player inv
		for(int y = 0; y < 3; ++y){
			for(int x = 0; x < 9; ++x){
				addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, invStart[0] + x * 18, invStart[1] + y * 18));
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
	@OnlyIn(Dist.CLIENT)
	public void updateProgressBar(int id, int data){
		te.setField(id, data);
	}

	@Override
	public void addListener(IContainerListener listener){
		super.addListener(listener);
		listener.sendAllWindowProperties(this, te);
	}

	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();

		for(int i = 0; i < fields.length; i++){
			if(fields[i] != te.getField(i)){
				fields[i] = te.getField(i);
				for(IContainerListener listener : listeners){
					listener.sendWindowProperty(this, i, fields[i]);
				}
			}
		}
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn){
		super.onContainerClosed(playerIn);


		if(!te.getWorld().isRemote){
			if(playerIn.isEntityAlive() && !(playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity) playerIn).hasDisconnected())){
				for(Slot s : inventorySlots){
					if(s instanceof TemporarySlot){
						playerIn.inventory.placeItemBackInInventory(te.getWorld(), s.getStack());
					}
				}
			}else{
				for(Slot s : inventorySlots){
					if(s instanceof TemporarySlot){
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

	/**
	 * Any slot that extends this will have its stack returned to the player inventory when the UI is closed
	 */
	protected abstract static class TemporarySlot extends Slot{

		public TemporarySlot(IInventory te, int index, int x, int y){
			super(te, index, x, y);
		}
	}

	protected static class FluidSlot extends TemporarySlot{

		protected final MachineContainer cont;
		protected final TemporarySlot outputSlot;

		/**
		 * Note that this constructor will also initialize an output slot and add it to the container, meaning each FluidSlot added occupies two slots.
		 *
		 * @param container The containing container
		 * @param x The x position of this slot
		 * @param y The y position of this slot
		 * @param outputX The x position of the output slot
		 * @param outputY The y position of the output slot
		 */
		public FluidSlot(MachineContainer container, int x, int y, int outputX, int outputY){
			super(new FakeInventory(), 0, x, y);
			this.cont = container;
			outputSlot = new TemporarySlot(inventory, 1, outputX, outputY){
				@Override
				public boolean isItemValid(ItemStack stack){
					return false;
				}

				@Override
				public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack){
					FluidSlot.this.onSlotChanged();
					return stack;
				}
			};
			container.addSlotToContainer(outputSlot);
		}

		@Override
		public boolean isItemValid(ItemStack stack){
			return FluidUtil.getFluidHandler(stack) != null;
		}

		@Override
		public void onSlotChanged(){
			if(!cont.te.getWorld().isRemote){
				cont.detectAndSendChanges();

				ItemStack stack = getStack().copy();
				if(!stack.isEmpty()){
					stack.setCount(1);
					IFluidHandler teHandler = cont.te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
					IFluidHandlerItem stackHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

					if(teHandler != null && stackHandler != null){
						FluidStack stFs = stackHandler.drain(Integer.MAX_VALUE, false);
						FluidStack teFs = teHandler.drain(Integer.MAX_VALUE, false);
						ItemStack outputStack = inventory.getStackInSlot(1);
						if(stFs == null && teFs != null){
							//Try filling item
							int filled = stackHandler.fill(teFs, true);
							ItemStack container = stackHandler.getContainer();//The container is only updated if we actually do the fill. This is dumb, but there's no way around it
							if(filled > 0 && (outputStack.isEmpty() || ItemStack.areItemsEqual(outputStack, container) && ItemStack.areItemStackTagsEqual(outputStack, container) && outputStack.getCount() < outputStack.getMaxStackSize())){
								teHandler.drain(filled, true);

								if(outputStack.isEmpty()){
									outputStack = container;
								}else{
									outputStack.grow(container.getCount());
								}

								inventory.setInventorySlotContents(1, outputStack);
								inventory.decrStackSize(0, 1);
								cont.detectAndSendChanges();
							}
						}else if(stFs != null){
							//Try draining item
							int drained = teHandler.fill(stFs, false);
							FluidStack drainedFs = stackHandler.drain(drained, true);
							if(drained == 0 || drainedFs == null || drained != drainedFs.amount){
								return;//Something has gone weird, and a checksum failed. May be caused by, for example, buckets with a minimum drain qty
							}
							ItemStack container = stackHandler.getContainer();//The container is only updated if we actually do the drain. This is dumb, but there's no way around it
							if((outputStack.isEmpty() || ItemStack.areItemsEqual(outputStack, container) && ItemStack.areItemStackTagsEqual(outputStack, container) && outputStack.getCount() < outputStack.getMaxStackSize())){
								teHandler.fill(drainedFs, true);

								if(outputStack.isEmpty()){
									outputStack = container;
								}else{
									outputStack.grow(container.getCount());
								}

								inventory.setInventorySlotContents(1, outputStack);
								inventory.decrStackSize(0, 1);
								cont.detectAndSendChanges();
							}
						}
					}
				}
			}
		}

		private static class FakeInventory implements IInventory{

			protected final ItemStack[] stacks = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY};

			@Override
			public int getSizeInventory(){
				return 2;
			}

			@Override
			public boolean isEmpty(){
				return stacks[0].isEmpty() && stacks[1].isEmpty();
			}

			@Override
			public ItemStack getStackInSlot(int index){
				return stacks[index];
			}

			@Override
			public ItemStack decrStackSize(int index, int count){
				return stacks[index].split(count);
			}

			@Override
			public ItemStack removeStackFromSlot(int index){
				ItemStack stack = stacks[index];
				stacks[index] = ItemStack.EMPTY;
				return stack;
			}

			@Override
			public void setInventorySlotContents(int index, ItemStack stack){
				stacks[index] = stack;
			}

			@Override
			public int getInventoryStackLimit(){
				return 64;
			}

			@Override
			public void markDirty(){

			}

			@Override
			public boolean isUsableByPlayer(PlayerEntity player){
				return true;
			}

			@Override
			public void openInventory(PlayerEntity player){

			}

			@Override
			public void closeInventory(PlayerEntity player){

			}

			@Override
			public boolean isItemValidForSlot(int index, ItemStack stack){
				return index == 0 && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
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
				stacks[0] = ItemStack.EMPTY;
				stacks[1] = ItemStack.EMPTY;
			}

			@Override
			public String getName(){
				return "";
			}

			@Override
			public boolean hasCustomName(){
				return false;
			}

			@Override
			public ITextComponent getDisplayName(){
				return new StringTextComponent("");
			}
		}
	}
}
