package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class SamplingBenchTileEntity extends TileEntity{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private void setGlassware(ItemStack stack){
		glassware = stack;
		
		reag = null;
		if(!stack.isEmpty() && stack.getItem() instanceof AbstractGlassware){
			for(ReagentStack r : ((AbstractGlassware) stack.getItem()).getReagants(stack).getLeft()){
				if(r != null && (reag == null || r.getAmount() > reag.getAmount())){
					reag = r;
				}
			}
		}
		
		world.setBlockState(pos, world.getBlockState(pos).withProperty(Properties.ACTIVE, !glassware.isEmpty()).withProperty(Properties.CRYSTAL, !glassware.isEmpty() && glassware.getMetadata() == 1));
		markDirty();
	}

	public ItemStack getGlassware(){
		return glassware;
	}

	public ReagentStack reag = null;
	private ItemStack glassware = ItemStack.EMPTY;

	public WrapperInv inv = new WrapperInv();

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		glassware = nbt.hasKey("phial") ? new ItemStack(nbt.getCompoundTag("phial")) : ItemStack.EMPTY;
		
		reag = null;
		if(!glassware.isEmpty() && glassware.getItem() instanceof AbstractGlassware){
			for(ReagentStack r : ((AbstractGlassware) glassware.getItem()).getReagants(glassware).getLeft()){
				if(r != null && (reag == null || r.getAmount() > reag.getAmount())){
					reag = r;
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(!glassware.isEmpty()){
			nbt.setTag("phial", glassware.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TextComponentTranslation("container.sampling_bench");
	}

	private class WrapperInv implements IInventory{

		@Override
		public String getName(){
			return "wapper_inv";
		}

		@Override
		public boolean hasCustomName(){
			return false;
		}

		@Override
		public ITextComponent getDisplayName(){
			return new TextComponentString(getName());
		}

		@Override
		public int getSizeInventory(){
			return 2;
		}

		@Override
		public boolean isEmpty(){
			return glassware.isEmpty();
		}

		@Override
		public ItemStack getStackInSlot(int index){
			if(index == 0){
				return glassware;
			}
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack decrStackSize(int index, int count){
			ItemStack out = ItemStack.EMPTY;
			if(index == 0){
				out = glassware.copy();
				count = Math.min(out.getCount(), count);
				out.setCount(count);
				glassware.shrink(count);
				markDirty();
			}
			return out;
		}

		@Override
		public ItemStack removeStackFromSlot(int index){
			ItemStack out = ItemStack.EMPTY;
			if(index == 0){
				out = glassware;
				glassware = ItemStack.EMPTY;
				markDirty();
			}
			return out;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack){
			if(index == 0){
				setGlassware(stack);
			}
		}

		@Override
		public int getInventoryStackLimit(){
			return 1;
		}

		@Override
		public void markDirty(){
			SamplingBenchTileEntity.this.markDirty();
		}

		@Override
		public boolean isUsableByPlayer(EntityPlayer player){
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player){

		}

		@Override
		public void closeInventory(EntityPlayer player){

		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack){
			if(index == 1){
				return stack.getItem() == Items.PAPER;
			}
			if(index == 0){
				return stack.getItem() instanceof AbstractGlassware;
			}
			return false;
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
			glassware = ItemStack.EMPTY;
			markDirty();
		}
	}
}
