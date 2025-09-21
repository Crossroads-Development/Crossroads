package com.Da_Technomancer.crossroads.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.IOptionalRecipe;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.witchcraft.IncubatorTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
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
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class IncubatorRec implements IOptionalRecipe<RecipeInput>{

	private final String group;
	private final Ingredient mainInput;
	private final Ingredient secondaryInput;
	private final ItemStack product;
	private final boolean datacopy;
	private final boolean active;

	public IncubatorRec(String group, Ingredient mainInput, Ingredient secondaryInput, ItemStack product, boolean datacopy, boolean active){
		this.group = group;
		this.mainInput = mainInput;
		this.secondaryInput = secondaryInput;
		this.product = product;
		this.datacopy = datacopy;
		this.active = active;
	}

	public Ingredient getMainInput(){
		return mainInput;
	}

	public Ingredient getSecondaryInput(){
		return secondaryInput;
	}

	@Override
	public boolean matches(RecipeInput input, Level worldIn){
		return active && input instanceof IncubatorTileEntity incubator && mainInput.test(incubator.getItem(0)) && secondaryInput.test(incubator.getItem(1));
	}

	/**
	 * TODO: what the hell is this? And should it be using RecipeInput instead of Container now
	 * Use this instead of getResultItem
	 * It is safe to modify the returned itemstack
	 * @param inv Container with the ingredient item in slot 0
	 * @param worldIn World
	 * @return The created itemstack
	 */
	public ItemStack getCreatedItem(Container inv, Level worldIn){
		ItemStack created = getResultItem().copy();
		if(datacopy){
			try{
				CRItems.geneticSpawnEgg.withEntityTypeData(created, CRItems.embryo.getEntityTypeData(inv.getItem(0)));
			}catch(Exception e){
				Crossroads.logger.error("Invalid item types for datacopy in incubator recipe", e); // TODO: figure out something other than ID that'll identify this recipe
			}
			return created;
		}
		if(created.getItem() instanceof IPerishable perishable){
			IPerishable.getAndInitSpoilTime(created, worldIn);//Set the spoil time if perishable
		}
		return created;
	}

	@Override
	public ItemStack getResultItem(){
		return product;//Note: lacks NBT data for datacopy true; use getCreatedItem instead
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
		return CRRecipes.INCUBATOR_SERIAL;
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
		return CRRecipes.INCUBATOR_TYPE;
	}

	public static class Serializer implements RecipeSerializer<IncubatorRec>{
		//String group, Ingredient mainInput, Ingredient secondaryInput, ItemStack product, boolean datacopy, boolean active
		private static final MapCodec<IncubatorRec> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(IncubatorRec::getGroup),
				Ingredient.CODEC.fieldOf("main_input").forGetter(IncubatorRec::getMainInput),
				Ingredient.CODEC.fieldOf("secondary_input").forGetter(IncubatorRec::getSecondaryInput),
				ItemStack.CODEC.fieldOf("output").forGetter(IncubatorRec::getResultItem),
				Codec.BOOL.optionalFieldOf("datacopy", false).forGetter((IncubatorRec incubatorRec) -> incubatorRec.datacopy),
				Codec.BOOL.optionalFieldOf("active", true).forGetter(IncubatorRec::isEnabled)
		).apply(instance, IncubatorRec::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, IncubatorRec> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8, IncubatorRec::getGroup,
				Ingredient.CONTENTS_STREAM_CODEC, IncubatorRec::getMainInput,
				Ingredient.CONTENTS_STREAM_CODEC, IncubatorRec::getSecondaryInput,
				ItemStack.STREAM_CODEC, IncubatorRec::getResultItem,
				ByteBufCodecs.BOOL, (IncubatorRec incubatorRec) -> incubatorRec.datacopy,
				ByteBufCodecs.BOOL, IncubatorRec::isEnabled,
				IncubatorRec::new
		);

		@Override
		public MapCodec<IncubatorRec> codec(){
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, IncubatorRec> streamCodec(){
			return STREAM_CODEC;
		}
	}
}
