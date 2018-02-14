package com.Da_Technomancer.crossroads.tileentities.heat;

import java.util.ArrayList;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendStringToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.OmniMeter;
import com.Da_Technomancer.crossroads.items.Thermometer;
import com.Da_Technomancer.crossroads.items.crafting.ICraftingStack;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class HeatingCrucibleTileEntity extends TileEntity implements ITickable, IInfoTE, IStringReceiver{

	private FluidStack content = null;
	private static final int CAPACITY = 16 * 200;
	private boolean init = false;
	private double temp;
	private ItemStack inventory = ItemStack.EMPTY;

	@Override
	public void receiveString(String context, String message, EntityPlayerMP sender){
		if(world.isRemote && context.equals("text")){
			activeText = message;
		}
	}

	/**
	 * The texture of the solid material, if any. Server side only. 
	 */
	private String solidText = null;
	/**
	 * The texture to be displayed, if any. 
	 */
	private String activeText = null;

	public String getActiveTexture(){
		return activeText;
	}

	@Override
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, EnumFacing side){
		if(device instanceof OmniMeter || device == EnumGoggleLenses.RUBY || device instanceof Thermometer){
			chat.add("Temp: " + MiscOp.betterRound(heatHandler.getTemp(), 3) + "°C");
			if(!(device instanceof Thermometer)){
				chat.add("Biome Temp: " + EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
			}
		}
	}

	/**
	 * This controls whether the tile entity gets replaced whenever the block
	 * state is changed. Normally only want this when block actually is
	 * replaced.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private Pair<FluidStack, String> getRecipe(ItemStack stack){
		if(stack.isEmpty()){
			return Pair.of(null, null);
		}

		for(Triple<ICraftingStack<ItemStack>, FluidStack, String> rec : RecipeHolder.heatingCrucibleRecipes){
			if(rec.getLeft().softMatch(stack)){
				return Pair.of(rec.getMiddle(), rec.getRight());
			}
		}
		return Pair.of(null, null);
	}

	@Override
	public void update(){		
		if(world.isRemote){
			return;
		}

		if(!init){
			temp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			init = true;
		}

		int fullness = (int) Math.ceil(Math.min(3, (content == null ? 0F : ((float) content.amount) * 3F / ((float) CAPACITY)) + ((float) inventory.getCount()) * 3F / 16F));
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() != ModBlocks.heatingCrucible){
			invalidate();
			return;
		}
		if(state.getValue(Properties.FULLNESS) != fullness){
			world.setBlockState(pos, state.withProperty(Properties.FULLNESS, fullness), 2);
		}

		if(fullness != 0 && world.getTotalWorldTime() % 2 == 0){
			if(solidText != null && content == null && !solidText.equals(activeText)){
				activeText = solidText;
				ModPackets.network.sendToAllAround(new SendStringToClient("text", activeText, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}else if(content != null && content.getFluid().getStill() != null){
				String goal = content.getFluid().getStill().toString();
				if(!goal.equals(activeText)){
					activeText = goal;
					ModPackets.network.sendToAllAround(new SendStringToClient("text", activeText, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}
		}

		if(temp >= 1000D){
			temp -= 10D;
			if(!inventory.isEmpty() && Math.random() < (temp >= 1490 ? 0.05 : 0.01)){
				FluidStack created = getRecipe(inventory).getLeft();
				if(created == null){
					inventory = ItemStack.EMPTY;
				}else if(content == null || (CAPACITY - content.amount >= created.amount && content.getFluid() == created.getFluid())){
					if(content == null){
						content = created.copy();
					}else{
						content.amount += created.amount;
					}
				}


				inventory.shrink(1);
			}
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);

		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		solidText = nbt.getString("sol");
		activeText = nbt.getString("act");
		if(nbt.hasKey("inv")){
			inventory = new ItemStack(nbt.getCompoundTag("inv"));
		}
		if(solidText == null && !inventory.isEmpty()){
			solidText = getRecipe(inventory).getRight();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(content != null){
			content.writeToNBT(nbt);
		}

		nbt.setBoolean("init", init);
		nbt.setDouble("temp", temp);
		if(solidText != null){
			nbt.setString("sol", solidText);
		}
		if(activeText != null){
			nbt.setString("act", activeText);
		}

		if(!inventory.isEmpty()){
			nbt.setTag("inv", inventory.writeToNBT(new NBTTagCompound()));
		}

		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setString("act", activeText);
		return nbt;
	}

	private final IFluidHandler fluidHandler = new FluidHandler();
	private final IHeatHandler heatHandler = new HeatHandler();
	private final IItemHandler itemHandler = new ItemHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) fluidHandler;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing != EnumFacing.UP){
			return (T) heatHandler;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null)){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return true;
		}

		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing != EnumFacing.UP){
			return true;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == EnumFacing.UP || facing == null)){
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
			return slot == 0 ? inventory : ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || (!inventory.isEmpty() && !stack.isItemEqual(inventory))){
				return stack;
			}
			Pair<FluidStack, String> rec = getRecipe(stack);
			if(rec.getLeft() == null){
				return stack;
			}

			int amount = Math.min(16 - inventory.getCount(), stack.getCount());

			if(!simulate){
				inventory = new ItemStack(stack.getItem(), amount + inventory.getCount(), stack.getMetadata());
				solidText = rec.getRight();
				markDirty();
				
			}

			return amount == stack.getCount() ? ItemStack.EMPTY : new ItemStack(stack.getItem(), stack.getCount() - amount, stack.getMetadata());
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return slot == 0 ? 16 : 0;
		}
	}

	private class FluidHandler implements IFluidHandler{

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

			if(resource == null || content == null || !resource.isFluidEqual(content)){
				return null;
			}

			int change = Math.min(content.amount, resource.amount);
			if(doDrain){
				if(change != 0){
					content.amount -= change;
					if(content.amount == 0){
						content = null;
					}
				}
			}
			return change == 0 ? null : new FluidStack(resource.getFluid(), change);

		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(content == null){
				return null;
			}

			int change = Math.min(content.amount, maxDrain);
			Fluid fluid = content.getFluid();
			if(doDrain){
				content.amount -= change;
				if(content.amount == 0){
					content = null;
				}
			}
			return change == 0 ? null : new FluidStack(fluid, change);
		}
	}

	private class HeatHandler implements IHeatHandler{
		private void init(){
			if(!init){
				temp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
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
