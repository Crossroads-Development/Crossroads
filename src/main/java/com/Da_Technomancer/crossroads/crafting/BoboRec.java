package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.items.CRItems;
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
import java.util.ArrayList;
import java.util.List;

public class BoboRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient[] ingr;
	private final ItemStack output;
	private final boolean active;

	public BoboRec(String name, List<Ingredient> input, ItemStack output, boolean active){
		group = name;
		ingr = input.toArray(new Ingredient[] {});
		this.output = output;
		this.active = active;
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		if(!isEnabled() || input.size() != 3){
			return false;
		}
		//Known issue: this will pass if one input meets 2+ ingredients, even if the third input is irrelevant
		//No default Crossroads recipes have this issue- it would be silly to add a recipe that does
		for(Ingredient ingredient : ingr){
			boolean pass = false;
			for(int i = 0; i < 3; i++){
				if(ingredient.test(input.getItem(i))){
					pass = true;
					break;
				}
			}
			if(!pass){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEnabled(){
		return active;
	}

	@Override
	public NonNullList<Ingredient> getIngredients(){
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingr[0]);
		nonnulllist.add(ingr[1]);
		nonnulllist.add(ingr[2]);
		return nonnulllist;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height){
		return true;
	}

	@Override
	public ItemStack getResultItem(){
		return output;
	}

	@Override
	public ItemStack getToastSymbol(){
		return new ItemStack(CRItems.boboRod);
	}

	@Override
	public String getGroup(){
		return group;
	}

	@Override
	public RecipeSerializer<?> getSerializer(){
		return CRRecipes.BOBO_SERIAL;
	}

	@Override
	public RecipeType<?> getType(){
		return CRRecipes.BOBO_TYPE;
	}

	public static class Serializer implements RecipeSerializer<BoboRec>{
		//ResourceLocation location, String name, List<Ingredient> input, ItemStack output, boolean active
		public static final MapCodec<BoboRec> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(BoboRec::getGroup),
				Codec.list(Ingredient.CODEC).fieldOf("input").forGetter(BoboRec::getIngredients), //TODO: change the file format to actually match this description.
				ItemStack.CODEC.fieldOf("output").forGetter(BoboRec::getResultItem),
				Codec.BOOL.optionalFieldOf("active", true).forGetter(BoboRec::isEnabled)
		).apply(instance, BoboRec::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, BoboRec> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, BoboRec::getGroup,
				ByteBufCodecs.collection(ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC), BoboRec::getIngredients,
				ItemStack.STREAM_CODEC, BoboRec::getResultItem,
				ByteBufCodecs.BOOL, BoboRec::isEnabled,
				BoboRec::new
		);

		@Override
		public MapCodec<BoboRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BoboRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
