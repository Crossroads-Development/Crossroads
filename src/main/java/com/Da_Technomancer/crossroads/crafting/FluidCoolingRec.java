package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.heat.FluidCoolingChamberTileEntity;
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
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidCoolingRec implements IOptionalRecipe<RecipeInput>{

	private final String group;

	private final FluidIngredient input;
	private final int inputQty;
	private final ItemStack created;
	private final float maxTemp;
	private final float addedHeat;
	private final boolean active;

	public FluidCoolingRec(String name, FluidIngredient input, int inputQty, ItemStack output, float maxTemp, float addedHeat, boolean active){
		group = name;
		this.input = input;
		this.inputQty = inputQty;
		this.created = output;
		this.maxTemp = maxTemp;
		this.addedHeat = addedHeat;
		this.active = active;
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
		return active && inv instanceof FluidCoolingChamberTileEntity && input.test(((FluidCoolingChamberTileEntity) inv).getFluid());
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
		// String name, FluidIngredient input, int inputQty, ItemStack output, float maxTemp, float addedHeat, boolean active
		private static final MapCodec<FluidCoolingRec> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(FluidCoolingRec::getGroup),
				FluidIngredient.CODEC.fieldOf("tag").forGetter(FluidCoolingRec::getInput),
				Codec.INT.fieldOf("fluid_amount").forGetter(FluidCoolingRec::getInputQty),
				ItemStack.CODEC.fieldOf("output").forGetter(FluidCoolingRec::getResultItem),
				Codec.FLOAT.fieldOf("max_temp").forGetter(FluidCoolingRec::getMaxTemp),
				Codec.FLOAT.fieldOf("temp_change").forGetter(FluidCoolingRec::getAddedHeat),
				Codec.BOOL.optionalFieldOf("active", true).forGetter(FluidCoolingRec::isEnabled)
		).apply(instance, FluidCoolingRec::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, FluidCoolingRec> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, FluidCoolingRec::getGroup,
				FluidIngredient.STREAM_CODEC, FluidCoolingRec::getInput,
				ByteBufCodecs.INT, FluidCoolingRec::getInputQty,
				ItemStack.STREAM_CODEC, FluidCoolingRec::getResultItem,
				ByteBufCodecs.FLOAT, FluidCoolingRec::getMaxTemp,
				ByteBufCodecs.FLOAT, FluidCoolingRec::getAddedHeat,
				ByteBufCodecs.BOOL, FluidCoolingRec::isEnabled,
				FluidCoolingRec::new
		);

		@Nonnull
		@Override
		public MapCodec<FluidCoolingRec> codec(){
			return CODEC;
		}

		@Nonnull
		@Override
		public StreamCodec<RegistryFriendlyByteBuf, FluidCoolingRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
