package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.gui.container.FluidCoolerContainer;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.items.crafting.recipes.FluidCoolingRec;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class FluidCoolingChamberTileEntity extends InventoryTE{

	@ObjectHolder("fluid_cooling_chamber")
	private static TileEntityType<FluidCoolingChamberTileEntity> type = null;

	public static final int HEATING_RATE = 40;
	private double storedHeat = 0;//The buffered heat that will be added to the temperature over time at a constant rate

	public FluidCoolingChamberTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(4_000, true, false, o -> true);
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

		//We can not use the recipe manager to filter recipes due to the fluid input
		List<FluidCoolingRec> recipes = world.getRecipeManager().getRecipes(CRRecipes.FLUID_COOLING_TYPE, this, world);
		//Filter the recipes by fluid type, fluid qty, temperature, and item type of output, and take the first recipe that matches the laundry list of specifications
		Optional<FluidCoolingRec> recOpt = recipes.parallelStream().filter(rec -> rec.getMaxTemp() > temp + storedHeat && BlockUtil.sameFluid(rec.getInput(), fluids[0]) && rec.getInput().getAmount() >= fluids[0].getAmount() && (inventory[0].isEmpty() || BlockUtil.sameItem(inventory[0], rec.getRecipeOutput()))).findFirst();
		if(recOpt.isPresent()){
			FluidCoolingRec rec = recOpt.get();
			//Check the output will fit
			if(inventory[0].getMaxStackSize() - inventory[0].getCount() >= rec.getRecipeOutput().getCount()){
				storedHeat += rec.getAddedHeat();
				fluids[0].shrink(rec.getInput().getAmount());
				if(inventory[0].isEmpty()){
					inventory[0] = rec.getCraftingResult(this);
				}else{
					inventory[0].grow(rec.getRecipeOutput().getCount());
				}
				markDirty();
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
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) globalFluidOpt;
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