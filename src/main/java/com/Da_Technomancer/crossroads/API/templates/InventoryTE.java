package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import com.Da_Technomancer.essentials.gui.container.IFluidSlotTE;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class InventoryTE extends ModuleTE implements WorldlyContainer, MenuProvider, IFluidSlotTE{

	protected final ItemStack[] inventory;
	public final FluidSlotManager[] fluidManagers = new FluidSlotManager[fluidTanks()];

	public InventoryTE(BlockEntityType<? extends InventoryTE> type, BlockPos pos, BlockState state, int invSize){
		super(type, pos, state);
		inventory = new ItemStack[invSize];
		for(int i = 0; i < invSize; i++){
			inventory[i] = ItemStack.EMPTY;
		}
	}

	/**
	 * Call this in your constructor if using fluids. This has to be called after setting fluidProps
	 */
	protected void initFluidManagers(){
		for(int i = 0; i < fluids.length; i++){
			fluidManagers[i] = new FluidSlotManager(fluids[i], fluidProps[i].capacity);
		}
	}

	/**
	 * Gets a value that should be used for display in UIs. Usually used in conjunction with IntDeferredRef
	 * The result is only valid on the virtual server side
	 * The result is only applicable if useRotary()
	 * @return 100 * the speed that should be displayed in UIs
	 */
	public int getUISpeed(){
		//Capped as a short to prevent overflow with the UI packets
		return (int) Math.min(Short.MAX_VALUE, Math.max(Short.MIN_VALUE, Math.round(axleHandler.getSpeed() * 100D)));
	}

	/**
	 * Gets a value that should be used for display in UIs. Usually used in conjunction with IntDeferredRef
	 * The result is only valid on the virtual server side
	 * The result is only applicable if useHeat()
	 * @return The temperature
	 */
	public int getUITemp(){
		//Capped as a short to prevent overflow with the UI packets
		return (int) Math.min(temp, Short.MAX_VALUE);
	}

	@Override
	public CompoundTag m_6945_(CompoundTag nbt){
		super.m_6945_(nbt);
		for(int i = 0; i < inventory.length; i++){
			if(!inventory[i].isEmpty()){
				CompoundTag stackTag = new CompoundTag();
				inventory[i].save(stackTag);
				nbt.put("inv_" + i, stackTag);
			}
		}
		nbt.putBoolean("server", true);
		return nbt;
	}

	@Override
	public void setChanged(){
		super.setChanged();
		if(level != null && !level.isClientSide){
			//We update our IntReferenceHolders that notify client side containers
			//We only update them directly on the server side. Updating on the client is unneeded, and may cause things to get out of sync
			for(int i = 0; i < fluidManagers.length; i++){
				fluidManagers[i].updateState(fluids[i]);
			}
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		for(int i = 0; i < inventory.length; i++){
			if(nbt.contains("inv_" + i)){
				inventory[i] = ItemStack.of(nbt.getCompound("inv_" + i));
			}
		}
		if(nbt.getBoolean("server")){
			for(int i = 0; i < fluidManagers.length; i++){
				fluidManagers[i].updateState(fluids[i]);
			}
		}
	}

	@Override
	public boolean stillValid(Player player){
		return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64;
	}

	@Override
	public int getContainerSize(){
		return inventory.length;
	}

	@Override
	public boolean isEmpty(){
		for(ItemStack stack : inventory){
			if(!stack.isEmpty()){
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getItem(int index){
		return index >= inventory.length ? ItemStack.EMPTY : inventory[index];
	}

	@Override
	public ItemStack removeItem(int index, int count){
		if(index >= inventory.length || inventory[index].isEmpty()){
			return ItemStack.EMPTY;
		}
		setChanged();
		return inventory[index].split(count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index){
		if(index >= inventory.length || inventory[index].isEmpty()){
			return ItemStack.EMPTY;
		}
		setChanged();
		ItemStack removed = inventory[index];
		inventory[index]= ItemStack.EMPTY;
		return removed;
	}

	@Override
	public void setItem(int index, ItemStack stack){
		if(index >= inventory.length){
			return;
		}
		inventory[index] = stack;
		setChanged();
	}

	@Override
	public int getMaxStackSize(){
		return inventory.length == 0 ? 0 : 64;
	}

	public int getMaxStackSize(int slot){
		return getMaxStackSize();
	}

	@Override
	public void clearContent(){
		Arrays.fill(inventory, ItemStack.EMPTY);
		if(inventory.length != 0){
			setChanged();
		}
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index >= 0 && index < inventory.length && inventory[index].getCount() < getMaxStackSize(index);
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction){
		return canPlaceItem(index, stack);
	}

	@Override
	public int[] getSlotsForFace(Direction side){
		int[] out = new int[inventory.length];
		for(int i = 0; i < out.length; i++){
			out[i] = i;
		}
		return out;
	}

	/**
	 * Makes the Essentials based fluid slots work
	 * @return The active fluid handler
	 */
	@Override
	public IFluidHandler getFluidHandler(){
		return globalFluidHandler;
	}

	/**
	 * Purely a convenience method
	 * @return A new PacketBuffer pre-formatted with standard InventoryContainer info
	 */
	protected FriendlyByteBuf createContainerBuf(){
		return new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(worldPosition);
	}

	protected class ItemHandler implements IItemHandlerModifiable{

		/**
		 * A direction that this should act as internally. Does not need to match the side passed to the getCapability call
		 */
		private final Direction dir;

		public ItemHandler(){
			this(null);
		}

		public ItemHandler(@Nullable Direction dir){
			this.dir = dir;
		}

		@Override
		public int getSlots(){
			return inventory.length;
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot){
			return slot >= inventory.length ? ItemStack.EMPTY : inventory[slot];
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			if(canPlaceItemThroughFace(slot, stack, dir) && (inventory[slot].isEmpty() || BlockUtil.sameItem(stack, inventory[slot]))){
				int oldCount = inventory[slot].getCount();
				int moved = Math.max(0, Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), getMaxStackSize(slot)) - oldCount));
				ItemStack out = stack.copy();
				out.setCount(stack.getCount() - moved);

				if(!simulate){
					setChanged();
					inventory[slot] = stack.copy();
					inventory[slot].setCount(moved + oldCount);
				}
				return out;
			}else{
				return stack;
			}
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot >= inventory.length || !canTakeItemThroughFace(slot, inventory[slot], dir)){
				return ItemStack.EMPTY;
			}

			int moved = Math.min(amount, inventory[slot].getCount());
			if(simulate){
				ItemStack simOut = inventory[slot].copy();
				simOut.setCount(moved);
				return simOut;
			}
			setChanged();
			return inventory[slot].split(moved);
		}

		@Override
		public int getSlotLimit(int slot){
			return getMaxStackSize(slot);
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return canPlaceItem(slot, stack);
		}

		@Override
		public void setStackInSlot(int slot, @Nonnull ItemStack stack){
			if(slot < inventory.length){
				inventory[slot] = stack;
				setChanged();
			}
		}
	}
}
