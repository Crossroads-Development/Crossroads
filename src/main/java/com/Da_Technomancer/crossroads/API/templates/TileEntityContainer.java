package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

public abstract class TileEntityContainer<U extends BlockEntity & Container> extends AbstractContainerMenu{

	protected final Container playerInv;
	protected final U te;

	//The passed PacketBuffer must have the blockpos as the first encoded datum
	@SuppressWarnings("unchecked")
	public TileEntityContainer(MenuType<? extends AbstractContainerMenu> type, int windowId, Inventory playerInv, FriendlyByteBuf data){
		super(type, windowId);
		this.playerInv = playerInv;
		BlockPos pos = data.readBlockPos();
		BlockEntity rawTE = playerInv.player.level.getBlockEntity(pos);
		U worldTe = null;
		if(rawTE != null){
			try{
				//Java doesn't let us do an instanceof check with a type parameter, so we cast and catch any exception
				worldTe = (U) rawTE;
			}catch(ClassCastException e){
				//Should never happen
				Crossroads.logger.error("UI opened without TE in world!");
			}
		}else{
			//Should never happen
			Crossroads.logger.error("Null TileEntity passed to TileEntityContainer!");
		}
		if(worldTe == null){
			//Just in case one of the two things that should never happen happens, we create a fake instance of type U
			//The UI will be basically non-functional, but we prevent a hard crash
			worldTe = generateEmptyTE(pos, playerInv.player.level.getBlockState(pos));
			worldTe.setLevel(playerInv.player.level);
			Crossroads.logger.error("Null world tile entity! Generating dummy TE. Report to mod author; type=%1$s", type.toString());
		}
		this.te = worldTe;

		addSlots();
		int[] invStart = getInvStart();

		//Hotbar
		for(int x = 0; x < 9; x++){
			addSlot(new Slot(playerInv, x, invStart[0] + x * 18, invStart[1] + 58));
		}

		//player inv
		for(int y = 0; y < 3; y++){
			for(int x = 0; x < 9; x++){
				addSlot(new Slot(playerInv, x + y * 9 + 9, invStart[0] + x * 18, invStart[1] + y * 18));
			}
		}
	}

	protected U generateEmptyTE(BlockPos pos, BlockState state){
		//Uses reflection to get the class for type U (illegal in normal Java due to type scrubbing).
		//Uses reflection to instantiate a new instance of type U with a generic (BlockPos, BlockState) constructor
		//If no such generic constructor exists, we hard crash because either Crossroads or an addon added a subclass without a default constructor and didn't override this method in its MachineContainer subclass
		U created;
		try{
			Class<U> clazz = (Class<U>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			created = clazz.getConstructor(BlockPos.class, BlockState.class).newInstance(pos, state);
		}catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
			throw new NullPointerException("Could not instantiate fake TileEntity for TileEntityContainer! Report to mod author!");
		}
		return created;
	}

	protected abstract void addSlots();

	protected abstract int slotCount();

	/**
	 * Gets the position the inventory menu slots start at
	 * @return A size two integer[] (x, y) where the player inventory slots start
	 */
	protected int[] getInvStart(){
		return new int[] {8, 84};
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int fromSlot){
		ItemStack previous = ItemStack.EMPTY;
		Slot slot = slots.get(fromSlot);

		if(slot != null && slot.hasItem()){
			ItemStack current = slot.getItem();
			previous = current.copy();

			//fromSlot < slotCount means TE -> Player, else Player -> TE input slots
			if(fromSlot < slotCount()){
				if(!moveItemStackTo(current, slotCount(), 36 + slotCount(), true)){
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(current, previous);
			}else{
				if(!moveItemStackTo(current, 0, slotCount(), false)){
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(current, previous);
			}

			if(current.isEmpty()){
				slot.set(ItemStack.EMPTY);
			}else{
				slot.setChanged();
			}

			if(current.getCount() == previous.getCount()){
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, current);
		}

		return previous;
	}

	@Override
	public boolean stillValid(Player playerIn){
		return te.stillValid(playerIn);
	}

	protected static class StrictSlot extends Slot{

		public StrictSlot(Container te, int index, int x, int y){
			super(te, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack){
			return container.canPlaceItem(getSlotIndex(), stack);
		}

		@Override
		public int getMaxStackSize(){
			if(container instanceof InventoryTE){
				return ((InventoryTE) container).getMaxStackSize(getSlotIndex());
			}
			return super.getMaxStackSize();
		}
	}

	protected static class OutputSlot extends Slot{

		public OutputSlot(Container te, int index, int x, int y){
			super(te, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack){
			return false;
		}
	}
}
