package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.DetailedCrafterRec;
import com.Da_Technomancer.crossroads.gui.container.DetailedAutoCrafterContainer;
import com.Da_Technomancer.crossroads.items.PathSigil;
import com.Da_Technomancer.essentials.blocks.AutoCrafterTileEntity;
import com.Da_Technomancer.essentials.gui.container.AutoCrafterContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class DetailedAutoCrafterTileEntity extends AutoCrafterTileEntity{

	public static final BlockEntityType<DetailedAutoCrafterTileEntity> TYPE = CRTileEntity.createType(DetailedAutoCrafterTileEntity::new, CRBlocks.detailedAutoCrafter);

	public DetailedAutoCrafterTileEntity(BlockPos pos, BlockState state){
		super((BlockEntityType<? extends AutoCrafterTileEntity>) TYPE, pos, state);
	}

	@Override
	protected int invSize(){
		return 20;//We use slot 19 for the sigil
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.detailed_auto_crafter");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player){
		return new DetailedAutoCrafterContainer(id, playerInventory, iInv, worldPosition);
	}

	@Nullable
	@Override
	public Recipe<CraftingContainer> validateRecipe(Recipe<?> rec, @Nullable AutoCrafterContainer container){
		if(rec != null && rec.getType() == CRRecipes.DETAILED_TYPE && rec.canCraftInDimensions(3, 3)){
			DetailedCrafterRec drec = (DetailedCrafterRec) rec;
			ItemStack sigil = container == null ? inv[19] : container.getSlot(55).getItem();
			if(!sigil.isEmpty() && sigil.getItem() instanceof PathSigil && drec.getPath() == ((PathSigil) sigil.getItem()).getPath()){
				return drec;
			}
		}
		return null;
	}

	@Override
	@Nullable
	public Recipe<CraftingContainer> findRecipe(CraftingContainer fakeInv, @Nullable AutoCrafterContainer container){
		//Same as super version, except with different recipe type

		Recipe<CraftingContainer> iRecipe;

		if(recipe == null){
			//No recipe has been directly set via recipe book/JEI. Pick a recipe based on manually configured inputs, if applicable
			//Use the recipe manager to find a recipe matching the inputs
			List<DetailedCrafterRec> recipeList = getRecipeManager().getRecipesFor(CRRecipes.DETAILED_TYPE, fakeInv, level);
			iRecipe = null;
			//There may be several recipes with the same inputs, but different path. Check to find one for the current path
			for(DetailedCrafterRec rec : recipeList){
				Recipe<CraftingContainer> validatedRec;
				if((validatedRec = validateRecipe(rec, container)) != null){
					iRecipe = validatedRec;
					break;
				}
			}
		}else{
			//Recipe set via recipe book/JEI
			iRecipe = validateRecipe(lookupRecipe(getRecipeManager(), recipe), container);
		}
		return iRecipe;
	}
}
