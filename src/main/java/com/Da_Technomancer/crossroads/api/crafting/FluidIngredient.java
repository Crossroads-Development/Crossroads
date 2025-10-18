package com.Da_Technomancer.crossroads.api.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is a bare-bones fluid version of net.minecraft.item.crafting.Ingredient
 * Does not support NBT on fluids
 * Does support fluid tags
 * In the likely event that either vanilla or Forge adds a standard way to put fluids in recipe inputs in a way that supports tags, we will switch to that immediately
 */
public class FluidIngredient implements Predicate<FluidStack>{

	public static final FluidIngredient EMPTY = new FluidIngredient(List.of());

	private final List<IFluidList> keys;
	private boolean cacheValid = false;//Currently nothing invalidates the cache
	private final Collection<Fluid> matched = new HashSet<>();

	/**
	 * Everything in matched should be either a fluid tag, a fluid, an IFluidList, or a fluidstate
	 * @param matched Everything this ingredient should match
	 */
	public static FluidIngredient of(Object... matched){
		if(matched.length == 1 && matched[0].getClass().isArray()){
			//Because of the unusually vague parameters for the constructor, it's easy to accidentally pass an array of values as an array of the array (due to it being a varArgs)
			//This detects that case, and corrects it rather than throwing an error
			matched = (Object[]) matched[0];
		}

		ArrayList<IFluidList> keys = new ArrayList<>(matched.length);
		for(Object key : matched){
			switch(key){
				case IFluidList iFluidList -> keys.add(iFluidList);
				case TagKey<?> tagKey -> {
					try{
						TagKey<Fluid> tag = (TagKey<Fluid>) tagKey;
						keys.add(new TagList(tag));
					}catch(ClassCastException e){
						Crossroads.logger.error("An illegal tag type was added to a FluidIngredient. Report to mod author!", e);
						throw e;
					}
				}
				case Fluid fluid -> keys.add(new FluidList(List.of(fluid)));
				case FluidStack fluidStack -> keys.add(new FluidList(List.of(fluidStack.getFluid())));
				case null, default -> {
					ClassCastException e = new ClassCastException("Illegal type added to FluidIngredient; Type: " + key.getClass() + "; Value: " + key.toString());
					Crossroads.logger.error("An illegal value was added to a FluidIngredient. Report to mod author!", e);
					throw e;
				}
			}
		}
		return new FluidIngredient(keys);
	}

	private FluidIngredient(List<IFluidList> matched){
		this.keys = matched;
	}

	public Collection<Fluid> getMatchedFluids(){
		updateCache();
		return matched;
	}

	/**
	 * Checks if this was defined as an empty ingredient
	 * This does not load contained tags, making this method safe for lazy-loading
	 * Note that if this ingredient was defined as containing only tags which are empty, it will return false
	 * @return Whether this ingredient was defined as being totally empty
	 */
	public boolean isStrictlyEmpty(){
		return this == EMPTY || keys.isEmpty() || keys.stream().allMatch(IFluidList::isEmpty);
	}

	/**
	 * Creates a list of the item forms of every matched fluid
	 * @param size The size of the fluidstacks to return
	 * @return The fluids matched, with the passed size. Will contain no duplicates, may be empty
	 */
	public List<FluidStack> getMatchedFluidStacks(int size){
		return getMatchedFluids().parallelStream().unordered().map(fluid -> new FluidStack(fluid, size)).distinct().filter(s -> s.getFluid() != Fluids.EMPTY).collect(Collectors.toList());
	}

	private void updateCache(){
		if(!cacheValid){
			matched.clear();
			keys.forEach(key -> matched.addAll(key.getMatched()));
			cacheValid = true;
		}
	}

	@Override
	public boolean test(FluidStack fluidState){
		updateCache();
		if(fluidState == null){
			return false;
		}
		Fluid b = fluidState.getFluid();
		return matched.contains(b);
	}

	public static final Codec<FluidIngredient> CODEC = CraftingUtil.singleOrListCodec(IFluidList.CODEC, 1, Integer.MAX_VALUE).xmap(FluidIngredient::new, fluidIngredient -> fluidIngredient.keys);

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidIngredient> STREAM_CODEC = StreamCodec.of(new StreamEncoder<RegistryFriendlyByteBuf, FluidIngredient>(){
		@Override
		public void encode(RegistryFriendlyByteBuf buf, FluidIngredient ingr){
			ingr.updateCache();
			buf.writeVarInt(ingr.matched.size());//Write how many Fluids this matches
			for(Fluid b : ingr.matched){
				buf.writeResourceLocation(MiscUtil.getRegistryName(b, BuiltInRegistries.FLUID));//Write the registry name of every matched fluid.
			}
		}
	}, new StreamDecoder<RegistryFriendlyByteBuf, FluidIngredient>(){
		@Override
		public FluidIngredient decode(RegistryFriendlyByteBuf buf){
			int count = buf.readVarInt();
			if(count <= 0){
				return FluidIngredient.EMPTY;
			}
			List<Fluid> matched = new ArrayList<>(count);
			for(int i = 0; i < count; i++){
				matched.add(BuiltInRegistries.FLUID.get(buf.readResourceLocation()));
			}
			//Create a fluid ingredient with one large IFluidList that matches every fluid. Note this doesn't preserve Tag associations of the original definition
			return new FluidIngredient(List.of(new FluidList(matched)));
		}
	});

	private interface IFluidList{

		//Based on Ingredient.Value.MAP_CODEC
		static final MapCodec<FluidIngredient.IFluidList> MAP_CODEC = NeoForgeExtraCodecs.xor(FluidIngredient.FluidList.MAP_CODEC, FluidIngredient.TagList.MAP_CODEC)
				.xmap(either -> either.map(fluidList -> fluidList, tagList -> tagList), iFluidList -> {
					if(iFluidList instanceof FluidIngredient.TagList tagList){
						return Either.right(tagList);
					}else if(iFluidList instanceof FluidIngredient.FluidList fluidList){
						return Either.left(fluidList);
					}else{
						throw new UnsupportedOperationException("This is neither a fluid value nor a tag value.");
					}
				});
		static final Codec<FluidIngredient.IFluidList> CODEC = MAP_CODEC.codec();

		Collection<Fluid> getMatched();

		boolean isEmpty();

	}

	private static class TagList implements IFluidList{

		private static final MapCodec<TagList> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(tagList -> tagList.tag))
						.apply(instance, FluidIngredient.TagList::new)
		);

		private final TagKey<Fluid> tag;

		public TagList(TagKey<Fluid> matched){
			tag = matched;
			if(tag == null){
				throw new JsonParseException("No defined tag in FluidIngredient");
			}
		}

		@Override
		public Collection<Fluid> getMatched(){
			return CraftingUtil.getTagContents(tag);
		}

		@Override
		public boolean isEmpty(){
			return false;//We do not check the tag contents, to enable lazyloading
		}
	}

	private static class FluidList implements IFluidList{

		//This codec is a bit over-engineered. Technically, it allows the fluid tag in JSON to have a list of fluid IDs instead of just a single fluid ID, but this is only to allow Codec re-encoding of FluidList with multiple entries, which only occurs for a FluidIngredient which has been de-serialized by the StreamCodec
		private static final MapCodec<FluidList> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(CraftingUtil.singleOrListCodec(BuiltInRegistries.FLUID.byNameCodec(), 1, Integer.MAX_VALUE).fieldOf("fluid").forGetter(fluidList -> fluidList.fluids))
						.apply(instance, FluidIngredient.FluidList::new)
		);

		private final List<Fluid> fluids;

		public FluidList(List<Fluid> matched){
			fluids = matched;
			if(matched == null){
				throw new JsonParseException("No defined fluid in FluidIngredient");
			}
		}

		@Override
		public Collection<Fluid> getMatched(){
			return fluids;
		}

		@Override
		public boolean isEmpty(){
			return fluids.isEmpty();
		}
	}
}
