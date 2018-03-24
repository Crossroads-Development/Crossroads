package com.Da_Technomancer.crossroads.API.alchemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.API.effects.alchemy.AcidAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.AetherEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.AquaRegiaAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.ChlorineAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.EldrineEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.FusasEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.PhelostogenEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.SaltAlchemyEffect;
import com.Da_Technomancer.crossroads.API.effects.alchemy.StasisolEffect;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
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

	public static final int REAGENT_COUNT = 64;
	public static final int ALCHEMY_TIME = 2;
	public static final double MIN_QUANTITY = 0.005D;


	/** Note that the contained predicates have the side effect of performing the reaction. */
	public static final List<IReaction> REACTIONS = new ArrayList<IReaction>();// TODO set size

	public static final LinkedHashMap<Item, IReagent> ITEM_TO_REAGENT = new LinkedHashMap<Item, IReagent>();// TODO set size
	public static final BiMap<Fluid, IReagent> FLUID_TO_LIQREAGENT = HashBiMap.create(); // For liquid phase. TODO set size
	public static final BiMap<Fluid, IReagent> FLUID_TO_GASREAGENT = HashBiMap.create(); // For gas phase. TODO set size
	public static final ArrayList<IElementReagent> ELEMENTAL_REAGS = new ArrayList<IElementReagent>(8);

	public static final IReagent[] REAGENTS = new IReagent[REAGENT_COUNT];

	static{
		// A large number of colors are defined here so that a new color instance won't be initialized every time getColor is called.
		Color PHELOSTIGEN_COLOR = new Color(255, 100, 0, 150);
		Color CLEAR_COLOR = new Color(255, 255, 255, 100);//"Clear" is actually a faint translucent so that it's visible when something is in the tube. 
		Color TRANSLUCENT_BLUE_COLOR = new Color(0, 0, 255, 200);
		Color TRANSLUCENT_WHITE_COLOR = new Color(255, 255, 255, 200);
		Color TRANSLUCENT_LIME_COLOR = new Color(200, 255, 0, 100);
		Color TRANSLUCENT_YELLOW_COLOR = new Color(255, 255, 0, 200);
		Color BROWN_COLOR = new Color(130, 50, 0, 255);
		Color FAINT_BLUE_COLOR = new Color(255, 100, 0, 100);
		// Various effects
		AcidAlchemyEffect ACID_EFFECT = new AcidAlchemyEffect();
		SaltAlchemyEffect SALT_EFFECT = new SaltAlchemyEffect();

		// Reagents
		REAGENTS[0] = new BaseElementReagent("Phelostigen", -275D, -274D, 0, (EnumMatterPhase phase) -> PHELOSTIGEN_COLOR, null, 1, EnumSolventType.FLAME, null, 2, new PhelostogenEffect((Double amount) -> (int) Math.min(8, Math.round(amount / 2D))), new MagicUnit(1, 0, 0, 0)){
			@Override
			public boolean isLockedFlame(){
				return true;
			}
		};
		REAGENTS[1] = new BaseElementReagent("Aether", -275D, -274D, 1, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, null, null, 1, new AetherEffect(), new MagicUnit(0, 1, 0, 0));
		REAGENTS[2] = new BaseElementReagent("Adamant", Short.MAX_VALUE - 1, Short.MAX_VALUE, 2, (EnumMatterPhase phase) -> Color.GRAY, ModItems.adamant, 1, null, EnumSolventType.AQUA_REGIA, 1, null, new MagicUnit(0, 0, 1, 0));
		REAGENTS[3] = new StaticReagent("Sulfur", 115D, 445D, 3, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_YELLOW_COLOR : phase == EnumMatterPhase.LIQUID ? Color.RED : Color.YELLOW, ModItems.sulfur, 10, null, EnumSolventType.NON_POLAR, 0, null);
		REAGENTS[4] = new StaticReagent("Water", 0D, 100D, 4, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_WHITE_COLOR : TRANSLUCENT_BLUE_COLOR, Item.getItemFromBlock(Blocks.PACKED_ICE), 50, EnumSolventType.POLAR, null, 0, null);
		REAGENTS[5] = new StaticReagent("Hydrogen Nitrate", -40D, 80D, 5, (EnumMatterPhase phase) -> Color.YELLOW, null, 1, null, EnumSolventType.POLAR, 0, ACID_EFFECT);// Salt that forms nitric acid, AKA aqua fortis, in water.
		REAGENTS[6] = new StaticReagent("Sodium Chloride", 800D, 1400D, 6, (EnumMatterPhase phase) -> phase == EnumMatterPhase.LIQUID ? Color.ORANGE : Color.WHITE, ModItems.dustSalt, 1, null, EnumSolventType.POLAR, 0, SALT_EFFECT);// AKA table salt.
		REAGENTS[7] = new StaticReagent("Vanadium (V) Oxide", 690D, 1750D, 7, (EnumMatterPhase phase) -> Color.YELLOW, ModItems.vanadiumVOxide, 1, null, EnumSolventType.POLAR, 0, null);// Vanadium (V) oxide. This should decompose at the specified boiling point, but there isn't any real point to adding that.
		REAGENTS[8] = new StaticReagent("Sulfur Dioxide", -72D, -10D, 8, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, 1, EnumSolventType.POLAR, null, 0, null);
		REAGENTS[9] = new StaticReagent("Sulfur Trioxide", 20D, 40D, 9, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, 1, null, EnumSolventType.POLAR, 0, null);
		REAGENTS[10] = new StaticReagent("Hydrogen Sulfate", 10D, 340D, 10, (EnumMatterPhase phase) -> BROWN_COLOR, null, 1, null, EnumSolventType.POLAR, 0, ACID_EFFECT);// Salt that forms sulfuric acid, AKA Oil of Vitriol, in water.
		REAGENTS[11] = new StaticReagent("Aqua Regia", -40D, 100D, 11, (EnumMatterPhase phase) -> Color.ORANGE, null, 1, EnumSolventType.AQUA_REGIA, EnumSolventType.POLAR, 0, new AquaRegiaAlchemyEffect());// Shouldn't really be its own substance (actually a mixture of nitric and hydrochloric acid), but the code is greatly simplified by making it a separate substance.
		REAGENTS[12] = new StaticReagent("Murcury (II) Sulfide", 580D, Short.MAX_VALUE, 12, (EnumMatterPhase phase) -> Color.RED, Items.REDSTONE, 5, null, null, 0, null);// Mercury (II) sulfide, AKA cinnabar.
		REAGENTS[13] = new StaticReagent("Murcury", -40D, 560D, 13, (EnumMatterPhase phase) -> Color.LIGHT_GRAY, ModItems.solidQuicksilver, 10, null, null, 0, null);// AKA quicksilver
		REAGENTS[14] = new StaticReagent("Gold", 1100D, 3000D, 14, (EnumMatterPhase phase) -> Color.YELLOW, Items.GOLD_NUGGET, 16, null, EnumSolventType.AQUA_REGIA, 0, null);
		REAGENTS[15] = new StaticReagent("Hydrogen Chloride", -110D, 90D, 15, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, EnumSolventType.POLAR, null, 0, ACID_EFFECT);// Salt that forms hydrochloric acid, AKA muriatic acid, in water. Boiling point should be -90, set to 90 due to the alchemy system not allowing gasses to dissolve. 
		REAGENTS[16] = new StaticReagent("Alchemical Salt", 900D, 1400D, 16, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, ModItems.wasteSalt, 10, null, EnumSolventType.POLAR, 0, new SaltAlchemyEffect());//Any salt byproduct that is too boring to bother adding separately. 
		REAGENTS[17] = new StaticReagent("Ethanol", -110D, 80D, 17, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, EnumSolventType.NON_POLAR, null, 0, null);// If anyone asks, this is denatured alcohol for legal reasons.
		REAGENTS[18] = new StaticReagent("Philosopher Stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 18, (EnumMatterPhase phase) -> Color.BLACK, ModItems.philosopherStone, 5, null, null, 2, null);
		REAGENTS[19] = new StaticReagent("Practitioner Stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 19, (EnumMatterPhase phase) -> Color.BLACK, ModItems.practitionerStone, 5, null, null, 2, null);
		REAGENTS[20] = new StaticReagent("Bedrock", Short.MAX_VALUE - 1, Short.MAX_VALUE, 20, (EnumMatterPhase phase) -> Color.GRAY, Item.getItemFromBlock(Blocks.BEDROCK), 50, null, EnumSolventType.AQUA_REGIA, 0, null);
		REAGENTS[21] = new StaticReagent("Chlorine", -100D, -35D, 21, (EnumMatterPhase phase) -> TRANSLUCENT_LIME_COLOR, null, 1, EnumSolventType.NON_POLAR, EnumSolventType.NON_POLAR, 0, new ChlorineAlchemyEffect());
		REAGENTS[22] = new StaticReagent("Alchemical Crystal", Short.MAX_VALUE - 1, Short.MAX_VALUE, 22, (EnumMatterPhase phase) -> FAINT_BLUE_COLOR, ModItems.alchCrystal, 1, null, null, 0, null);
		REAGENTS[23] = new StaticReagent("Copper", 1000D, 2560D, 23, (EnumMatterPhase phase) -> Color.ORANGE, OreSetup.nuggetCopper, 16, null, null, 0, null);
		REAGENTS[24] = new StaticReagent("Iron", 1500D, 2560D, 24, (EnumMatterPhase phase) -> phase == EnumMatterPhase.SOLID ? Color.GRAY : Color.RED, Items.IRON_NUGGET, 16, null, null, 0, null);
		REAGENTS[25] = new StaticReagent("Tin", 230D, 2560D, 25, (EnumMatterPhase phase) -> Color.LIGHT_GRAY, OreSetup.nuggetTin, 16, null, null, 0, null);
		REAGENTS[26] = new StaticReagent("Gunpowder", -275D, -274D, 26, (EnumMatterPhase phase) -> Color.GRAY, Items.GUNPOWDER, 10, null, null, 0, null);
		
		//Dynamic reagents
		REAGENTS[32] = new ElementalReagent("Lumen", 32, (byte) 1, -275, -274, null, false, new MagicUnit(32, 32, 32, 0), null);//TODO effect
		REAGENTS[33] = new ElementalReagent("Eldrine", 33, (byte) 1, -100, 350, new EldrineEffect(), true, new MagicUnit(32, 0, 32, 0), ModItems.solidEldrine);
		REAGENTS[34] = new ElementalReagent("Stasisol", 34, (byte) 1, 800, 1800, new StasisolEffect(), false, new MagicUnit(32, 16, 32, 0), ModItems.solidStasisol);
		REAGENTS[35] = new ElementalReagent("Fusas", 35, (byte) 2, Short.MAX_VALUE - 1, Short.MAX_VALUE, new FusasEffect(), false, new MagicUnit(16, 32, 32, 0), null, (IElementReagent) REAGENTS[34]);
		REAGENTS[36] = new ElementalReagent("Voltus", 36, (byte) 2, -275, -274, null, true, new MagicUnit(32, 32, 8, 0), null, (IElementReagent) REAGENTS[32]);//TODO effect
		REAGENTS[37] = new StaticReagent("Ignus Infernum", -275D, -274D, 37, (EnumMatterPhase phase) -> Color.RED, null, 10, EnumSolventType.FLAME, null, 2, new PhelostogenEffect((Double amount) -> (int) Math.min(64, amount * 2D))){
			@Override
			public boolean isLockedFlame(){
				return true;
			}
		};
		
		//TODO REAGENTS[]

		FLUID_TO_LIQREAGENT.put(BlockDistilledWater.getDistilledWater(), REAGENTS[4]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenCopper.getMoltenCopper(), REAGENTS[23]);
		FLUID_TO_GASREAGENT.put(BlockSteam.getSteam(), REAGENTS[4]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenIron.getMoltenIron(), REAGENTS[24]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenTin.getMoltenTin(), REAGENTS[25]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenGold.getMoltenGold(), REAGENTS[14]);

		// Reactions
		// Sulfur combustion.
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[3], 1)}, new Pair[] {Pair.of(REAGENTS[8], 1)}, null, 190D, Double.MAX_VALUE, -300D, null, false){
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
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[8], 1)}, new Pair[] {Pair.of(REAGENTS[9], 1)}, REAGENTS[7], 400D, 620D, -100D, null, false));
		//Sulfuric Acid production. 
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[9], 1), Pair.of(REAGENTS[4], 1)}, new Pair[] {Pair.of(REAGENTS[10], 1)}, null, -300, Double.MAX_VALUE, -100D, null, false));
		//Gunpowder combustion
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[26], 3)}, new Pair[] {}, null, 200D, Double.MAX_VALUE, 0D, null, false){
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
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[26], 3)}, new Pair[] {Pair.of(REAGENTS[0], 1), Pair.of(REAGENTS[3], 1)}, REAGENTS[18], -300D, 200D, 0D, null, false));
		//Aether production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[12], 1)}, new Pair[] {Pair.of(REAGENTS[1], 1), Pair.of(REAGENTS[13], 1), Pair.of(REAGENTS[8], 1)}, REAGENTS[18], -360D, Double.MAX_VALUE, 0D, null, false));
		//Adamant production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[20], 5)}, new Pair[] {Pair.of(REAGENTS[2], 1), Pair.of(REAGENTS[16], 4)}, REAGENTS[18], 0D, 100D, 0D, new EnumSolventType[] {EnumSolventType.AQUA_REGIA}, false));

		//Gunpowder washing
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[26], 3)}, new Pair[] {Pair.of(REAGENTS[3], 1)}, null, -300D, 200D, 0D, new EnumSolventType[] {EnumSolventType.POLAR}, false));
		//Cinnebar decomposition
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[12], 1)}, new Pair[] {Pair.of(REAGENTS[13], 1), Pair.of(REAGENTS[8], 1)}, null, 360D, Double.MAX_VALUE, 60D, null, false));
		//Hydrochloric Acid production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[6], 2), Pair.of(REAGENTS[10], 1)}, new Pair[] {Pair.of(REAGENTS[16], 1), Pair.of(REAGENTS[15], 2)}, null, 90D, Double.MAX_VALUE, 30D, new EnumSolventType[] {EnumSolventType.POLAR}, false));
		//Nitric Acid production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[4], 1)}, new Pair[] {Pair.of(REAGENTS[5], 2)}, null, 100D, Double.MAX_VALUE, 70D, new EnumSolventType[] {EnumSolventType.POLAR}, true));
		//Aqua Regia production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[5], 1), Pair.of(REAGENTS[15], 3)}, new Pair[] {Pair.of(REAGENTS[11], 4)}, null, -300D, Double.MAX_VALUE, 0D, new EnumSolventType[] {EnumSolventType.POLAR}, false));
		//Chlorine gas production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[6], 2), Pair.of(REAGENTS[4], 2)}, new Pair[] {Pair.of(REAGENTS[21], 1), Pair.of(REAGENTS[16], 2)}, null, 100D, 600D, 0D, null, true));
		//Bedrock decomposition
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[20], 5)}, new Pair[] {Pair.of(REAGENTS[16], 4)}, null, 0D, 100D, 100D, new EnumSolventType[] {EnumSolventType.AQUA_REGIA}, false));
		//Alchemical Crystal production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[13], 2), Pair.of(REAGENTS[16], 1)}, new Pair[] {Pair.of(REAGENTS[22], 3)}, null, -300D, -40D, -35D, null, false));
		//Philosopher's Stone creation
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[3], 1), Pair.of(REAGENTS[13], 1), Pair.of(REAGENTS[14], 1), Pair.of(REAGENTS[16], 1)}, new Pair[] {Pair.of(REAGENTS[18], 4)}, null, -300D, -40D, -500D, new EnumSolventType[] {EnumSolventType.AQUA_REGIA}, false));
		//Practitioner's Stone creation (destroys chamber if proportions are wrong.)
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[0], 1), Pair.of(REAGENTS[1], 1), Pair.of(REAGENTS[2], 1), Pair.of(REAGENTS[14], 1)}, new Pair[] {Pair.of(REAGENTS[19], 4)}, null, -300D, -20D, -5000D, new EnumSolventType[] {EnumSolventType.AQUA_REGIA}, false){
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
		//Crystal formation
		REACTIONS.add(new CrystalFormationReaction());
		//Lumen production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS[32]));
		//Eldrine production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS[33]));
		//Stasisol production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS[34]));
		//Fusas production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS[35]));
		//Voltus production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS[36]));

		// TODO reactions
	}
}
