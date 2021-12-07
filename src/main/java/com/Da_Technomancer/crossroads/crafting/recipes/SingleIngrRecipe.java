package com.Da_Technomancer.crossroads.crafting.recipes;

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

public abstract class SingleIngrRecipe implements IOptionalRecipe<Container>{
	
	protected final Ingredient ingredient;
	protected final ItemStack result;
	private final RecipeType<?> type;
	private final RecipeSerializer<?> serializer;
	protected final ResourceLocation id;
	protected final String group;
	protected final boolean active;

	public SingleIngrRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient ingredient, ItemStack result, boolean active){
		this.type = type;
		this.serializer = serializer;
		this.id = id;
		this.group = group;
		this.ingredient = ingredient;
		this.result = result;
		this.active = active;
	}

	@Override
	public RecipeType<?> getType() {
		return type;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return serializer;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	/**
	 * Recipes with equal group are combined into one button in the recipe book
	 */
	@Override
	public String getGroup() {
		return group;
	}

	/**
	 * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
	 * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
	 */
	@Override
	public ItemStack getResultItem() {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingredient);
		return nonnulllist;
	}

	@Override
	public boolean matches(Container inv, Level worldIn){
		return isEnabled() && ingredient.test(inv.getItem(0));
	}

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width != 0 && height != 0;
	}

	public static class SingleRecipeSerializer<T extends SingleIngrRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T>{

		private final IRecipeFactory<T> factory;

		public SingleRecipeSerializer(IRecipeFactory<T> factory){
			this.factory = factory;
		}

		@Override
		public T fromJson(ResourceLocation recipeId, JsonObject json){
			String s = GsonHelper.getAsString(json, "group", "");
			if(!CraftingUtil.isActiveJSON(json)){
				return factory.create(recipeId, s, Ingredient.EMPTY, ItemStack.EMPTY, false);
			}
			Ingredient ingredient = CraftingUtil.getIngredient(json, "ingredient", false);

			ItemStack itemstack = CraftingUtil.getItemStack(json, "output", true, true);
			return factory.create(recipeId, s, ingredient, itemstack, true);
		}

		@Override
		public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){
				return factory.create(recipeId, s, Ingredient.EMPTY, ItemStack.EMPTY, false);
			}
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			ItemStack itemstack = buffer.readItem();
			return factory.create(recipeId, s, ingredient, itemstack, true);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, T recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.isEnabled());
			if(recipe.active){
				recipe.getIngredients().get(0).toNetwork(buffer);
				buffer.writeItem(recipe.getResultItem());
			}
		}

		public interface IRecipeFactory<T extends SingleIngrRecipe>{
			T create(ResourceLocation location, String name, Ingredient input, ItemStack output, boolean active);
		}
	}
}