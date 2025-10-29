package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.EnumPath;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Map;

public class DetailedCrafterRec extends ShapedRecipe implements IOptionalRecipe<CraftingInput>{

	private final EnumPath path;
	private final ItemStack result;
	private final boolean active;

	private DetailedCrafterRec(){
		super("", CraftingBookCategory.MISC, ShapedRecipePattern.of(Map.of('c', Ingredient.EMPTY), "c"), ItemStack.EMPTY, false);
		this.path = EnumPath.TECHNOMANCY;
		this.result = ItemStack.EMPTY;
		this.active = false;
	}

	private DetailedCrafterRec(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, EnumPath path){
		super(group, category, pattern, result, showNotification);
		this.result = result;
		this.path = path;
		this.active = true;
	}

	public EnumPath getPath(){
		return path;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRBlocks.detailedCrafter);
	}

	@Override
	public ItemStack getResultItem(){
		return result;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.DETAILED_SERIAL;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.DETAILED_TYPE;
	}

	@Override
	public boolean matches(CraftingInput inv, Level world){
		return active && super.matches(inv, world);
	}

	public static class Serializer implements RecipeSerializer<DetailedCrafterRec>{

		static{
			MapCodec<DetailedCrafterRec> codec = RecordCodecBuilder.mapCodec(
					p_340778_ -> p_340778_.group(
								Codec.STRING.optionalFieldOf("group", "").forGetter(DetailedCrafterRec::getGroup),
								CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(DetailedCrafterRec::category),
								ShapedRecipePattern.MAP_CODEC.forGetter(p_311733_ -> p_311733_.pattern),
								ItemStack.STRICT_CODEC.fieldOf("result").forGetter(DetailedCrafterRec::getResultItem),
								Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(DetailedCrafterRec::showNotification),
								StringRepresentable.fromEnum(EnumPath::values).fieldOf("path").forGetter(DetailedCrafterRec::getPath)
							).apply(p_340778_, DetailedCrafterRec::new));
			StreamCodec<RegistryFriendlyByteBuf, DetailedCrafterRec> streamCodec = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
			DetailedCrafterRec disabledRec = new DetailedCrafterRec();
			CODEC = IOptionalRecipe.codecWithDisable(codec, disabledRec);
			STREAM_CODEC = IOptionalRecipe.codecWithDisable(streamCodec, disabledRec);
		}

		private static final MapCodec<DetailedCrafterRec> CODEC;

		private static final StreamCodec<RegistryFriendlyByteBuf, DetailedCrafterRec> STREAM_CODEC;

		private static DetailedCrafterRec fromNetwork(RegistryFriendlyByteBuf buffer){
			String s = buffer.readUtf();
			CraftingBookCategory craftingbookcategory = buffer.readEnum(CraftingBookCategory.class);
			ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.STREAM_CODEC.decode(buffer);
			ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
			boolean flag = buffer.readBoolean();
			EnumPath path = EnumPath.fromIndex(buffer.readByte());
			return new DetailedCrafterRec(s, craftingbookcategory, shapedrecipepattern, itemstack, flag, path);
		}

		private static void toNetwork(RegistryFriendlyByteBuf buffer, DetailedCrafterRec recipe){
			ShapedRecipe.Serializer.STREAM_CODEC.encode(buffer, recipe);
			buffer.writeByte(recipe.path.getIndex());
		}

		@Override
		public MapCodec<DetailedCrafterRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, DetailedCrafterRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
