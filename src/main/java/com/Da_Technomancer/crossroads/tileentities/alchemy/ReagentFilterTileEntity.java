package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.alchemy.ReagentFilter;
import com.Da_Technomancer.crossroads.gui.container.ReagentFilterContainer;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class ReagentFilterTileEntity extends AlchemyCarrierTE implements INamedContainerProvider, IInventory{

	@ObjectHolder("reagent_filter")
	private static TileEntityType<ReagentFilterTileEntity> type = null;

	private Direction facing = null;
	private ItemStack inventory = ItemStack.EMPTY;

	public ReagentFilterTileEntity(){
		super(type);
	}

	public ReagentFilterTileEntity(boolean crystal){
		super(type, !crystal);
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
			facing = state.get(CRProperties.HORIZ_FACING);
		}
		return facing;
	}

	public void clearCache(){
		facing = null;
		chemOpt.invalidate();
		chemOpt = LazyOptional.of(() -> handler);
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		inventory = nbt.contains("inv") ? ItemStack.read(nbt.getCompound("inv")) : ItemStack.EMPTY;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		if(!inventory.isEmpty()){
			nbt.put("inv", inventory.write(new CompoundNBT()));
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
		LazyOptional<IChemicalHandler> chemOpt;
		if(toTrans.getTotalQty() <= 0 || te == null || !(chemOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent()){
			return false;
		}
		IChemicalHandler otherHandler = chemOpt.orElseThrow(NullPointerException::new);
		EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
		if((cont == EnumContainerType.GLASS) != glass){
			return false;
		}
		return otherHandler.insertReagents(toTrans, side.getOpposite(), handler, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(facing == getFacing() || facing != null && facing.getAxis() == Direction.Axis.Y){
			return (LazyOptional<T>) chemOpt;
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
		return world.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5F) <= 64;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && stack.getItem() instanceof AbstractGlassware;
	}

	@Override
	public void clear(){
		inventory = ItemStack.EMPTY;
		markDirty();
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.reagent_filter");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new ReagentFilterContainer(id, playerInv, new PacketBuffer(Unpooled.buffer()).writeBlockPos(pos));
	}
}
