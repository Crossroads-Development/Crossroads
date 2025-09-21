package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public class EmbryoLabMorphRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final ResourceLocation inputMob;
	private final ResourceLocation outputMob;
	private final Ingredient ingr;
	private final boolean active;

	public EmbryoLabMorphRec(String group, ResourceLocation inputMob, ResourceLocation outputMob, Ingredient ingr, boolean active){
		this.group = group;
		this.inputMob = inputMob;
		this.outputMob = outputMob;
		this.ingr = ingr;
		this.active = active;
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
		//String group, ResourceLocation inputMob, ResourceLocation outputMob, Ingredient ingr, boolean active
		public static final MapCodec<EmbryoLabMorphRec> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(EmbryoLabMorphRec::getGroup),
				ResourceLocation.CODEC.fieldOf("input_mob").forGetter(EmbryoLabMorphRec::getInputMob),
				ResourceLocation.CODEC.fieldOf("output_mob").forGetter(EmbryoLabMorphRec::getOutputMob),
				Ingredient.CODEC.fieldOf("input").forGetter(EmbryoLabMorphRec::getIngr),
				Codec.BOOL.optionalFieldOf("active", true).forGetter(EmbryoLabMorphRec::isEnabled)
		).apply(instance, EmbryoLabMorphRec::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, EmbryoLabMorphRec> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, EmbryoLabMorphRec::getGroup,
				ResourceLocation.STREAM_CODEC, EmbryoLabMorphRec::getInputMob,
				ResourceLocation.STREAM_CODEC, EmbryoLabMorphRec::getOutputMob,
				Ingredient.CONTENTS_STREAM_CODEC, EmbryoLabMorphRec::getIngr,
				ByteBufCodecs.BOOL, EmbryoLabMorphRec::isEnabled,
				EmbryoLabMorphRec::new
		);

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
