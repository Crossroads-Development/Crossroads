package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
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

public class BeamExtractRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient ingr;
	private final BeamUnit output;

	private final int duration;

	private final boolean active;

	public BeamExtractRec(String name, Ingredient input, BeamUnit output, int duration, boolean active){
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
	public boolean matches(RecipeInput inv, Level worldIn){
		return active && ingr.test(inv.getItem(0));
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

	public Ingredient getIngredient(){
		return ingr;
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
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.BEAM_EXTRACT_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.BEAM_EXTRACT_TYPE;
	}

	public static class Serializer implements RecipeSerializer<BeamExtractRec>{

		//ResourceLocation location, String name, Ingredient input, BeamUnit output, int duration, boolean active
		public static MapCodec<BeamExtractRec> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.fieldOf("name").forGetter(BeamExtractRec::getGroup),
				Ingredient.CODEC.fieldOf("input").forGetter(BeamExtractRec::getIngredient),
				BeamUnit.CODEC.fieldOf("output").forGetter(BeamExtractRec::getOutput),
				Codec.INT.fieldOf("duration").forGetter(BeamExtractRec::getDuration),
				Codec.BOOL.fieldOf("active").forGetter(BeamExtractRec::isActive)
		).apply(instance, BeamExtractRec::new));

		public static StreamCodec<RegistryFriendlyByteBuf, BeamExtractRec> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, BeamExtractRec::getGroup,
				Ingredient.CONTENTS_STREAM_CODEC, BeamExtractRec::getIngredient,
				BeamUnit.STREAM_CODEC, BeamExtractRec::getOutput,
				ByteBufCodecs.INT, BeamExtractRec::getDuration,
				ByteBufCodecs.BOOL, BeamExtractRec::isActive,
				BeamExtractRec::new
		);

		@Override
		public MapCodec<BeamExtractRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BeamExtractRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
