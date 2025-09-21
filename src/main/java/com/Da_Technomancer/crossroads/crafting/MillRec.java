package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MillRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient ingr;
	private final List<ItemStack> outputs;
	private final boolean active;

	/**
	 *
	 * @param name Recipe group
	 * @param input Input ingredient
	 * @param active Whether this recipe is active
	 * @param output Maximum of 3 ItemStacks
	 */
	public MillRec(String name, Ingredient input, boolean active, List<ItemStack> output){
		group = name;
		ingr = input;
		this.active = active;
		outputs = output;
	}

	/**
	 * This recipe has up to 3 outputs. This method should be used in place of getCraftingReuslt or getRecipeOutput
	 * @return An array of up to 3 created ItemStacks
	 */
	public List<ItemStack> getOutputs(){
		return outputs;
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
	public boolean matches(RecipeInput input, Level worldIn){
		return ingr.test(input.getItem(0));
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return outputs.size() != 0 ? outputs.get(0).copy() : ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.millstone);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.MILL_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.MILL_TYPE;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public static class Serializer implements RecipeSerializer<MillRec>{
		//String name, Ingredient input, boolean active, ItemStack[] output
		public static final MapCodec<MillRec> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(MillRec::getGroup),
				Ingredient.CODEC.fieldOf("input").forGetter(MillRec::getIngredient),
				Codec.BOOL.optionalFieldOf("active", true).forGetter(MillRec::isEnabled),
				Codec.list(ItemStack.CODEC).fieldOf("output").forGetter(MillRec::getOutputs)
		).apply(instance, MillRec::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, MillRec> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, MillRec::getGroup,
				Ingredient.CONTENTS_STREAM_CODEC, MillRec::getIngredient,
				ByteBufCodecs.BOOL, MillRec::isEnabled,
				ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), MillRec::getOutputs,
				MillRec::new
		);

		@Override
		public MapCodec<MillRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MillRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
