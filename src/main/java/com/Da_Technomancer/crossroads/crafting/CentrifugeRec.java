package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.fluid.WaterCentrifugeTileEntity;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CentrifugeRec implements IOptionalRecipe<RecipeInput>{

	private static final Random RAND = new Random();

	private final String group;

	private final FluidStack input;

	private final FluidStack fluidOutput;
	private final List<WeightOutput> outputs;
	private final boolean active;

	private final int totalWeight;//Cached

	private CentrifugeRec(){
		active = false;
		group = "";
		input = FluidStack.EMPTY;
		fluidOutput = FluidStack.EMPTY;
		outputs = List.of();
		totalWeight = 0;
	}

	private CentrifugeRec(String name, FluidStack input, FluidStack fluidOutput, List<WeightOutput> outputs){
		group = name;
		this.input = input;
		this.fluidOutput = fluidOutput;
		this.outputs = outputs;
		this.active = true;

		int weight = 0;
		for(WeightOutput out : outputs){
			weight += out.weight;
		}
		totalWeight = weight;
	}

	@Override
	public boolean matches(RecipeInput inv, Level worldIn){
		FluidStack teInput;
		return active && inv instanceof WaterCentrifugeTileEntity centrifuge && BlockUtil.sameFluid(teInput = centrifuge.getInputFluid(), input) && teInput.getAmount() >= input.getAmount();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	/**
	 * Gets a list of every produced item. Used for JEI support
	 * @return Every produced item
	 */
	public List<ItemStack> getOutputList(){
		List<ItemStack> out = new ArrayList<>(outputs.size());
		for(WeightOutput output : outputs){
			out.add(output.item);
		}
		return out;
	}

	@Override
	public ItemStack getResultItem(){
		if(totalWeight == 0){
			return ItemStack.EMPTY;
		}
		int selected = RAND.nextInt(totalWeight);
		for(WeightOutput out : outputs){
			selected -= out.weight;
			if(selected <= 0){
				return out.item;
			}
		}
		return ItemStack.EMPTY;
	}

	private List<WeightOutput> getOutputWeights(){
		return outputs;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.waterCentrifuge);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.CENTRIFUGE_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.CENTRIFUGE_TYPE;
	}

	public FluidStack getInput(){
		return input.copy();
	}

	public FluidStack getFluidOutput(){
		return fluidOutput.copy();
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	private static record WeightOutput(ItemStack item, int weight){

		private static final Codec<WeightOutput> CODEC = RecordCodecBuilder.create(instance -> instance.group(
						CraftingUtil.itemStackMapCodec("", true).forGetter(WeightOutput::item),
						ExtraCodecs.POSITIVE_INT.optionalFieldOf("weight", 1).forGetter(WeightOutput::weight))
				.apply(instance, WeightOutput::new));
		private static final StreamCodec<RegistryFriendlyByteBuf, WeightOutput> STREAM_CODEC = StreamCodec.composite(
				ItemStack.STREAM_CODEC, WeightOutput::item,
				ByteBufCodecs.VAR_INT, WeightOutput::weight,
				WeightOutput::new);

	}

	public static class Serializer implements RecipeSerializer<CentrifugeRec>{

		static{
			CentrifugeRec disabledRec = new CentrifugeRec();
			MapCodec<CentrifugeRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(CentrifugeRec::getGroup),
					CraftingUtil.fluidStackMapCodec("input", false).forGetter(CentrifugeRec::getInput),
					CraftingUtil.fluidStackMapCodec("output_fluid", false).forGetter(CentrifugeRec::getFluidOutput),
					CraftingUtil.singleOrListCodec(WeightOutput.CODEC, 1, Integer.MAX_VALUE).fieldOf("output").forGetter(CentrifugeRec::getOutputWeights)
			).apply(instance, CentrifugeRec::new));
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			StreamCodec<RegistryFriendlyByteBuf, CentrifugeRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, CentrifugeRec::getGroup,
					FluidStack.STREAM_CODEC, CentrifugeRec::getInput,
					FluidStack.STREAM_CODEC, CentrifugeRec::getFluidOutput,
					ByteBufCodecs.collection(ArrayList::new, WeightOutput.STREAM_CODEC), CentrifugeRec::getOutputWeights,
					CentrifugeRec::new
			);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static final MapCodec<CentrifugeRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugeRec> STREAM_CODEC;

		@Override
		public MapCodec<CentrifugeRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CentrifugeRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
