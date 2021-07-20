package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.API.beams.BeamMod;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import java.util.Locale;

public class BeamLensRec implements IOptionalRecipe<IInventory>{

	// Blank recipe for when containing an item that doesn't have a recipe
	public static final BeamLensRec BLANK = new BeamLensRec(
			new ResourceLocation(""),
			"",
			Ingredient.EMPTY,
			BeamMod.EMPTY,
			ItemStack.EMPTY,
			EnumBeamAlignments.NO_MATCH,
			false,
			false
	);

	private final ResourceLocation id;
	private final String group;
	private final Ingredient ingr;
	private final BeamMod output;
	private final EnumBeamAlignments transformAlignment;
	private final boolean transformVoid;
	private final ItemStack transform;

	private final boolean active;
	public BeamLensRec(ResourceLocation location, String name, Ingredient input, BeamMod output, ItemStack transform, EnumBeamAlignments transformAlignment, boolean transformVoid, boolean active){
		id = location;
		group = name;
		ingr = input;
		this.output = output;
		this.active = active;
		this.transform = transform;
		this.transformVoid = transformVoid;
		this.transformAlignment = transformAlignment;
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

	public EnumBeamAlignments getTransformAlignment(){
		return transformAlignment;
	}

	public boolean isVoid(){
		return transformVoid;
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
			ItemStack transform = ItemStack.EMPTY;
			EnumBeamAlignments alignment = EnumBeamAlignments.NO_MATCH;
			boolean transformVoid = false;

			boolean active = CraftingUtil.isActiveJSON(json);
			if(active){
				ingredient = CraftingUtil.getIngredient(json, "input", true);

				if(JSONUtils.isValidNode(json, "transform")){
					transform = CraftingUtil.getItemStack(json, "transform", false, true);
				}

				if(JSONUtils.isValidNode(json, "transform_alignment")){
					try{
						String alignName = JSONUtils.getAsString(json, "transform_alignment");
						alignment = EnumBeamAlignments.valueOf(alignName.toUpperCase(Locale.US));
					}catch(NullPointerException e){
						throw new JsonParseException("Non-existent alignment specified");
					}
				}
				transformVoid = JSONUtils.getAsBoolean(json, "transform_void", false);

				//Output specified as 5 float tags, all of which are optional
				//Filters default to 1, while void conversion defaults to 0
				//This means beams pass right through by default
				float[] mults = new float[5];
				mults[0] = JSONUtils.getAsFloat(json, "energy", 1);
				mults[1] = JSONUtils.getAsFloat(json, "potential", 1);
				mults[2] = JSONUtils.getAsFloat(json, "stability", 1);
				mults[3] = JSONUtils.getAsFloat(json, "void", 1);
				mults[4] = JSONUtils.getAsFloat(json, "void_convert", 0);

				return new BeamLensRec(recipeId, s, ingredient, new BeamMod(mults), transform, alignment, transformVoid, true);
			}
			return new BeamLensRec(recipeId, s, ingredient, BeamMod.EMPTY, transform, alignment, transformVoid, false);
		}

		@Nullable
		@Override
		public BeamLensRec fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
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
				return new BeamLensRec(recipeId, s, Ingredient.EMPTY, BeamMod.EMPTY, ItemStack.EMPTY, EnumBeamAlignments.NO_MATCH, false, false);
			}
		}

		@Override
		public void toNetwork(PacketBuffer buffer, BeamLensRec recipe){
			buffer.writeUtf(recipe.getGroup());
			buffer.writeBoolean(recipe.active);
			if(recipe.active){
				recipe.ingr.toNetwork(buffer);
				buffer.writeItem(recipe.transform);
				buffer.writeUtf(recipe.transformAlignment.name());
				buffer.writeBoolean(recipe.transformVoid);
				buffer.writeFloat(recipe.output.getEnergyMult());
				buffer.writeFloat(recipe.output.getPotentialMult());
				buffer.writeFloat(recipe.output.getStabilityMult());
				buffer.writeFloat(recipe.output.getVoidMult());
				buffer.writeFloat(recipe.output.getVoidConvert());
			}
		}
	}
}
