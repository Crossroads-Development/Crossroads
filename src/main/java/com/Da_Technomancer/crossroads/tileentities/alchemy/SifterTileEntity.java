package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class SifterTileEntity extends TileEntity implements ITickable{

	private ItemStack inv = ItemStack.EMPTY;
	private double[] motionData = new double[4];
	private static final double[] PHYS_DATA = {500D, 2D};

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(world.getTotalWorldTime() % 10 == 0 && Math.abs(motionData[1]) >= .5D && !inv.isEmpty()){
			ItemStack out = RecipeHolder.sifterRecipes.get(inv.getItem());
			if(out == null){
				out = ItemStack.EMPTY;//Should only happen if recipes are removed. 
			}
			out = out.copy();

			TileEntity downTE = world.getTileEntity(pos.offset(EnumFacing.DOWN));
			if(downTE != null && downTE.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)){
				IItemHandler itemHand = downTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
				for(int i = 0; i < itemHand.getSlots(); i++){
					if(itemHand.insertItem(i, out, false).isEmpty()){
						inv = ItemStack.EMPTY;
						gear.addEnergy(-.5D, false, false);
						markDirty();
						break;
					}
				}
				return;
			}
			if(!out.isEmpty()){
				world.spawnEntity(new EntityItem(world, pos.getX() + .5D, pos.getY() - 1D, pos.getZ() + .5D, out));
			}
			inv = ItemStack.EMPTY;
			gear.addEnergy(-.5D, false, false);
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		inv = new ItemStack(nbt.getCompoundTag("inv"));

		NBTTagCompound innerMot = nbt.getCompoundTag("motionData");
		for(int i = 0; i < 4; i++){
			motionData[i] = (innerMot.hasKey(i + "motion")) ? innerMot.getDouble(i + "motion") : 0;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setTag("inv", inv.writeToNBT(new NBTTagCompound()));

		NBTTagCompound motionTags = new NBTTagCompound();
		for(int i = 0; i < 3; i++){
			if(motionData[i] != 0)
				motionTags.setDouble(i + "motion", motionData[i]);
		}
		nbt.setTag("motionData", motionTags);

		return nbt;
	}

	private final InternalGear gear = new InternalGear();
	private final TopInv invHandler = new TopInv();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side != null && side.getAxis() == (world.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.Z : Axis.X)){
			return true;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (side == null || side == EnumFacing.UP)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side != null && side.getAxis() == (world.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.Z : Axis.X)){
			return (T) gear;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (side == null || side == EnumFacing.UP)){
			return (T) invHandler;
		}
		return super.getCapability(cap, side);
	}

	private class TopInv implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return inv;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || !inv.isEmpty() || stack.isEmpty() || !RecipeHolder.sifterRecipes.containsKey(stack.getItem())){
				return stack;
			}

			if(!simulate){
				inv = stack.copy();
				inv.setCount(1);
				markDirty();
			}
			ItemStack out = stack.copy();
			out.shrink(1);
			return out.isEmpty() ? ItemStack.EMPTY : out;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return 1;
		}
	}

	private class InternalGear implements IAxleHandler{

		private byte key;
		private double rotRatio;

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte keyIn, double rotRatioIn, double lastRadius){
			if(rotRatioIn == 0){
				rotRatioIn = 1;
			}
			//If true, this has already been checked.
			if(key == keyIn){
				//If true, there is rotation conflict.
				if(rotRatio != rotRatioIn){
					masterIn.lock();
				}
				return;
			}

			if(masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn;

			if(key == 0){
				resetAngle();
			}
			key = keyIn;

			EnumFacing endPos = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, world.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.Z : Axis.X);
			EnumFacing endNeg = endPos.getOpposite();

			TileEntity posTE = world.getTileEntity(pos.offset(endPos));

			if(posTE != null){
				if(posTE.hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, endNeg)){
					posTE.getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, endNeg).trigger(masterIn, key);
				}

				if(posTE.hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, endNeg)){
					masterIn.addAxisToList(posTE.getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, endNeg), endNeg);
				}

				if(posTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, endNeg)){
					posTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, endNeg).propogate(masterIn, key, rotRatio, 0);
				}
			}

			TileEntity negTE = world.getTileEntity(pos.offset(endNeg));

			if(negTE != null){
				if(negTE.hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, endPos)){
					negTE.getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, endPos).trigger(masterIn, key);
				}

				if(negTE.hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, endPos)){
					masterIn.addAxisToList(negTE.getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, endPos), endPos);
				}

				if(negTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, endPos)){
					negTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, endPos).propogate(masterIn, key, rotRatio, 0);
				}
			}
		}

		@Override
		public double[] getPhysData(){
			return PHYS_DATA;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
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

		@Override
		public boolean shouldManageAngle(){
			return false;
		}	
	}
}
