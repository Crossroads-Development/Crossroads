package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.PredicateMap;
import com.Da_Technomancer.crossroads.crafting.recipes.AlchemyRec;
import com.Da_Technomancer.crossroads.crafting.recipes.FluidIngredient;
import com.Da_Technomancer.crossroads.crafting.recipes.ReagentRec;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.tags.ITag;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class ReagentManager{

	private static final HashMap<String, IReagent> REAGENTS = new HashMap<>(EnumReagents.values().length);
	private static final PredicateMap<Item, IReagent> ITEM_TO_REAGENT = new PredicateMap<>();
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

	public static PredicateMap<Item, IReagent> getItemToReagent(){
		return ITEM_TO_REAGENT;
	}

	public static List<AlchemyRec> getReactions(World world){
		return world.getRecipeManager().getAllRecipesFor(CRRecipes.ALCHEMY_TYPE);
	}

	public static void updateReagent(ReagentRec changedReag){
		//Adding and updating reagents is done through this method, which is called via the RecipeManager when the reagent recipes change
		//This method does not remove reagents
		REAGENTS.put(changedReag.getID(), changedReag);
		FluidIngredient fluid = changedReag.getFluid();
		if(fluid.getMatchedFluids().isEmpty()){
			REAGENT_WITH_FLUID.remove(changedReag.getID());
		}else if(!REAGENT_WITH_FLUID.contains(changedReag.getID())){
			REAGENT_WITH_FLUID.add(changedReag.getID());
		}

		ITEM_TO_REAGENT.values().removeIf(val -> val.getID().equals(changedReag.getID()));
		ITEM_TO_REAGENT.put(changedReag.getSolid()::contains, changedReag);
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
		//Now that everything is loaded and the tags are bound, we remove any reagent that no longer exists, and remove any mapping with an empty tag to speed things up in future
		ITEM_TO_REAGENT.entrySet().removeIf(entry -> toRemove.contains(entry.getValue().getID()) || entry.getKey() instanceof ITag && ((ITag<?>) entry.getKey()).getValues().isEmpty());
	}

	public static void updateFromServer(RecipeManager recManager){
		//Called on client side when new recipe sets are received from the server
		//This is necessary, because the client can have data packs change while running when connecting to different worlds or servers
		//This removes any no-longer registered reagents (the recipe manager handles overwriting and adding new ones)
		trimReagents(recManager.getAllRecipesFor(CRRecipes.REAGENT_TYPE).stream().map(ReagentRec::getID).collect(Collectors.toList()));
	}
}
