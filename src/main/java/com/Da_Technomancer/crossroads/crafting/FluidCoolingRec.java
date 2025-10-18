package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.heat.FluidCoolingChamberTileEntity;
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

import javax.annotation.Nonnull;

public class FluidCoolingRec implements IOptionalRecipe<RecipeInput>{

	private final String group;

	private final FluidIngredient input;
	private final int inputQty;
	private final ItemStack created;
	private final float maxTemp;
	private final float addedHeat;
	private final boolean active;

	private FluidCoolingRec(){
		group = "";
		inputQty = 0;
		created = ItemStack.EMPTY;
		maxTemp = 0;
		addedHeat = 0;
		active = false;
		input = FluidIngredient.EMPTY;
	}

	private FluidCoolingRec(String name, FluidIngredient input, int inputQty, ItemStack output, float maxTemp, float addedHeat){
		group = name;
		this.input = input;
		this.inputQty = inputQty;
		this.created = output;
		this.maxTemp = maxTemp;
		this.addedHeat = addedHeat;
		active = true;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public FluidIngredient getInput(){
		return input;
	}

	public int getInputQty(){
		return inputQty;
	}

	public boolean inputMatches(FluidStack available){
		return input != null && input.test(available) && inputQty <= available.getAmount();
	}

	public ItemStack getCreated(){
		return created;
	}

	public float getMaxTemp(){
		return maxTemp;
	}

	public float getAddedHeat(){
		return addedHeat;
	}

	@Override
	public boolean matches(RecipeInput inv, Level worldIn){
		return active && inv instanceof FluidCoolingChamberTileEntity fcc && input.test(fcc.getFluid());
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return created;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.fluidCoolingChamber);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.FLUID_COOLING_SERIAL;
	}

	@Nonnull
	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.FLUID_COOLING_TYPE;
	}

	public static class Serializer implements RecipeSerializer<FluidCoolingRec>{

		static{
			MapCodec<FluidCoolingRec> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
					CraftingUtil.recipeGroupFieldCodec().forGetter(FluidCoolingRec::getGroup),
					CraftingUtil.fluidIngredientMapCodec("input", true).forGetter(FluidCoolingRec::getInput),
					ExtraCodecs.POSITIVE_INT.fieldOf("fluid_amount").forGetter(FluidCoolingRec::getInputQty),
					CraftingUtil.itemStackMapCodec("output", true).forGetter(FluidCoolingRec::getResultItem),
					Codec.FLOAT.fieldOf("max_temp").forGetter(FluidCoolingRec::getMaxTemp),
					Codec.FLOAT.optionalFieldOf("temp_change", 0F).forGetter(FluidCoolingRec::getAddedHeat)
			).apply(instance, FluidCoolingRec::new));

			StreamCodec<RegistryFriendlyByteBuf, FluidCoolingRec> streamCodec = StreamCodec.composite(
					ByteBufCodecs.STRING_UTF8, FluidCoolingRec::getGroup,
					FluidIngredient.STREAM_CODEC, FluidCoolingRec::getInput,
					ByteBufCodecs.INT, FluidCoolingRec::getInputQty,
					ItemStack.STREAM_CODEC, FluidCoolingRec::getResultItem,
					ByteBufCodecs.FLOAT, FluidCoolingRec::getMaxTemp,
					ByteBufCodecs.FLOAT, FluidCoolingRec::getAddedHeat,
					FluidCoolingRec::new
			);
			FluidCoolingRec disabledRec = new FluidCoolingRec();
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		public static final MapCodec<FluidCoolingRec> CODEC;
		public static final StreamCodec<RegistryFriendlyByteBuf, FluidCoolingRec> STREAM_CODEC;

		@Override
		public MapCodec<FluidCoolingRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, FluidCoolingRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
