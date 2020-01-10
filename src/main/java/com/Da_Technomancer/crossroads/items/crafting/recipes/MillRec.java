package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class MillRec implements IRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;
	private final ItemStack[] outputs;

	/**
	 *
	 * @param location File ID
	 * @param name Recipe group
	 * @param input Input ingredient
	 * @param output Maximum of 3 ItemStacks
	 */
	public MillRec(ResourceLocation location, String name, Ingredient input, ItemStack... output){
		id = location;
		group = name;
		ingr = input;
		outputs = output;
	}

	/**
	 * This recipe has up to 3 outputs. This method should be used in place of getCraftingReuslt or getRecipeOutput
	 * @return An array of up to 3 created ItemStacks
	 */
	public ItemStack[] getOutputs(){
		return outputs;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return ingr.test(inv.getStackInSlot(0));
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv){
		return getRecipeOutput();
	}

	@Override
	public boolean canFit(int width, int height){
		return true;
	}

	@Override
	public ItemStack getRecipeOutput(){
		return outputs.length != 0 ? outputs[0].copy() : ItemStack.EMPTY;
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CrossroadsBlocks.millstone);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return RecipeHolder.MILL_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return RecipeHolder.MILL_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MillRec>{

		@Override
		public MillRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getString(json, "group", "");
			Ingredient ingredient = CraftingUtil.getIngredient(json, "input", true);

			//Output(s) can be specified in one of 2 ways:
			//As an array ("output") of objects, where each object contains a result and count,
			//As a single object ("output") containing result and count for one output

			ItemStack[] outputs;
			if(JSONUtils.isJsonArray(json, "output")){
				JsonArray array = JSONUtils.getJsonArray(json, "output");
				outputs = new ItemStack[Math.min(3, array.size())];
				for(int i = 0; i < outputs.length; i++){
					JsonObject outputObj = array.get(i).getAsJsonObject();
					outputs[i] = CraftingUtil.getItemStack(outputObj, "", true, false);
				}
			}else{
				outputs = new ItemStack[1];
				outputs[0] = CraftingUtil.getItemStack(json, "output", false, false);
			}

			return new MillRec(recipeId, s, ingredient, outputs);
		}

		@Nullable
		@Override
		public MillRec read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);
			Ingredient ingredient = Ingredient.read(buffer);
			int outputCount = buffer.readByte();
			ItemStack[] outputs = new ItemStack[outputCount];
			for(int i = 0; i < outputCount; i++){
				outputs[i] = buffer.readItemStack();
			}
			return new MillRec(recipeId, s, ingredient, outputs);
		}

		@Override
		public void write(PacketBuffer buffer, MillRec recipe){
			buffer.writeString(recipe.getGroup());
			recipe.getIngredients().get(0).write(buffer);
			buffer.writeByte(recipe.outputs.length);
			for(ItemStack stack : recipe.outputs){
				buffer.writeItemStack(stack);
			}
		}
	}
}
