package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class BeamExtractRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient ingr;
	private final BeamUnit output;

	private final int duration;

	private final boolean active;

	private BeamExtractRec(){
		group = "";
		ingr = Ingredient.EMPTY;
		this.output = BeamUnit.EMPTY;
		this.duration = 0;
		this.active = false;
	}

	private BeamExtractRec(String name, Ingredient input, BeamUnit output, int duration){
		group = name;
		ingr = input;
		this.output = output;
		this.duration = duration;
		this.active = true;
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

		static{
			MapCodec<BeamExtractRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(BeamExtractRec::getGroup),
					CraftingUtil.itemIngredientMapCodec("input", false).forGetter(BeamExtractRec::getIngredient),
					BeamUnit.CODEC.fieldOf("output").forGetter(BeamExtractRec::getOutput),
					ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("duration", 1).forGetter(BeamExtractRec::getDuration)
			).apply(instance, BeamExtractRec::new));

			StreamCodec<RegistryFriendlyByteBuf, BeamExtractRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, BeamExtractRec::getGroup,
					Ingredient.CONTENTS_STREAM_CODEC, BeamExtractRec::getIngredient,
					BeamUnit.STREAM_CODEC, BeamExtractRec::getOutput,
					ByteBufCodecs.INT, BeamExtractRec::getDuration,
					BeamExtractRec::new
			);
			BeamExtractRec disabledRec = new BeamExtractRec();
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static MapCodec<BeamExtractRec> CODEC;
		public static StreamCodec<RegistryFriendlyByteBuf, BeamExtractRec> STREAM_CODEC;

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
