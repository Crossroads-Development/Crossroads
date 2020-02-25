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
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

public abstract class MachineContainer<U extends InventoryTE> extends Container{

	protected final IInventory playerInv;
	protected final U te;

	//The passed PacketBuffer must have the blockpos as the first encoded datum
	@SuppressWarnings("unchecked")
	public MachineContainer(ContainerType<? extends MachineContainer> type, int windowId, PlayerInventory playerInv, PacketBuffer data){
		super(type, windowId);
		this.playerInv = playerInv;
		BlockPos pos = data.readBlockPos();
		TileEntity rawTE = playerInv.player.world.getTileEntity(pos);
		U worldTe = null;
		if(rawTE instanceof InventoryTE){
			try{
				//Java doesn't let us do an instanceof check with a type parameter, so we check against InventoryTE and catch any exception
				worldTe = (U) rawTE;
			}catch(ClassCastException e){
				//Should never happen
				Crossroads.logger.error("UI opened without TE in world!");
			}
		}else{
			//Should never happen
			Crossroads.logger.error("Null TileEntity passed to MachineContainer!");
		}
		if(worldTe == null){
			//Just in case one of the two things that should never happen happens, we create a fake instance of type U
			//The UI will be basically non-functional, but we prevent a hard crash
			worldTe = generateEmptyTE();
			worldTe.setWorld(playerInv.player.world);
			worldTe.setPos(pos);
		}
		this.te = worldTe;

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

	protected U generateEmptyTE(){
		//Uses reflection to get the class for type U (illegal in normal Java due to type scrubbing).
		//Uses reflection to instantiate a new instance of type U with a no-args constructor
		//If no no-args constructor exists, we hard crash because either Crossroads or an addon added a subclass without a default constructor and didn't override this method in its MachineContainer subclass
		U created;
		try{
			Class<U> clazz = (Class<U>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			created = clazz.getConstructor().newInstance();
		}catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
			throw new NullPointerException("Could not instantiate fake TileEntity for MachineContainer! Report to mod author!");
		}
		return created;
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
