package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.effects.alchemy.*;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static com.Da_Technomancer.crossroads.API.alchemy.EnumReagents.*;

@SuppressWarnings("unchecked")
public final class AlchemyCore{

	public static final int MB_PER_REAG = 100;
	//A non-binding reagent count to optimize around
	protected static final int REAGENT_COUNT = EnumReagents.values().length;
	public static final int ALCHEMY_TIME = 2;

	public static final ArrayList<IReaction> REACTIONS = new ArrayList<>();

	public static final PredicateMap<ItemStack, IReagent> ITEM_TO_REAGENT = new PredicateMap<>();
	public static final BiMap<Fluid, IReagent> FLUID_TO_LIQREAGENT = HashBiMap.create(5); // For liquid phase.
	public static final ArrayList<IElementReagent> ELEMENTAL_REAGS = new ArrayList<>(8);

	public static final HashMap<String, IReagent> REAGENTS = new HashMap<>(REAGENT_COUNT);

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
		Color DARK_BLUE_COLOR = new Color(50, 50, 100);
		Color FAINT_GREEN_COLOR = new Color(0, 255, 0, 150);
		// Various effects
		AcidAlchemyEffect ACID_EFFECT = new AcidAlchemyEffect();
		SaltAlchemyEffect SALT_EFFECT = new SaltAlchemyEffect();

		// Reagents
		REAGENTS.put(PHELOSTOGEN.id(), new StaticReagent(PHELOSTOGEN.id(), -275D, -274D, (EnumMatterPhase phase) -> PHELOSTIGEN_COLOR, null, null, 2, new PhelostogenEffect((Integer amount) -> (int) Math.min(8, Math.round(amount / 2D)))){
			@Override
			public boolean isLockedFlame(){
				return true;
			}
		});
		REAGENTS.put(AETHER.id(), new StaticReagent(AETHER.id(), -275D, -274D, (EnumMatterPhase phase) -> FAINT_GREEN_COLOR, null, null, 1, new AetherEffect()));
		REAGENTS.put(ADAMANT.id(), new StaticReagent(ADAMANT.id(), Short.MAX_VALUE - 1, Short.MAX_VALUE, (EnumMatterPhase phase) -> DARK_BLUE_COLOR, (stack) -> stack.getItem() == ModItems.adamant, () -> new ItemStack(ModItems.adamant, 1), 0, null));
		REAGENTS.put(SULFUR.id(), new StaticReagent(SULFUR.id(), 115D, 445D, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_YELLOW_COLOR : phase == EnumMatterPhase.LIQUID ? Color.RED : Color.YELLOW, MiscUtil.oreDictPred("dustSulfur"), () -> MiscUtil.getOredictStack("dustSulfur", 1), 0, null));
		REAGENTS.put(WATER.id(), new StaticReagent(WATER.id(), 0D, 100D, (EnumMatterPhase phase) -> phase == EnumMatterPhase.GAS ? TRANSLUCENT_WHITE_COLOR : TRANSLUCENT_BLUE_COLOR, (stack) -> stack.getItem() == Item.getItemFromBlock(Blocks.PACKED_ICE), () -> new ItemStack(Blocks.PACKED_ICE), 0, null));
		REAGENTS.put(NITRIC_ACID.id(), new StaticReagent(NITRIC_ACID.id(), -40D, 80D, (EnumMatterPhase phase) -> Color.YELLOW, (stack) -> stack.getItem() == ModItems.solidFortis, () -> new ItemStack(ModItems.solidFortis), 0, ACID_EFFECT));// Salt that forms nitric acid, AKA aqua fortis, in water.
		REAGENTS.put(SALT.id(), new StaticReagent(SALT.id(), 800D, 1400D, (EnumMatterPhase phase) -> phase == EnumMatterPhase.LIQUID ? Color.ORANGE : Color.WHITE, MiscUtil.oreDictPred("dustSalt"), () -> MiscUtil.getOredictStack("dustSalt", 1), 0, SALT_EFFECT));// AKA table salt (sodium chloride).
		REAGENTS.put(VANADIUM.id(), new StaticReagent(VANADIUM.id(), 690D, 1750D, (EnumMatterPhase phase) -> Color.YELLOW, (stack) -> stack.getItem() == ModItems.vanadiumOxide, () -> new ItemStack(ModItems.vanadiumOxide), 0, null));// Vanadium (V) oxide. This should decompose at the specified boiling point, but there isn't any real point to adding that.
		REAGENTS.put(SULFUR_DIOXIDE.id(), new StaticReagent(SULFUR_DIOXIDE.id(), -72D, -10D, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, null, 0, null));
		REAGENTS.put(SULFUR_TRIOXIDE.id(), new StaticReagent(SULFUR_TRIOXIDE.id(), 20D, 40D, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, null, null, 0, null));
		REAGENTS.put(SULFURIC_ACID.id(), new StaticReagent(SULFURIC_ACID.id(), 10D, 340D, (EnumMatterPhase phase) -> BROWN_COLOR, (stack) -> stack.getItem() == ModItems.solidVitriol, () -> new ItemStack(ModItems.solidVitriol, 1), 0, ACID_EFFECT));// Hydrogen Sulfate, salt that forms sulfuric acid, AKA Oil of Vitriol, in water.
		REAGENTS.put(AQUA_REGIA.id(), new StaticReagent(AQUA_REGIA.id(), -40D, 200D, (EnumMatterPhase phase) -> Color.ORANGE, (stack) -> stack.getItem() == ModItems.solidRegia, () -> new ItemStack(ModItems.solidRegia), 0, new AquaRegiaAlchemyEffect()));// Shouldn't really be its own substance (actually a mixture of nitric and hydrochloric acid), but the code is greatly simplified by making it a separate substance.
		REAGENTS.put(REDSTONE.id(), new StaticReagent(REDSTONE.id(), 580D, Short.MAX_VALUE, (EnumMatterPhase phase) -> Color.RED, MiscUtil.oreDictPred("dustRedstone"), () -> MiscUtil.getOredictStack("dustRedstone", 1), 0, null));// Mercury (II) sulfide.
		REAGENTS.put(QUICKSILVER.id(), new StaticReagent(QUICKSILVER.id(), -40D, 560D, (EnumMatterPhase phase) -> Color.LIGHT_GRAY, (stack) -> stack.getItem() == ModItems.solidQuicksilver, () -> new ItemStack(ModItems.solidQuicksilver), 0, null));// AKA murcury
		REAGENTS.put(GOLD.id(), new StaticReagent(GOLD.id(), 1100D, 3000D, (EnumMatterPhase phase) -> Color.YELLOW, MiscUtil.oreDictPred("nuggetGold"), () -> MiscUtil.getOredictStack("nuggetGold", 1), 0, null));
		REAGENTS.put(HYDROCHLORIC_ACID.id(), new StaticReagent(HYDROCHLORIC_ACID.id(), -110D, 90D, (EnumMatterPhase phase) -> CLEAR_COLOR, (ItemStack stack) -> stack.getItem() == ModItems.solidMuriatic, () -> new ItemStack(ModItems.solidMuriatic), 0, ACID_EFFECT));// Hydrogen Chloride, salt that forms hydrochloric acid, AKA muriatic acid, in water. Boiling point should be -90, set to 90 due to the alchemy system not allowing gasses to dissolve.
		REAGENTS.put(ALCHEMICAL_SALT.id(), new StaticReagent(ALCHEMICAL_SALT.id(), 900D, 1400D, (EnumMatterPhase phase) -> TRANSLUCENT_WHITE_COLOR, MiscUtil.oreDictPred("dustAlcSalt"), () -> MiscUtil.getOredictStack("dustAlcSalt", 1), 0, SALT_EFFECT));//Any salt byproduct that is too boring to bother adding separately.
		REAGENTS.put(SLAG.id(), new StaticReagent(SLAG.id(), 2000D, 3000D, (EnumMatterPhase phase) -> Color.DARK_GRAY, MiscUtil.oreDictPred("materialSlag"), () -> MiscUtil.getOredictStack("materialSlag", 1), 0, null));
		REAGENTS.put(PHILOSOPHER.id(), new StaticReagent(PHILOSOPHER.id(), Short.MAX_VALUE - 1, Short.MAX_VALUE, (EnumMatterPhase phase) -> FAINT_BLUE_COLOR, (stack) -> stack.getItem() == ModItems.philosopherStone, () -> new ItemStack(ModItems.philosopherStone), 2, null));
		REAGENTS.put(PRACTITIONER.id(), new StaticReagent(PRACTITIONER.id(), Short.MAX_VALUE - 1, Short.MAX_VALUE, (EnumMatterPhase phase) -> FAINT_RED_COLOR, (stack) -> stack.getItem() == ModItems.practitionerStone, () -> new ItemStack(ModItems.practitionerStone), 2, null));
		REAGENTS.put(BEDROCK.id(), new StaticReagent(BEDROCK.id(), Short.MAX_VALUE - 1, Short.MAX_VALUE, (EnumMatterPhase phase) -> Color.GRAY, (stack) -> stack.getItem() == Item.getItemFromBlock(Blocks.BEDROCK), () -> new ItemStack(Blocks.BEDROCK), 0, null));
		REAGENTS.put(CHLORINE.id(), new StaticReagent(CHLORINE.id(), -100D, -35D, (EnumMatterPhase phase) -> TRANSLUCENT_LIME_COLOR, null, null, 0, new ChlorineAlchemyEffect()));
		REAGENTS.put(CRYSTAL.id(), new StaticReagent(CRYSTAL.id(), Short.MAX_VALUE - 1, Short.MAX_VALUE, (EnumMatterPhase phase) -> FAINT_BLUE_COLOR, (stack) -> stack.getItem() == ModItems.alchCrystal, () -> new ItemStack(ModItems.alchCrystal), 0, null));
		REAGENTS.put(COPPER.id(), new StaticReagent(COPPER.id(), 1000D, 2560D, (EnumMatterPhase phase) -> Color.ORANGE, MiscUtil.oreDictPred("nuggetCopper"), () -> MiscUtil.getOredictStack("nuggetCopper", 1), 0, null));
		REAGENTS.put(IRON.id(), new StaticReagent(IRON.id(), 1500D, 2560D, (EnumMatterPhase phase) -> phase == EnumMatterPhase.SOLID ? Color.GRAY : Color.RED, MiscUtil.oreDictPred("nuggetIron"), () -> MiscUtil.getOredictStack("nuggetIron", 1), 0, null));
		REAGENTS.put(TIN.id(), new StaticReagent(TIN.id(), 230D, 2560D, (EnumMatterPhase phase) -> Color.LIGHT_GRAY, MiscUtil.oreDictPred("nuggetTin"), () -> MiscUtil.getOredictStack("nuggetTin", 1), 0, null));
		REAGENTS.put(GUNPOWDER.id(), new StaticReagent(GUNPOWDER.id(), Short.MAX_VALUE - 1, Short.MAX_VALUE, (EnumMatterPhase phase) -> Color.GRAY, MiscUtil.oreDictPred("gunpowder"), () -> MiscUtil.getOredictStack("gunpowder", 1), 0, null));
		REAGENTS.put(RUBY.id(), new StaticReagent(RUBY.id(), 2000D, 3000D, (EnumMatterPhase phase) -> Color.RED, MiscUtil.oreDictPred("gemRuby"), () -> MiscUtil.getOredictStack("gemRuby", 1), 0, null));
		REAGENTS.put(EMERALD.id(), new StaticReagent(EMERALD.id(), 2000D, 3000D, (EnumMatterPhase phase) -> Color.GREEN, MiscUtil.oreDictPred("gemEmerald"), () -> MiscUtil.getOredictStack("gemEmerald", 1), 0, null));//Couldn't find actual figures on melting/boiling points of emerald/diamond, perhaps due to large variance.
		REAGENTS.put(DIAMOND.id(), new StaticReagent(DIAMOND.id(), 2000D, 3000D, (EnumMatterPhase phase) -> Color.CYAN, MiscUtil.oreDictPred("gemDiamond"), () -> MiscUtil.getOredictStack("gemDiamond", 1), 0, null));
		REAGENTS.put(QUARTZ.id(), new StaticReagent(QUARTZ.id(), 2000D, 3000D, (EnumMatterPhase phase) -> Color.WHITE, (stack) -> stack.getItem() == Items.QUARTZ, () -> new ItemStack(Items.QUARTZ), 0, null));
		REAGENTS.put(DENSUS.id(), new StaticReagent(DENSUS.id(), Short.MAX_VALUE - 1, Short.MAX_VALUE, (EnumMatterPhase phase) -> Color.BLUE, MiscUtil.oreDictPred("gemDensus"), () -> MiscUtil.getOredictStack("gemDensus", 1), 0, null));
		REAGENTS.put(ANTI_DENSUS.id(), new StaticReagent(ANTI_DENSUS.id(), Short.MAX_VALUE - 1, Short.MAX_VALUE, (EnumMatterPhase phase) -> Color.ORANGE, MiscUtil.oreDictPred("gemAntiDensus"), () -> MiscUtil.getOredictStack("gemAntiDensus", 1), 0, null));
		REAGENTS.put(CAVORITE.id(), new StaticReagent(CAVORITE.id(), 1000D, 1001D, (EnumMatterPhase phase) -> Color.YELLOW, MiscUtil.oreDictPred("gemCavorite"), () -> MiscUtil.getOredictStack("gemCavorite", 1), 0, null));

		//Dynamic reagents
		REAGENTS.put(ELEM_LIGHT.id(), new ElementalReagent(ELEM_LIGHT.id(), -275, -274, new LumenEffect(), false, EnumBeamAlignments.LIGHT, new Color(200, 255, 255), null));
		REAGENTS.put(ELEM_RIFT.id(), new ElementalReagent(ELEM_RIFT.id(), -100, 350, new EldrineEffect(), true, EnumBeamAlignments.RIFT, Color.MAGENTA, ModItems.solidEldrine));
		REAGENTS.put(ELEM_EQUAL.id(), new ElementalReagent(ELEM_EQUAL.id(), 800, 1800, new StasisolEffect(), false, EnumBeamAlignments.EQUALIBRIUM, new Color(255, 128, 255), ModItems.solidStasisol));
		REAGENTS.put(ELEM_FUSION.id(), new ElementalReagent(ELEM_FUSION.id(), Short.MAX_VALUE - 1, Short.MAX_VALUE, null, false, EnumBeamAlignments.FUSION, new Color(128, 255, 255), ModItems.solidFusas));
		REAGENTS.put(ELEM_CHARGE.id(), new ElementalReagent(ELEM_CHARGE.id(), -275, -274, new VoltusEffect(), true, EnumBeamAlignments.CHARGE, new Color(255, 255, 64, 0), null));
		REAGENTS.put(HELLFIRE.id(), new StaticReagent(HELLFIRE.id(), -275D, -274D, (EnumMatterPhase phase) -> Color.RED, null, null, 2, new PhelostogenEffect((Integer amount) -> (int) Math.min(64, amount * 2D))){
			@Override
			public boolean isLockedFlame(){
				return true;
			}
		});

		FLUID_TO_LIQREAGENT.put(BlockDistilledWater.getDistilledWater(), REAGENTS.get(WATER.id()));
		FLUID_TO_LIQREAGENT.put(BlockMoltenCopper.getMoltenCopper(), REAGENTS.get(COPPER.id()));
		FLUID_TO_LIQREAGENT.put(BlockMoltenIron.getMoltenIron(), REAGENTS.get(IRON.id()));
		FLUID_TO_LIQREAGENT.put(BlockMoltenTin.getMoltenTin(), REAGENTS.get(TIN.id()));
		FLUID_TO_LIQREAGENT.put(BlockMoltenGold.getMoltenGold(), REAGENTS.get(GOLD.id()));

		// Reactions
		// Sulfur combustion.
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFUR.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFUR_DIOXIDE.id()), 1)}, null, 190D, Double.MAX_VALUE, -300D, false){
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
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFUR_DIOXIDE.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFUR_TRIOXIDE.id()), 1)}, REAGENTS.get(VANADIUM.id()), 400D, 620D, -100D, false));
		//Sulfuric Acid production. 
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFUR_TRIOXIDE.id()), 1), new ReagentStack(REAGENTS.get(WATER.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFURIC_ACID.id()), 1)}, null, -300, Double.MAX_VALUE, -100D, false));
		//Gunpowder combustion
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(GUNPOWDER.id()), 3)}, new ReagentStack[] {}, null, 200D, Double.MAX_VALUE, 0D, false){
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

		//Phelostogen production (from Gunpowder)
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(GUNPOWDER.id()), 2)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFUR.id()), 1), new ReagentStack(REAGENTS.get(PHELOSTOGEN.id()), 1)}, REAGENTS.get(PHILOSOPHER.id()), -300D, 200D, 0D, false));
		//Phelostogen production (from Slag)
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(SLAG.id()), 2)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFUR.id()), 1), new ReagentStack(REAGENTS.get(PHELOSTOGEN.id()), 1)}, REAGENTS.get(PHILOSOPHER.id()), -300D, 200D, 0D, false));
		//Aether production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(REDSTONE.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 1), new ReagentStack(REAGENTS.get(AETHER.id()), 1), new ReagentStack(REAGENTS.get(SULFUR_DIOXIDE.id()), 1)}, REAGENTS.get(PHILOSOPHER.id()), -360D, Double.MAX_VALUE, 0D, false));
		//Adamant production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(BEDROCK.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(ADAMANT.id()), 2), new ReagentStack(REAGENTS.get(ALCHEMICAL_SALT.id()), 3)}, REAGENTS.get(PHILOSOPHER.id()), 0D, 100D, 0D, false));

		//Gunpowder washing
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(GUNPOWDER.id()), 2)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFUR.id()), 1)}, REAGENTS.get(WATER.id()), -300D, 100D, 0D, false));
		//Slag washing
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(SLAG.id()), 2)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFUR.id()), 1)}, REAGENTS.get(WATER.id()), -300D, 100D, 0D, false));
		//Cinnabar decomposition
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(REDSTONE.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 1), new ReagentStack(REAGENTS.get(SULFUR_DIOXIDE.id()), 1)}, null, 360D, Double.MAX_VALUE, 60D, false));
		//Hydrochloric Acid production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(SALT.id()), 2), new ReagentStack(REAGENTS.get(SULFURIC_ACID.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(ALCHEMICAL_SALT.id()), 2), new ReagentStack(REAGENTS.get(HYDROCHLORIC_ACID.id()), 1)}, REAGENTS.get(WATER.id()), 90D, Double.MAX_VALUE, 30D, false));
		//Nitric Acid production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(WATER.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(NITRIC_ACID.id()), 1)}, null, 100D, Double.MAX_VALUE, 70D, true));
		//Aqua Regia production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(NITRIC_ACID.id()), 1), new ReagentStack(REAGENTS.get(HYDROCHLORIC_ACID.id()), 3)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(AQUA_REGIA.id()), 4)}, null, -300D, Double.MAX_VALUE, 0D, false));
		//Chlorine gas production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(WATER.id()), 2), new ReagentStack(REAGENTS.get(SALT.id()), 2)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(CHLORINE.id()), 1), new ReagentStack(REAGENTS.get(ALCHEMICAL_SALT.id()), 2)}, null, 20D, 100D, 20D, true));
		//Bedrock decomposition
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(BEDROCK.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(ALCHEMICAL_SALT.id()), 4)}, REAGENTS.get(AQUA_REGIA.id()), 0D, 100D, 100D, false));
		//Alchemical Crystal production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 2), new ReagentStack(REAGENTS.get(ALCHEMICAL_SALT.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(CRYSTAL.id()), 3)}, REAGENTS.get(VANADIUM.id()), -300D, -40D, -35D, false));
		//Philosopher's Stone creation
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(GOLD.id()), 1), new ReagentStack(REAGENTS.get(SULFUR.id()), 1), new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 1), new ReagentStack(REAGENTS.get(ALCHEMICAL_SALT.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(PHILOSOPHER.id()), 4)}, REAGENTS.get(AQUA_REGIA.id()), -300D, -20D, -500D, false));
		//Practitioner's Stone creation (destroys chamber if proportions are wrong.)
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(GOLD.id()), 1), new ReagentStack(REAGENTS.get(PHELOSTOGEN.id()), 1), new ReagentStack(REAGENTS.get(AETHER.id()), 1), new ReagentStack(REAGENTS.get(ADAMANT.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(PRACTITIONER.id()), 4)}, REAGENTS.get(AQUA_REGIA.id()), -300D, -20D, -5000D, false){
			@Override
			public boolean performReaction(IReactionChamber chamb){
				boolean performed = super.performReaction(chamb);
				if(performed){
					ReagentMap reags = chamb.getReagants();
					int phel = reags.getQty(PHELOSTOGEN.id());
					int aeth = reags.getQty(AETHER.id());
					int adam = reags.getQty(ADAMANT.id());
					int gold = reags.getQty(GOLD.id());
					if(Math.max(Math.max(phel, aeth), Math.max(adam, gold)) - Math.min(Math.min(phel, aeth), Math.min(adam, gold)) >= 2){
						chamb.destroyChamber();
					}
				}
				return performed;

			}
		});
		//Lumen production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS.get(ELEM_LIGHT.id())));
		//Eldrine production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS.get(ELEM_RIFT.id())));
		//Stasisol production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS.get(ELEM_EQUAL.id())));
		//Fusas production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS.get(ELEM_FUSION.id())));
		//Voltus production
		REACTIONS.add(new ElementalReaction((IElementReagent) REAGENTS.get(ELEM_CHARGE.id())));
		//Ruby production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(ELEM_FUSION.id()), 2), new ReagentStack(REAGENTS.get(COPPER.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(RUBY.id()), 2)}, REAGENTS.get(PRACTITIONER.id()), -100, 1000, 0,false));
		//Emerald production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(ELEM_FUSION.id()), 2), new ReagentStack(REAGENTS.get(IRON.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(EMERALD.id()), 2)}, REAGENTS.get(PRACTITIONER.id()), -100, 1000, 0,false));
		//Diamond production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(ELEM_FUSION.id()), 2), new ReagentStack(REAGENTS.get(TIN.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(DIAMOND.id()), 2)}, REAGENTS.get(PRACTITIONER.id()), -100, 1000, 0, false));
		//Quartz production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(ELEM_FUSION.id()), 2), new ReagentStack(REAGENTS.get(GOLD.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(QUARTZ.id()), 2)}, REAGENTS.get(PRACTITIONER.id()), -100, 1000, 0, false));
		//Gold production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(AQUA_REGIA.id()), 1), new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(GOLD.id()), 5)}, REAGENTS.get(PRACTITIONER.id()), -40D, 560D, 10D, false));
		//Copper production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFURIC_ACID.id()), 1), new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(COPPER.id()), 5)}, REAGENTS.get(PRACTITIONER.id()), -40D, 560D, 10D, false));
		//Iron production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(HYDROCHLORIC_ACID.id()), 1), new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(IRON.id()), 5)}, REAGENTS.get(PRACTITIONER.id()), -40D, 560D, 10D, false));
		//Tin production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(NITRIC_ACID.id()), 1), new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(TIN.id()), 5)}, REAGENTS.get(PRACTITIONER.id()), -40D, 560D, 10D, false));
		//Gold decomposition
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(GOLD.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(AQUA_REGIA.id()), 1), new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 5)}, REAGENTS.get(PHILOSOPHER.id()), -40D, 560D, -10D, false));
		//Copper decomposition
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(COPPER.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(SULFURIC_ACID.id()), 1), new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 5)}, REAGENTS.get(PHILOSOPHER.id()), -40D, 560D, -10D, false));
		//Iron decomposition
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(IRON.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(HYDROCHLORIC_ACID.id()), 1), new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 5)}, REAGENTS.get(PHILOSOPHER.id()), -40D, 560D, -10D, false));
		//Tin decomposition
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(TIN.id()), 5)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(NITRIC_ACID.id()), 1), new ReagentStack(REAGENTS.get(QUICKSILVER.id()), 5)}, REAGENTS.get(PHILOSOPHER.id()), -40D, 560D, -10D, false));
		//Ignus Infernum production
		if(ModConfig.getConfigBool(ModConfig.allowHellfire, true)){
			REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(PHELOSTOGEN.id()), 5), new ReagentStack(REAGENTS.get(SULFUR_DIOXIDE.id()), 1), new ReagentStack(REAGENTS.get(CHLORINE.id()), 1), new ReagentStack(REAGENTS.get(ELEM_CHARGE.id()), 2)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(HELLFIRE.id()), 1)}, REAGENTS.get(PRACTITIONER.id()), 2250D, Short.MAX_VALUE, -200D, false));
		}
		//Densus production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(ADAMANT.id()), 3), new ReagentStack(REAGENTS.get(EMERALD.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(DENSUS.id()), 3)}, null, -273D, 30D, -5D, true));
		//Anti-Densus production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(DENSUS.id()), 1), new ReagentStack(REAGENTS.get(PHELOSTOGEN.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(ANTI_DENSUS.id()), 1)}, null, 200D, 3000D, 20D, true));
		//Cavorite production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(DENSUS.id()), 1), new ReagentStack(REAGENTS.get(AETHER.id()), 1)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(CAVORITE.id()), 1)}, null, 2000D, 2200D, -100D, true));
		//Bedrock production
		REACTIONS.add(new SimpleTransparentReaction(new ReagentStack[] {new ReagentStack(REAGENTS.get(ADAMANT.id()), 1), new ReagentStack(REAGENTS.get(ALCHEMICAL_SALT.id()), 4)}, new ReagentStack[] {new ReagentStack(REAGENTS.get(BEDROCK.id()), 5)}, REAGENTS.get(PRACTITIONER.id()), 0D, 100D, 0D, false));
	}
}
