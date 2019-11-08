package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.FluidTankContainer;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class FluidTankTileEntity extends InventoryTE{

	@ObjectHolder("fluid_tank")
	private static TileEntityType<FluidTankTileEntity> type = null;

	public static final int CAPACITY = 16_000;

	public FluidTankTileEntity(){
		super(type, 0);
		fluidProps[0] = new TankProperty(CAPACITY, true, true);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	public int getRedstone(){
		return RedstoneUtil.clampToVanilla(15F * fluids[0].getAmount() / CAPACITY);
	}

	/*
	 * For setting the fluidstack on placement.
	 */
	public void setContent(FluidStack contentsIn){
		fluids[0] = contentsIn;
		markDirty();
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
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.fluid_tank");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new FluidTankContainer(id, playerInv, createContainerBuf());
	}
}
