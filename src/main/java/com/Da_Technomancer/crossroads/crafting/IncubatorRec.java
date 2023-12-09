package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.witchcraft.IncubatorTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class IncubatorRec implements IOptionalRecipe<Container>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient mainInput;
	private final Ingredient secondaryInput;
	private final ItemStack product;
	private final boolean datacopy;
	private final boolean active;

	public IncubatorRec(ResourceLocation id, String group, Ingredient mainInput, Ingredient secondaryInput, ItemStack product, boolean datacopy, boolean active){
		this.id = id;
		this.group = group;
		this.mainInput = mainInput;
		this.secondaryInput = secondaryInput;
		this.product = product;
		this.datacopy = datacopy;
		this.active = active;
	}

	public Ingredient getMainInput(){
		return mainInput;
	}

	public Ingredient getSecondaryInput(){
		return secondaryInput;
	}

	@Override
	public boolean matches(Container inv, Level worldIn){
		return active && inv instanceof IncubatorTileEntity incubator && mainInput.test(incubator.getItem(0)) && secondaryInput.test(incubator.getItem(1));
	}

	/**
	 * Use this instead of getResultItem
	 * It is safe to modify the returned itemstack
	 * @param inv Container with the ingredient item in slot 0
	 * @param worldIn World
	 * @return The created itemstack
	 */
	public ItemStack getCreatedItem(Container inv, Level worldIn){
		ItemStack created = getResultItem().copy();
		if(datacopy){
			try{
				CRItems.geneticSpawnEgg.withEntityTypeData(created, CRItems.embryo.getEntityTypeData(inv.getItem(0)));
			}catch(Exception e){
				Crossroads.logger.error("Invalid item types for datacopy in incubator recipe: " + getId().toString(), e);
			}
			return created;
		}
		if(created.getItem() instanceof IPerishable perishable){
			IPerishable.getAndInitSpoilTime(created, worldIn);//Set the spoil time if perishable
		}
		return created;
	}

	@Override
	public ItemStack getResultItem(){
		return product;//Note: lacks NBT data for datacopy true; use getCreatedItem instead
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.embryoLab);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.INCUBATOR_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.INCUBATOR_TYPE;
	}

	public static class Serializer implements RecipeSerializer<IncubatorRec>{

		@Override
		public IncubatorRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = GsonHelper.getAsString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new IncubatorRec(recipeId, s, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY, false, false);
			}

			Ingredient mainIngr = CraftingUtil.getIngredient(json, "main_input", false);
			Ingredient secondIngr = CraftingUtil.getIngredient(json, "secondary_input", false);
			boolean datacopy = GsonHelper.getAsBoolean(json, "datacopy", false);
			ItemStack itemstack = CraftingUtil.getItemStack(json, "output", false, true);
			return new IncubatorRec(recipeId, s, mainIngr, secondIngr, itemstack, datacopy, true);
		}

		@Nullable
		@Override
		public IncubatorRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			boolean active = buffer.readBoolean();
			if(active){
				Ingredient ingredientMain = Ingredient.fromNetwork(buffer);
				Ingredient ingredientSecond = Ingredient.fromNetwork(buffer);
				ItemStack itemstack = buffer.readItem();
				boolean datacopy = buffer.readBoolean();
				return new IncubatorRec(recipeId, s, ingredientMain, ingredientSecond, itemstack, datacopy, true);
			}else{
				return new IncubatorRec(recipeId, s, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY, false, false);
			}
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, IncubatorRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.mainInput.toNetwork(buffer);
				recipe.secondaryInput.toNetwork(buffer);
				buffer.writeItem(recipe.product);
				buffer.writeBoolean(recipe.datacopy);
			}
		}
	}
}
