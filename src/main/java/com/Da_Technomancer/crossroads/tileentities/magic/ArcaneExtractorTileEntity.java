package com.Da_Technomancer.crossroads.tileentities.magic;

import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class ArcaneExtractorTileEntity extends BeamRenderTE{

	private ItemStack inv = ItemStack.EMPTY;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(!inv.isEmpty()){
			nbt.setTag("inv", inv.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		inv = nbt.hasKey("inv") ? new ItemStack(nbt.getCompoundTag("inv")) : ItemStack.EMPTY;
	}

	private final IItemHandler itemHandler = new ItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != world.getBlockState(pos).getValue(EssentialsProperties.FACING)){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != world.getBlockState(pos).getValue(EssentialsProperties.FACING)){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	private class ItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inv : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || stack.isEmpty() || inv.getCount() >= getSlotLimit(0) || !inv.isEmpty() && !inv.isItemEqual(stack) || !(RecipeHolder.magExtractRecipes.containsKey(stack.getItem()))){
				return stack;
			}

			int moved = Math.min(getSlotLimit(0) - inv.getCount(), stack.getCount());

			if(!simulate){
				if(inv.isEmpty()){
					inv = new ItemStack(stack.getItem(), moved, stack.getMetadata());
				}else{
					inv.grow(moved);
				}
				markDirty();
			}

			return stack.getCount() == moved ? ItemStack.EMPTY : new ItemStack(stack.getItem(), stack.getCount() - moved, stack.getMetadata());
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 4 : 0;
		}
	}

	@Override
	protected void doEmit(MagicUnit toEmit){
		if(!inv.isEmpty() && RecipeHolder.magExtractRecipes.containsKey(inv.getItem())){
			MagicUnit mag = RecipeHolder.magExtractRecipes.get(inv.getItem());
			inv.shrink(1);
			beamer[world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex()].emit(mag, world);
		}else{
			beamer[world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex()].emit(null, world);
			if(!inv.isEmpty()){
				inv = ItemStack.EMPTY;
			}
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[6];
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex()] = true;
		return out;
	}
}
