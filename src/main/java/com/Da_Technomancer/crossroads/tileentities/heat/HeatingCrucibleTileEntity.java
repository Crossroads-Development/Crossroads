package com.Da_Technomancer.crossroads.tileentities.heat;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class HeatingCrucibleTileEntity extends TileEntity implements ITickable{

	private FluidStack content = null;
	private static final int PRODUCED_LAVA = 200;
	private static final int PRODUCED_COPPER = 144;
	private static final int CAPACITY = 16 * PRODUCED_LAVA;
	private boolean init = false;
	private double temp;
	private ItemStack inventory = ItemStack.EMPTY;

	/**
	 * This controls whether the tile entity gets replaced whenever the block
	 * state is changed. Normally only want this when block actually is
	 * replaced.
	 */
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}

	/**
	 * 0 = not locked, 1 = copper, 2 = cobble
	 */
	private byte getType(){
		if(!inventory.isEmpty()){
			for(int ID : OreDictionary.getOreIDs(inventory)){
				if(ID == OreDictionary.getOreID("dustCopper")){
					return 1;
				}
				if(ID == OreDictionary.getOreID("cobblestone")){
					return 2;
				}
			}
		}

		if(content != null){
			if(content.getFluid() == FluidRegistry.LAVA){
				return 2;
			}
			if(content.getFluid() == BlockMoltenCopper.getMoltenCopper()){
				return 1;
			}
		}

		return 0;
	}

	private boolean recipeIsValid(ItemStack stack){

		if(stack.isEmpty()){
			return false;
		}

		byte type = 0;

		for(int ID : OreDictionary.getOreIDs(stack)){
			if(ID == OreDictionary.getOreID("dustCopper")){
				type = 1;
				break;
			}
			if(ID == OreDictionary.getOreID("cobblestone")){
				type = 2;
				break;
			}
		}

		if(type == 0){
			return false;
		}

		if(type == getType() || getType() == 0){
			return true;
		}

		return false;
	}

	private IBlockState getCorrectState(){
		return ModBlocks.heatingCrucible.getDefaultState().withProperty(Properties.FULLNESS, (int) Math.ceil(Math.min(3, (content == null ? 0F : ((float) content.amount) * 3F / ((float) CAPACITY)) + ((float) inventory.getCount()) * 3F / 16F))).withProperty(Properties.TEXTURE_4, (getType() == 2 ? 2 : 0) + (content != null ? 1 : 0));
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!init){
			temp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getFloatTemperature(pos);
			init = true;
		}

		byte type = getType();

		if(temp >= 1000D){
			temp -= 10D;
			if(!inventory.isEmpty() && Math.random() < (temp >= 1490 ? 0.05 : 0.01) && (content == null || CAPACITY - content.amount >= (type == 1 ? PRODUCED_COPPER : PRODUCED_LAVA))){
				if(content == null){
					content = new FluidStack(type == 1 ? BlockMoltenCopper.getMoltenCopper() : FluidRegistry.LAVA, type == 1 ? PRODUCED_COPPER : PRODUCED_LAVA);
				}else{
					content.amount += type == 1 ? PRODUCED_COPPER : PRODUCED_LAVA;
				}

				inventory.shrink(1);
			}
			markDirty();
		}

		
		IBlockState correctState = getCorrectState();
		if(world.getBlockState(pos).getValue(Properties.FULLNESS) != getCorrectState().getValue(Properties.FULLNESS) || world.getBlockState(pos).getValue(Properties.TEXTURE_4) != correctState.getValue(Properties.TEXTURE_4)){
			world.setBlockState(pos, correctState, 2);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);

		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");

		if(nbt.hasKey("inv")){
			inventory = new ItemStack(nbt.getCompoundTag("inv"));
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

		if(!inventory.isEmpty()){
			nbt.setTag("inv", inventory.writeToNBT(new NBTTagCompound()));
		}

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
			if(slot != 0 || !recipeIsValid(stack)){
				return stack;
			}

			int amount = Math.min(16 - inventory.getCount(), stack.getCount());

			if(!simulate){
				inventory = new ItemStack(stack.getItem(), amount + inventory.getCount(), stack.getMetadata());
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
				temp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getFloatTemperature(pos);
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
