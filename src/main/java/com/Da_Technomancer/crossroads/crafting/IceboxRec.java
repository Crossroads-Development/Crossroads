package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
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

public class IceboxRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient ingr;
	private final float cooling;
	private final boolean active;

	public IceboxRec(String name, Ingredient input, float cooling, boolean active){
		group = name;
		ingr = input;
		this.cooling = cooling;
		this.active = active;
	}

	public float getCooling(){
		return cooling;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr);
		return nonnulllist;
	}

	public Ingredient getIngredient(){
		return ingr;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public boolean matches(RecipeInput inv, Level worldIn){
		return active && ingr.test(inv.getItem(0));
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
		return new ItemStack(CRBlocks.icebox);
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.COOLING_SERIAL;
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.COOLING_TYPE;
	}

	public static class Serializer implements RecipeSerializer<IceboxRec>{
		//String name, Ingredient input, double cooling, boolean active
		private static final MapCodec<IceboxRec> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(IceboxRec::getGroup),
				Ingredient.CODEC.fieldOf("fuel").forGetter(IceboxRec::getIngredient),
				Codec.FLOAT.fieldOf("cooling").forGetter(IceboxRec::getCooling),
				Codec.BOOL.fieldOf("active").forGetter(IceboxRec::isEnabled)
		).apply(instance, IceboxRec::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, IceboxRec> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, IceboxRec::getGroup,
				Ingredient.CONTENTS_STREAM_CODEC, IceboxRec::getIngredient,
				ByteBufCodecs.FLOAT, IceboxRec::getCooling,
				ByteBufCodecs.BOOL, IceboxRec::isEnabled,
				IceboxRec::new
		);

		@Override
		public MapCodec<IceboxRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, IceboxRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
