package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CrossroadsProperties;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.blocks.alchemy.ReagentFilter;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReagentFilterTileEntity extends AlchemyCarrierTE implements IInventory{

	private Direction facing = null;
	private ItemStack inventory = ItemStack.EMPTY;

	public ReagentFilterTileEntity(){
		super();
	}

	public ReagentFilterTileEntity(boolean crystal){
		super(!crystal);
	}

	private Direction getFacing(){
		if(world == null){
			return Direction.NORTH;
		}
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(!(state.getBlock() instanceof ReagentFilter)){
				return Direction.NORTH;
			}
			facing = state.get(CrossroadsProperties.HORIZ_FACING);
		}
		return facing;
	}

	public void clearCache(){
		facing = null;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		inventory = nbt.contains("inv") ? new ItemStack(nbt.getCompound("inv")) : ItemStack.EMPTY;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		if(!inventory.isEmpty()){
			nbt.put("inv", inventory.writeToNBT(new CompoundNBT()));
		}
		return nbt;
	}

	@Override
	protected void performTransfer(){

		ReagentMap filterMap = new ReagentMap();

		//Separate reagents to be filtered
		if(!contents.isEmpty() && !inventory.isEmpty() && inventory.getItem() instanceof AbstractGlassware && inventory.hasTag()){
			ReagentMap filtered = ((AbstractGlassware) inventory.getItem()).getReagants(inventory);
			for(IReagent filtReag : filtered.keySet()){
				if(filtered.getQty(filtReag) != 0){
					filterMap.transferReagent(filtReag, contents.getQty(filtReag), contents);
				}
			}
		}

		//Transfer reagents
		boolean transfered = transfer(contents, Direction.DOWN);
		transfered = transfer(filterMap, getFacing()) || transfered;

		if(!filterMap.isEmpty()){
			//Move untransfered filtered reagents back into contents
			for(IReagent filtReag : filterMap.keySet()){
				int qty = filterMap.getQty(filtReag);
				if(qty != 0){
					contents.transferReagent(filtReag, qty, filterMap);
				}
			}
		}

		dirtyReag |= transfered;
	}

	private boolean transfer(ReagentMap toTrans, Direction side){
		TileEntity te = world.getTileEntity(pos.offset(side));
		IChemicalHandler otherHandler;
		if(toTrans.getTotalQty() <= 0 || te == null || (otherHandler = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())) == null){
			return false;
		}
		EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
		if((cont == EnumContainerType.GLASS) != glass){
			return false;
		}
		return otherHandler.insertReagents(toTrans, side.getOpposite(), handler, true);
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
		if(facing == getFacing() || facing != null && facing.getAxis() == Direction.Axis.Y){
			return (T) handler;
		}
		return super.getCapability(capability, facing);
	}

	@Nonnull
	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] modes = {EnumTransferMode.OUTPUT, EnumTransferMode.INPUT, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		modes[getFacing().getIndex()] = EnumTransferMode.OUTPUT;
		return modes;
	}

	@Override
	public int getSizeInventory(){
		return 1;
	}

	@Override
	public boolean isEmpty(){
		return inventory.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index){
		return inventory;
	}

	@Override
	public ItemStack decrStackSize(int index, int count){
		if(count >= 1){
			return removeStackFromSlot(index);
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index){
		if(index == 0){
			ItemStack removed = inventory;
			inventory = ItemStack.EMPTY;
			markDirty();
			return removed;
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack){
		if(index == 0){
			inventory = stack;
			markDirty();
		}
	}

	@Override
	public int getInventoryStackLimit(){
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player){
		return world.getTileEntity(pos) == this && player.getDistanceSq(pos.add(0.5, 0.5, 0.5)) <= 64;
	}

	@Override
	public void openInventory(PlayerEntity player){

	}

	@Override
	public void closeInventory(PlayerEntity player){

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && stack.getItem() instanceof AbstractGlassware;
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
		inventory = ItemStack.EMPTY;
		markDirty();
	}

	@Override
	public String getName(){
		return "container.reagent_filter";
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent(getName());
	}

	@Override
	public boolean hasCustomName(){
		return false;
	}
}
