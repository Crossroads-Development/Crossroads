package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SteamBoilerTileEntity extends TileEntity implements ITickable, IInfoTE{

	private FluidStack steamContent;
	private FluidStack waterContent;
	public ItemStack inventory = ItemStack.EMPTY;
	private static final int CAPACITY = 10_000;
	public static final int BATCH_SIZE = 100;
	public static final int[] TIERS = {100, 200, 300, 400, 500};

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side){
		chat.add("Temp: " + MiscUtil.betterRound(heatHandler.getTemp(), 3) + "°C");
		chat.add("Biome Temp: " + HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
		chat.add(inventory.getCount() + "/64 salt");
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(!init){
			temp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			init = true;
		}

		int tier = HeatUtil.getHeatTier(temp, TIERS);


		if(tier != -1){
			temp -= EnergyConverters.degPerSteamBucket(false) * (tier + 1) * (double) BATCH_SIZE / 1000D;

			if(waterContent != null && waterContent.amount >= BATCH_SIZE && (steamContent == null || CAPACITY - steamContent.amount >= BATCH_SIZE) && inventory.getCount() < 64){
				boolean salty = waterContent.getFluid() == FluidRegistry.WATER;

				int batches = Math.min(tier + 1, waterContent.amount / BATCH_SIZE);
				batches = Math.min(batches, steamContent == null ? CAPACITY / BATCH_SIZE : (CAPACITY - steamContent.amount) / BATCH_SIZE);
				if(salty){
					batches = Math.min(batches, 64 - inventory.getCount());
				}
				waterContent.amount -= batches * BATCH_SIZE;
				if(waterContent.amount == 0){
					waterContent = null;
				}

				if(steamContent == null){
					steamContent = new FluidStack(BlockSteam.getSteam(), BATCH_SIZE * batches);
				}else{
					steamContent.amount += BATCH_SIZE * batches;
				}

				if(salty){
					if(inventory.isEmpty()){
						inventory = new ItemStack(ModItems.dustSalt, batches);
					}else{
						inventory.grow(batches);
					}
				}
			}

			markDirty();
		}
	}

	private boolean init = false;
	private double temp;

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		steamContent = FluidStack.loadFluidStackFromNBT(nbt);
		waterContent = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("water"));
		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		inventory = nbt.hasKey("inv") ? new ItemStack(nbt.getCompoundTag("inv")) : ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(steamContent != null){
			steamContent.writeToNBT(nbt);
		}

		NBTTagCompound waterHolder = new NBTTagCompound();
		if(waterContent != null){
			waterContent.writeToNBT(waterHolder);
		}

		nbt.setTag("water", waterHolder);
		nbt.setBoolean("init", init);
		nbt.setDouble("temp", temp);

		if(!inventory.isEmpty()){
			nbt.setTag("inv", inventory.writeToNBT(new NBTTagCompound()));
		}

		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN){
			return true;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return true;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return true;
		}

		return super.hasCapability(capability, facing);
	}

	private final IFluidHandler waterHandler = new WaterFluidHandler();
	private final IFluidHandler steamHandler = new SteamFluidHandler();
	private final IFluidHandler innerHandler = new InnerFluidHandler();
	private final IHeatHandler heatHandler = new HeatHandler();
	private final IItemHandler itemHandler = new ItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){

		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(facing == null){
				return (T) innerHandler;
			}
			switch(facing){
				case NORTH:
					return (T) waterHandler;
				case SOUTH:
					return (T) waterHandler;
				case EAST:
					return (T) waterHandler;
				case WEST:
					return (T) waterHandler;
				case UP:
					return (T) steamHandler;
				case DOWN:
					return null;
			}
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || facing == null)){
			return (T) heatHandler;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	private class ItemHandler implements IItemHandler{

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
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount <= 0 || inventory.isEmpty()){
				return ItemStack.EMPTY;
			}

			int count = Math.min(inventory.getCount(), amount);

			if(!simulate){
				inventory.shrink(count);
				markDirty();
			}

			return amount > 0 ? new ItemStack(ModItems.dustSalt, count) : ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 64 : 0;
		}
	}

	private class WaterFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(waterContent, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null || (resource.getFluid() != FluidRegistry.WATER && resource.getFluid() != BlockDistilledWater.getDistilledWater()) || (waterContent != null && !waterContent.isFluidEqual(resource))){
				return 0;
			}
			int change = Math.min(CAPACITY - (waterContent == null ? 0 : waterContent.amount), resource.amount);
			if(doFill){
				waterContent = new FluidStack(resource.getFluid(), change + (waterContent == null ? 0 : waterContent.amount));
			}
			return change;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			return null;
		}

	}

	private class SteamFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(steamContent, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){

			if(resource != null && resource.getFluid() == BlockSteam.getSteam() && steamContent != null){
				int change = Math.min(steamContent.amount, resource.amount);

				if(doDrain){
					steamContent.amount -= change;
					if(steamContent.amount == 0){
						steamContent = null;
					}
				}

				return new FluidStack(BlockSteam.getSteam(), change);
			}else{
				return null;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(steamContent == null || maxDrain == 0){
				return null;
			}

			int change = Math.min(steamContent.amount, maxDrain);

			if(doDrain){
				steamContent.amount -= change;
				if(steamContent.amount == 0){
					steamContent = null;
				}
			}

			return new FluidStack(BlockSteam.getSteam(), change);
		}

	}

	private class InnerFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(steamContent, CAPACITY, true, true), new FluidTankProperties(waterContent, CAPACITY, true, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource != null && (resource.getFluid() == FluidRegistry.WATER || resource.getFluid() == BlockDistilledWater.getDistilledWater()) && (waterContent == null || waterContent.isFluidEqual(resource))){
				int change = Math.min(CAPACITY - (waterContent == null ? 0 : waterContent.amount), resource.amount);

				if(doFill){
					waterContent = new FluidStack(resource.getFluid(), (waterContent == null ? 0 : waterContent.amount) + change);
				}

				return change;
			}else{
				return 0;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(steamContent == null || maxDrain == 0){
				return null;
			}

			int change = Math.min(steamContent.amount, maxDrain);

			if(doDrain){
				steamContent.amount -= change;
				if(steamContent.amount == 0){
					steamContent = null;
				}
			}

			return new FluidStack(BlockSteam.getSteam(), change);
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){

			if(resource != null && resource.getFluid() == BlockSteam.getSteam() && steamContent != null){
				int change = Math.min(steamContent.amount, resource.amount);

				if(doDrain){
					steamContent.amount -= change;
					if(steamContent.amount == 0){
						steamContent = null;
					}
				}

				return new FluidStack(BlockSteam.getSteam(), change);
			}else{
				return null;
			}
		}

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
}
