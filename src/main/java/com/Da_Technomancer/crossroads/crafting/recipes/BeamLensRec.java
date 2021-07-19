package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.API.beams.BeamMod;
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

public class BeamLensRec implements IOptionalRecipe<IInventory>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;
	private final BeamMod output;
	private final ItemStack transform;

	private final boolean active;
	public BeamLensRec(ResourceLocation location, String name, Ingredient input, ItemStack transform, BeamMod output, boolean active){
		id = location;
		group = name;
		ingr = input;
		this.output = output;
		this.active = active;
		this.transform = transform;
	}

	public BeamMod getOutput(){
		return output;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn){
		return active && ingr.test(inv.getItem(0));
	}

	public boolean canApply(ItemStack stack) {
		return active && ingr.test(stack);
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
		return transform.copy();
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
		return new ItemStack(CRBlocks.lensFrame);
	}

	@Override
	public ResourceLocation getId(){
		return id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer(){
		return CRRecipes.BEAM_LENS_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public IRecipeType<?> getType(){
		return CRRecipes.BEAM_LENS_TYPE;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BeamLensRec>{

		@Override
		public BeamLensRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = JSONUtils.getAsString(json, "group", "");
			Ingredient ingredient = Ingredient.EMPTY;
			ItemStack output = ItemStack.EMPTY;
			boolean active = CraftingUtil.isActiveJSON(json);
			if(active){
				ingredient = CraftingUtil.getIngredient(json, "input", true);

				if(JSONUtils.isValidNode(json, "output")) {
					output = CraftingUtil.getItemStack(json, "output", false, true);
				} else {
					output = CraftingUtil.getIngredient(json, "input", true).getItems()[0];
				}

				//Output specified as 4 float tags, all of which are optional and default to zero
				float[] mults = new float[4];
				if(JSONUtils.isValidNode(json, "energy")){
					mults[0] = JSONUtils.getAsFloat(json, "energy");
				}
				if(JSONUtils.isValidNode(json, "potential")){
					mults[1] = JSONUtils.getAsFloat(json, "potential");
				}
				if(JSONUtils.isValidNode(json, "stability")){
					mults[2] = JSONUtils.getAsFloat(json, "stability");
				}
				if(JSONUtils.isValidNode(json, "void")){
					mults[3] = JSONUtils.getAsFloat(json, "void");
				}

				return new BeamLensRec(recipeId, s, ingredient, output, new BeamMod(mults), true);
			}
			return new BeamLensRec(recipeId, s, ingredient, output, BeamMod.EMPTY, false);
		}

		@Nullable
		@Override
		public BeamLensRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			boolean active = buffer.readBoolean();

			if(active){
				Ingredient ingredient = Ingredient.fromNetwork(buffer);
				ItemStack stack = buffer.readItem();

				float[] units = new float[4];
				for(int i = 0; i < 4; i++){
					units[i] = buffer.readFloat();
				}
				return new BeamLensRec(recipeId, s, ingredient, stack, new BeamMod(units), true);
			}else{
				return new BeamLensRec(recipeId, s, Ingredient.EMPTY, ItemStack.EMPTY, BeamMod.EMPTY, false);
			}
		}

		@Override
		public void toNetwork(PacketBuffer buffer, BeamLensRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.ingr.toNetwork(buffer);
				buffer.writeItem(recipe.transform);
				buffer.writeFloat(recipe.output.getEnergyMult());
				buffer.writeFloat(recipe.output.getPotentialMult());
				buffer.writeFloat(recipe.output.getStabilityMult());
				buffer.writeFloat(recipe.output.getVoidMult());
			}
		}
	}
}
