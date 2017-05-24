package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemChutePortTileEntity extends TileEntity implements ITickable{

	private ItemStack inventory = ItemStack.EMPTY;
	private final double[] motionData = new double[4];
	private final double[] physData = new double[] {500, 2};

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(isSpotInvalid() && !inventory.isEmpty()){
			world.spawnEntity(new EntityItem(world, pos.offset(world.getBlockState(pos).getValue(Properties.FACING)).getX(), pos.getY(), pos.offset(world.getBlockState(pos).getValue(Properties.FACING)).getZ(), inventory.copy()));
			inventory = ItemStack.EMPTY;
		}else{
			if(!inventory.isEmpty()){
				BlockPos outputPos = getOutput();
				if(outputPos != null){
					EnumFacing facing = world.getBlockState(outputPos).getValue(Properties.FACING);
					if(Math.abs(motionData[1]) > .5D){
						axleHandler.addEnergy(-.5D, false, false);
						EntityItem ent = new EntityItem(world, pos.getX() + (facing == EnumFacing.EAST ? 1.5D : facing == EnumFacing.WEST ? -.5D : facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? .5D : 0), outputPos.getY(), pos.getZ() + (facing == EnumFacing.SOUTH ? 1.5D : facing == EnumFacing.EAST || facing == EnumFacing.WEST ? .5D : facing == EnumFacing.NORTH ? -.5D : 0), inventory);
						ent.motionX = 0;
						ent.motionZ = 0;
						world.spawnEntity(ent);
						inventory = ItemStack.EMPTY;
						markDirty();
					}
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		if(!inventory.isEmpty()){
			nbt.setTag("inv", inventory.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		if(nbt.hasKey("inv")){
			inventory = new ItemStack(nbt.getCompoundTag("inv"));
		}
	}

	private boolean isSpotInvalid(){
		Block block = world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock();
		return block == ModBlocks.itemChutePort || block == ModBlocks.itemChute;
	}

	private BlockPos getOutput(){
		for(int height = 1; height < 255 - pos.getY(); height++){
			Block block = world.getBlockState(pos.offset(EnumFacing.UP, height)).getBlock();
			if(block == ModBlocks.itemChutePort){
				return pos.offset(EnumFacing.UP, height);
			}

			if(block != ModBlocks.itemChute){
				return null;
			}
			if(++height > 255){
				break;
			}
		}
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing facing){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getOutput() != null && (facing == null || facing == world.getBlockState(pos).getValue(Properties.FACING))){
			return true;
		}
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && facing == world.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y)){
			return true;
		}

		return super.hasCapability(cap, facing);
	}

	private final IAxleHandler axleHandler = new AxleHandler();
	private final IItemHandler handler = new InventoryHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing facing){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getOutput() != null && (facing == null || facing == world.getBlockState(pos).getValue(Properties.FACING))){
			return (T) handler;
		}
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && facing == world.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y)){
			return (T) axleHandler;
		}

		return super.getCapability(cap, facing);
	}

	private class AxleHandler implements IAxleHandler{
		@Override
		public double[] getMotionData(){
			return motionData;
		}

		private double rotRatio;
		private byte updateKey;

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
		}

		@Override
		public double[] getPhysData(){
			return physData;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void resetAngle(){

		}

		@SideOnly(Side.CLIENT)
		@Override
		public double getAngle(){
			return 0;
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[1] += energy;
			}else if(allowInvert){
				motionData[1] += energy * Math.signum(motionData[1]);
			}else if(absolute){
				int sign = (int) Math.signum(motionData[1]);
				motionData[1] += energy;
				if(sign != 0 && Math.signum(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}else{
				int sign = (int) Math.signum(motionData[1]);
				motionData[1] += energy * ((double) sign);
				if(Math.signum(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}
			markDirty();
		}

		@Override
		public void markChanged(){
			markDirty();
		}
	}

	private class InventoryHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? inventory : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || stack.isEmpty() || !inventory.isEmpty()){
				return stack;
			}

			if(!simulate){
				inventory = stack.copy();
				inventory.setCount(1);
				markDirty();
			}

			ItemStack holder = stack.copy();
			holder.shrink(1);
			return holder;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 1 : 0;
		}
	}
}
