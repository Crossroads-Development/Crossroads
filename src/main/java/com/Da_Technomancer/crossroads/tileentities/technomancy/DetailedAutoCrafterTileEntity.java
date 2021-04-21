package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.DetailedCrafterRec;
import com.Da_Technomancer.crossroads.gui.container.DetailedAutoCrafterContainer;
import com.Da_Technomancer.crossroads.items.PathSigil;
import com.Da_Technomancer.essentials.gui.container.AutoCrafterContainer;
import com.Da_Technomancer.essentials.tileentities.AutoCrafterTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.Optional;

@ObjectHolder(Crossroads.MODID)
public class DetailedAutoCrafterTileEntity extends AutoCrafterTileEntity{

	@ObjectHolder("detailed_auto_crafter")
	public static TileEntityType<DetailedAutoCrafterTileEntity> type = null;

	public DetailedAutoCrafterTileEntity(){
		super(type);
	}

	@Override
	protected int invSize(){
		return 20;//We use slot 19 for the sigil
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.detailed_auto_crafter");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
		return new DetailedAutoCrafterContainer(id, playerInventory, iInv, worldPosition);
	}

	@Nullable
	@Override
	public IRecipe<CraftingInventory> validateRecipe(IRecipe<?> rec, @Nullable AutoCrafterContainer container){
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
	public IRecipe<CraftingInventory> findRecipe(CraftingInventory fakeInv, @Nullable AutoCrafterContainer container){
		//Same as super version, except with different recipe type

		IRecipe<CraftingInventory> iRecipe;

		if(recipe == null){
			//No recipe has been directly set via recipe book/JEI. Pick a recipe based on manually configured inputs, if applicable
			//Use the recipe manager to find a recipe matching the inputs
			Optional<DetailedCrafterRec> recipeOptional = getRecipeManager().getRecipeFor(CRRecipes.DETAILED_TYPE, fakeInv, level);
			iRecipe = validateRecipe(recipeOptional.orElse(null), container);
		}else{
			//Recipe set via recipe book/JEI
			iRecipe = validateRecipe(lookupRecipe(getRecipeManager(), recipe), container);
		}
		return iRecipe;
	}
}
