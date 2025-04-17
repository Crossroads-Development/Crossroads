package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.FluidCoolingRec;
import com.Da_Technomancer.crossroads.gui.container.FluidCoolerContainer;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class FluidCoolingChamberTileEntity extends InventoryTE{

	public static final BlockEntityType<FluidCoolingChamberTileEntity> TYPE = CRTileEntity.createType(FluidCoolingChamberTileEntity::new, CRBlocks.fluidCoolingChamber);

	public static final int HEATING_RATE = 40;
	private double releasedHeat = 0;//Released heat to this point for the active recipe. 0 for no active recipe
	private double totalHeat = -1;//Total heat for the recipe. Negative value for no active recipe.
	private double maxRecipeTemp;//Maximum temperature at which this recipe can proceed. Undefined value for no active recipe.

	private final ItemHandler itemHandler = new ItemHandler();

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

	public int getReleasedHeat(){
		return (int) Math.round(releasedHeat);
	}

	public int getTotalHeat(){
		return (int) Math.round(totalHeat);
	}

	public int getMaxRecipeTemp(){
		return (int) Math.floor(maxRecipeTemp);
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(totalHeat >= 0){
			double moved = Math.min(maxRecipeTemp - temp, Math.min(totalHeat - releasedHeat, HEATING_RATE));
			if(moved > 0){
				//Ongoing recipe
				releasedHeat += moved;
				temp += moved;

				if(releasedHeat >= totalHeat - 0.01D){//0.01 to compensate for floating point errors
					//Finished crafting
					temp += totalHeat - releasedHeat;//compensate for floating point errors
					releasedHeat = 0;
					totalHeat = -1;
					FluidCoolingRec rec = getRecipe();
					if(rec != null){
						fluids[0].shrink(rec.getInputQty());
						if(inventory[0].isEmpty()){
							inventory[0] = rec.assemble(this);
						}else{
							inventory[0].grow(rec.getResultItem().getCount());
						}
					}
					startNewRecipe(rec);
				}

				setChanged();
			}
		}else{
			startNewRecipe(null);
		}
	}

	private void startNewRecipe(@Nullable FluidCoolingRec recipeHint){
		Predicate<FluidCoolingRec> recipeValidator = rec -> rec.inputMatches(fluids[0]) && (inventory[0].isEmpty() || BlockUtil.sameItem(inventory[0], rec.getResultItem()));
		FluidCoolingRec rec;
		if(recipeHint != null && recipeValidator.test(recipeHint)){
			rec = recipeHint;
		}else{
			rec = getRecipe();
		}
		if(rec != null && inventory[0].getCount() + rec.getResultItem().getCount() <= rec.getResultItem().getMaxStackSize()){
			totalHeat = rec.getAddedHeat();
			maxRecipeTemp = rec.getMaxTemp();
			setChanged();
		}
	}

	@Nullable
	private FluidCoolingRec getRecipe(){
		//We can not use the recipe manager to filter recipes due to the fluid input

		// TODO: it looks like everything here just got a RecipeHolder wrapped around it for some reason, see if there's a way to bypass that because it's
		//  generating a bunch of obnoxious calls.
		List<RecipeHolder<FluidCoolingRec>> recipes = level.getRecipeManager().getRecipesFor(CRRecipes.FLUID_COOLING_TYPE, this, level);
		Optional<RecipeHolder<FluidCoolingRec>> recOpt = recipes.parallelStream().filter(rec -> rec.value().inputMatches(fluids[0]) && (inventory[0].isEmpty() || BlockUtil.sameItem(inventory[0], rec.value().getResultItem()))).findAny();
		return recOpt.orElse(null) == null ? null : recOpt.get().value();
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		releasedHeat = nbt.getDouble("released_heat");
		totalHeat = nbt.getDouble("total_heat");
		maxRecipeTemp = nbt.getDouble("max_recipe_temp");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putDouble("released_heat", releasedHeat);
		nbt.putDouble("total_heat", totalHeat);
		nbt.putDouble("max_recipe_temp", maxRecipeTemp);
	}

	@Override
	@Nullable
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction direction){
		return globalFluidHandler;
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction direction){
		return heatHandler;
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