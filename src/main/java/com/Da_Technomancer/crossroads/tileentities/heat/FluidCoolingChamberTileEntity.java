package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.gui.container.FluidCoolerContainer;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidCoolingChamberTileEntity extends InventoryTE{

	@ObjectHolder("fluid_cooling_chamber")
	private static TileEntityType<FluidCoolingChamberTileEntity> type = null;

	public static final int HEATING_RATE = 40;
	private double storedHeat = 0;

	public FluidCoolingChamberTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(4_000, true, false, RecipeHolder.fluidCoolingRecipes::containsKey);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	public FluidStack getFluid(){
		return fluids[0];
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}

		double moved = Math.min(storedHeat, HEATING_RATE);
		if(moved > 0){
			storedHeat -= moved;
			temp += moved;
			markDirty();
		}

		//TODO Migrate to non-null fluidstacks and JSON recipes
		Pair<Integer, Triple<ItemStack, Double, Double>> craft = fluids[0] == null ? null : RecipeHolder.fluidCoolingRecipes.get(fluids[0].getFluid());

		if(craft != null && fluids[0].amount >= craft.getLeft()){
			if(temp + storedHeat < craft.getRight().getMiddle() && (inventory[0].isEmpty() || ItemStack.areItemsEqual(craft.getRight().getLeft(), inventory[0]) && inventory[0].getMaxStackSize() - inventory[0].getCount() >= craft.getRight().getLeft().getCount())){
				storedHeat += craft.getRight().getRight();
				if((fluids[0].amount -= craft.getLeft()) <= 0){
					fluids[0] = null;
				}
				markDirty();
				if(inventory[0].isEmpty()){
					inventory[0] = craft.getRight().getLeft().copy();
				}else{
					inventory[0].grow(craft.getRight().getLeft().getCount());
				}
			}
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		storedHeat = nbt.getDouble("heat_stored");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putDouble("heat_stored", storedHeat);
		return nbt;
	}

	private final LazyOptional<ItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@Override
	public void remove(){
		super.remove();
		itemOpt.invalidate();
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction dir){
		if(cap == Capabilities.HEAT_CAPABILITY && dir == Direction.UP){
			return (LazyOptional<T>) heatOpt;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(cap, dir);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.fluid_cooler");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new FluidCoolerContainer(id, playerInventory, createContainerBuf());
	}
}