package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public final class AlchemyCraftingManager{

	private static final List<Predicate<IReactionChamber>> BASE_REACTIONS = new ArrayList<Predicate<IReactionChamber>>();//TODO set size

	/**
	 * Note that the contained predicates have the side effect of performing the reaction. 
	 */
	public static final List<Predicate<IReactionChamber>> REACTIONS = new ArrayList<Predicate<IReactionChamber>>();//TODO set size

	protected static final BiMap<Item, IReagentType> BASE_ITEM_TO_REAGENT = HashBiMap.create();//TODO set size

	public static final BiMap<Item, IReagentType> ITEM_TO_REAGENT = HashBiMap.create();//TODO set size

	public static final int RESERVED_REAGENT_COUNT = 32;

	public static final int DYNAMIC_REAGENT_COUNT = 32;

	public static final IReagentType[] REAGENTS = new IReagentType[RESERVED_REAGENT_COUNT + DYNAMIC_REAGENT_COUNT];

	//A large number of colors are defined here so that a new color instance won't be initialized every time getColor is called. 
	private static final Color PHELOSTIGEN_COLOR = new Color(255, 200, 0, 150);
	private static final Color CLEAR_COLOR = new Color(255, 255, 255, 0);
	private static final Color TRANSLUCENT_WHITE_COLOR = new Color(255, 255, 255, 200);

	static{
		REAGENTS[0] = new SimpleReagentType("phelostigen", -275D, -274D, 0, (MatterPhase phase) -> PHELOSTIGEN_COLOR, null, 1, true, 0, null, null, 2, null);//TODO effect
		REAGENTS[1] = new SimpleReagentType("aether", -275D, -274D, 1, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, null, null, 1, null);
		REAGENTS[2] = new SimpleReagentType("adamant", Short.MAX_VALUE - 1, Short.MAX_VALUE, 2, (MatterPhase phase) -> Color.GRAY, null, 100, true, 0, null, SolventType.AQUA_REGIA, 0, null);//TODO item
		REAGENTS[3] = new SimpleReagentType("sulfur", 115D, 445D, 3, (MatterPhase phase) -> phase == MatterPhase.GAS ? CLEAR_COLOR : phase == MatterPhase.LIQUID ? Color.RED : Color.YELLOW, null, 100, true, 0, null, SolventType.NON_POLAR, 0, null);//TODO item
		REAGENTS[4] = new SimpleReagentType("water", 0D, 100D, 4, (MatterPhase phase) -> CLEAR_COLOR, Item.getItemFromBlock(Blocks.ICE), 1000, true, 0, SolventType.POLAR, null, 0, null);
		REAGENTS[5] = new SimpleReagentType("hydrogen_nitrate", -40D, 80D, 5, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, null, SolventType.POLAR, 0, null);//Salt that forms nitric acid, AKA aqua fortis, in water. TODO acid effect
		REAGENTS[6] = new SimpleReagentType("sodium_chloride", 800D, 1400D, 6, (MatterPhase phase) -> phase == MatterPhase.LIQUID ? Color.ORANGE : Color.WHITE, ModItems.dustSalt, 100, true, 0, null, SolventType.POLAR, 0, null);//AKA table salt TODO kill plants effect
		REAGENTS[7] = new SimpleReagentType("vanadium_5_oxide", 690D, 1750D, 7, (MatterPhase phase) -> Color.YELLOW, null, 100, true, 0, null, SolventType.POLAR, 0, null);//Vanadium (V) oxide. TODO item. This should decompose at the specified boiling point, but there isn't any real point to adding that. 
		REAGENTS[8] = new SimpleReagentType("sulfur_dioxide", -72D, -10D, 8, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, SolventType.POLAR, SolventType.POLAR, 0, null);
		REAGENTS[9] = new SimpleReagentType("sulfur_trioxide", 20D, 40D, 9, (MatterPhase phase) -> phase == MatterPhase.SOLID ? TRANSLUCENT_WHITE_COLOR : CLEAR_COLOR, null, 1, true, 0, null, SolventType.POLAR, 0, null);
		REAGENTS[10] = new SimpleReagentType("hydrogen_sulfate", 10D, 340D, 10, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, null, SolventType.POLAR, 0, null);//Salt that forms sulfuric acid, AKA Oil of Vitriol, in water. TODO acid effect. 
		REAGENTS[11] = new SimpleReagentType("aqua_regia", -40D, 100D, 11, (MatterPhase phase) -> Color.ORANGE, null, 1, true, 0, SolventType.AQUA_REGIA, null, 0, null);//Shouldn't really be its own substance (actually a mixture of nitric and hydrochloric acid), but the code is greatly simplified by making it a separate substance. TODO acid effect and bedrock dissolving effect. 
		REAGENTS[12] = new SimpleReagentType("murcury_2_sulfide", 580D, Short.MAX_VALUE, 12, (MatterPhase phase) -> Color.RED, Items.REDSTONE, 100, true, 0, null, null, 0, null);//Mercury (II) sulfide, AKA cinnabar. 
		REAGENTS[13] = new SimpleReagentType("murcury", -40D, 560D, 13, (MatterPhase phase) -> Color.LIGHT_GRAY, null, 1, true, 0, null, null, 0, null);//AKA quicksilver
		REAGENTS[14] = new SimpleReagentType("gold", 1100D, 3000D, 14, (MatterPhase phase) -> Color.YELLOW, Items.GOLD_INGOT, 144, true, 0, null, SolventType.AQUA_REGIA, 0, null);
		REAGENTS[15] = new SimpleReagentType("hydrogen_chloride", -110D, -90D, 15, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, SolventType.POLAR, null, 0, null);//Salt that forms hydrochloric acid in water. TODO acid effect
		REAGENTS[16] = new SimpleReagentType("sodium_sulfate", 890D, 1400D, 16, (MatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, 1, true, 0, null, SolventType.POLAR, 0, null);
		REAGENTS[17] = new SimpleReagentType("ethanol", -110D, 80D, 17, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, SolventType.NON_POLAR, null, 0, null);//If anyone asks, this is denatured alcohol for legal reasons. 

		//TODO reactions
	}

	/**
	 * Assumes the chamber has had {@link AlchemyHelper#updateContents(IReactionChamber, double)} called to fix the contents first. This calls it every time it changes the contents. 
	 * 
	 * @param chamber
	 * @param passes The maximum number of reactions to do. 
	 */
	public static void performReaction(IReactionChamber chamber, int passes){
		for(int pass = 0; pass < passes; passes++){
			boolean operated = false;
			for(Predicate<IReactionChamber> react : REACTIONS){
				if(react.test(chamber)){
					AlchemyHelper.updateContents(chamber, 0);
					operated = true;
					break;
				}
			}
			if(!operated){
				break;
			}
		}
	}

	public static void setup(long seed){
		Random rand = new Random(seed);

		REACTIONS.clear();
		REACTIONS.addAll(BASE_REACTIONS);

		ITEM_TO_REAGENT.clear();
		ITEM_TO_REAGENT.putAll(BASE_ITEM_TO_REAGENT);

		for(int i = RESERVED_REAGENT_COUNT; i < RESERVED_REAGENT_COUNT + DYNAMIC_REAGENT_COUNT; i++){
			int gen_minor = rand.nextInt();
			int gen_middle = rand.nextInt();
			int gen_major = rand.nextInt();
			//TODO create the dynamic reagents and reactions
		}
	}
}
