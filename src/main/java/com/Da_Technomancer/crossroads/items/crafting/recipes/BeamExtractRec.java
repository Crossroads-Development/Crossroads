package com.Da_Technomancer.crossroads.items.crafting.recipes;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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

public class BeamExtractRec implements IRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;
	private final BeamUnit output;

	public BeamExtractRec(ResourceLocation location, String name, Ingredient input, BeamUnit output){
		id = location;
		group = name;
		ingr = input;
		this.output = output;
	}

	public BeamUnit getOutput(){
		return output;
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
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr);
		return nonnulllist;
	}

	@Override
	public ItemStack getIcon(){
		return new ItemStack(CRBlocks.beamExtractor);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.BEAM_EXTRACT_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.BEAM_EXTRACT_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BeamExtractRec>{

		@Override
		public BeamExtractRec read(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getString(json, "group", "");
			Ingredient ingredient = CraftingUtil.getIngredient(json, "input", true);

			//Output specified as 4 integer tags, all of which are optional and default to zero
			int[] units = new int[4];
			if(JSONUtils.hasField(json, "energy")){
				units[0] = JSONUtils.getInt(json, "energy");
			}
			if(JSONUtils.hasField(json, "potential")){
				units[1] = JSONUtils.getInt(json, "potential");
			}
			if(JSONUtils.hasField(json, "stability")){
				units[2] = JSONUtils.getInt(json, "stability");
			}
			if(JSONUtils.hasField(json, "void")){
				units[3] = JSONUtils.getInt(json, "void");
			}
			return new BeamExtractRec(recipeId, s, ingredient, new BeamUnit(units));
		}

		@Nullable
		@Override
		public BeamExtractRec read(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readString(Short.MAX_VALUE);
			Ingredient ingredient = Ingredient.read(buffer);
			int[] units = new int[4];
			for(int i = 0; i < 4; i++){
				units[i] = buffer.readInt();
			}
			return new BeamExtractRec(recipeId, s, ingredient, new BeamUnit(units));
		}

		@Override
		public void write(PacketBuffer buffer, BeamExtractRec recipe){
			buffer.writeString(recipe.getGroup());
			recipe.ingr.write(buffer);
			buffer.writeInt(recipe.output.getEnergy());
			buffer.writeInt(recipe.output.getPotential());
			buffer.writeInt(recipe.output.getStability());
			buffer.writeInt(recipe.output.getVoid());
		}
	}
}
