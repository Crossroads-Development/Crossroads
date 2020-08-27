package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class SingleIngrRecipe implements IOptionalRecipe<IInventory>{
	
	protected final Ingredient ingredient;
	protected final ItemStack result;
	private final IRecipeType<?> type;
	private final IRecipeSerializer<?> serializer;
	protected final ResourceLocation id;
	protected final String group;
	protected final boolean active;

	public SingleIngrRecipe(IRecipeType<?> type, IRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient ingredient, ItemStack result, boolean active){
		this.type = type;
		this.serializer = serializer;
		this.id = id;
		this.group = group;
		this.ingredient = ingredient;
		this.result = result;
		this.active = active;
	}

	@Override
	public IRecipeType<?> getType() {
		return type;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
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
	public ItemStack getRecipeOutput() {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingredient);
		return nonnulllist;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return isEnabled() && ingredient.test(inv.getStackInSlot(0));
	}

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	@Override
	public boolean canFit(int width, int height) {
		return width != 0 && height != 0;
	}

	public static class SingleRecipeSerializer<T extends SingleIngrRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>{

		private final IRecipeFactory<T> factory;

		public SingleRecipeSerializer(IRecipeFactory<T> factory){
			this.factory = factory;
		}

		@Override
		public T read(ResourceLocation recipeId, JsonObject json){
			String s = JSONUtils.getString(json, "group", "");
			if(!CraftingUtil.isActiveJSON(json)){
				return factory.create(recipeId, s, Ingredient.EMPTY, ItemStack.EMPTY, false);
			}
			Ingredient ingredient = CraftingUtil.getIngredient(json, "ingredient", false);

			ItemStack itemstack = CraftingUtil.getItemStack(json, "output", true, true);
			return factory.create(recipeId, s, ingredient, itemstack, true);
		}

		@Override
		public T read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);
			if(!buffer.readBoolean()){
				return factory.create(recipeId, s, Ingredient.EMPTY, ItemStack.EMPTY, false);
			}
			Ingredient ingredient = Ingredient.read(buffer);
			ItemStack itemstack = buffer.readItemStack();
			return factory.create(recipeId, s, ingredient, itemstack, true);
		}

		@Override
		public void write(PacketBuffer buffer, T recipe){
			buffer.writeString(recipe.getGroup());
			buffer.writeBoolean(recipe.isEnabled());
			recipe.getIngredients().get(0).write(buffer);
			buffer.writeItemStack(recipe.getRecipeOutput());
		}

		public interface IRecipeFactory<T extends SingleIngrRecipe>{
			T create(ResourceLocation location, String name, Ingredient input, ItemStack output, boolean active);
		}
	}
}