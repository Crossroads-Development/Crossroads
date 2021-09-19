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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class EmbryoLabMorphRec implements IOptionalRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final ResourceLocation inputMob;
	private final ResourceLocation outputMob;
	private final Ingredient ingr;
	private final boolean active;

	public EmbryoLabMorphRec(ResourceLocation id, String group, ResourceLocation inputMob, ResourceLocation outputMob, Ingredient ingr, boolean active){
		this.id = id;
		this.group = group;
		this.inputMob = inputMob;
		this.outputMob = outputMob;
		this.ingr = ingr;
		this.active = active;
	}

	public ResourceLocation getInputMob(){
		return inputMob;
	}

	public ResourceLocation getOutputMob(){
		return outputMob;
	}

	public Ingredient getIngr(){
		return ingr;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return true;//The entire condition of this recipe is based on block type and power, which can't be determined here
	}

	@Override
	public ItemStack getResultItem(){
		return ItemStack.EMPTY;
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
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.EMBRYO_LAB_MORPH_SERIAL;
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
	public IRecipeType<?> getType(){
		return CRRecipes.EMBRYO_LAB_MORPH_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EmbryoLabMorphRec>{

		@Override
		public EmbryoLabMorphRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getAsString(json, "group", "");

			if(!CraftingUtil.isActiveJSON(json)){
				return new EmbryoLabMorphRec(recipeId, s, new ResourceLocation("none"), new ResourceLocation("none"), Ingredient.EMPTY, false);
			}

			//Input mob as a string registry name
			String inputName = JSONUtils.getAsString(json, "input_mob");
			ResourceLocation inputMob = new ResourceLocation(inputName);
			//Output mob as a string registry name
			String outputName = JSONUtils.getAsString(json, "output_mob");
			ResourceLocation ouputMob = new ResourceLocation(outputName);

			Ingredient inputIngr = CraftingUtil.getIngredient(json, "input", true);

			return new EmbryoLabMorphRec(recipeId, s, inputMob, ouputMob, inputIngr, true);
		}

		@Nullable
		@Override
		public EmbryoLabMorphRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			boolean active = buffer.readBoolean();
			if(active){
				ResourceLocation inputMob = new ResourceLocation(buffer.readUtf());
				ResourceLocation outputMob = new ResourceLocation(buffer.readUtf());
				Ingredient input = Ingredient.fromNetwork(buffer);
				return new EmbryoLabMorphRec(recipeId, s, inputMob, outputMob, input, true);
			}else{
				return new EmbryoLabMorphRec(recipeId, s, new ResourceLocation("none"), new ResourceLocation("none"), Ingredient.EMPTY, false);
			}
		}

		@Override
		public void toNetwork(PacketBuffer buffer, EmbryoLabMorphRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				buffer.writeUtf(recipe.inputMob.toString());
				buffer.writeUtf(recipe.outputMob.toString());
				recipe.ingr.toNetwork(buffer);
			}
		}
	}
}
