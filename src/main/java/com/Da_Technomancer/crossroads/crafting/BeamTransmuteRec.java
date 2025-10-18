package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.crafting.BlockIngredient;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BeamTransmuteRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final EnumBeamAlignments align;
	private final boolean voi;
	private final BlockIngredient ingr;
	private final Block output;
	private final int power;
	private final boolean active;

	private BeamTransmuteRec(){
		group = "";
		align = EnumBeamAlignments.NO_MATCH;
		voi = false;
		ingr = BlockIngredient.EMPTY;
		output = Blocks.AIR;
		power = 0;
		active = false;
	}

	private BeamTransmuteRec(String name, EnumBeamAlignments align, boolean voi, BlockIngredient input, Block output, int power){
		group = name;
		ingr = input;
		this.align = align;
		this.voi = voi;
		this.output = output;
		this.power = power;
		this.active = true;
	}

	public EnumBeamAlignments getAlign(){
		return align;
	}

	public boolean isVoid(){
		return voi;
	}

	public BlockIngredient getIngr(){
		return isEnabled() ? ingr : BlockIngredient.EMPTY;
	}

	public Block getOutput(){
		return output;
	}

	public int getPower(){
		return Math.max(power, 0);
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		return active;//The entire condition of this recipe is based on block type and power, which can't be determined here
	}

	/**
	 * The condition actually used to determine if this recipe applies
	 * @param alignment The Alignment of the beam
	 * @param voidBeam Whether the beam has any void
	 * @param beamPower The power of the beam. Must meet or exceed the recipe power
	 * @param state The blockstate being hit
	 * @return Whether this recipe can apply
	 */
	public boolean canApply(EnumBeamAlignments alignment, boolean voidBeam, int beamPower, BlockState state){
		return alignment == align && voidBeam == voi && beamPower >= power && ingr.test(state);
	}

	@Override
	public ItemStack getResultItem(){
		return new ItemStack(getOutput());
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.beamReflector);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.BEAM_TRANSMUTE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.BEAM_TRANSMUTE_TYPE;
	}

	public static class Serializer implements RecipeSerializer<BeamTransmuteRec>{

		static{
			BeamTransmuteRec disabledRec = new BeamTransmuteRec();
			MapCodec<BeamTransmuteRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(BeamTransmuteRec::getGroup),
					EnumBeamAlignments.CODEC.optionalFieldOf("alignment", EnumBeamAlignments.NO_MATCH).forGetter(BeamTransmuteRec::getAlign),
					Codec.BOOL.optionalFieldOf("void", false).forGetter(BeamTransmuteRec::isVoid),
					CraftingUtil.blockIngredientMapCodec("input", false).forGetter(BeamTransmuteRec::getIngr),
					BuiltInRegistries.BLOCK.byNameCodec().fieldOf("output").forGetter(BeamTransmuteRec::getOutput),
					ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("power", 1).forGetter(BeamTransmuteRec::getPower)
			).apply(instance, BeamTransmuteRec::new));
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);

			StreamCodec<RegistryFriendlyByteBuf, BeamTransmuteRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, BeamTransmuteRec::getGroup,
					EnumBeamAlignments.STREAM_CODEC, BeamTransmuteRec::getAlign,
					ByteBufCodecs.BOOL, BeamTransmuteRec::isVoid,
					BlockIngredient.STREAM_CODEC, BeamTransmuteRec::getIngr,
					ByteBufCodecs.fromCodecWithRegistries(BuiltInRegistries.BLOCK.byNameCodec()), BeamTransmuteRec::getOutput,
					ByteBufCodecs.VAR_INT, BeamTransmuteRec::getPower,
					BeamTransmuteRec::new
			);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static MapCodec<BeamTransmuteRec> CODEC;
		public static StreamCodec<RegistryFriendlyByteBuf, BeamTransmuteRec> STREAM_CODEC;

		@Override
		public MapCodec<BeamTransmuteRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BeamTransmuteRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
