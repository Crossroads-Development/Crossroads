package com.Da_Technomancer.crossroads.API.templates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
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

		//Main player inv
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
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot){
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
	public boolean canInteractWith(EntityPlayer playerIn){
		return te.isUsableByPlayer(playerIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
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
	public void onContainerClosed(EntityPlayer playerIn){
		super.onContainerClosed(playerIn);


		if(!te.getWorld().isRemote){
			if(playerIn.isEntityAlive() && !(playerIn instanceof EntityPlayerMP && ((EntityPlayerMP) playerIn).hasDisconnected())){
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
			return inventory.isItemValidForSlot(0, stack);
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
				public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack){
					FluidSlot.this.onSlotChanged();
					return stack;
				}
			};
			container.addSlotToContainer(outputSlot);
		}

		@Override
		public void onSlotChanged(){
			if(!cont.te.getWorld().isRemote){
				cont.detectAndSendChanges();

				ItemStack stack = getStack();
				if(!stack.isEmpty()){
					IFluidHandler teHandler = cont.te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
					if(teHandler != null){
						FluidStack fs = FluidUtil.getFluidContained(stack);
						if(fs == null){
							//Try filling item
							FluidActionResult result = FluidUtil.tryFillContainer(stack, teHandler, 100_000, null, false);
							ItemStack outputStack = inventory.getStackInSlot(1);
							if(result.success && (outputStack.isEmpty() || ItemStack.areItemsEqual(outputStack, result.result) && ItemStack.areItemStackTagsEqual(outputStack, result.result) && outputStack.getCount() < outputStack.getMaxStackSize())){
								result = FluidUtil.tryFillContainer(stack, teHandler, 100_000, null, true);
								result.result.grow(outputStack.getCount());
								outputStack = result.result.isEmpty() ? outputStack : result.result;
								inventory.setInventorySlotContents(1, outputStack);
								inventory.decrStackSize(0, 1);
								cont.detectAndSendChanges();
							}
						}else{
							//Try draining item
							FluidActionResult result = FluidUtil.tryEmptyContainer(stack, teHandler, 100_000, null, false);
							ItemStack outputStack = inventory.getStackInSlot(1);
							if(result.success && (outputStack.isEmpty() || ItemStack.areItemsEqual(outputStack, result.result) && ItemStack.areItemStackTagsEqual(outputStack, result.result) && outputStack.getCount() < outputStack.getMaxStackSize())){
								result = FluidUtil.tryEmptyContainer(stack, teHandler, 100_000, null, true);
								result.result.grow(outputStack.getCount());
								outputStack = result.result.isEmpty() ? outputStack : result.result;
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
				return stacks[index].splitStack(count);
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
			public boolean isUsableByPlayer(EntityPlayer player){
				return true;
			}

			@Override
			public void openInventory(EntityPlayer player){

			}

			@Override
			public void closeInventory(EntityPlayer player){

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
				return new TextComponentString("");
			}
		}
	}
}
