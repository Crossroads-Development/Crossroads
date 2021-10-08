package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.FluidCoolingRec;
import com.Da_Technomancer.crossroads.gui.container.FluidCoolerContainer;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

import com.Da_Technomancer.crossroads.API.templates.InventoryTE.ItemHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE.TankProperty;

@ObjectHolder(Crossroads.MODID)
public class FluidCoolingChamberTileEntity extends InventoryTE{

	@ObjectHolder("fluid_cooling_chamber")
	private static BlockEntityType<FluidCoolingChamberTileEntity> type = null;

	public static final int HEATING_RATE = 40;
	private double storedHeat = 0;//The buffered heat that will be added to the temperature over time at a constant rate

	public FluidCoolingChamberTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(4_000, true, false, o -> true);
		initFluidManagers();
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

		if(level.isClientSide){
			return;
		}

		double moved = Math.min(storedHeat, HEATING_RATE);
		if(moved > 0){
			storedHeat -= moved;
			temp += moved;
			setChanged();
		}

		//We can not use the recipe manager to filter recipes due to the fluid input
		List<FluidCoolingRec> recipes = level.getRecipeManager().getRecipesFor(CRRecipes.FLUID_COOLING_TYPE, this, level);
		//Filter the recipes by fluid type, fluid qty, temperature, and item type of output, and take the first recipe that matches the laundry list of specifications
		Optional<FluidCoolingRec> recOpt = recipes.parallelStream().filter(rec -> rec.getMaxTemp() > temp + storedHeat && rec.inputMatches(fluids[0]) && (inventory[0].isEmpty() || BlockUtil.sameItem(inventory[0], rec.getResultItem()))).findFirst();
		if(recOpt.isPresent()){
			FluidCoolingRec rec = recOpt.get();
			//Check the output will fit
			if(inventory[0].getMaxStackSize() - inventory[0].getCount() >= rec.getResultItem().getCount()){
				storedHeat += rec.getAddedHeat();
				fluids[0].shrink(rec.getInputQty());
				if(inventory[0].isEmpty()){
					inventory[0] = rec.assemble(this);
				}else{
					inventory[0].grow(rec.getResultItem().getCount());
				}
				setChanged();
			}
		}
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		storedHeat = nbt.getDouble("heat_stored");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putDouble("heat_stored", storedHeat);
		return nbt;
	}

	private final LazyOptional<ItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@Override
	public void setRemoved(){
		super.setRemoved();
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
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return true;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.fluid_cooler");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new FluidCoolerContainer(id, playerInventory, createContainerBuf());
	}
}