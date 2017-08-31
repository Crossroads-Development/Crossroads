package com.Da_Technomancer.crossroads.API.alchemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.item.Item;

public final class AlchemyCraftingManager{
	
	static{
		
		//TODO
	}
	
	private static final List<Predicate<IReactionChamber>> BASE_REACTIONS = new ArrayList<Predicate<IReactionChamber>>();//TODO set size
	
	/**
	 * Note that the contained predicated have the side effect of performing the reaction. 
	 */
	public static final List<Predicate<IReactionChamber>> REACTIONS = new ArrayList<Predicate<IReactionChamber>>();//TODO set size

	protected static final BiMap<Item, IReagentType> BASE_ITEM_TO_REAGENT = HashBiMap.create();//TODO set size
	
	public static final BiMap<Item, IReagentType> ITEM_TO_REAGENT = HashBiMap.create();//TODO set size
	
	private static final List<IReagentType> BASE_REAGENTS = new ArrayList<IReagentType>();//TODO set size
	
	public static final List<IReagentType> REAGENTS = new ArrayList<IReagentType>();//TODO set size
	
	public static void performReaction(IReactionChamber chamber, int passes){
		for(int pass = 0; pass < passes; passes++){
			boolean operated = false;
			for(Predicate<IReactionChamber> react : REACTIONS){
				if(react.test(chamber)){
					operated = true;
					break;
				}
			}
			if(!operated){
				break;
			}
		}
	}
	
	private static final int CUSTOM_REAGENT_COUNT = 32;
	
	public static void setup(long seed){
		Random rand = new Random(seed);
		
		REACTIONS.clear();
		REACTIONS.addAll(BASE_REACTIONS);
		
		ITEM_TO_REAGENT.clear();
		ITEM_TO_REAGENT.putAll(BASE_ITEM_TO_REAGENT);
		
		REAGENTS.clear();
		REAGENTS.addAll(BASE_REAGENTS);
		
		for(int i = 0; i < CUSTOM_REAGENT_COUNT; i++){
			int gen_minor = rand.nextInt();
			int gen_middle = rand.nextInt();
			int gen_major = rand.nextInt();
			//TODO
		}
	}
}
