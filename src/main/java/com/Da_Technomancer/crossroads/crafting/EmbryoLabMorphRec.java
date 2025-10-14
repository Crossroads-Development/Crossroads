package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class EmbryoLabMorphRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final ResourceLocation inputMob;
	private final ResourceLocation outputMob;
	private final Ingredient ingr;
	private final boolean active;

	private EmbryoLabMorphRec(){
		group = "";
		inputMob = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "empty");
		outputMob = inputMob;
		ingr = Ingredient.EMPTY;
		active = false;
	}

	private EmbryoLabMorphRec(String group, ResourceLocation inputMob, ResourceLocation outputMob, Ingredient ingr){
		this.group = group;
		this.inputMob = inputMob;
		this.outputMob = outputMob;
		this.ingr = ingr;
		this.active = true;
	}

	public ResourceLocation getInputMob(){
		return inputMob;
	}

	public ResourceLocation getOutputMob(){
		return outputMob;
	}

	public Ingredient getIngr(){
		return ingr;
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		return true;//The entire condition of this recipe is based on block type and power, which can't be determined here
	}

	@Override
	public ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.embryoLab);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.EMBRYO_LAB_MORPH_SERIAL;
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
		return CRRecipes.EMBRYO_LAB_MORPH_TYPE;
	}

	public static class Serializer implements RecipeSerializer<EmbryoLabMorphRec>{

		static{
			MapCodec<EmbryoLabMorphRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(EmbryoLabMorphRec::getGroup),
					ResourceLocation.CODEC.fieldOf("input_mob").forGetter(EmbryoLabMorphRec::getInputMob),
					ResourceLocation.CODEC.fieldOf("output_mob").forGetter(EmbryoLabMorphRec::getOutputMob),
					CraftingUtil.itemIngredientMapCodec("input", false).forGetter(EmbryoLabMorphRec::getIngr)
			).apply(instance, EmbryoLabMorphRec::new));
			StreamCodec<RegistryFriendlyByteBuf, EmbryoLabMorphRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, EmbryoLabMorphRec::getGroup,
					ResourceLocation.STREAM_CODEC, EmbryoLabMorphRec::getInputMob,
					ResourceLocation.STREAM_CODEC, EmbryoLabMorphRec::getOutputMob,
					Ingredient.CONTENTS_STREAM_CODEC, EmbryoLabMorphRec::getIngr,
					EmbryoLabMorphRec::new
			);
			EmbryoLabMorphRec disabledRec = new EmbryoLabMorphRec();
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static final MapCodec<EmbryoLabMorphRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, EmbryoLabMorphRec> STREAM_CODEC;

		@Override
		public MapCodec<EmbryoLabMorphRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, EmbryoLabMorphRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
