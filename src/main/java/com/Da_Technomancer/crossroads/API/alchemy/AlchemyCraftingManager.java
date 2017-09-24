package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.effects.alchemy.AcidAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.AquaRegiaAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.ChlorineAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.SaltAlchemyEffect;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public final class AlchemyCraftingManager{

	private static final List<IReaction> BASE_REACTIONS = new ArrayList<IReaction>();// TODO set size

	/** Note that the contained predicates have the side effect of performing the reaction. */
	public static final List<IReaction> REACTIONS = new ArrayList<IReaction>();// TODO set size

	protected static final BiMap<Item, IReagentType> BASE_ITEM_TO_REAGENT = HashBiMap.create();// TODO set size

	public static final BiMap<Item, IReagentType> ITEM_TO_REAGENT = HashBiMap.create();// TODO set size

	public static final int RESERVED_REAGENT_COUNT = 32;

	public static final int DYNAMIC_REAGENT_COUNT = 32;

	public static final IReagentType[] REAGENTS = new IReagentType[RESERVED_REAGENT_COUNT + DYNAMIC_REAGENT_COUNT];

	// A large number of colors are defined here so that a new color instance won't be initialized every time getColor is called.
	private static final Color PHELOSTIGEN_COLOR = new Color(255, 200, 0, 150);
	private static final Color CLEAR_COLOR = new Color(255, 255, 255, 0);
	private static final Color TRANSLUCENT_WHITE_COLOR = new Color(255, 255, 255, 200);
	private static final Color TRANSLUCENT_LIME_COLOR = new Color(200, 255, 0, 100);
	
	// Various effects
	private static final AcidAlchemyEffect ACID_EFFECT = new AcidAlchemyEffect();
	private static final SaltAlchemyEffect SALT_EFFECT = new SaltAlchemyEffect();

	static{

		// Reagents
		REAGENTS[0] = new SimpleReagentType("phelostigen", -275D, -274D, 0, (MatterPhase phase) -> PHELOSTIGEN_COLOR, null, 1, true, 0, null, null, 2, null);// TODO effect
		REAGENTS[1] = new SimpleReagentType("aether", -275D, -274D, 1, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, null, null, 1, null);
		REAGENTS[2] = new SimpleReagentType("adamant", Short.MAX_VALUE - 1, Short.MAX_VALUE, 2, (MatterPhase phase) -> Color.GRAY, ModItems.adamant, 100, true, 0, null, SolventType.AQUA_REGIA, 0, null);
		REAGENTS[3] = new SimpleReagentType("sulfur", 115D, 445D, 3, (MatterPhase phase) -> phase == MatterPhase.GAS ? CLEAR_COLOR : phase == MatterPhase.LIQUID ? Color.RED : Color.YELLOW, ModItems.sulfur, 100, true, 0, null, SolventType.NON_POLAR, 0, null);
		REAGENTS[4] = new SimpleReagentType("water", 0D, 100D, 4, (MatterPhase phase) -> CLEAR_COLOR, Item.getItemFromBlock(Blocks.ICE), 100, true, 0, SolventType.POLAR, null, 0, null);
		REAGENTS[5] = new SimpleReagentType("hydrogen_nitrate", -40D, 80D, 5, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, null, SolventType.POLAR, 0, ACID_EFFECT);// Salt that forms nitric acid, AKA aqua fortis, in water.
		REAGENTS[6] = new SimpleReagentType("sodium_chloride", 800D, 1400D, 6, (MatterPhase phase) -> phase == MatterPhase.LIQUID ? Color.ORANGE : Color.WHITE, ModItems.dustSalt, 100, true, 0, null, SolventType.POLAR, 0, SALT_EFFECT);// AKA table salt.
		REAGENTS[7] = new SimpleReagentType("vanadium_5_oxide", 690D, 1750D, 7, (MatterPhase phase) -> Color.YELLOW, ModItems.vanadiumVOxide, 100, true, 0, null, SolventType.POLAR, 0, null);// Vanadium (V) oxide. This should decompose at the specified boiling point, but there isn't any real point to adding that.
		REAGENTS[8] = new SimpleReagentType("sulfur_dioxide", -72D, -10D, 8, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, SolventType.POLAR, SolventType.POLAR, 0, null);
		REAGENTS[9] = new SimpleReagentType("sulfur_trioxide", 20D, 40D, 9, (MatterPhase phase) -> phase == MatterPhase.SOLID ? TRANSLUCENT_WHITE_COLOR : CLEAR_COLOR, null, 1, true, 0, null, SolventType.POLAR, 0, null);
		REAGENTS[10] = new SimpleReagentType("hydrogen_sulfate", 10D, 340D, 10, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, null, SolventType.POLAR, 0, ACID_EFFECT);// Salt that forms sulfuric acid, AKA Oil of Vitriol, in water.
		REAGENTS[11] = new SimpleReagentType("aqua_regia", -40D, 100D, 11, (MatterPhase phase) -> Color.ORANGE, null, 1, true, 0, SolventType.AQUA_REGIA, SolventType.POLAR, 0, new AquaRegiaAlchemyEffect());// Shouldn't really be its own substance (actually a mixture of nitric and hydrochloric acid), but the code is greatly simplified by making it a separate substance.
		REAGENTS[12] = new SimpleReagentType("murcury_2_sulfide", 580D, Short.MAX_VALUE, 12, (MatterPhase phase) -> Color.RED, Items.REDSTONE, 100, true, 0, null, null, 0, null);// Mercury (II) sulfide, AKA cinnabar.
		REAGENTS[13] = new SimpleReagentType("murcury", -40D, 560D, 13, (MatterPhase phase) -> Color.LIGHT_GRAY, null, 1, true, 0, null, null, 0, null);// AKA quicksilver
		REAGENTS[14] = new SimpleReagentType("gold", 1100D, 3000D, 14, (MatterPhase phase) -> Color.YELLOW, Items.GOLD_NUGGET, 16, true, 0, null, SolventType.AQUA_REGIA, 0, null);
		REAGENTS[15] = new SimpleReagentType("hydrogen_chloride", -110D, 90D, 15, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, SolventType.POLAR, null, 0, ACID_EFFECT);// Salt that forms hydrochloric acid, AKA muriatic acid, in water. Boiling point should be -90, set to 90 due to the alchemy system not allowing gasses to dissolve. 
		REAGENTS[16] = new SimpleReagentType("waste_salt", 900D, 1400D, 16, (MatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, ModItems.wasteSalt, 1, true, 0, null, SolventType.POLAR, 0, new SaltAlchemyEffect());//Any salt byproduct that is too boring to bother adding. 
		REAGENTS[17] = new SimpleReagentType("ethanol", -110D, 80D, 17, (MatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, SolventType.NON_POLAR, null, 0, null);// If anyone asks, this is denatured alcohol for legal reasons.
		REAGENTS[18] = new SimpleReagentType("philosopher_stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 18, (MatterPhase phase) -> Color.BLACK, ModItems.philosopherStone, 100, true, 1, null, null, 2, null);
		REAGENTS[19] = new SimpleReagentType("practitioner_stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 19, (MatterPhase phase) -> Color.BLACK, ModItems.practitionerStone, 100, true, 2, null, null, 2, null);
		REAGENTS[20] = new SimpleReagentType("bedrock", Short.MAX_VALUE - 1, Short.MAX_VALUE, 20, (MatterPhase phase) -> Color.GRAY, Item.getItemFromBlock(Blocks.BEDROCK), 100, true, 0, null, SolventType.AQUA_REGIA, 0, null);
		REAGENTS[21] = new SimpleReagentType("chlorine", -100D, -35D, 21, (MatterPhase phase) -> TRANSLUCENT_LIME_COLOR, null, 1, true, 0, SolventType.NON_POLAR, SolventType.NON_POLAR, 0, new ChlorineAlchemyEffect());
		REAGENTS[22] = new SimpleReagentType("alchemical_crystal", Short.MAX_VALUE - 1, Short.MAX_VALUE, 22, (MatterPhase phase) -> CLEAR_COLOR, ModItems.alchCrystal, 10, true, 0, null, null, 0, null);
		
		// Reactions
		// Sulfur combustion.
		BASE_REACTIONS.add((IReactionChamber chamb) -> {
			if(chamb.getReagants()[3] != null && chamb.getReagants()[3].getTemp() >= 190){
				double temp = chamb.getReagants()[3].getTemp();
				double amount = Math.min(2D, chamb.getReagants()[3].getAmount());
				if(chamb.getReagants()[3].increaseAmount(-amount, 0) <= 0){
					chamb.getReagants()[3] = null;
				}
				if(chamb.getReagants()[8] == null){
					chamb.getReagants()[8] = new Reagent(REAGENTS[8], temp, amount * 8);
				}else{
					chamb.getReagants()[8].increaseAmount(amount * 8D, temp);
				}
				chamb.addVisualEffect(ModParticles.COLOR_FIRE, 0, 0, 0, 2.4D, 1D);
				return -amount * 8D * 300D * 15D;
			}else{
				return 0;
			}
		});
		//Sulfur Dioxide oxidation
		BASE_REACTIONS.add(new SimpleReaction(-100D, 400D, 620D, REAGENTS[7], false, Triple.of(REAGENTS[8], 1D, MatterPhase.GAS), null, null, Pair.of(REAGENTS[9], 1D), null, null));
		//Sulfuric Acid production. 
		BASE_REACTIONS.add(new SimpleReaction(-100D, -300D, -300D, null, false, Triple.of(REAGENTS[9], 1D, MatterPhase.GAS), Triple.of(REAGENTS[4], 1D, MatterPhase.GAS), null, Pair.of(REAGENTS[10], 1D), null, null));		
		//Cinnebar decomposition
		BASE_REACTIONS.add(new SimpleReaction(60D, 360D, -300D, null, false, Triple.of(REAGENTS[12], 1D, null), null, null, Pair.of(REAGENTS[13], 1D), Pair.of(REAGENTS[8], 1D), Pair.of(REAGENTS[1], 2D)));
		//Hydrochloric Acid production
		BASE_REACTIONS.add(new SimpleReaction(30D, 90D, -300D, null, false, Triple.of(REAGENTS[6], 2D, MatterPhase.SOLUTE), Triple.of(REAGENTS[10], 1D, MatterPhase.SOLUTE), null, Pair.of(REAGENTS[16], 1D), Pair.of(REAGENTS[15], 2D), null));
		//Nitric Acid production
		BASE_REACTIONS.add(new SimpleReaction(70D, 100D, -300D, null, true, Triple.of(REAGENTS[4], 1D, MatterPhase.SOLUTE), null, null, Pair.of(REAGENTS[5], 2D), null, null));
		//Aqua Regia production
		BASE_REACTIONS.add(new SimpleReaction(0.015625D, -300D, -300D, null, false, Triple.of(REAGENTS[5], 1D, MatterPhase.SOLUTE), Triple.of(REAGENTS[15], 3D, MatterPhase.SOLUTE), null, Pair.of(REAGENTS[11], 4D), null, null));
		//Chlorine gas production
		BASE_REACTIONS.add(new SimpleReaction(0D, 100D, 600D, null, true, Triple.of(REAGENTS[6], 2D, MatterPhase.SOLUTE), Triple.of(REAGENTS[4], 2D, MatterPhase.LIQUID), null, Pair.of(REAGENTS[21], 1D), Pair.of(REAGENTS[16], 2D), null));	
		//Adamant production
		BASE_REACTIONS.add(new SimpleReaction(0D, 100D, 600D, null, false, Triple.of(REAGENTS[20], 5D, MatterPhase.SOLUTE), null, null, Pair.of(REAGENTS[2], 1D), Pair.of(REAGENTS[6], 4D), null));
		//Philosopher's Stone creation
		BASE_REACTIONS.add(new SimpleReaction(-35D, -20D, -1000D, null, false, Triple.of(REAGENTS[2], 50D, MatterPhase.SOLID), Triple.of(REAGENTS[13], 50D, MatterPhase.LIQUID), Triple.of(REAGENTS[11], 1D, MatterPhase.LIQUID), Pair.of(REAGENTS[18], 100D), null, null));
		//Alchemical Crystal production
		BASE_REACTIONS.add(new SimpleReaction(-35D, -20D, -50D, null, false, Triple.of(REAGENTS[2], 1D, MatterPhase.SOLID), Triple.of(REAGENTS[13], 4D, MatterPhase.LIQUID), null, Pair.of(REAGENTS[22], 5D), null, null));
		//Practitioner's Stone creation (destroys chamber if proportions are wrong.)
		BASE_REACTIONS.add((IReactionChamber chamb) -> {
			Reagent phel = chamb.getReagants()[0];
			Reagent aeth = chamb.getReagants()[1];
			Reagent adam = chamb.getReagants()[2];
			if(phel != null && aeth != null && adam != null && aeth.getTemp() <= 20D){
				Reagent gold = chamb.getReagants()[14];
				if(gold != null && gold.getPhase() == MatterPhase.SOLUTE){
					if(Math.abs(phel.getAmount() - aeth.getAmount()) >= 1D || Math.abs(phel.getAmount() - adam.getAmount()) >= 1D || Math.abs(phel.getAmount() + aeth.getAmount() + adam.getAmount() - gold.getAmount()) >= 3D || Math.abs(aeth.getAmount() - adam.getAmount()) >= 1D){
						chamb.destroyChamber();
						return .01D;
					}
					double amount = 4D * Math.min(phel.getAmount(), Math.min(aeth.getAmount(), Math.min(adam.getAmount(), gold.getAmount())));
					chamb.getReagants()[0] = null;
					chamb.getReagants()[1] = null;
					chamb.getReagants()[2] = null;
					chamb.getReagants()[14] = null;
					if(chamb.getReagants()[19] == null){
						chamb.getReagants()[19] = new Reagent(REAGENTS[19], 0D, amount);
					}else{
						chamb.getReagants()[19].increaseAmount(amount, 0D);
					}
					return amount * 500D;
				}
				chamb.destroyChamber();
			}
			return 0;
		});
		// TODO reactions
	}

	/** Assumes the chamber has had {@link AlchemyHelper#updateContents(IReactionChamber, double)} called to fix the contents first. This calls it every time it changes the contents.
	 * 
	 * @param chamber
	 * @param passes
	 *            The maximum number of reactions to do. */
	public static void performReaction(IReactionChamber chamber, int passes){
		for(int pass = 0; pass < passes; passes++){
			boolean operated = false;
			for(IReaction react : REACTIONS){
				double change = -react.performReaction(chamber);
				if(change != 0){
					AlchemyHelper.updateContents(chamber, change);
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
			// TODO create the dynamic reagents and reactions
		}
	}
}
