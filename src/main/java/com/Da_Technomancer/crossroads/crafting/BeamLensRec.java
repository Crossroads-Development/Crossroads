package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.beams.BeamMod;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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

import javax.annotation.Nullable;
import java.util.Locale;

public class BeamLensRec implements IOptionalRecipe<Container>{

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;
	private final BeamMod output;
	private final EnumBeamAlignments transmuteAlignment;
	private final boolean transmuteVoid;
	private final ItemStack transmuteResult;

	private final boolean active;
	public BeamLensRec(ResourceLocation location, String name, Ingredient input, BeamMod output, ItemStack transmuteResult, EnumBeamAlignments transmuteAlignment, boolean transmuteVoid, boolean active){
		id = location;
		group = name;
		ingr = input;
		this.output = output;
		this.active = active;
		this.transmuteResult = transmuteResult;
		this.transmuteVoid = transmuteVoid;
		this.transmuteAlignment = transmuteAlignment;
	}

	public BeamMod getOutput(){
		return output;
	}

	@Override
	public boolean matches(Container inv, Level worldIn){
		return active && ingr.test(inv.getItem(0));
	}

	public boolean canApply(ItemStack stack) {
		return active && ingr.test(stack);
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
		return transmuteResult;
	}

	public EnumBeamAlignments getTransmuteAlignment(){
		return transmuteAlignment;
	}

	public boolean isVoid(){
		return transmuteVoid;
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

	public Ingredient getIngr() {
		return ingr;
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
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.BEAM_LENS_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.BEAM_LENS_TYPE;
	}

	public static class Serializer implements RecipeSerializer<BeamLensRec>{

		@Override
		public BeamLensRec fromJson(ResourceLocation recipeId, JsonObject json){
			//Normal specification of recipe group and ingredient
			String s = GsonHelper.getAsString(json, "group", "");
			Ingredient ingredient = Ingredient.EMPTY;
			ItemStack transform = ItemStack.EMPTY;
			EnumBeamAlignments alignment = EnumBeamAlignments.NO_MATCH;
			boolean transformVoid = false;

			boolean active = CraftingUtil.isActiveJSON(json);
			if(active){
				ingredient = CraftingUtil.getIngredient(json, "input", true);

				if(GsonHelper.isValidNode(json, "transmute_result")){
					transform = CraftingUtil.getItemStack(json, "transmute_result", false, true);
				}

				if(GsonHelper.isValidNode(json, "transmute_alignment")){
					try{
						String alignName = GsonHelper.getAsString(json, "transmute_alignment");
						alignment = EnumBeamAlignments.valueOf(alignName.toUpperCase(Locale.US));
					}catch(NullPointerException e){
						throw new JsonParseException("Non-existent alignment specified");
					}
				}
				transformVoid = GsonHelper.getAsBoolean(json, "transmute_void", false);

				//Output specified as 5 float tags, all of which are optional
				//Filters default to 1, while void conversion defaults to 0
				//This means beams pass right through by default
				float[] mults = new float[5];
				mults[0] = GsonHelper.getAsFloat(json, "energy", 1);
				mults[1] = GsonHelper.getAsFloat(json, "potential", 1);
				mults[2] = GsonHelper.getAsFloat(json, "stability", 1);
				mults[3] = GsonHelper.getAsFloat(json, "void", 1);
				mults[4] = GsonHelper.getAsFloat(json, "void_convert", 0);

				return new BeamLensRec(recipeId, s, ingredient, new BeamMod(mults), transform, alignment, transformVoid, true);
			}
			return new BeamLensRec(recipeId, s, ingredient, BeamMod.IDENTITY, transform, alignment, transformVoid, false);
		}

		@Nullable
		@Override
		public BeamLensRec fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
			String s = buffer.readUtf(Short.MAX_VALUE);
			boolean active = buffer.readBoolean();

			if(active){
				Ingredient ingredient = Ingredient.fromNetwork(buffer);
				ItemStack stack = buffer.readItem();
				EnumBeamAlignments alignment = EnumBeamAlignments.valueOf(buffer.readUtf());
				boolean transformVoid = buffer.readBoolean();

				float[] units = new float[5];
				for(int i = 0; i < units.length; i++){
					units[i] = buffer.readFloat();
				}
				return new BeamLensRec(recipeId, s, ingredient, new BeamMod(units), stack, alignment, transformVoid, true);
			}else{
				return new BeamLensRec(recipeId, s, Ingredient.EMPTY, BeamMod.IDENTITY, ItemStack.EMPTY, EnumBeamAlignments.NO_MATCH, false, false);
			}
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, BeamLensRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.ingr.toNetwork(buffer);
				buffer.writeItem(recipe.transmuteResult);
				buffer.writeUtf(recipe.transmuteAlignment.name());
				buffer.writeBoolean(recipe.transmuteVoid);
				buffer.writeFloat(recipe.output.getEnergyMult());
				buffer.writeFloat(recipe.output.getPotentialMult());
				buffer.writeFloat(recipe.output.getStabilityMult());
				buffer.writeFloat(recipe.output.getVoidMult());
				buffer.writeFloat(recipe.output.getVoidConvert());
			}
		}
	}
}
