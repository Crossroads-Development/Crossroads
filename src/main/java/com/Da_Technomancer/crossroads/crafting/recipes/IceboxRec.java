package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
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

import javax.annotation.Nullable;

public class IceboxRec implements IOptionalRecipe<IInventory>{

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

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return active && ingr.test(inv.getItem(0));
	}

	@Override
	public ItemStack assemble(IInventory inv){
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
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.COOLING_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.COOLING_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<IceboxRec>{

		@Override
		public IceboxRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getAsString(json, "group", "");
			if(!CraftingUtil.isActiveJSON(json)){
				return new IceboxRec(recipeId, s, Ingredient.EMPTY, 0, false);
			}

			Ingredient ingredient = CraftingUtil.getIngredient(json, "fuel", false);

			//Output specified as 1 float tag
			double cooling = JSONUtils.getAsFloat(json, "cooling");
			return new IceboxRec(recipeId, s, ingredient, cooling, true);
		}

		@Nullable
		@Override
		public IceboxRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){
				return new IceboxRec(recipeId, s, Ingredient.EMPTY, 0, false);
			}
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			float cooling = buffer.readFloat();
			return new IceboxRec(recipeId, s, ingredient, cooling, true);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, IceboxRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.ingr.toNetwork(buffer);
				buffer.writeFloat(recipe.cooling);
			}
		}
	}
}
