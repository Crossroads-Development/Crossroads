package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.alchemy.ReagentFilter;
import com.Da_Technomancer.crossroads.gui.container.ReagentFilterContainer;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class ReagentFilterTileEntity extends AlchemyCarrierTE implements MenuProvider, Container{

	@ObjectHolder("reagent_filter")
	public static BlockEntityType<ReagentFilterTileEntity> TYPE = null;

	private Direction facing = null;
	private ItemStack inventory = ItemStack.EMPTY;

	public ReagentFilterTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public ReagentFilterTileEntity(BlockPos pos, BlockState state, boolean crystal){
		super(TYPE, pos, state, !crystal);
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
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		facing = null;
		chemOpt.invalidate();
		chemOpt = LazyOptional.of(() -> handler);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		inventory = nbt.contains("inv") ? ItemStack.of(nbt.getCompound("inv")) : ItemStack.EMPTY;
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		if(!inventory.isEmpty()){
			nbt.put("inv", inventory.save(new CompoundTag()));
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
		BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
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
	public boolean stillValid(Player player){
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
	public Component getDisplayName(){
		return new TranslatableComponent("container.reagent_filter");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new ReagentFilterContainer(id, playerInv, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(worldPosition));
	}
}
