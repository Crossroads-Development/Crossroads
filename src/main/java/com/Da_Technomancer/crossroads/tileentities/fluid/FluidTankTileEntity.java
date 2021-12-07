package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.FluidTankContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class FluidTankTileEntity extends InventoryTE{

	@ObjectHolder("fluid_tank")
	public static BlockEntityType<FluidTankTileEntity> TYPE = null;

	public static final int CAPACITY = 16_000;

	public FluidTankTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 0);
		fluidProps[0] = new TankProperty(CAPACITY, true, true);
		initFluidManagers();
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	protected IFluidHandler createGlobalFluidHandler(){
		return new FluidTankHandler(0);
	}

	/*
	 * For setting the fluidstack on placement.
	 */
	public void setContent(FluidStack contentsIn){
		fluids[0] = contentsIn;
		setChanged();
	}

	/**
	 * For saving fluid to item form and redstone output
	 * @return The stored fluid
	 */
	public FluidStack getContent(){
		return fluids[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) globalFluidOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.fluid_tank");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new FluidTankContainer(id, playerInv, createContainerBuf());
	}
}
