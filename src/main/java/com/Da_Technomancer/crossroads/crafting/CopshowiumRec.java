package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.technomancy.CopshowiumCreationChamberTileEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class CopshowiumRec implements IOptionalRecipe<RecipeInput>{

	private final ResourceLocation id;
	private final String group;

	private final FluidIngredient input;
	private final float mult;
	private final boolean flux;
	private final boolean active;

	public CopshowiumRec(ResourceLocation location, String name, FluidIngredient input, float expandFactor, boolean flux, boolean active){
		id = location;
		group = name;
		this.input = input;
		this.mult = expandFactor;
		this.flux = flux;
		this.active = active;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	public FluidIngredient getInput(){
		return input;
	}

	public float getMult(){
		return mult;
	}

	public boolean isFlux(){
		return flux;
	}

	@Override
	public boolean matches(RecipeInput recipeInput, Level worldIn){
		return active && recipeInput instanceof CopshowiumCreationChamberTileEntity && input.test(((CopshowiumCreationChamberTileEntity) recipeInput).getInputFluid());
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.copshowiumCreationChamber);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.COPSHOWIUM_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.COPSHOWIUM_TYPE;
	}

	public static class Serializer implements RecipeSerializer<CopshowiumRec>{

		//ResourceLocation location, String name, FluidIngredient input, float expandFactor, boolean flux, boolean active
		private static final MapCodec<CopshowiumRec> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("id").forGetter((CopshowiumRec copshowiumRec) -> copshowiumRec.id),
				Codec.STRING.optionalFieldOf("group", "").forGetter(CopshowiumRec::getGroup),
				FluidIngredient.CODEC.fieldOf("input").forGetter(CopshowiumRec::getInput),
				Codec.FLOAT.optionalFieldOf("mult", 1f).forGetter(CopshowiumRec::getMult),
				Codec.BOOL.optionalFieldOf("entropy", false).forGetter(CopshowiumRec::isFlux),
				Codec.BOOL.optionalFieldOf("active", true).forGetter(CopshowiumRec::isEnabled)
		).apply(instance, CopshowiumRec::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, CopshowiumRec> STREAM_CODEC = StreamCodec.composite(
				ResourceLocation.STREAM_CODEC, (CopshowiumRec copshowiumRec) -> copshowiumRec.id,
				ByteBufCodecs.STRING_UTF8, CopshowiumRec::getGroup,
				FluidIngredient.STREAM_CODEC, CopshowiumRec::getInput,
				ByteBufCodecs.FLOAT, CopshowiumRec::getMult,
				ByteBufCodecs.BOOL, CopshowiumRec::isFlux,
				ByteBufCodecs.BOOL, CopshowiumRec::isEnabled,
				CopshowiumRec::new
		);

		@Override
		public MapCodec<CopshowiumRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CopshowiumRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
