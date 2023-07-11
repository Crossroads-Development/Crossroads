package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.IncubatorRec;
import com.Da_Technomancer.crossroads.gui.container.IncubatorContainer;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IncubatorTileEntity extends InventoryTE{

	public static final int REQUIRED = 60 * 20;//Total required progress for one operation, 1min
	public static final double SLOW_MULT = 0.05D;//Speed compared to full speed when in the operating temp range, but not near the target temp
	public static final int MIN_TEMP = 25;//Minimum operating temp
	public static final int MAX_TEMP = 150;//Maximum operating temp
	public static final int MARGIN = 10;//Considered near the target temp if within MARGIN of targetTemp

	private double progress = 0;
	private int targetTemp = 0;//Target temperature will be between (MIN_TEMP + MARGIN) and (MAX_TEMP - MARGIN), inclusive, once initialized

	public static final BlockEntityType<IncubatorTileEntity> TYPE = CRTileEntity.createType(IncubatorTileEntity::new, CRBlocks.incubator);

	public IncubatorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 3);
		//Index 0: mutator, index 1: eggs, index 2: output
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		double target = getTargetTemp();
		chat.add(Component.translatable("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		chat.add(Component.translatable("tt.crossroads.incubator.target", target, target - MARGIN, target + MARGIN));
		super.addInfo(chat, player, hit);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	public int getProgress(){
		return (int) progress;
	}

	public int getTargetTemp(){
		if(targetTemp < MIN_TEMP){
			//Assume the target temperature has been reset, select a new value
			targetTemp = level.random.nextInt((int) (MAX_TEMP - MIN_TEMP - MARGIN * 2D) + 1) + MIN_TEMP + MARGIN;
			setChanged();
		}
		return targetTemp;
	}

	public static boolean withinTarget(double temp, double target){
		return temp <= target + MARGIN && temp >= target - MARGIN;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		boolean validRecipe = false;

		if(!inventory[0].isEmpty() && !inventory[1].isEmpty()){
			if(IPerishable.isSpoiled(inventory[0], level)){
				if(inventory[2].isEmpty()){
					//Eject the invalid input item, including spoiled embryos
					inventory[2] = inventory[0];
					inventory[0] = ItemStack.EMPTY;
					setChanged();
				}
				return;
			}
		}

		Optional<IncubatorRec> recipeOpt = level.getRecipeManager().getRecipeFor(CRRecipes.INCUBATOR_TYPE, this, level);
		if(recipeOpt.isPresent()){
			ItemStack toCreate = recipeOpt.get().getCreatedItem(this, level);
			//Check that we have the other ingredient, and that there is space for the output
			if((inventory[2].isEmpty() || BlockUtil.sameItem(inventory[2], toCreate) && toCreate.getCount() + inventory[2].getCount() <= toCreate.getMaxStackSize())){
				validRecipe = true;

				//Increase progress
				if(temp <= MAX_TEMP && temp >= MIN_TEMP){
					if(withinTarget(temp, getTargetTemp())){
						progress += 1D;
					}else{
						progress += SLOW_MULT;
					}
					if(progress >= REQUIRED){
						progress = 0;
						targetTemp = 0;//Reset the target temp, to be re-randomized
						if(inventory[2].isEmpty()){
							inventory[2] = toCreate;
						}else{
							inventory[2].grow(toCreate.getCount());
						}
						inventory[0].shrink(1);
						inventory[1].shrink(1);
					}
					setChanged();
				}
			}
		}

		if(!validRecipe){
			//Reset any accumulated progress
			progress = 0;
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		targetTemp = nbt.getInt("target");
		progress = nbt.getDouble("progress");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("target", targetTemp);
		nbt.putDouble("progress", progress);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		//Only accept inputs listed in one of the recipes for each slot to make it clearer what-goes-where
		if(index == 0){
			List<IncubatorRec> recipes = level.getRecipeManager().getAllRecipesFor(CRRecipes.INCUBATOR_TYPE);
			return recipes.stream().anyMatch(rec -> rec.getMainInput().test(stack));
		}else if(index == 1){
			List<IncubatorRec> recipes = level.getRecipeManager().getAllRecipesFor(CRRecipes.INCUBATOR_TYPE);
			return recipes.stream().anyMatch(rec -> rec.getSecondaryInput().test(stack));
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
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	private LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (LazyOptional<T>) heatOpt;
		}
		if(capability == ForgeCapabilities.ITEM_HANDLER){
			return (LazyOptional<T>) itemOpt;
		}
		return super.getCapability(capability, facing);
	}
}
