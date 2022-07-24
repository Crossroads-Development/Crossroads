package com.Da_Technomancer.crossroads.api.alchemy;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.Da_Technomancer.crossroads.crafting.AlchemyRec;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.ReagentRec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ReagentManager{

	private static final HashMap<String, IReagent> REAGENTS = new HashMap<>(EnumReagents.values().length);
	private static final HashMap<IReagent, Predicate<Item>> REAGENT_FROM_ITEM = new HashMap<>();
	private static final ArrayList<String> REAGENT_WITH_FLUID = new ArrayList<>(6);

	@Nullable
	public static IReagent getReagent(String id){
		return REAGENTS.get(id);
	}

	public static Collection<IReagent> getRegisteredReags(){
		return REAGENTS.values();
	}

	/**
	 * Lists all reagents with an associated fluid
	 * @return A list of all reagents with an associated fluid
	 */
	public static List<String> getFluidReags(){
		return REAGENT_WITH_FLUID;
	}

	/**
	 * Finds a reagent which has a given item as the solid form, or null if none.
	 * Runs in linear time against list of all reagents with items
	 * @param item An item (or itemlike, ex. block) to query reagents for
	 * @return Any one reagent with the given item associated with the solid form, or null if none.
	 */
	public static IReagent findReagentForItem(ItemLike item){
		Item it = item.asItem();
		Optional<Map.Entry<IReagent, Predicate<Item>>> foundResult = REAGENT_FROM_ITEM.entrySet().parallelStream().filter(entry -> entry.getValue().test(it)).findAny();
		return foundResult.map(Map.Entry::getKey).orElse(null);
	}

	public static List<AlchemyRec> getReactions(Level world){
		return world.getRecipeManager().getAllRecipesFor(CRRecipes.ALCHEMY_TYPE);
	}

	public static void updateReagent(ReagentRec changedReag){
		//Adding and updating reagents is done through this method, which is called via the RecipeManager when the reagent recipes change
		//This method does not remove reagents
		REAGENTS.put(changedReag.getID(), changedReag);
		FluidIngredient fluid = changedReag.getFluid();
		if(fluid.isStrictlyEmpty()){
			REAGENT_WITH_FLUID.remove(changedReag.getID());
		}else if(!REAGENT_WITH_FLUID.contains(changedReag.getID())){
			REAGENT_WITH_FLUID.add(changedReag.getID());
		}

		REAGENT_FROM_ITEM.keySet().removeIf(key -> key.getID().equals(changedReag.getID()));
		REAGENT_FROM_ITEM.put(changedReag, item -> CraftingUtil.tagContains(changedReag.getSolid(), item));
	}

	private static void trimReagents(List<String> validReagents){
		//This method removes any reagent currently registered not listed in the passed list (by string reagent ids)
		//This is used to un-register any reagent that isn't in the new set (which may have changed due to changing data pack)

		HashSet<String> toRemove = new HashSet<>(REAGENTS.keySet());
		toRemove.removeAll(validReagents);

		if(!toRemove.isEmpty()){
			for(String removeKey : toRemove){
				REAGENTS.remove(removeKey);
				REAGENT_WITH_FLUID.remove(removeKey);
			}
		}
		//Now that everything is loaded and the tags are bound, we remove any reagent that no longer exists to speed things up in future
		REAGENT_FROM_ITEM.entrySet().removeIf(entry -> toRemove.contains(entry.getKey().getID()));
	}

	public static void updateFromServer(RecipeManager recManager){
		//Called on client side when new recipe sets are received from the server
		//This is necessary, because the client can have data packs change while running when connecting to different worlds or servers
		//This removes any no-longer registered reagents (the recipe manager handles overwriting and adding new ones)
		trimReagents(recManager.getAllRecipesFor(CRRecipes.REAGENT_TYPE).stream().map(ReagentRec::getID).collect(Collectors.toList()));
	}
}
