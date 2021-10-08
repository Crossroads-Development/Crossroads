package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CopshowiumCreationChamberTileEntity;
import com.google.gson.JsonObject;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class CopshowiumRec implements IOptionalRecipe<Container>{

	private final ResourceLocation id;
	private final String group;

	private final FluidIngredient input;
	private final float mult;
	private final boolean flux;
	private final boolean active;

	public CopshowiumRec(ResourceLocation location, String name, FluidIngredient input, float expandFactor, boolean flux, boolean active){
		id = location;
		group = name;
		this.input = input;
		this.mult = expandFactor;
		this.flux = flux;
		this.active = active;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public FluidIngredient getInput(){
		return input;
	}

	public float getMult(){
		return mult;
	}

	public boolean isFlux(){
		return flux;
	}

	@Override
	public boolean matches(Container inv, Level worldIn){
		return active && inv instanceof CopshowiumCreationChamberTileEntity && input.test(((CopshowiumCreationChamberTileEntity) inv).getInputFluid());
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
		return new ItemStack(CRBlocks.copshowiumCreationChamber);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.COPSHOWIUM_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.COPSHOWIUM_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CopshowiumRec>{

		@Override
		public CopshowiumRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = GsonHelper.getAsString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new CopshowiumRec(recipeId, s, FluidIngredient.EMPTY, 0, false, false);
			}

			//Specify the fluid input as an ingredient
			FluidIngredient input = CraftingUtil.getFluidIngredient(json, "input", true);
			//How much to expand the fluid by when crafting
			float mult = GsonHelper.getAsFloat(json, "mult", 1);
			//Whether this recipe generates temporal entropy
			boolean flux = GsonHelper.getAsBoolean(json, "entropy", false);
			return new CopshowiumRec(recipeId, s, input, mult, flux, true);
		}

		@Nullable
		@Override
		public CopshowiumRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){
				return new CopshowiumRec(recipeId, s, FluidIngredient.EMPTY, 0, false, false);
			}
			FluidIngredient input = FluidIngredient.readFromBuffer(buffer);
			float mult = buffer.readFloat();
			boolean flux = buffer.readBoolean();
			return new CopshowiumRec(recipeId, s, input, mult, flux, true);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, CopshowiumRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.getInput().writeToBuffer(buffer);
				buffer.writeFloat(recipe.mult);
				buffer.writeBoolean(recipe.flux);
			}
		}
	}
}
