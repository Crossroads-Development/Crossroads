package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.witchcraft.HydroponicsTroughTileEntity;
import com.mojang.serialization.Codec;
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

import java.util.ArrayList;
import java.util.List;

public class HydroponicTroughRec implements HydroponicsTroughTileEntity.HydroponicsRecGeneric, IOptionalRecipe<RecipeInput>{

	/*
	 * Plants that implement certain superclasses are handled automatically, and don't need JSON recipes made for them
	 * But JSON recipes will override the auto-generated ones where applicable
	 */

	private final String group;
	private final Ingredient ingr;
	private final List<ItemStack> outputs;
	private final boolean needsLight;
	private final int growthStages;
	private final boolean active;

	private HydroponicTroughRec(){
		group = "";
		ingr = Ingredient.EMPTY;
		outputs = List.of();
		needsLight = false;
		growthStages = 1;
		active = false;
	}

	/**
	 *
	 * @param name Recipe group
	 * @param input Input ingredient
	 * @param output Maximum of 3 ItemStacks
	 */
	private HydroponicTroughRec(String name, Ingredient input, List<ItemStack> output, boolean needsLight, int growthStages){
		group = name;
		ingr = input;
		outputs = output;
		this.needsLight = needsLight;
		this.growthStages = growthStages;
		this.active = true;
	}

	public List<ItemStack> getOutputs(){
		return outputs;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr);
		return nonnulllist;
	}

	@Override
	public Ingredient getIngredient(){
		return ingr;
	}

	@Override
	public int getGrowthStages(){
		return growthStages;
	}

	@Override
	public boolean needsLight(){
		return needsLight;
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
		return !outputs.isEmpty() ? outputs.get(0) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.hydroponicsTrough);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.HYDROPONICS_TROUGH_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.HYDROPONIC_TROUGH_TYPE;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public static class Serializer implements RecipeSerializer<HydroponicTroughRec>{

		static{
			MapCodec<HydroponicTroughRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(HydroponicTroughRec::getGroup),
					CraftingUtil.itemIngredientMapCodec("input", false).forGetter(HydroponicTroughRec::getIngredient),
					CraftingUtil.singleOrListCodec(ItemStack.CODEC, 1, 4).fieldOf("output").forGetter(HydroponicTroughRec::getOutputs),
					Codec.BOOL.optionalFieldOf("needs_light", true).forGetter(HydroponicTroughRec::needsLight),
					Codec.INT.optionalFieldOf("growth_stages", 1).forGetter(HydroponicTroughRec::getGrowthStages)
			).apply(instance, HydroponicTroughRec::new));

			StreamCodec<RegistryFriendlyByteBuf, HydroponicTroughRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, HydroponicTroughRec::getGroup,
					Ingredient.CONTENTS_STREAM_CODEC, HydroponicTroughRec::getIngredient,
					ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HydroponicTroughRec::getOutputs,
					ByteBufCodecs.BOOL, HydroponicTroughRec::needsLight,
					ByteBufCodecs.VAR_INT, HydroponicTroughRec::getGrowthStages,
					HydroponicTroughRec::new
			);
			HydroponicTroughRec disabledRec = new HydroponicTroughRec();
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static final MapCodec<HydroponicTroughRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, HydroponicTroughRec> STREAM_CODEC;

		@Override
		public MapCodec<HydroponicTroughRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, HydroponicTroughRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
