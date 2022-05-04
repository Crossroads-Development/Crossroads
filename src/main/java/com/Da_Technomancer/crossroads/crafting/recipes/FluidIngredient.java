package com.Da_Technomancer.crossroads.crafting.recipes;

import com.Da_Technomancer.crossroads.Crossroads;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is a bare-bones fluid version of net.minecraft.item.crafting.Ingredient
 * Does not support NBT on fluids
 * Does support fluid tags
 * In the likely event that either vanilla or Forge adds a standard way to put fluids in recipe inputs in a way that supports tags, we will switch to that immediately
 */
public class FluidIngredient implements Predicate<FluidStack>{

	public static final FluidIngredient EMPTY = new FluidIngredient();

	private final List<IFluidList> keys;
	private boolean cacheValid = false;//Currently nothing invalidates the cache
	private final Collection<Fluid> matched = new HashSet<>();

	/**
	 * Everything in matched should be either a fluid tag, a fluid, an IFluidList, or a fluidstate
	 * @param matched Everything this ingredient should match
	 */
	public FluidIngredient(Object... matched){
		if(matched.length == 1 && matched[0].getClass().isArray()){
			//Because of the unusually vague parameters for the constructor, it's easy to accidentally pass an array of values as an array of the array (due to it being a varArgs)
			//This detects that case, and corrects it rather than throwing an error
			matched = (Object[]) matched[0];
		}

		keys = new ArrayList<>(matched.length);
		for(Object key : matched){
			if(key instanceof IFluidList){
				keys.add((IFluidList) key);
			}else if(key instanceof Tag){
				try{
					Tag<Fluid> tag = (Tag<Fluid>) key;
					keys.add(new TagList(tag));
				}catch(ClassCastException e){
					Crossroads.logger.error("An illegal tag type was added to a FluidIngredient. Report to mod author!", e);
					throw e;
				}
			}else if(key instanceof Fluid){
				keys.add(new SingleList((Fluid) key));
			}else if(key instanceof FluidStack){
				keys.add(new SingleList(((FluidStack) key).getFluid()));
			}else{
				JsonParseException e = new JsonParseException("Illegal type added to FluidIngredient; Type: " + key.getClass() + "; Value: " + key.toString());
				Crossroads.logger.error("An illegal value was added to a FluidIngredient. Report to mod author!", e);
				throw e;
			}
		}
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

	public void writeToBuffer(FriendlyByteBuf buf){
		updateCache();
		buf.writeVarInt(matched.size());//Write how many Fluids this matches
		for(Fluid b : matched){
			buf.writeResourceLocation(b.getRegistryName());//Write the registry name of every matched fluid.
		}
	}

	public static FluidIngredient readFromBuffer(FriendlyByteBuf buf){
		int count = buf.readVarInt();
		if(count <= 0){
			return FluidIngredient.EMPTY;
		}
		Fluid[] matched = new Fluid[count];
		for(int i = 0; i < count; i++){
			matched[i] = ForgeRegistries.FLUIDS.getValue(buf.readResourceLocation());
		}
		//Create a fluid ingredient with one large IFluidList that matches every fluid. Note this doesn't preserve Tag associations of the original definition
		return new FluidIngredient(new FluidList(matched));
	}

	public static FluidIngredient readFromJSON(JsonElement o){
		if(o.isJsonArray()){
			JsonArray array = (JsonArray) o;
			IFluidList[] lists = new IFluidList[array.size()];
			for(int i = 0; i < array.size(); i++){
				JsonElement el = array.get(i);
				if(el.isJsonObject()){
					lists[i] = readIngr((JsonObject) el);
				}else{
					throw new JsonParseException("Value in JSON array instead of JSON object");
				}
			}
			return new FluidIngredient((Object[]) lists);
		}else if(o.isJsonObject()){
			return new FluidIngredient(readIngr((JsonObject) o));
		}else{
			throw new JsonParseException("Value passed to FluidIngredient");
		}
	}

	private static IFluidList readIngr(JsonObject o){
		if(o.has("tag")){
			return new TagList(FluidTags.createOptional(new ResourceLocation(GsonHelper.getAsString(o, "tag"))));
		}else if(o.has("fluid")){
			return new SingleList(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(GsonHelper.getAsString(o, "fluid"))));
		}else{
			throw new JsonParseException("No value defined in FluidIngredient");
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

	private interface IFluidList{

		Collection<Fluid> getMatched();

		boolean isEmpty();

	}

	private static class SingleList implements IFluidList{

		private final List<Fluid> matchL;

		public SingleList(Fluid matched){
			matchL = new ArrayList<>(1);
			matchL.add(matched);
			if(matched == null){
				throw new JsonParseException("No defined fluid in FluidIngredient");
			}
		}

		@Override
		public Collection<Fluid> getMatched(){
			return matchL;
		}

		@Override
		public boolean isEmpty(){
			return false;
		}
	}

	private static class TagList implements IFluidList{

		private final Tag<Fluid> tag;

		public TagList(Tag<Fluid> matched){
			tag = matched;
			if(tag == null){
				throw new JsonParseException("No defined tag in FluidIngredient");
			}
		}

		@Override
		public Collection<Fluid> getMatched(){
			return tag.getValues();
		}

		@Override
		public boolean isEmpty(){
			return false;//We do not check the tag contents, to enable lazyloading
		}
	}

	private static class FluidList implements IFluidList{

		private final Collection<Fluid> fluids;

		public FluidList(Fluid... matched){
			fluids = Arrays.asList(matched);
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
