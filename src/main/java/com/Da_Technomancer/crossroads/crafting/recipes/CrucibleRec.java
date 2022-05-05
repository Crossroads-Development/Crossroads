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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class CrucibleRec implements IOptionalRecipe<Container>{

	private final ResourceLocation id;
	private final String group;

	private final Ingredient input;
	private final FluidStack output;
	private final boolean active;

	public CrucibleRec(ResourceLocation location, String name, Ingredient input, FluidStack output, boolean active){
		id = location;
		group = name;
		this.input = input;
		this.output = output;
		this.active = active;
	}

	public FluidStack getOutput(){
		return output;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public boolean matches(Container inv, Level worldIn){
		return active && input.test(inv.getItem(0));
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
		return new ItemStack(CRBlocks.heatingCrucible);
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(input);
		return nonnulllist;
	}

	public Ingredient getIngredient(){
		return input;
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.CRUCIBLE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.CRUCIBLE_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CrucibleRec>{

		@Override
		public CrucibleRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = GsonHelper.getAsString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new CrucibleRec(recipeId, s, Ingredient.EMPTY, FluidStack.EMPTY, false);
			}

			Ingredient in = CraftingUtil.getIngredient(json, "input", true);
			FluidStack out = CraftingUtil.getFluidStack(json, "output");
			return new CrucibleRec(recipeId, s, in, out, true);
		}

		@Nullable
		@Override
		public CrucibleRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){
				return new CrucibleRec(recipeId, s, Ingredient.EMPTY, FluidStack.EMPTY, false);
			}
			Ingredient input = Ingredient.fromNetwork(buffer);
			FluidStack output = FluidStack.readFromPacket(buffer);
			return new CrucibleRec(recipeId, s, input, output, true);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, CrucibleRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.input.toNetwork(buffer);
				recipe.output.writeToPacket(buffer);
			}
		}
	}
}
