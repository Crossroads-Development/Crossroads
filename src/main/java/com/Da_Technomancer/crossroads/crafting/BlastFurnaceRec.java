package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
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

import javax.annotation.Nullable;

public class BlastFurnaceRec implements IOptionalRecipe<Container>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;

	private final FluidStack output;
	private final int slag;

	private final boolean active;

	public BlastFurnaceRec(ResourceLocation location, String name, Ingredient input, FluidStack output, int slag, boolean active){
		id = location;
		group = name;
		ingr = input;
		this.output = output;
		this.slag = slag;
		this.active = active;
	}

	public FluidStack getOutput(){
		return output;
	}

	public int getSlag(){
		return slag;
	}

	@Override
	public boolean matches(Container inv, Level worldIn){
		return isEnabled() && ingr.test(inv.getItem(0));
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
	public ItemStack assemble(Container inv){
		return getResultItem().copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public ItemStack getResultItem(){
		return new ItemStack(CRItems.slag, slag);
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.blastFurnace);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.BLAST_FURNACE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.BLAST_FURNACE_TYPE;
	}

	public static class Serializer implements RecipeSerializer<BlastFurnaceRec>{

		@Override
		public BlastFurnaceRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = GsonHelper.getAsString(json, "group", "");
			if(!CraftingUtil.isActiveJSON(json)){
				return new BlastFurnaceRec(recipeId, s, Ingredient.EMPTY, FluidStack.EMPTY, 0, false);
			}
			Ingredient ingredient = CraftingUtil.getIngredient(json, "ingredient", false);
			//Output specified as fluid- see CraftingUtil
			FluidStack fluid = CraftingUtil.getFluidStack(json, "output");
			//Slag specified as 1 int tag (default 0)
			int slag = GsonHelper.getAsInt(json, "slag", 0);
			return new BlastFurnaceRec(recipeId, s, ingredient, fluid, slag, true);
		}

		@Nullable
		@Override
		public BlastFurnaceRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(buffer.readBoolean()){
				Ingredient ingredient = Ingredient.fromNetwork(buffer);
				FluidStack fluid = FluidStack.readFromPacket(buffer);
				int slag = buffer.readVarInt();
				return new BlastFurnaceRec(recipeId, s, ingredient, fluid, slag, true);
			}
			return new BlastFurnaceRec(recipeId, s, Ingredient.EMPTY, FluidStack.EMPTY, 0, false);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BlastFurnaceRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.ingr.toNetwork(buffer);
				recipe.output.writeToPacket(buffer);
				buffer.writeVarInt(recipe.slag);
			}
		}
	}
}
