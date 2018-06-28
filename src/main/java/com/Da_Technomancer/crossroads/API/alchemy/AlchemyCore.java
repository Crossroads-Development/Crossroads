package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.effects.alchemy.*;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.fluids.*;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.PredicateMap;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fluids.Fluid;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class AlchemyCore{

	public static final int REAGENT_COUNT = 64;
	public static final int ALCHEMY_TIME = 2;
	public static final double MIN_QUANTITY = 0.005D;


	/** Note that the contained predicates have the side effect of performing the reaction. */
	public static final List<IReaction> REACTIONS = new ArrayList<IReaction>();

	public static final PredicateMap<ItemStack, IReagent> ITEM_TO_REAGENT = new PredicateMap<>();
	public static final BiMap<Fluid, IReagent> FLUID_TO_LIQREAGENT = HashBiMap.create(5); // For liquid phase.
	public static final BiMap<Fluid, IReagent> FLUID_TO_GASREAGENT = HashBiMap.create(1); // For gas phase. 
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
		Color FAINT_BLUE_COLOR = new Color(0, 100, 255, 100);
		Color FAINT_RED_COLOR = new Color(200, 20, 0, 100);
		// Various effects
		AcidAlchemyEffect ACID_EFFECT = new AcidAlchemyEffect();
		SaltAlchemyEffect SALT_EFFECT = new SaltAlchemyEffect();

		// Reagents
		REAGENTS[0] = new BaseElementReagent("Phelostigen", -275D, -274D, 0, (EnumMatterPhase phase) -> PHELOSTIGEN_COLOR, null, 1, 2, new PhelostogenEffect((Double amount) -> (int) Math.min(8, Math.round(amount / 2D))), new MagicUnit(1, 0, 0, 0)){
			@Override
			public boolean isLockedFlame(){
				return true;
			}
		};
		REAGENTS[1] = new BaseElementReagent("Aether", -275D, -274D, 1, (EnumMatterPhase phase) -> CLEAR_COLOR, null, 1, 1, new AetherEffect(), new MagicUnit(0, 1, 0, 0));
		REAGENTS[2] = new BaseElementReagent("Adamant", Short.MAX_VALUE - 1, Short.MAX_VALUE, 2, (EnumMatterPhase phase) -> Color.GRAY, ModItems.adamant, 2, 0, null, new MagicUnit(0, 0, 1, 0));
		REAGENTS[3] = new StaticReagent("Sulfur", 115D, 445D, 3, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_YELLOW_COLOR : phase == EnumMatterPhase.LIQUID ? Color.RED : Color.YELLOW, MiscOp.oreDictPred("dustSulfur"), () -> MiscOp.getOredictStack("dustSulfur", 1), 10, 0, null);
		REAGENTS[4] = new StaticReagent("Water", 0D, 100D, 4, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_WHITE_COLOR : TRANSLUCENT_BLUE_COLOR, (stack) -> stack.getItem() == Item.getItemFromBlock(Blocks.PACKED_ICE), () -> new ItemStack(Blocks.PACKED_ICE), 50, 0, null);
		REAGENTS[5] = new StaticReagent("Salt of Aqua Fortis", -40D, 80D, 5, (EnumMatterPhase phase) -> Color.YELLOW, (stack) -> stack.getItem() == ModItems.solidFortis, () -> new ItemStack(ModItems.solidFortis), 10, 0, ACID_EFFECT);// Salt that forms nitric acid, AKA aqua fortis, in water.
		REAGENTS[6] = new StaticReagent("Mundane Salt", 800D, 1400D, 6, (EnumMatterPhase phase) -> phase == EnumMatterPhase.LIQUID ? Color.ORANGE : Color.WHITE, MiscOp.oreDictPred("dustSalt"), () -> MiscOp.getOredictStack("dustSalt", 1), 10, 0, SALT_EFFECT);// AKA table salt (sodium chloride).
		REAGENTS[7] = new StaticReagent("Vanadium Oxide", 690D, 1750D, 7, (EnumMatterPhase phase) -> Color.YELLOW, (stack) -> stack.getItem() == ModItems.vanadiumVOxide, () -> new ItemStack(ModItems.vanadiumVOxide), 1, 0, null);// Vanadium (V) oxide. This should decompose at the specified boiling point, but there isn't any real point to adding that.
		REAGENTS[8] = new StaticReagent("Sulfur Dioxide", -72D, -10D, 8, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, null, 1, 0, null);
		REAGENTS[9] = new StaticReagent("Sulfur Trioxide", 20D, 40D, 9, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, null, 1, 0, null);
		REAGENTS[10] = new StaticReagent("Salt of Oil of Vitriol", 10D, 340D, 10, (EnumMatterPhase phase) -> BROWN_COLOR, (stack) -> stack.getItem() == ModItems.solidVitriol, () -> new ItemStack(ModItems.solidVitriol), 10, 0, ACID_EFFECT);// Hydrogen Sulfate, salt that forms sulfuric acid, AKA Oil of Vitriol, in water.
		REAGENTS[11] = new StaticReagent("Salt of Aqua Regia", -40D, 200D, 11, (EnumMatterPhase phase) -> Color.ORANGE, (stack) -> stack.getItem() == ModItems.solidRegia, () -> new ItemStack(ModItems.solidRegia), 10, 0, new AquaRegiaAlchemyEffect());// Shouldn't really be its own substance (actually a mixture of nitric and hydrochloric acid), but the code is greatly simplified by making it a separate substance.
		REAGENTS[12] = new StaticReagent("Cinnabar", 580D, Short.MAX_VALUE, 12, (EnumMatterPhase phase) -> Color.RED, MiscOp.oreDictPred("dustRedstone"), () -> MiscOp.getOredictStack("dustRedstone", 1), 5, 0, null);// Mercury (II) sulfide.
		REAGENTS[13] = new StaticReagent("Quicksilver", -40D, 560D, 13, (EnumMatterPhase phase) -> Color.LIGHT_GRAY, (stack) -> stack.getItem() == ModItems.solidQuicksilver, () -> new ItemStack(ModItems.solidQuicksilver), 10, 0, null);// AKA murcury
		REAGENTS[14] = new StaticReagent("Gold", 1100D, 3000D, 14, (EnumMatterPhase phase) -> Color.YELLOW, MiscOp.oreDictPred("nuggetGold"), () -> MiscOp.getOredictStack("nuggetGold", 1), 2, 0, null);
		REAGENTS[15] = new StaticReagent("Salt of Muriatic Acid", -110D, 90D, 15, (EnumMatterPhase phase) -> CLEAR_COLOR, (stack) -> stack.getItem() == ModItems.solidMuriatic, () -> new ItemStack(ModItems.solidMuriatic), 10, 0, ACID_EFFECT);// Hydrogen Chloride, salt that forms hydrochloric acid, AKA muriatic acid, in water. Boiling point should be -90, set to 90 due to the alchemy system not allowing gasses to dissolve.
		REAGENTS[16] = new StaticReagent("Alchemical Salt", 900D, 1400D, 16, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, MiscOp.oreDictPred("dustAlcSalt"), () -> MiscOp.getOredictStack("dustAlcSalt", 1), 10, 0, SALT_EFFECT);//Any salt byproduct that is too boring to bother adding separately.
		//
		REAGENTS[18] = new StaticReagent("Philosopher Stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 18, (EnumMatterPhase phase) -> FAINT_BLUE_COLOR, (stack) -> stack.getItem() == ModItems.philosopherStone, () -> new ItemStack(ModItems.philosopherStone), 5, 2, null);
		REAGENTS[19] = new StaticReagent("Practitioner Stone", Short.MAX_VALUE - 1, Short.MAX_VALUE, 19, (EnumMatterPhase phase) -> FAINT_RED_COLOR, (stack) -> stack.getItem() == ModItems.practitionerStone, () -> new ItemStack(ModItems.practitionerStone), 5, 2, null);
		REAGENTS[20] = new StaticReagent("Bedrock", Short.MAX_VALUE - 1, Short.MAX_VALUE, 20, (EnumMatterPhase phase) -> Color.GRAY, (stack) -> stack.getItem() == Item.getItemFromBlock(Blocks.BEDROCK), () -> new ItemStack(Blocks.BEDROCK), 50, 0, null);
		REAGENTS[21] = new StaticReagent("Chlorine", -100D, -35D, 21, (EnumMatterPhase phase) -> TRANSLUCENT_LIME_COLOR, null, null, 1, 0, new ChlorineAlchemyEffect());
		REAGENTS[22] = new StaticReagent("Alchemical Crystal", Short.MAX_VALUE - 1, Short.MAX_VALUE, 22, (EnumMatterPhase phase) -> FAINT_BLUE_COLOR, (stack) -> stack.getItem() == ModItems.alchCrystal, () -> new ItemStack(ModItems.alchCrystal), 3, 0, null);
		REAGENTS[23] = new StaticReagent("Copper", 1000D, 2560D, 23, (EnumMatterPhase phase) -> Color.ORANGE, MiscOp.oreDictPred("nuggetCopper"), () -> MiscOp.getOredictStack("nuggetCopper", 1), 2, 0, null);
		REAGENTS[24] = new StaticReagent("Iron", 1500D, 2560D, 24, (EnumMatterPhase phase) -> phase == EnumMatterPhase.SOLID ? Color.GRAY : Color.RED, MiscOp.oreDictPred("nuggetIron"), () -> MiscOp.getOredictStack("nuggetIron", 1), 2, 0, null);
		REAGENTS[25] = new StaticReagent("Tin", 230D, 2560D, 25, (EnumMatterPhase phase) -> Color.LIGHT_GRAY, MiscOp.oreDictPred("nuggetTin"), () -> MiscOp.getOredictStack("nuggetTin", 1), 2, 0, null);
		REAGENTS[26] = new StaticReagent("Gunpowder", Short.MAX_VALUE - 1, Short.MAX_VALUE, 26, (EnumMatterPhase phase) -> Color.GRAY, MiscOp.oreDictPred("gunpowder"), () -> MiscOp.getOredictStack("gunpowder", 1), 10, 0, null);
		REAGENTS[27] = new StaticReagent("Ruby", 2000D, 3000D, 27, (EnumMatterPhase phase) -> Color.RED, MiscOp.oreDictPred("gemRuby"), () -> MiscOp.getOredictStack("gemRuby", 1), 10, 0, null);
		REAGENTS[28] = new StaticReagent("Emerald", 2000D, 3000D, 28, (EnumMatterPhase phase) -> Color.GREEN, MiscOp.oreDictPred("gemEmerald"), () -> MiscOp.getOredictStack("gemEmerald", 1), 10, 0, null);//Couldn't find actual figures on melting/boiling points of emerald/diamond, perhaps due to large variance.
		REAGENTS[29] = new StaticReagent("Diamond", 2000D, 3000D, 29, (EnumMatterPhase phase) -> Color.CYAN, MiscOp.oreDictPred("gemDiamond"), () -> MiscOp.getOredictStack("gemDiamond", 1), 10, 0, null);
		REAGENTS[30] = new StaticReagent("Nether Quartz", 2000D, 3000D, 30, (EnumMatterPhase phase) -> Color.WHITE, (stack) -> stack.getItem() == Items.QUARTZ, () -> new ItemStack(Items.QUARTZ), 10, 0, null);

		//Dynamic reagents
		REAGENTS[32] = new ElementalReagent("Lumen", 32, (byte) 1, -275, -274, new LumenEffect(), false, new MagicUnit(32, 32, 32, 0), null);
		REAGENTS[33] = new ElementalReagent("Eldrine", 33, (byte) 1, -100, 350, new EldrineEffect(), true, new MagicUnit(32, 0, 32, 0), ModItems.solidEldrine);
		REAGENTS[34] = new ElementalReagent("Stasisol", 34, (byte) 1, 800, 1800, new StasisolEffect(), false, new MagicUnit(32, 16, 32, 0), ModItems.solidStasisol);
		REAGENTS[35] = new ElementalReagent("Fusas", 35, (byte) 2, Short.MAX_VALUE - 1, Short.MAX_VALUE, null, false, new MagicUnit(16, 32, 32, 0), ModItems.solidFusas, (IElementReagent) REAGENTS[34]);
		REAGENTS[36] = new ElementalReagent("Voltus", 36, (byte) 2, -275, -274, new VoltusEffect(), true, new MagicUnit(32, 32, 8, 0), null, (IElementReagent) REAGENTS[32]);
		REAGENTS[37] = new StaticReagent("Ignis Infernum", -275D, -274D, 37, (EnumMatterPhase phase) -> Color.RED, null, null, 10, 2, new PhelostogenEffect((Double amount) -> (int) Math.min(64, amount * 2D))){
			@Override
			public boolean isLockedFlame(){
				return true;
			}
		};
		REAGENTS[38] = new StaticReagent("Densus", Short.MAX_VALUE - 1, Short.MAX_VALUE, 38, (EnumMatterPhase phase) -> Color.BLUE, MiscOp.oreDictPred("gemDensus"), () -> MiscOp.getOredictStack("gemDensus", 1), 20, 0, null);
		REAGENTS[39] = new StaticReagent("Anti-Densus", Short.MAX_VALUE - 1, Short.MAX_VALUE, 39, (EnumMatterPhase phase) -> Color.ORANGE, MiscOp.oreDictPred("gemAntiDensus"), () -> MiscOp.getOredictStack("gemAntiDensus", 1), 20, 0, null);

		FLUID_TO_LIQREAGENT.put(BlockDistilledWater.getDistilledWater(), REAGENTS[4]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenCopper.getMoltenCopper(), REAGENTS[23]);
		FLUID_TO_GASREAGENT.put(BlockSteam.getSteam(), REAGENTS[4]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenIron.getMoltenIron(), REAGENTS[24]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenTin.getMoltenTin(), REAGENTS[25]);
		FLUID_TO_LIQREAGENT.put(BlockMoltenGold.getMoltenGold(), REAGENTS[14]);

		// Reactions
		// Sulfur combustion.
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[3], 1)}, new Pair[] {Pair.of(REAGENTS[8], 1)}, null, 190D, Double.MAX_VALUE, -300D, false){
			@Override
			public boolean performReaction(IReactionChamber chamb){
				boolean performed = super.performReaction(chamb);
				if(performed){
					chamb.addVisualEffect(ModParticles.COLOR_FLAME, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 128, 0, 255, 128);
				}

				return performed;
			}
		});
		//Sulfur Dioxide oxidation
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[8], 1)}, new Pair[] {Pair.of(REAGENTS[9], 1)}, REAGENTS[7], 400D, 620D, -100D, false));
		//Sulfuric Acid production. 
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[9], 1), Pair.of(REAGENTS[4], 1)}, new Pair[] {Pair.of(REAGENTS[10], 1)}, null, -300, Double.MAX_VALUE, -100D, false));
		//Gunpowder combustion
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[26], 3)}, new Pair[] {}, null, 200D, Double.MAX_VALUE, 0D, false){
			@Override
			public boolean performReaction(IReactionChamber chamb){
				boolean performed = super.performReaction(chamb);
				if(performed){
					chamb.destroyChamber();
					chamb.addVisualEffect(EnumParticleTypes.SMOKE_NORMAL, Math.random(), Math.random(), Math.random());
				}

				return performed;
			}
		});

		//Phelostogen production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[26], 2)}, new Pair[] {Pair.of(REAGENTS[0], 1), Pair.of(REAGENTS[3], 1)}, REAGENTS[18], -300D, 200D, 0D, false));
		//Aether production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[12], 1)}, new Pair[] {Pair.of(REAGENTS[1], 1), Pair.of(REAGENTS[13], 1), Pair.of(REAGENTS[8], 1)}, REAGENTS[18], -360D, Double.MAX_VALUE, 0D, false));
		//Adamant production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[20], 5)}, new Pair[] {Pair.of(REAGENTS[2], 2), Pair.of(REAGENTS[16], 3)}, REAGENTS[18], 0D, 100D, 0D, false));

		//Gunpowder washing
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[26], 2)}, new Pair[] {Pair.of(REAGENTS[3], 1)}, REAGENTS[4], -300D, 200D, 0D, false));
		//Cinnabar decomposition
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[12], 1)}, new Pair[] {Pair.of(REAGENTS[13], 1), Pair.of(REAGENTS[8], 1)}, null, 360D, Double.MAX_VALUE, 60D, false));
		//Hydrochloric Acid production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[6], 2), Pair.of(REAGENTS[10], 1)}, new Pair[] {Pair.of(REAGENTS[16], 1), Pair.of(REAGENTS[15], 2)}, REAGENTS[4], 90D, Double.MAX_VALUE, 30D, false));
		//Nitric Acid production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[4], 1)}, new Pair[] {Pair.of(REAGENTS[5], 2)}, null, 100D, Double.MAX_VALUE, 70D, true));
		//Aqua Regia production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[5], 1), Pair.of(REAGENTS[15], 3)}, new Pair[] {Pair.of(REAGENTS[11], 4)}, null, -300D, Double.MAX_VALUE, 0D, false));
		//Chlorine gas production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[6], 2), Pair.of(REAGENTS[4], 2)}, new Pair[] {Pair.of(REAGENTS[21], 1), Pair.of(REAGENTS[16], 2)}, null, 20D, 100D, 20D, true));
		//Bedrock decomposition
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[20], 5)}, new Pair[] {Pair.of(REAGENTS[16], 4)}, REAGENTS[11], 0D, 100D, 100D, false));
		//Alchemical Crystal production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[13], 2), Pair.of(REAGENTS[16], 1)}, new Pair[] {Pair.of(REAGENTS[22], 3)}, REAGENTS[25], -300D, -40D, -35D, false));
		//Philosopher's Stone creation
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[3], 1), Pair.of(REAGENTS[13], 1), Pair.of(REAGENTS[14], 1), Pair.of(REAGENTS[16], 1)}, new Pair[] {Pair.of(REAGENTS[18], 4)}, REAGENTS[11], -300D, -20D, -500D, false));
		//Practitioner's Stone creation (destroys chamber if proportions are wrong.)
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[0], 1), Pair.of(REAGENTS[1], 1), Pair.of(REAGENTS[2], 1), Pair.of(REAGENTS[14], 1)}, new Pair[] {Pair.of(REAGENTS[19], 4)}, REAGENTS[11], -300D, -20D, -5000D, false){
			@Override
			public boolean performReaction(IReactionChamber chamb){
				boolean performed = super.performReaction(chamb);
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
		//TODO REACTIONS.add(new CrystalFormationReaction());
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
		//Ruby production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[35], 2), Pair.of(REAGENTS[23], 1)}, new Pair[] {Pair.of(REAGENTS[27], 1)}, REAGENTS[19], -100, 1000, 0,false));
		//Emerald production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[35], 2), Pair.of(REAGENTS[24], 1)}, new Pair[] {Pair.of(REAGENTS[28], 1)}, REAGENTS[19], -100, 1000, 0,false));
		//Diamond production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[35], 2), Pair.of(REAGENTS[25], 1)}, new Pair[] {Pair.of(REAGENTS[29], 1)}, REAGENTS[19], -100, 1000, 0, false));
		//Quartz production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[35], 2), Pair.of(REAGENTS[14], 1)}, new Pair[] {Pair.of(REAGENTS[30], 1)}, REAGENTS[19], -100, 1000, 0, false));
		//Gold production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[11], 1), Pair.of(REAGENTS[13], 5)}, new Pair[] {Pair.of(REAGENTS[14], 5)}, REAGENTS[2], -40D, 560D, 10D, false));
		//Copper production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[3], 1), Pair.of(REAGENTS[13], 5)}, new Pair[] {Pair.of(REAGENTS[23], 5)}, REAGENTS[2], -40D, 560D, 10D, false));
		//Iron production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[10], 1), Pair.of(REAGENTS[13], 5)}, new Pair[] {Pair.of(REAGENTS[24], 5)}, REAGENTS[2], -40D, 560D, 10D, false));
		//Tin production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[5], 1), Pair.of(REAGENTS[13], 5)}, new Pair[] {Pair.of(REAGENTS[25], 5)}, REAGENTS[2], -40D, 560D, 10D, false));
		//Gold decomposition
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[14], 5)}, new Pair[] {Pair.of(REAGENTS[11], 1), Pair.of(REAGENTS[13], 5)}, REAGENTS[18], -40D, 560D, -10D, false));
		//Copper decomposition
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[23], 5)}, new Pair[] {Pair.of(REAGENTS[3], 1), Pair.of(REAGENTS[13], 5)}, REAGENTS[18], -40D, 560D, -10D, false));
		//Iron decomposition
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[24], 5)}, new Pair[] {Pair.of(REAGENTS[10], 1), Pair.of(REAGENTS[13], 5)}, REAGENTS[18], -40D, 560D, -10D, false));
		//Tin decomposition
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[25], 5)}, new Pair[] {Pair.of(REAGENTS[5], 1), Pair.of(REAGENTS[13], 5)}, REAGENTS[18], -40D, 560D, -10D, false));
		//Ignus Infernum production
		if(ModConfig.getConfigBool(ModConfig.allowHellfire, true)){
			REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[0], 5), Pair.of(REAGENTS[3], 1), Pair.of(REAGENTS[21], 1), Pair.of(REAGENTS[36], 2)}, new Pair[] {Pair.of(REAGENTS[37], 1)}, REAGENTS[19], 2250D, Short.MAX_VALUE, -200D, false));
		}
		//Densus production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[1], 3), Pair.of(REAGENTS[28], 1)}, new Pair[] {Pair.of(REAGENTS[38], 3)}, null, -273D, 30D, -5D, true));
		//Anti-Densus production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[38], 1), Pair.of(REAGENTS[0], 1)}, new Pair[] {Pair.of(REAGENTS[39], 2)}, null, 200D, 3000D, 20D, true));
		//Bedrock production
		REACTIONS.add(new SimpleTransparentReaction(new Pair[] {Pair.of(REAGENTS[2], 1), Pair.of(REAGENTS[16], 4)}, new Pair[] {Pair.of(REAGENTS[20], 5)}, REAGENTS[19], 0D, 100D, 0D, false));
	}
}
