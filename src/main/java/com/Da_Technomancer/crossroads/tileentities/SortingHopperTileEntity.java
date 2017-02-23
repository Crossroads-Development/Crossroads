package com.Da_Technomancer.crossroads.tileentities;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

public class SortingHopperTileEntity extends TileEntityLockable implements IHopper, ITickable{

	public SortingHopperTileEntity(){
		super();
		for(int i = 0; i < 5; i++){
			inventory[i] = ItemStack.EMPTY;
		}
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}
	
	private void updateHopper(){
		if(world != null && !world.isRemote){
			if(!isOnTransferCooldown() && BlockHopper.isEnabled(getBlockMetadata())){
				boolean flag = false;

				if(!isFull()){
					flag = captureDroppedItems(this);
				}

				if(!this.isEmpty()){
					flag = transferItemsOut() || flag;
				}

				if(flag){
					transferCooldown = 8;
					markDirty();
				}
			}
		}
	}

	@Override
	public String getName(){
		return "container.sorting_hopper";
	}

	@Override
	protected net.minecraftforge.items.IItemHandler createUnSidedHandler(){
		return new HopperItemHandler(this);
	}
	
	@Override
	public boolean hasCustomName(){
		return false;
	}

	private ItemStack[] inventory = new ItemStack[5];
	private int transferCooldown = -1;

	/**
	 * VanillaHopperItemHandler modified to take SortingHopper
	 */
	private class HopperItemHandler extends InvWrapper{
		private final SortingHopperTileEntity hopper;

		public HopperItemHandler(SortingHopperTileEntity hopper){
			super(hopper);
			this.hopper = hopper;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(stack.isEmpty())
				return ItemStack.EMPTY;
			if(simulate || !hopper.mayTransfer())
				return super.insertItem(slot, stack, simulate);

			int curStackSize = stack.getCount();
			ItemStack itemStack = super.insertItem(slot, stack, false);
			if(itemStack.isEmpty() || curStackSize != itemStack.getCount()){
				transferCooldown = 8;
			}
			return itemStack;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		transferCooldown = nbt.getInteger("transferCooldown");

		for(int i = 0; i < 5; i++){
			NBTTagCompound stackNBT = nbt.getCompoundTag("inv" + i);
			inventory[i] = new ItemStack(stackNBT);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		for(int i = 0; i < 5; i++){
				if(!inventory[i].isEmpty()){
					NBTTagCompound stackNBT = new NBTTagCompound();
					this.inventory[i].writeToNBT(stackNBT);
					nbt.setTag("inv" + i, stackNBT);
				}
			}

		nbt.setInteger("transferCooldown", transferCooldown);

		return nbt;
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory(){
		return 5;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Override
	public ItemStack getStackInSlot(int index){
		return index > 4 ? ItemStack.EMPTY : inventory[index];
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and
	 * returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count){
		if(count > 4 || inventory[index].isEmpty()){
			return ItemStack.EMPTY;
		}
		return inventory[index].splitStack(count);
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index){
		if(index > 4){
			return ItemStack.EMPTY;
		}
		ItemStack copy = inventory[4].copy();
		inventory[4] = ItemStack.EMPTY;
		return copy;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index > 4){
			return;
		}
		inventory[index] = stack;

		if(!stack.isEmpty() && stack.getCount() > stack.getMaxStackSize()){
			stack.setCount(stack.getMaxStackSize());
		}
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended.
	 */
	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	@Override
	public boolean isUsableByPlayer(EntityPlayer player){
		return world.getTileEntity(pos) != this ? false : player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player){
		
	}

	@Override
	public void closeInventory(EntityPlayer player){
		
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring
	 * stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index < 5;
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	@Override
	public void update(){
		if(world != null && !world.isRemote){
			--transferCooldown;

			if(!isOnTransferCooldown()){
				transferCooldown = 0;
				updateHopper();
			}
		}
	}

	@Override
	public boolean isEmpty(){
		for(ItemStack itemstack : inventory){
			if(!itemstack.isEmpty()){
				return false;
			}
		}

		return true;
	}

	private boolean isFull(){
		for(ItemStack itemstack : inventory){
			if(itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()){
				return false;
			}
		}

		return true;
	}

	private boolean transferItemsOut(){
		if(insertHook(this)){
			return true;
		}
		IInventory iinventory = this.getInventoryForHopperTransfer();

		if(iinventory == null){
			return false;
		}else{
			EnumFacing enumfacing = BlockHopper.getFacing(getBlockMetadata()).getOpposite();

			if(isInventoryFull(iinventory, enumfacing)){
				return false;
			}else{
				for(int i = 0; i < getSizeInventory(); ++i){
					if(!getStackInSlot(i).isEmpty()){
						ItemStack itemstack = this.getStackInSlot(i).copy();
						ItemStack itemstack1 = putStackInInventoryAllSlots(iinventory, this.decrStackSize(i, 1), enumfacing);

						if(itemstack1.isEmpty()){
							iinventory.markDirty();
							return true;
						}

						setInventorySlotContents(i, itemstack);
					}
				}

				return false;
			}
		}
	}

	/**
	 * A version of the forge hook that takes this tile entity
	 */
	private static boolean insertHook(SortingHopperTileEntity hopper){
		return insertHook(hopper, EnumFacing.getFront(hopper.getBlockMetadata() & 7));
	}

	private static boolean insertHook(IHopper hopper, EnumFacing facing){
		TileEntity tileEntity = hopper.getWorld().getTileEntity(new BlockPos(hopper.getXPos(), hopper.getYPos(), hopper.getZPos()).offset(facing));

		if(tileEntity == null)
			return false;
		if(!tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()))
			return false;

		IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());

		for(int i = 0; i < hopper.getSizeInventory(); i++){
			ItemStack stackInSlot = hopper.getStackInSlot(i);
			if(stackInSlot != null){
				ItemStack insert = stackInSlot.copy();
				insert.setCount(1);
				ItemStack newStack = ItemHandlerHelper.insertItem(handler, insert, true);
				if(newStack.isEmpty()){
					ItemHandlerHelper.insertItem(handler, hopper.decrStackSize(i, 1), false);
					hopper.markDirty();
					return true;
				}
			}
		}

		return true;
	}

	/**
	 * Returns false if the inventory has any room to place items in
	 */
	private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side){
		if(inventoryIn instanceof ISidedInventory){
			ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
			int[] aint = isidedinventory.getSlotsForFace(side);
			if(aint == null){
				return false;
			}
			for(int k : aint){
				ItemStack itemstack1 = isidedinventory.getStackInSlot(k);

				if(itemstack1.isEmpty() || itemstack1.getCount() != itemstack1.getMaxStackSize()){
					return false;
				}
			}
		}else{
			int i = inventoryIn.getSizeInventory();

			for(int j = 0; j < i; ++j){
				ItemStack itemstack = inventoryIn.getStackInSlot(j);

				if(itemstack.isEmpty() || itemstack.getCount() != itemstack.getMaxStackSize()){
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Returns false if the specified IInventory contains any items
	 */
	private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side){
		if(inventoryIn instanceof ISidedInventory){
			ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
			int[] aint = isidedinventory.getSlotsForFace(side);

			for(int i : aint){
				if(!isidedinventory.getStackInSlot(i).isEmpty()){
					return false;
				}
			}
		}else{
			int j = inventoryIn.getSizeInventory();

			for(int k = 0; k < j; ++k){
				if(!inventoryIn.getStackInSlot(k).isEmpty()){
					return false;
				}
			}
		}

		return true;
	}

	private static boolean captureDroppedItems(IHopper hopper){
		Boolean ret = net.minecraftforge.items.VanillaInventoryCodeHooks.extractHook(hopper);
		if(ret != null)
			return ret;
		IInventory iinventory = getHopperInventory(hopper);

		if(iinventory != null){
			EnumFacing enumfacing = EnumFacing.DOWN;

			if(isInventoryEmpty(iinventory, enumfacing)){
				return false;
			}

			if(iinventory instanceof ISidedInventory){
				ISidedInventory isidedinventory = (ISidedInventory) iinventory;
				int[] aint = isidedinventory.getSlotsForFace(enumfacing);

				for(int i : aint){
					if(pullItemFromSlot(hopper, iinventory, i, enumfacing)){
						return true;
					}
				}
			}else{
				int j = iinventory.getSizeInventory();

				for(int k = 0; k < j; ++k){
					if(pullItemFromSlot(hopper, iinventory, k, enumfacing)){
						return true;
					}
				}
			}
		}else{
			for(EntityItem entityitem : getCaptureItems(hopper.getWorld(), hopper.getXPos(), hopper.getYPos(), hopper.getZPos())){
				if(putDropInInventoryAllSlots(hopper, entityitem)){
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Pulls from the specified slot in the inventory and places in any
	 * available slot in the hopper. Returns true if the entire stack was moved
	 */
	private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction){
		ItemStack itemstack = inventoryIn.getStackInSlot(index);

		if(itemstack != null && canExtractItemFromSlot(inventoryIn, itemstack, index, direction)){
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = putStackInInventoryAllSlots(hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing) null);

			if(itemstack2.isEmpty()){
				inventoryIn.markDirty();
				return true;
			}

			inventoryIn.setInventorySlotContents(index, itemstack1);
		}

		return false;
	}

	/**
	 * Attempts to place the passed EntityItem's stack into the inventory using
	 * as many slots as possible. Returns false if the stackSize of the drop was
	 * not depleted.
	 */
	private static boolean putDropInInventoryAllSlots(IInventory p_145898_0_, EntityItem itemIn){
		boolean flag = false;

		if(itemIn == null){
			return false;
		}else{
			ItemStack itemstack = itemIn.getEntityItem().copy();
			ItemStack itemstack1 = putStackInInventoryAllSlots(p_145898_0_, itemstack, (EnumFacing) null);

			if(!itemstack1.isEmpty()){
				itemIn.setEntityItemStack(itemstack1);
			}else{
				flag = true;
				itemIn.setDead();
			}

			return flag;
		}
	}

	/**
	 * Attempts to place the passed stack in the inventory, using as many slots
	 * as required. Returns leftover items
	 */
	private static ItemStack putStackInInventoryAllSlots(IInventory inventoryIn, ItemStack stack, @Nullable EnumFacing side){
		if(inventoryIn instanceof ISidedInventory && side != null){
			ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
			int[] aint = isidedinventory.getSlotsForFace(side);

			if(aint == null){
				return stack;
			}
			for(int k = 0; k < aint.length && !stack.isEmpty(); ++k){
				stack = insertStack(inventoryIn, stack, aint[k], side);
			}
		}else{
			int i = inventoryIn.getSizeInventory();

			for(int j = 0; j < i && !stack.isEmpty(); ++j){
				stack = insertStack(inventoryIn, stack, j, side);
			}
		}

		if(!stack.isEmpty()){
			stack = ItemStack.EMPTY;
		}

		return stack;
	}

	/**
	 * Can this hopper insert the specified item from the specified slot on the
	 * specified side?
	 */
	private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side){
		return !inventoryIn.isItemValidForSlot(index, stack) ? false : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canInsertItem(index, stack, side);
	}

	/**
	 * Can this hopper extract the specified item from the specified slot on the
	 * specified side?
	 */
	private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side){
		return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canExtractItem(index, stack, side);
	}

	/**
	 * Insert the specified stack to the specified inventory and return any
	 * leftover items
	 */
	private static ItemStack insertStack(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side){
		ItemStack itemstack = inventoryIn.getStackInSlot(index);

		if(canInsertItemInSlot(inventoryIn, stack, index, side)){
			boolean flag = false;

			if(itemstack.isEmpty()){
				int max = Math.min(stack.getMaxStackSize(), inventoryIn.getInventoryStackLimit());
				if(max >= stack.getCount()){
					inventoryIn.setInventorySlotContents(index, stack);
					stack = ItemStack.EMPTY;
				}else{
					inventoryIn.setInventorySlotContents(index, stack.splitStack(max));
				}
				flag = true;
			}else if(canCombine(itemstack, stack)){
				int max = Math.min(stack.getMaxStackSize(), inventoryIn.getInventoryStackLimit());
				if(max > itemstack.getCount()){
					int i = max - itemstack.getCount();
					int j = Math.min(stack.getCount(), i);
					stack.shrink(j);
					itemstack.grow(j);
					flag = j > 0;
				}
			}

			if(flag){
				if(inventoryIn instanceof TileEntityHopper){
					TileEntityHopper tileentityhopper = (TileEntityHopper) inventoryIn;

					if(tileentityhopper.mayTransfer()){
						tileentityhopper.setTransferCooldown(8);
					}

					inventoryIn.markDirty();
				}

				inventoryIn.markDirty();
			}
		}

		return stack;
	}

	/**
	 * Returns the IInventory that this hopper is pointing into
	 */
	private IInventory getInventoryForHopperTransfer(){
		EnumFacing enumfacing = BlockHopper.getFacing(this.getBlockMetadata());
		/**
		 * Returns the IInventory (if applicable) of the TileEntity at the
		 * specified position
		 */
		return getInventoryAtPosition(world, this.getXPos() + enumfacing.getFrontOffsetX(), this.getYPos() + enumfacing.getFrontOffsetY(), this.getZPos() + enumfacing.getFrontOffsetZ());
	}

	/**
	 * Returns the IInventory for the specified hopper
	 */
	private static IInventory getHopperInventory(IHopper hopper){
		/**
		 * Returns the IInventory (if applicable) of the TileEntity at the
		 * specified position
		 */
		return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());
	}

	public static List<EntityItem> getCaptureItems(World worldIn, double p_184292_1_, double p_184292_3_, double p_184292_5_){
		return worldIn.<EntityItem> getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(p_184292_1_ - 0.5D, p_184292_3_, p_184292_5_ - 0.5D, p_184292_1_ + 0.5D, p_184292_3_ + 1.5D, p_184292_5_ + 0.5D), EntitySelectors.IS_ALIVE);
	}

	/**
	 * Returns the IInventory (if applicable) of the TileEntity at the specified
	 * position
	 */
	public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z){
		IInventory iinventory = null;
		int i = MathHelper.floor(x);
		int j = MathHelper.floor(y);
		int k = MathHelper.floor(z);
		BlockPos blockpos = new BlockPos(i, j, k);
		Block block = worldIn.getBlockState(blockpos).getBlock();

		if(block instanceof BlockContainer){
			TileEntity tileentity = worldIn.getTileEntity(blockpos);

			if(tileentity instanceof IInventory){
				iinventory = (IInventory) tileentity;

				if(iinventory instanceof TileEntityChest && block instanceof BlockChest){
					iinventory = ((BlockChest) block).getContainer(worldIn, blockpos, true);
				}
			}
		}

		if(iinventory == null){
			List<Entity> list = worldIn.getEntitiesInAABBexcluding((Entity) null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntitySelectors.HAS_INVENTORY);

			if(!list.isEmpty()){
				iinventory = (IInventory) list.get(worldIn.rand.nextInt(list.size()));
			}
		}

		return iinventory;
	}

	private static boolean canCombine(ItemStack stack1, ItemStack stack2){
		return stack1.getItem() != stack2.getItem() ? false : (stack1.getMetadata() != stack2.getMetadata() ? false : (stack1.getCount() > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2)));
	}

	@Override
	public String getGuiID(){
		return "minecraft:hopper";
	}
	
	/**
	 * Gets the world X position for this hopper entity.
	 */
	@Override
	public double getXPos(){
		return pos.getX() + 0.5D;
	}

	/**
	 * Gets the world Y position for this hopper entity.
	 */
	@Override
	public double getYPos(){
		return pos.getY() + 0.5D;
	}

	/**
	 * Gets the world Z position for this hopper entity.
	 */
	@Override
	public double getZPos(){
		return pos.getZ() + 0.5D;
	}

	public boolean isOnTransferCooldown(){
		return transferCooldown > 0;
	}

	public boolean mayTransfer(){
		return transferCooldown <= 1;
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
		for(int i = 0; i < 5; ++i){
			this.inventory[i] = ItemStack.EMPTY;
		}
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn){
		return new ContainerHopper(playerInventory, this, playerIn);
	}
}
