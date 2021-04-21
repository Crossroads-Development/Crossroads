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
		if(level == null){
			return Direction.NORTH;
		}
		if(facing == null){
			BlockState state = getBlockState();
			if(!(state.getBlock() instanceof ReagentFilter)){
				return Direction.NORTH;
			}
			facing = state.getValue(CRProperties.HORIZ_FACING);
		}
		return facing;
	}

	@Override
	public void clearCache(){
		super.clearCache();
		facing = null;
		chemOpt.invalidate();
		chemOpt = LazyOptional.of(() -> handler);
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		inventory = nbt.contains("inv") ? ItemStack.of(nbt.getCompound("inv")) : ItemStack.EMPTY;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		if(!inventory.isEmpty()){
			nbt.put("inv", inventory.save(new CompoundNBT()));
		}
		return nbt;
	}

	@Override
	protected void performTransfer(){

		ReagentMap filterMap = new ReagentMap();

		//Separate reagents to be filtered
		if(!contents.isEmpty() && !inventory.isEmpty() && inventory.getItem() instanceof AbstractGlassware && inventory.hasTag()){
			ReagentMap filtered = ((AbstractGlassware) inventory.getItem()).getReagants(inventory);
			for(IReagent filtReag : filtered.keySetReag()){
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
			for(IReagent filtReag : filterMap.keySetReag()){
				int qty = filterMap.getQty(filtReag);
				if(qty != 0){
					contents.transferReagent(filtReag, qty, filterMap);
				}
			}
		}

		dirtyReag |= transfered;
	}

	private boolean transfer(ReagentMap toTrans, Direction side){
		TileEntity te = level.getBlockEntity(worldPosition.relative(side));
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
		if(capability == Capabilities.CHEMICAL_CAPABILITY && (facing == getFacing() || facing != null && facing.getAxis() == Direction.Axis.Y)){
			return (LazyOptional<T>) chemOpt;
		}
		return super.getCapability(capability, facing);
	}

	@Nonnull
	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] modes = {EnumTransferMode.OUTPUT, EnumTransferMode.INPUT, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		modes[getFacing().get3DDataValue()] = EnumTransferMode.OUTPUT;
		return modes;
	}

	@Override
	public int getContainerSize(){
		return 1;
	}

	@Override
	public boolean isEmpty(){
		return inventory.isEmpty();
	}

	@Override
	public ItemStack getItem(int index){
		return inventory;
	}

	@Override
	public ItemStack removeItem(int index, int count){
		if(count >= 1){
			return removeItemNoUpdate(index);
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeItemNoUpdate(int index){
		if(index == 0){
			ItemStack removed = inventory;
			inventory = ItemStack.EMPTY;
			setChanged();
			return removed;
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setItem(int index, ItemStack stack){
		if(index == 0){
			inventory = stack;
			setChanged();
		}
	}

	@Override
	public int getMaxStackSize(){
		return 1;
	}

	@Override
	public boolean stillValid(PlayerEntity player){
		return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5F) <= 64;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && stack.getItem() instanceof AbstractGlassware;
	}

	@Override
	public void clearContent(){
		inventory = ItemStack.EMPTY;
		setChanged();
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.reagent_filter");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new ReagentFilterContainer(id, playerInv, new PacketBuffer(Unpooled.buffer()).writeBlockPos(worldPosition));
	}
}
