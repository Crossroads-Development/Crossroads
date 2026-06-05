package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.IncubatorRec;
import com.Da_Technomancer.crossroads.gui.container.IncubatorContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IncubatorTileEntity extends InventoryTE implements IHeatCapable, IItemCapable{

	public static final int REQUIRED = 3 * 60 * 20;//Total required progress for one operation, 3min
	public static final int MIN_TEMP = 30;//Minimum operating temp
	public static final int MAX_TEMP = 40;//Maximum operating temp
	public static final int LUCKY_DIGIT = 3;
	public static final String TARGET_TEMP = "33.333";
	public static final int MAX_ADDED_QUALITY = 50;
	public static final double CONSUMED_HEAT = 3;

	private int time = 0;//in ticks
	private int progress = 0;

	public static final BlockEntityType<IncubatorTileEntity> TYPE = CRTileEntity.createType(IncubatorTileEntity::new, CRBlocks.incubator);

	public IncubatorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 3);
		//Index 0: mutator, index 1: eggs, index 2: output
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.boilerplate.progress", CRConfig.formatVal(progress, player), REQUIRED));
		super.addInfo(chat, player, hit);
	}

	public int getProgress(){
		return progress;
	}

	public int getTime(){
		return time / 20;
	}

	@Override
	public int getUITemp(){
		//3 decimal points
		//Normally we cap UI data slot values at Short.MAX_VALUE, but Neoforge added a patch that lets it use the full int
		//If this ever breaks for temperatures above 32.767 C, that's why
		return (int) Math.round(temp * 1000);
	}

	@Override
	public void serverTick(){
		super.serverTick();

		boolean validRecipe = false;

		for(int i = 0; i < 2; i++){
			if(!inventory[i].isEmpty() && IPerishable.isSpoiled(inventory[i], level)){
				if(inventory[2].isEmpty()){
					//Eject any spoiled ingredients
					inventory[2] = inventory[i];
					inventory[i] = ItemStack.EMPTY;
					setChanged();
				}
				return;
			}
		}

		Optional<RecipeHolder<IncubatorRec>> recipeOpt = level.getRecipeManager().getRecipeFor(CRRecipes.INCUBATOR_TYPE, this, level);
		if(recipeOpt.isPresent()){
			//Normally we'd try and stack the outputs, but every recipe for this machine makes some weird non-stacking thing
			//So just don't bother
//			ItemStack toCreate = recipeOpt.get().value().assemble(this, level.registryAccess());
//			//Check that we have the other ingredient, and that there is space for the output
//			if((inventory[2].isEmpty() || BlockUtil.sameItem(inventory[2], toCreate) && toCreate.getCount() + inventory[2].getCount() <= toCreate.getMaxStackSize())){
			if(inventory[2].isEmpty()){
				validRecipe = true;
				time++;

				//Increase progress
				if(temp <= MAX_TEMP && temp >= MIN_TEMP){
					int evalTemp = (int) Math.round(temp * 1000);
					int matches = 0;
					for(int i = 0; i < 5; i++){
						if(evalTemp % 10 == LUCKY_DIGIT){
							matches++;
						}
						evalTemp /= 10;
					}

					progress += matches;

					if(progress >= REQUIRED){
						if(time < REQUIRED / 10){
							//Should be impossible
							Crossroads.logger.warn("Incubator recipe completed in invalid time: " + time);
							time = REQUIRED;
						}
						ItemStack resultItem = recipeOpt.get().value().assemble(this, level.registryAccess());
						EntityTemplate geneticData = resultItem.get(CRItems.GENETICS_DATA);
						if(geneticData != null){
							resultItem.set(CRItems.GENETICS_DATA, geneticData.withQuality(geneticData.quality() + (MAX_ADDED_QUALITY / 5) * REQUIRED / time));
						}
						if(inventory[2].isEmpty()){
							inventory[2] = resultItem;
						}else{
							inventory[2].grow(resultItem.getCount());
						}
						inventory[0].shrink(1);
						inventory[1].shrink(1);
						progress = 0;
						time = 0;
						temp -= CONSUMED_HEAT;
					}
				}
				setChanged();
			}
		}

		if(!validRecipe){
			//Reset any accumulated progress
			progress = 0;
			time = 0;
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		time = nbt.getInt("time");
		progress = nbt.getInt("progress");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("time", time);
		nbt.putInt("progress", progress);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		//Only accept inputs listed in one of the recipes for each slot to make it clearer what-goes-where
		if(index == 0){
			List<RecipeHolder<IncubatorRec>> recipes = level.getRecipeManager().getAllRecipesFor(CRRecipes.INCUBATOR_TYPE);
			return recipes.stream().anyMatch(rec -> rec.value().getMainInput().test(stack));
		}else if(index == 1){
			List<RecipeHolder<IncubatorRec>> recipes = level.getRecipeManager().getAllRecipesFor(CRRecipes.INCUBATOR_TYPE);
			return recipes.stream().anyMatch(rec -> rec.value().getSecondaryInput().test(stack));
		}
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction side){
		return index == 2;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.crossroads.incubator");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new IncubatorContainer(id, playerInv, createContainerBuf());
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		return heatHandler;
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}
}
