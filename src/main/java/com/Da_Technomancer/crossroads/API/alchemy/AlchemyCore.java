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
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopper;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.Fluid;

public final class AlchemyCore{

	public static final int RESERVED_REAGENT_COUNT = 32;
	public static final int DYNAMIC_REAGENT_COUNT = 32;
	public static final int REAGENT_COUNT = RESERVED_REAGENT_COUNT + DYNAMIC_REAGENT_COUNT;
	public static final int ALCHEMY_TIME = 2;//TODO
	
	private static final List<IReaction> BASE_REACTIONS = new ArrayList<IReaction>();// TODO set size
	/** Note that the contained predicates have the side effect of performing the reaction. */
	public static final List<IReaction> REACTIONS = new ArrayList<IReaction>();// TODO set size

	protected static final BiMap<Item, IReagent> BASE_ITEM_TO_REAGENT = HashBiMap.create();// TODO set size
	public static final BiMap<Item, IReagent> ITEM_TO_REAGENT = HashBiMap.create();// TODO set size
	public static final BiMap<Fluid, IReagent> FLUID_TO_LIQREAGENT = HashBiMap.create(); // For liquid phase. TODO set size
	public static final BiMap<Fluid, IReagent> FLUID_TO_GASREAGENT = HashBiMap.create(); // For gas phase. TODO set size
	
	public static final IReagent[] REAGENTS = new IReagent[REAGENT_COUNT];

	// A large number of colors are defined here so that a new color instance won't be initialized every time getColor is called.
	private static final Color PHELOSTIGEN_COLOR = new Color(255, 200, 0, 150);
	private static final Color CLEAR_COLOR = new Color(255, 255, 255, 100);//"Clear" is actually a faint translucent so that it's visible when something is in the tube. 
	private static final Color TRANSLUCENT_BLUE_COLOR = new Color(0, 0, 255, 200);
	private static final Color TRANSLUCENT_WHITE_COLOR = new Color(255, 255, 255, 200);
	private static final Color TRANSLUCENT_LIME_COLOR = new Color(200, 255, 0, 100);
	private static final Color TRANSLUCENT_YELLOW_COLOR = new Color(255, 255, 0, 200);
	private static final Color BROWN_COLOR = new Color(130, 50, 0, 255);
	private static final Color FAINT_BLUE_COLOR = new Color(255, 100, 0, 100);
	
	// Various effects
	private static final AcidAlchemyEffect ACID_EFFECT = new AcidAlchemyEffect();
	private static final SaltAlchemyEffect SALT_EFFECT = new SaltAlchemyEffect();

	static{

		// Reagents
		REAGENTS[0] = new SimpleReagentType("Phelostigen", -275D, -274D, 0, (EnumMatterPhase phase) -> PHELOSTIGEN_COLOR, null, 1, true, 0, null, null, 2, null);// TODO effect
		REAGENTS[1] = new SimpleReagentType("Aether", -275D, -274D, 1, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, null, null, 1, null);
		REAGENTS[2] = new SimpleReagentType("Adamant", Short.MAX_VALUE - 1, Short.MAX_VALUE, 2, (EnumMatterPhase phase) -> Color.GRAY, ModItems.adamant, 100, true, 0, null, SolventType.AQUA_REGIA, 0, null);
		REAGENTS[3] = new SimpleReagentType("Sulfur", 115D, 445D, 3, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_YELLOW_COLOR : phase == EnumMatterPhase.LIQUID ? Color.RED : Color.YELLOW, ModItems.sulfur, 100, true, 0, null, SolventType.NON_POLAR, 0, null);
		REAGENTS[4] = new SimpleReagentType("Water", 0D, 100D, 4, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_WHITE_COLOR : TRANSLUCENT_BLUE_COLOR, Item.getItemFromBlock(Blocks.ICE), 100, true, 0, SolventType.POLAR, null, 0, null);
		REAGENTS[5] = new SimpleReagentType("Hydrogen Nitrate", -40D, 80D, 5, (EnumMatterPhase phase) -> Color.YELLOW, null, 1, true, 0, null, SolventType.POLAR, 0, ACID_EFFECT);// Salt that forms nitric acid, AKA aqua fortis, in water.
		REAGENTS[6] = new SimpleReagentType("Sodium Chloride", 800D, 1400D, 6, (EnumMatterPhase phase) -> phase == EnumMatterPhase.LIQUID ? Color.ORANGE : Color.WHITE, ModItems.dustSalt, 100, true, 0, null, SolventType.POLAR, 0, SALT_EFFECT);// AKA table salt.
		REAGENTS[7] = new SimpleReagentType("Vanadium (V) Oxide", 690D, 1750D, 7, (EnumMatterPhase phase) -> Color.YELLOW, ModItems.vanadiumVOxide, 100, true, 0, null, SolventType.POLAR, 0, null);// Vanadium (V) oxide. This should decompose at the specified boiling point, but there isn't any real point to adding that.
		REAGENTS[8] = new SimpleReagentType("Sulfur Dioxide", -72D, -10D, 8, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, 1, true, 0, SolventType.POLAR, SolventType.POLAR, 0, null);
		REAGENTS[9] = new SimpleReagentType("Sulfur Trioxide", 20D, 40D, 9, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, 1, true, 0, null, SolventType.POLAR, 0, null);
		REAGENTS[10] = new SimpleReagentType("Hydrogen Sulfate", 10D, 340D, 10, (EnumMatterPhase phase) -> BROWN_COLOR, null, 1, true, 0, null, SolventType.POLAR, 0, ACID_EFFECT);// Salt that forms sulfuric acid, AKA Oil of Vitriol, in water.
		REAGENTS[11] = new SimpleReagentType("Aqua Regia", -40D, 100D, 11, (EnumMatterPhase phase) -> Color.ORANGE, null, 1, true, 0, SolventType.AQUA_REGIA, SolventType.POLAR, 0, new AquaRegiaAlchemyEffect());// Shouldn't really be its own substance (actually a mixture of nitric and hydrochloric acid), but the code is greatly simplified by making it a separate substance.
		REAGENTS[12] = new SimpleReagentType("Murcury (II) Sulfide", 580D, Short.MAX_VALUE, 12, (EnumMatterPhase phase) -> Color.RED, Items.REDSTONE, 100, true, 0, null, null, 0, null);// Mercury (II) sulfide, AKA cinnabar.
		REAGENTS[13] = new SimpleReagentType("Murcury", -40D, 560D, 13, (EnumMatterPhase phase) -> Color.LIGHT_GRAY, null, 1, true, 0, null, null, 0, null);// AKA quicksilver
		REAGENTS[14] = new SimpleReagentType("Gold", 1100D, 3000D, 14, (EnumMatterPhase phase) -> Color.YELLOW, Items.GOLD_NUGGET, 16, true, 0, null, SolventType.AQUA_REGIA, 0, null);
		REAGENTS[15] = new SimpleReagentType("Hydrogen Chloride", -110D, 90D, 15, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, SolventType.POLAR, null, 0, ACID_EFFECT);// Salt that forms hydrochloric acid, AKA muriatic acid, in water. Boiling point should be -90, set to 90 due to the alchemy system not allowing gasses to dissolve. 
		REAGENTS[16] = new SimpleReagentType("Waste Salt", 900D, 1400D, 16, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, ModItems.wasteSalt, 1, true, 0, null, SolventType.POLAR, 0, new SaltAlchemyEffect());//Any salt byproduct that is too boring to bother adding. 
		REAGENTS[17] = new SimpleReagentType("Ethanol", -110D, 80D, 17, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, true, 0, SolventType.NON_POLAR, null, 0, null);// If anyone asks, this is denatured alcohol for legal reasons.
		REAGENTS[18] = new SimpleReagentType("Philosopher Stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 18, (EnumMatterPhase phase) -> Color.BLACK, ModItems.philosopherStone, 100, true, 1, null, null, 2, null);
		REAGENTS[19] = new SimpleReagentType("Practitioner Stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 19, (EnumMatterPhase phase) -> Color.BLACK, ModItems.practitionerStone, 100, true, 2, null, null, 2, null);
		REAGENTS[20] = new SimpleReagentType("Bedrock", Short.MAX_VALUE - 1, Short.MAX_VALUE, 20, (EnumMatterPhase phase) -> Color.GRAY, Item.getItemFromBlock(Blocks.BEDROCK), 100, true, 0, null, SolventType.AQUA_REGIA, 0, null);
		REAGENTS[21] = new SimpleReagentType("Chlorine", -100D, -35D, 21, (EnumMatterPhase phase) -> TRANSLUCENT_LIME_COLOR, null, 1, true, 0, SolventType.NON_POLAR, SolventType.NON_POLAR, 0, new ChlorineAlchemyEffect());
		REAGENTS[22] = new SimpleReagentType("Alchemical Crystal", Short.MAX_VALUE - 1, Short.MAX_VALUE, 22, (EnumMatterPhase phase) -> FAINT_BLUE_COLOR, ModItems.alchCrystal, 10, true, 0, null, null, 0, null);
		REAGENTS[23] = new SimpleReagentType("Copper", 1000D, 2560D, 23, (EnumMatterPhase phase) -> Color.ORANGE, OreSetup.nuggetCopper, 16, true, 0, null, null, 0, null);
		
		FLUID_TO_LIQREAGENT.put(BlockDistilledWater.getDistilledWater(), REAGENTS[4]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenCopper.getMoltenCopper(), REAGENTS[23]);
		FLUID_TO_GASREAGENT.put(BlockSteam.getSteam(), REAGENTS[4]);
		
		// Reactions
		// Sulfur combustion.
		BASE_REACTIONS.add((IReactionChamber chamb) -> {
			if(chamb.getReagants()[3] != null && chamb.getTemp() >= 190){
				double temp = chamb.getTemp();
				double amount = Math.min(2D, chamb.getReagants()[3].getAmount());
				if(chamb.getReagants()[3].increaseAmount(-amount) <= 0){
					chamb.getReagants()[3] = null;
				}
				if(chamb.getReagants()[8] == null){
					chamb.getReagants()[8] = new ReagentStack(REAGENTS[8], amount * 8);
				}else{
					chamb.getReagants()[8].increaseAmount(amount * 8D);
				}
				chamb.addVisualEffect(ModParticles.COLOR_FIRE, 0, 0, 0, 2.4D, 1D);
				chamb.addHeat(amount * 8D * 300D * 15D + (temp * 7D * amount));
				return true;
			}else{
				return false;
			}
		});
		//Sulfur Dioxide oxidation
		BASE_REACTIONS.add(new SimpleReaction(-100D, 400D, 620D, REAGENTS[7], false, Triple.of(REAGENTS[8], 1D, false), null, null, Pair.of(REAGENTS[9], 1D), null, null));
		//Sulfuric Acid production. 
		BASE_REACTIONS.add(new SimpleReaction(-100D, -300D, -300D, null, false, Triple.of(REAGENTS[9], 1D, false), Triple.of(REAGENTS[4], 1D, false), null, Pair.of(REAGENTS[10], 1D), null, null));		
		//Cinnebar decomposition
		BASE_REACTIONS.add(new SimpleReaction(60D, 360D, -300D, null, false, Triple.of(REAGENTS[12], 1D, false), null, null, Pair.of(REAGENTS[13], 1D), Pair.of(REAGENTS[8], 1D), Pair.of(REAGENTS[1], 2D)));
		//Hydrochloric Acid production
		BASE_REACTIONS.add(new SimpleReaction(30D, 90D, -300D, null, false, Triple.of(REAGENTS[6], 2D, true), Triple.of(REAGENTS[10], 1D, true), null, Pair.of(REAGENTS[16], 1D), Pair.of(REAGENTS[15], 2D), null));
		//Nitric Acid production
		BASE_REACTIONS.add(new SimpleReaction(70D, 100D, -300D, null, true, Triple.of(REAGENTS[4], 1D, true), null, null, Pair.of(REAGENTS[5], 2D), null, null));
		//Aqua Regia production
		BASE_REACTIONS.add(new SimpleReaction(0.015625D, -300D, -300D, null, false, Triple.of(REAGENTS[5], 1D, true), Triple.of(REAGENTS[15], 3D, true), null, Pair.of(REAGENTS[11], 4D), null, null));
		//Chlorine gas production
		BASE_REACTIONS.add(new SimpleReaction(0D, 100D, 600D, null, true, Triple.of(REAGENTS[6], 2D, true), Triple.of(REAGENTS[4], 2D, false), null, Pair.of(REAGENTS[21], 1D), Pair.of(REAGENTS[16], 2D), null));	
		//Adamant production
		BASE_REACTIONS.add(new SimpleReaction(0D, 100D, 600D, null, false, Triple.of(REAGENTS[20], 5D, true), null, null, Pair.of(REAGENTS[2], 1D), Pair.of(REAGENTS[6], 4D), null));
		//Philosopher's Stone creation
		BASE_REACTIONS.add(new SimpleReaction(-35D, -20D, -1000D, null, false, Triple.of(REAGENTS[2], 50D, false), Triple.of(REAGENTS[13], 50D, false), Triple.of(REAGENTS[11], 1D, false), Pair.of(REAGENTS[18], 100D), null, null));
		//Alchemical Crystal production
		BASE_REACTIONS.add(new SimpleReaction(-35D, -50D, -20D, null, false, Triple.of(REAGENTS[2], 1D, false), Triple.of(REAGENTS[13], 4D, false), null, Pair.of(REAGENTS[22], 5D), null, null));
		//Practitioner's Stone creation (destroys chamber if proportions are wrong.)
		BASE_REACTIONS.add((IReactionChamber chamb) -> {
			ReagentStack phel = chamb.getReagants()[0];
			ReagentStack aeth = chamb.getReagants()[1];
			ReagentStack adam = chamb.getReagants()[2];
			double temp = chamb.getTemp();
			if(phel != null && aeth != null && adam != null && temp <= 20D){
				ReagentStack gold = chamb.getReagants()[14];
				if(gold != null && gold.getPhase(temp) == EnumMatterPhase.SOLUTE){
					if(Math.abs(phel.getAmount() - aeth.getAmount()) >= 1D || Math.abs(phel.getAmount() - adam.getAmount()) >= 1D || Math.abs(phel.getAmount() + aeth.getAmount() + adam.getAmount() - gold.getAmount()) >= 3D || Math.abs(aeth.getAmount() - adam.getAmount()) >= 1D){
						chamb.destroyChamber();
						return true;
					}
					double amount = 4D * Math.min(phel.getAmount(), Math.min(aeth.getAmount(), Math.min(adam.getAmount(), gold.getAmount())));
					chamb.getReagants()[0] = null;
					chamb.getReagants()[1] = null;
					chamb.getReagants()[2] = null;
					chamb.getReagants()[14] = null;
					if(chamb.getReagants()[19] == null){
						chamb.getReagants()[19] = new ReagentStack(REAGENTS[19], amount);
					}else{
						chamb.getReagants()[19].increaseAmount(amount);
					}
					chamb.addHeat(amount * 500D);
					return true;
				}
				chamb.destroyChamber();
			}
			return false;
		});
		// TODO reactions
	}

	public static void setup(long seed){
		Random rand = new Random(seed);

		REACTIONS.clear();
		REACTIONS.addAll(BASE_REACTIONS);

		ITEM_TO_REAGENT.clear();
		ITEM_TO_REAGENT.putAll(BASE_ITEM_TO_REAGENT);

		for(int i = RESERVED_REAGENT_COUNT; i < REAGENT_COUNT; i++){
			// TODO create the dynamic reagents and reactions
		}
	}
}
