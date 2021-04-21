package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
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

public class BeamExtractRec implements IOptionalRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;
	private final BeamUnit output;

	private final int duration;

	private final boolean active;
	public BeamExtractRec(ResourceLocation location, String name, Ingredient input, BeamUnit output, int duration, boolean active){
		id = location;
		group = name;
		ingr = input;
		this.output = output;
		this.duration = duration;
		this.active = active;
	}

	public BeamUnit getOutput(){
		return output;
	}

	public int getDuration(){
		return duration;
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
	public boolean isEnabled(){
		return active;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}

	public boolean isActive(){
		return active;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr);
		return nonnulllist;
	}

	@Override
	public ItemStack getToastSymbol(){
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
		public BeamExtractRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getAsString(json, "group", "");
			Ingredient ingredient = Ingredient.EMPTY;
			boolean active = CraftingUtil.isActiveJSON(json);
			if(active){
				ingredient = CraftingUtil.getIngredient(json, "input", true);

				//Output specified as 4 integer tags, all of which are optional and default to zero
				int[] units = new int[4];
				if(JSONUtils.isValidNode(json, "energy")){
					units[0] = JSONUtils.getAsInt(json, "energy");
				}
				if(JSONUtils.isValidNode(json, "potential")){
					units[1] = JSONUtils.getAsInt(json, "potential");
				}
				if(JSONUtils.isValidNode(json, "stability")){
					units[2] = JSONUtils.getAsInt(json, "stability");
				}
				if(JSONUtils.isValidNode(json, "void")){
					units[3] = JSONUtils.getAsInt(json, "void");
				}

				//Optional duration tag, for number of cycles an output lasts
				int dur = JSONUtils.getAsInt(json, "duration", 1);
				return new BeamExtractRec(recipeId, s, ingredient, new BeamUnit(units), dur, true);
			}
			return new BeamExtractRec(recipeId, s, ingredient, BeamUnit.EMPTY, 0, false);
		}

		@Nullable
		@Override
		public BeamExtractRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			boolean active = buffer.readBoolean();

			if(active){
				Ingredient ingredient = Ingredient.fromNetwork(buffer);
				int duration = buffer.readVarInt();

				int[] units = new int[4];
				for(int i = 0; i < 4; i++){
					units[i] = buffer.readVarInt();
				}
				return new BeamExtractRec(recipeId, s, ingredient, new BeamUnit(units), duration, true);
			}else{
				return new BeamExtractRec(recipeId, s, Ingredient.EMPTY, BeamUnit.EMPTY, 0, false);
			}
		}

		@Override
		public void toNetwork(PacketBuffer buffer, BeamExtractRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			recipe.ingr.toNetwork(buffer);
			buffer.writeVarInt(recipe.duration);
			buffer.writeVarInt(recipe.output.getEnergy());
			buffer.writeVarInt(recipe.output.getPotential());
			buffer.writeVarInt(recipe.output.getStability());
			buffer.writeVarInt(recipe.output.getVoid());
		}
	}
}
