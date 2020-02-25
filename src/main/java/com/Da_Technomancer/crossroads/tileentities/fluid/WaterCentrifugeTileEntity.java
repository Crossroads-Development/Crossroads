package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.WaterCentrifugeContainer;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.items.crafting.recipes.CentrifugeRec;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class WaterCentrifugeTileEntity extends InventoryTE{

	@ObjectHolder("water_centrifuge")
	private static TileEntityType<WaterCentrifugeTileEntity> type = null;
	
	public static final double TIP_POINT = .5D;
	public static final int INERTIA = 115;
//	private static final int BATCH_SIZE = 250;
	private boolean neg;

	public WaterCentrifugeTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(10_000, true, false, (Fluid f) -> true);
		fluidProps[1] = new TankProperty(10_000, false, true, (Fluid f) -> false);
		initFluidManagers();
	}

	public boolean isNeg(){
		return neg;
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	public FluidStack getInputFluid(){
		return fluids[0];//Used for recipe selection
	}

	@Override
	public boolean useRotary(){
		return true;
	}

	@Override
	public void tick(){
		super.tick();
		if(world.isRemote){
			return;
		}

		if(Math.abs(motData[0]) >= TIP_POINT && (Math.signum(motData[0]) == -1) == neg){
			neg = !neg;
			//Handle direction switching regardless of whether crafting occurred

			Optional<CentrifugeRec> recOpt = world.getRecipeManager().getRecipe(CRRecipes.CENTRIFUGE_TYPE, this, world);
			if(recOpt.isPresent()){
				CentrifugeRec rec = recOpt.get();
				//The recipe matches() method checks inputs- those being valid is a given

				FluidStack fluidOut = rec.getFluidOutput();
				ItemStack itemOut = rec.getRecipeOutput();

				fluids[0].shrink(rec.getInput().getAmount());
				if(fluids[1].isEmpty()){
					fluids[1] = fluidOut.copy();
				}else if(BlockUtil.sameFluid(fluids[1], fluidOut)){
					fluids[1].setAmount(Math.min(fluids[1].getAmount() + fluidOut.getAmount(), fluidProps[1].capacity));
				}

				if(inventory[0].isEmpty()){
					inventory[0] = itemOut.copy();
				}else if(BlockUtil.sameItem(inventory[0], itemOut)){
					inventory[0].setCount(Math.min(inventory[0].getCount() + itemOut.getCount(), itemOut.getMaxStackSize()));
				}
				markDirty();
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("neg", neg);

		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		neg = nbt.getBoolean("neg");
	}

	@Override
	public void remove(){
		super.remove();
		waterOpt.invalidate();
		dWaterOpt.invalidate();
		saltOpt.invalidate();
	}

	@Override
	public void updateContainingBlockInfo(){
		super.updateContainingBlockInfo();
		waterOpt.invalidate();
		dWaterOpt.invalidate();
		waterOpt = LazyOptional.of(() -> new FluidHandler(0));
		dWaterOpt = LazyOptional.of(() -> new FluidHandler(1));
	}

	private LazyOptional<IFluidHandler> waterOpt = LazyOptional.of(() -> new FluidHandler(0));
	private LazyOptional<IFluidHandler> dWaterOpt = LazyOptional.of(() -> new FluidHandler(1));
	private final LazyOptional<IItemHandler> saltOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction facing){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() != world.getBlockState(pos).get(CRProperties.HORIZ_AXIS)){
			return (LazyOptional<T>) waterOpt;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() == world.getBlockState(pos).get(CRProperties.HORIZ_AXIS)){
			return (LazyOptional<T>) dWaterOpt;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) saltOpt;
		}
		if(cap == Capabilities.AXLE_CAPABILITY && facing == Direction.UP){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(cap, facing);
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return index == 0;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.water_centrifuge");
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new WaterCentrifugeContainer(i, playerInventory, createContainerBuf());
	}
}
