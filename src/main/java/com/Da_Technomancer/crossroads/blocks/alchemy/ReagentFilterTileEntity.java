package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.ReagentFilterContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.essentials.api.BlockUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReagentFilterTileEntity extends ReagentHolderTE implements MenuProvider, Container{

	public static final BlockEntityType<ReagentFilterTileEntity> TYPE = CRTileEntity.createType(ReagentFilterTileEntity::new, CRBlocks.reagentFilterGlass, CRBlocks.reagentFilterCrystal);

	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE = new Pair[] {Pair.of(new Vector3f(3F / 16F, 4F / 16F, 3F / 16F), new Vector3f(13F / 16F, 12F / 16F, 13F / 16F))};

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
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		inventory = nbt.contains("inv") ? BlockUtil.nbtToItemStack(nbt.getCompound("inv"), registries) : ItemStack.EMPTY;
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		if(!inventory.isEmpty()){
			nbt.put("inv", BlockUtil.stackToNBT(inventory, pRegistries));
		}
	}

	@Override
	protected void performTransfer(){
		long worldTick = level.getGameTime();
		if(lastActTick == worldTick){
			//Already acted upon this tick
			return;
		}

		ReagentMap filterMap = new ReagentMap();

		//Separate reagents to be filtered
		if(!contents.isEmpty() && !inventory.isEmpty() && inventory.getItem() instanceof AbstractGlassware && inventory.has(CRItems.REAGENT_DATA)){
			ReagentMap filtered = ((AbstractGlassware) inventory.getItem()).getReagents(inventory);
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

		if(transfered){
			dirtyReag = true;
			lastActTick = worldTick;
		}
	}

	private boolean transfer(ReagentMap toTrans, Direction side){
		BlockPos relPos = worldPosition.relative(side);
		IChemicalHandler otherChemHandler;
		if(toTrans.getTotalQty() <= 0 || level == null || (otherChemHandler = level.getCapability(CRCapabilities.CHEMICAL_CAPABILITY, relPos, side.getOpposite())) == null){
			return false;
		}
		EnumContainerType otherChannel = otherChemHandler.getChannel(side.getOpposite());
		if(!getChannel().connectsWith(otherChannel)){
			return false;
		}
		return otherChemHandler.insertReagents(toTrans, side.getOpposite(), chemHandler, true);
	}

	@Override
	@Nullable
	public IChemicalHandler getChemicalHandler(Direction dir){
		if((facing == getFacing() || facing != null && facing.getAxis() == Direction.Axis.Y)){
			return chemHandler;
		}
		return null;
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
		return Component.translatable("container.reagent_filter");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new ReagentFilterContainer(id, playerInv, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(worldPosition));
	}

	@Override
	public Pair<Vector3f, Vector3f>[] getRenderVolumes(){
		return RENDER_SHAPE;
	}
}
