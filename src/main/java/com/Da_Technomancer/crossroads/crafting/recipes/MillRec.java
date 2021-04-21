package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.google.gson.JsonArray;
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

public class MillRec implements IOptionalRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;
	private final ItemStack[] outputs;
	private final boolean active;

	/**
	 *
	 * @param location File ID
	 * @param name Recipe group
	 * @param input Input ingredient
	 * @param active Whether this recipe is active
	 * @param output Maximum of 3 ItemStacks
	 */
	public MillRec(ResourceLocation location, String name, Ingredient input, boolean active, ItemStack... output){
		id = location;
		group = name;
		ingr = input;
		this.active = active;
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
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr);
		return nonnulllist;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return ingr.test(inv.getItem(0));
	}

	@Override
	public ItemStack assemble(IInventory inv){
		return getResultItem().copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return outputs.length != 0 ? outputs[0].copy() : ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.millstone);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.MILL_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.MILL_TYPE;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MillRec>{

		@Override
		public MillRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getAsString(json, "group", "");
			if(!CraftingUtil.isActiveJSON(json)){
				return new MillRec(recipeId, s, Ingredient.EMPTY, false);
			}

			Ingredient ingredient = CraftingUtil.getIngredient(json, "input", true);

			//Output(s) can be specified in one of 2 ways:
			//As an array ("output") of objects, where each object contains a result and count,
			//As a single object ("output") containing result and count for one output

			ItemStack[] outputs;
			if(JSONUtils.isArrayNode(json, "output")){
				JsonArray array = JSONUtils.getAsJsonArray(json, "output");
				outputs = new ItemStack[Math.min(3, array.size())];
				for(int i = 0; i < outputs.length; i++){
					JsonObject outputObj = array.get(i).getAsJsonObject();
					outputs[i] = CraftingUtil.getItemStack(outputObj, "", true, false);
				}
			}else{
				outputs = new ItemStack[1];
				outputs[0] = CraftingUtil.getItemStack(json, "output", false, false);
			}

			return new MillRec(recipeId, s, ingredient, true, outputs);
		}

		@Nullable
		@Override
		public MillRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			if(!buffer.readBoolean()){
				return new MillRec(recipeId, s, Ingredient.EMPTY, false);
			}
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			int outputCount = buffer.readByte();
			ItemStack[] outputs = new ItemStack[outputCount];
			for(int i = 0; i < outputCount; i++){
				outputs[i] = buffer.readItem();
			}
			return new MillRec(recipeId, s, ingredient, true, outputs);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, MillRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			recipe.ingr.toNetwork(buffer);
			buffer.writeByte(recipe.outputs.length);
			for(ItemStack stack : recipe.outputs){
				buffer.writeItem(stack);
			}
		}
	}
}
