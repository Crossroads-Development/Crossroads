package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.FluidCoolingRec;
import com.Da_Technomancer.crossroads.gui.container.FluidCoolerContainer;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class FluidCoolingChamberTileEntity extends InventoryTE{

	public static final BlockEntityType<FluidCoolingChamberTileEntity> TYPE = CRTileEntity.createType(FluidCoolingChamberTileEntity::new, CRBlocks.fluidCoolingChamber);

	public static final int HEATING_RATE = 40;
	private double storedHeat = 0;//The buffered heat that will be added to the temperature over time at a constant rate

	public FluidCoolingChamberTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
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
	public void serverTick(){
		super.serverTick();

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
	public void load(CompoundTag nbt){
		super.load(nbt);
		storedHeat = nbt.getDouble("heat_stored");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putDouble("heat_stored", storedHeat);
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
		return Component.translatable("container.fluid_cooler");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new FluidCoolerContainer(id, playerInventory, createContainerBuf());
	}
}