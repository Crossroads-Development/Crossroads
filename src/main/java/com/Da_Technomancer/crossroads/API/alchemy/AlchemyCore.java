package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.API.effects.alchemy.AcidAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.AetherEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.AquaRegiaAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.ChlorineAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.PhelostogenEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.SaltAlchemyEffect;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopper;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenGold;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenIron;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenTin;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fluids.Fluid;

@SuppressWarnings("unchecked")
public final class AlchemyCore{

	public static final int RESERVED_REAGENT_COUNT = 32;
	public static final int DYNAMIC_REAGENT_COUNT = 32;
	public static final int REAGENT_COUNT = RESERVED_REAGENT_COUNT + DYNAMIC_REAGENT_COUNT;
	public static final int ALCHEMY_TIME = 2;//TODO
	public static final double MIN_QUANTITY = 0.005D;

	private static final List<ITransparentReaction> BASE_REACTIONS = new ArrayList<ITransparentReaction>();// TODO set size
	/** Note that the contained predicates have the side effect of performing the reaction. */
	public static final List<IReaction> REACTIONS = new ArrayList<IReaction>();// TODO set size

	protected static final LinkedHashMap<Item, IReagent> BASE_ITEM_TO_REAGENT = new LinkedHashMap<Item, IReagent>();// TODO set size
	public static final LinkedHashMap<Item, IReagent> ITEM_TO_REAGENT = new LinkedHashMap<Item, IReagent>();// TODO set size
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
		REAGENTS[0] = new SimpleReagentType("Phelostigen", -275D, -274D, 0, (EnumMatterPhase phase) -> PHELOSTIGEN_COLOR, null, 1, true, EnumSolventType.FLAME, null, 2, new PhelostogenEffect()){
			@Override
			public boolean isLockedFlame(){
				return true;
			}
		};
		REAGENTS[1] = new SimpleReagentType("Aether", -275D, -274D, 1, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, true, null, null, 1, new AetherEffect());
		REAGENTS[2] = new SimpleReagentType("Adamant", Short.MAX_VALUE - 1, Short.MAX_VALUE, 2, (EnumMatterPhase phase) -> Color.GRAY, ModItems.adamant, 1, true, null, EnumSolventType.AQUA_REGIA, 0, null);
		REAGENTS[3] = new SimpleReagentType("Sulfur", 115D, 445D, 3, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_YELLOW_COLOR : phase == EnumMatterPhase.LIQUID ? Color.RED : Color.YELLOW, ModItems.sulfur, 10, true, null, EnumSolventType.NON_POLAR, 0, null);
		REAGENTS[4] = new SimpleReagentType("Water", 0D, 100D, 4, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_WHITE_COLOR : TRANSLUCENT_BLUE_COLOR, Item.getItemFromBlock(Blocks.PACKED_ICE), 50, true, EnumSolventType.POLAR, null, 0, null);
		REAGENTS[5] = new SimpleReagentType("Hydrogen Nitrate", -40D, 80D, 5, (EnumMatterPhase phase) -> Color.YELLOW, null, 1, true, null, EnumSolventType.POLAR, 0, ACID_EFFECT);// Salt that forms nitric acid, AKA aqua fortis, in water.
		REAGENTS[6] = new SimpleReagentType("Sodium Chloride", 800D, 1400D, 6, (EnumMatterPhase phase) -> phase == EnumMatterPhase.LIQUID ? Color.ORANGE : Color.WHITE, ModItems.dustSalt, 1, true, null, EnumSolventType.POLAR, 0, SALT_EFFECT);// AKA table salt.
		REAGENTS[7] = new SimpleReagentType("Vanadium (V) Oxide", 690D, 1750D, 7, (EnumMatterPhase phase) -> Color.YELLOW, ModItems.vanadiumVOxide, 1, true, null, EnumSolventType.POLAR, 0, null);// Vanadium (V) oxide. This should decompose at the specified boiling point, but there isn't any real point to adding that.
		REAGENTS[8] = new SimpleReagentType("Sulfur Dioxide", -72D, -10D, 8, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, 1, true, EnumSolventType.POLAR, EnumSolventType.POLAR, 0, null);
		REAGENTS[9] = new SimpleReagentType("Sulfur Trioxide", 20D, 40D, 9, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, 1, true, null, EnumSolventType.POLAR, 0, null);
		REAGENTS[10] = new SimpleReagentType("Hydrogen Sulfate", 10D, 340D, 10, (EnumMatterPhase phase) -> BROWN_COLOR, null, 1, true, null, EnumSolventType.POLAR, 0, ACID_EFFECT);// Salt that forms sulfuric acid, AKA Oil of Vitriol, in water.
		REAGENTS[11] = new SimpleReagentType("Aqua Regia", -40D, 100D, 11, (EnumMatterPhase phase) -> Color.ORANGE, null, 1, true, EnumSolventType.AQUA_REGIA, EnumSolventType.POLAR, 0, new AquaRegiaAlchemyEffect());// Shouldn't really be its own substance (actually a mixture of nitric and hydrochloric acid), but the code is greatly simplified by making it a separate substance.
		REAGENTS[12] = new SimpleReagentType("Murcury (II) Sulfide", 580D, Short.MAX_VALUE, 12, (EnumMatterPhase phase) -> Color.RED, Items.REDSTONE, 5, true, null, null, 0, null);// Mercury (II) sulfide, AKA cinnabar.
		REAGENTS[13] = new SimpleReagentType("Murcury", -40D, 560D, 13, (EnumMatterPhase phase) -> Color.LIGHT_GRAY, null, 1, true, null, null, 0, null);// AKA quicksilver
		REAGENTS[14] = new SimpleReagentType("Gold", 1100D, 3000D, 14, (EnumMatterPhase phase) -> Color.YELLOW, Items.GOLD_NUGGET, 16, true, null, EnumSolventType.AQUA_REGIA, 0, null);
		REAGENTS[15] = new SimpleReagentType("Hydrogen Chloride", -110D, 90D, 15, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, true, EnumSolventType.POLAR, null, 0, ACID_EFFECT);// Salt that forms hydrochloric acid, AKA muriatic acid, in water. Boiling point should be -90, set to 90 due to the alchemy system not allowing gasses to dissolve. 
		REAGENTS[16] = new SimpleReagentType("Alchemical Salt", 900D, 1400D, 16, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, ModItems.wasteSalt, 1, true, null, EnumSolventType.POLAR, 0, new SaltAlchemyEffect());//Any salt byproduct that is too boring to bother adding separately. 
		REAGENTS[17] = new SimpleReagentType("Ethanol", -110D, 80D, 17, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, true, EnumSolventType.NON_POLAR, null, 0, null);// If anyone asks, this is denatured alcohol for legal reasons.
		REAGENTS[18] = new SimpleReagentType("Philosopher Stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 18, (EnumMatterPhase phase) -> Color.BLACK, ModItems.philosopherStone, 100, true, null, null, 2, null);
		REAGENTS[19] = new SimpleReagentType("Practitioner Stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 19, (EnumMatterPhase phase) -> Color.BLACK, ModItems.practitionerStone, 100, true, null, null, 2, null);
		REAGENTS[20] = new SimpleReagentType("Bedrock", Short.MAX_VALUE - 1, Short.MAX_VALUE, 20, (EnumMatterPhase phase) -> Color.GRAY, Item.getItemFromBlock(Blocks.BEDROCK), 50, true, null, EnumSolventType.AQUA_REGIA, 0, null);
		REAGENTS[21] = new SimpleReagentType("Chlorine", -100D, -35D, 21, (EnumMatterPhase phase) -> TRANSLUCENT_LIME_COLOR, null, 1, true, EnumSolventType.NON_POLAR, EnumSolventType.NON_POLAR, 0, new ChlorineAlchemyEffect());
		REAGENTS[22] = new SimpleReagentType("Alchemical Crystal", Short.MAX_VALUE - 1, Short.MAX_VALUE, 22, (EnumMatterPhase phase) -> FAINT_BLUE_COLOR, ModItems.alchCrystal, 1, true, null, null, 0, null);
		REAGENTS[23] = new SimpleReagentType("Copper", 1000D, 2560D, 23, (EnumMatterPhase phase) -> Color.ORANGE, OreSetup.nuggetCopper, 16, true, null, null, 0, null);
		REAGENTS[24] = new SimpleReagentType("Iron", 1500D, 2560D, 24, (EnumMatterPhase phase) -> phase == EnumMatterPhase.SOLID ? Color.GRAY : Color.RED, Items.IRON_NUGGET, 16, true, null, null, 0, null);
		REAGENTS[25] = new SimpleReagentType("Tin", 230D, 2560D, 25, (EnumMatterPhase phase) -> Color.LIGHT_GRAY, OreSetup.nuggetTin, 16, true, null, null, 0, null);
		REAGENTS[26] = new SimpleReagentType("Gunpowder", -275D, -274D, 26, (EnumMatterPhase phase) -> Color.GRAY, Items.GUNPOWDER, 10, true, null, null, 0, null);
		
		FLUID_TO_LIQREAGENT.put(BlockDistilledWater.getDistilledWater(), REAGENTS[4]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenCopper.getMoltenCopper(), REAGENTS[23]);
		FLUID_TO_GASREAGENT.put(BlockSteam.getSteam(), REAGENTS[4]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenIron.getMoltenIron(), REAGENTS[24]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenTin.getMoltenTin(), REAGENTS[25]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenGold.getMoltenGold(), REAGENTS[14]);

		// Reactions
		// Sulfur combustion.
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[3], 1)}, new Pair[] {Pair.of(REAGENTS[8], 1)}, null, 190D, Double.MAX_VALUE, -300D, null, false){
			@Override
			public boolean performReaction(IReactionChamber chamb, boolean[] solventsIn){
				boolean performed = super.performReaction(chamb, solventsIn);
				if(performed){
					chamb.addVisualEffect(ModParticles.COLOR_FLAME, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, new int[] {128, 0, 255, 128});
				}

				return performed;
			}
		});
		//Sulfur Dioxide oxidation
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[8], 1)}, new Pair[] {Pair.of(REAGENTS[9], 1)}, REAGENTS[7], 400D, 620D, -100D, null, false));
		//Sulfuric Acid production. 
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[9], 1), Pair.of(REAGENTS[4], 1)}, new Pair[] {Pair.of(REAGENTS[10], 1)}, null, -300, Double.MAX_VALUE, -100D, null, false));
		//Gunpowder combustion
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[26], 3)}, new Pair[] {}, null, 200D, Double.MAX_VALUE, 0D, null, false){
			@Override
			public boolean performReaction(IReactionChamber chamb, boolean[] solventsIn){
				boolean performed = super.performReaction(chamb, solventsIn);
				if(performed){
					chamb.destroyChamber();
					chamb.addVisualEffect(EnumParticleTypes.SMOKE_NORMAL, Math.random(), Math.random(), Math.random());
				}

				return performed;
			}
		});		
		
		//Phelostogen production
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[26], 3)}, new Pair[] {Pair.of(REAGENTS[0], 1), Pair.of(REAGENTS[3], 1)}, REAGENTS[18], -300D, 200D, 0D, null, false));
		//Aether production
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[12], 1)}, new Pair[] {Pair.of(REAGENTS[1], 1), Pair.of(REAGENTS[13], 1), Pair.of(REAGENTS[8], 1)}, REAGENTS[18], -360D, Double.MAX_VALUE, 60D, null, false));
		//Adamant production
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[20], 5)}, new Pair[] {Pair.of(REAGENTS[2], 1), Pair.of(REAGENTS[16], 4)}, REAGENTS[18], 0D, 100D, 100D, new EnumSolventType[] {EnumSolventType.AQUA_REGIA}, false));
		
		//Gunpowder washing
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[26], 3)}, new Pair[] {Pair.of(REAGENTS[3], 1)}, null, -300D, 200D, 0D, new EnumSolventType[] {EnumSolventType.POLAR}, false));
		//Cinnebar decomposition
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[12], 1)}, new Pair[] {Pair.of(REAGENTS[13], 1), Pair.of(REAGENTS[8], 1)}, null, 360D, Double.MAX_VALUE, 60D, null, false));
		//Hydrochloric Acid production
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[6], 2), Pair.of(REAGENTS[10], 1)}, new Pair[] {Pair.of(REAGENTS[16], 1), Pair.of(REAGENTS[15], 2)}, null, 90D, Double.MAX_VALUE, 30D, new EnumSolventType[] {EnumSolventType.POLAR}, false));
		//Nitric Acid production
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[4], 1)}, new Pair[] {Pair.of(REAGENTS[5], 2)}, null, 100D, Double.MAX_VALUE, 70D, new EnumSolventType[] {EnumSolventType.POLAR}, true));
		//Aqua Regia production
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[5], 1), Pair.of(REAGENTS[15], 3)}, new Pair[] {Pair.of(REAGENTS[11], 4)}, null, -300D, Double.MAX_VALUE, 0D, new EnumSolventType[] {EnumSolventType.POLAR}, false));
		//Chlorine gas production
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[6], 2), Pair.of(REAGENTS[4], 2)}, new Pair[] {Pair.of(REAGENTS[21], 1), Pair.of(REAGENTS[16], 2)}, null, 100D, 600D, 0D, null, true));
		//Bedrock decomposition
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[20], 5)}, new Pair[] {Pair.of(REAGENTS[16], 4)}, null, 0D, 100D, 100D, new EnumSolventType[] {EnumSolventType.AQUA_REGIA}, false));
		//Alchemical Crystal production
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[13], 2), Pair.of(REAGENTS[16], 1)}, new Pair[] {Pair.of(REAGENTS[22], 3)}, null, -300D, -40D, -35D, null, false));
		//Philosopher's Stone creation
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[3], 1), Pair.of(REAGENTS[13], 1), Pair.of(REAGENTS[14], 1), Pair.of(REAGENTS[16], 1)}, new Pair[] {Pair.of(REAGENTS[18], 4)}, null, -300D, -40D, -500D, new EnumSolventType[] {EnumSolventType.AQUA_REGIA}, false));
		//Practitioner's Stone creation (destroys chamber if proportions are wrong.)
		BASE_REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[0], 1), Pair.of(REAGENTS[1], 1), Pair.of(REAGENTS[2], 1), Pair.of(REAGENTS[14], 1)}, new Pair[] {Pair.of(REAGENTS[19], 4)}, null, -300D, -20D, -5000D, new EnumSolventType[] {EnumSolventType.AQUA_REGIA}, false){
			@Override
			public boolean performReaction(IReactionChamber chamb, boolean[] solventsIn){
				boolean performed = super.performReaction(chamb, solventsIn);
				if(performed){
					ReagentStack[] reags = chamb.getReagants();
					double phel = reags[0] == null ? 0 : reags[0].getAmount();
					double aeth = reags[1] == null ? 0 : reags[1].getAmount();
					double adam = reags[2] == null ? 0 : reags[2].getAmount();
					double gold = reags[14] == null ? 0 : reags[14].getAmount();
					if(Math.max(Math.max(phel, aeth), Math.max(adam, gold)) - Math.min(Math.min(phel, aeth), Math.min(adam, gold)) >= 2D){
						chamb.destroyChamber();
					}
				}
				return performed;

			}
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
