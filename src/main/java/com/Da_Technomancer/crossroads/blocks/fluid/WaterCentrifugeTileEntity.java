package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CentrifugeRec;
import com.Da_Technomancer.crossroads.gui.container.WaterCentrifugeContainer;
import com.Da_Technomancer.essentials.api.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class WaterCentrifugeTileEntity extends InventoryTE{

	public static final BlockEntityType<WaterCentrifugeTileEntity> TYPE = CRTileEntity.createType(WaterCentrifugeTileEntity::new, CRBlocks.waterCentrifuge);
	
	public static final double TIP_POINT = .5D;
	public static final int INERTIA = 50;
//	private static final int BATCH_SIZE = 250;
	private boolean neg;

	public WaterCentrifugeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
		fluidProps[0] = new TankProperty(10_000, true, false);
		fluidProps[1] = new TankProperty(10_000, false, true);
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
	public void serverTick(){
		double axleSpeed = axleHandler.getSpeed();
		if(Math.abs(axleSpeed) >= TIP_POINT && (Math.signum(axleSpeed) == -1) == neg){
			neg = !neg;
			//Handle direction switching regardless of whether crafting occurred

			Optional<CentrifugeRec> recOpt = level.getRecipeManager().getRecipeFor(CRRecipes.CENTRIFUGE_TYPE, this, level);
			if(recOpt.isPresent() && !fluids[0].isEmpty()){
				CentrifugeRec rec = recOpt.get();
				//The recipe matches() method checks inputs- those being valid is a given

				FluidStack fluidOut = rec.getFluidOutput();
				ItemStack itemOut = rec.getResultItem();

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
				setChanged();
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putBoolean("neg", neg);

	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		neg = nbt.getBoolean("neg");
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		saltOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> saltOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction facing){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) globalFluidOpt;
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
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 0;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.water_centrifuge");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity){
		return new WaterCentrifugeContainer(i, playerInventory, createContainerBuf());
	}
}
