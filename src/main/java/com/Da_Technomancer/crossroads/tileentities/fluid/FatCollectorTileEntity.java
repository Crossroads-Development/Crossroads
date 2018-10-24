package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.fluids.BlockLiquidFat;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class FatCollectorTileEntity extends TileEntity implements ITickable, IInfoTE{

	private double temp;
	private boolean init = false;
	private FluidStack content = null;
	private static final int CAPACITY = 2_000;
	private ItemStack inv = ItemStack.EMPTY;

	public static final int[] TIERS = {100, 120, 140, 160, 180};
	public static final double[] EFFICIENCY = {0.8D, 1D, 1.2D, 1D, 0.8D};
	private static final double USE_PER_VALUE = 2D;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side){
		chat.add("Temp: " + MiscUtil.betterRound(heatHandler.getTemp(), 3) + "°C");
		chat.add("Biome Temp: " + HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
	}

	@Override
	public void update(){
		if(!init){
			heatHandler.init();
		}

		int tier = HeatUtil.getHeatTier(temp, TIERS);

		if(tier != -1 && !inv.isEmpty()){
			int liqAm = (int) (((ItemFood) inv.getItem()).getHealAmount(inv) + ((ItemFood) inv.getItem()).getSaturationModifier(inv));
			double heatUse = ((double) liqAm) * USE_PER_VALUE;
			liqAm *= EnergyConverters.FAT_PER_VALUE;
			liqAm *= EFFICIENCY[tier];
			if(liqAm <= (CAPACITY - (content == null ? 0 : content.amount))){
				temp -= heatUse;
				inv.shrink(1);
				content = new FluidStack(BlockLiquidFat.getLiquidFat(), liqAm + (content == null ? 0 : content.amount));
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		content = FluidStack.loadFluidStackFromNBT(nbt);
		inv = nbt.hasKey("inv") ? new ItemStack(nbt.getCompoundTag("inv")) : ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("init", init);
		nbt.setDouble("temp", temp);

		if(content != null){
			content.writeToNBT(nbt);
		}
		if(!inv.isEmpty()){
			nbt.setTag("inv", inv.writeToNBT(new NBTTagCompound()));
		}

		return nbt;
	}

	private final IItemHandler itemHandler = new FoodHandler();
	private final HeatHandler heatHandler = new HeatHandler();
	private final IFluidHandler mainHandler = new MainHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP){
			return (T) mainHandler;
		}
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return (T) heatHandler;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.UP)){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP){
			return true;
		}
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return true;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.UP)){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				temp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				init = true;
			}
		}

		@Override
		public double getTemp(){
			init();
			return temp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			temp = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			temp += heat;
		}

	}

	private class MainHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(content, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || content == null || resource.getFluid() != content.getFluid()){
				return null;
			}
			int amount = Math.min(resource.amount, content.amount);

			if(doDrain){
				content.amount -= amount;
				if(content.amount <= 0){
					content = null;
				}
			}

			return new FluidStack(resource.getFluid(), amount);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){

			if(maxDrain <= 0 || content == null){
				return null;
			}
			int amount = Math.min(maxDrain, content.amount);

			Fluid fluid = content.getFluid();

			if(doDrain){
				content.amount -= amount;
				if(content.amount <= 0){
					content = null;
				}
			}

			return new FluidStack(fluid, amount);
		}
	}

	private class FoodHandler implements IItemHandler{

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
			if(slot != 0 || stack.isEmpty() || !(stack.getItem() instanceof ItemFood) || stack.getItem() == ModItems.edibleBlob){
				return stack;
			}

			if(!inv.isEmpty() && !ItemStack.areItemsEqual(stack, inv)){
				return stack;
			}

			int limit = Math.min(stack.getMaxStackSize() - inv.getCount(), stack.getCount());
			if(!simulate){
				if(inv.isEmpty()){
					inv = new ItemStack(stack.getItem(), limit, stack.getMetadata());
				}else{
					inv.grow(limit);
				}

			}

			return stack.getCount() == limit ? ItemStack.EMPTY : new ItemStack(stack.getItem(), stack.getCount() - limit, stack.getMetadata());
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 64 : 0;
		}

	}
}
