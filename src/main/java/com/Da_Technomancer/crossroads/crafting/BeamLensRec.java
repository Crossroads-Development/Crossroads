package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.beams.BeamMod;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class BeamLensRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient ingr;
	private final BeamMod output;
	private final EnumBeamAlignments transmuteAlignment;
	private final boolean transmuteVoid;
	private final ItemStack transmuteResult;

	private final boolean active;

	private BeamLensRec(){
		group = "";
		ingr = Ingredient.EMPTY;
		output = BeamMod.IDENTITY;
		transmuteAlignment = EnumBeamAlignments.NO_MATCH;
		transmuteVoid = false;
		transmuteResult = ItemStack.EMPTY;
		active = false;
	}

	private BeamLensRec(String name, Ingredient input, BeamMod output, ItemStack transmuteResult, EnumBeamAlignments transmuteAlignment, boolean transmuteVoid){
		group = name;
		ingr = input;
		this.output = output;
		this.active = true;
		this.transmuteResult = transmuteResult;
		this.transmuteAlignment = transmuteAlignment;
		this.transmuteVoid = transmuteVoid;
	}

	public BeamMod getOutput(){
		return output;
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		return active && ingr.test(input.getItem(0));
	}

	public boolean canApply(ItemStack stack){
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

	public Boolean isVoid(){
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

	public Ingredient getIngr(){
		return ingr;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.lensFrame);
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

		static{
			BeamLensRec disabledRec = new BeamLensRec();
			MapCodec<BeamLensRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(BeamLensRec::getGroup),
					Ingredient.CODEC.fieldOf("input").forGetter(BeamLensRec::getIngr),
					BeamMod.CODEC.optionalFieldOf("beam_modification", BeamMod.IDENTITY).forGetter(BeamLensRec::getOutput),
					CraftingUtil.itemStackMapCodec("transmute_result", false, ItemStack.EMPTY).forGetter(BeamLensRec::getResultItem),
					EnumBeamAlignments.CODEC.optionalFieldOf("transmute_alignment", EnumBeamAlignments.NO_MATCH).forGetter(BeamLensRec::getTransmuteAlignment),
					Codec.BOOL.optionalFieldOf("transmute_void", false).forGetter(BeamLensRec::isVoid)
			).apply(instance, BeamLensRec::new));
			codec = codec.validate((BeamLensRec lensRec) -> !lensRec.active || lensRec.transmuteResult.isEmpty() || lensRec.transmuteAlignment != EnumBeamAlignments.NO_MATCH ? DataResult.success(lensRec) : DataResult.error(() -> "Invalid beam lens recipe; must specify transmute_alignment if a transmute_result is specified"));
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);

			StreamCodec<RegistryFriendlyByteBuf, BeamLensRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, BeamLensRec::getGroup,
					Ingredient.CONTENTS_STREAM_CODEC, BeamLensRec::getIngr,
					BeamMod.STREAM_CODEC, BeamLensRec::getOutput,
					ItemStack.OPTIONAL_STREAM_CODEC, BeamLensRec::getResultItem,
					EnumBeamAlignments.STREAM_CODEC, BeamLensRec::getTransmuteAlignment,
					ByteBufCodecs.BOOL, BeamLensRec::isVoid,
					BeamLensRec::new
			);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static MapCodec<BeamLensRec> CODEC;
		public static StreamCodec<RegistryFriendlyByteBuf, BeamLensRec> STREAM_CODEC;

		@Override
		public MapCodec<BeamLensRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BeamLensRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
