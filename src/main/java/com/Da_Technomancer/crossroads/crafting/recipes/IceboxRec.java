package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class IceboxRec implements IOptionalRecipe<Container>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;
	private final float cooling;
	private final boolean active;

	public IceboxRec(ResourceLocation location, String name, Ingredient input, double cooling, boolean active){
		id = location;
		group = name;
		ingr = input;
		this.cooling = (float) cooling;
		this.active = active;
	}

	public float getCooling(){
		return cooling;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr);
		return nonnulllist;
	}
	public Ingredient getIngredient(){
		return ingr;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public boolean matches(Container inv, Level worldIn){
		return active && ingr.test(inv.getItem(0));
	}

	@Override
	public ItemStack assemble(Container inv){
		return getResultItem();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.icebox);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.COOLING_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.COOLING_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<IceboxRec>{

		@Override
		public IceboxRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = GsonHelper.getAsString(json, "group", "");
			if(!CraftingUtil.isActiveJSON(json)){
				return new IceboxRec(recipeId, s, Ingredient.EMPTY, 0, false);
			}

			Ingredient ingredient = CraftingUtil.getIngredient(json, "fuel", false);

			//Output specified as 1 float tag
			double cooling = GsonHelper.getAsFloat(json, "cooling");
			return new IceboxRec(recipeId, s, ingredient, cooling, true);
		}

		@Nullable
		@Override
		public IceboxRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){
				return new IceboxRec(recipeId, s, Ingredient.EMPTY, 0, false);
			}
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			float cooling = buffer.readFloat();
			return new IceboxRec(recipeId, s, ingredient, cooling, true);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, IceboxRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.ingr.toNetwork(buffer);
				buffer.writeFloat(recipe.cooling);
			}
		}
	}
}
