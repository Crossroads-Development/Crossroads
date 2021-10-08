package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.EnumPath;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.witchcraft.EmbryoLab;
import com.Da_Technomancer.crossroads.entity.EntityHopperHawk;
import com.Da_Technomancer.crossroads.items.alchemy.*;
import com.Da_Technomancer.crossroads.items.itemSets.*;
import com.Da_Technomancer.crossroads.items.technomancy.*;
import com.Da_Technomancer.crossroads.items.witchcraft.*;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public final class CRItems{

	public static final CreativeModeTab TAB_CROSSROADS = new CreativeModeTab(Crossroads.MODID){
		@Override
		public ItemStack makeIcon(){
			return new ItemStack(omnimeter, 1);
		}
	};

	public static final CreativeModeTab TAB_HEAT_CABLE = new CreativeModeTab("heat_cable"){
		@Override
		public ItemStack makeIcon(){
			return new ItemStack(HeatCableFactory.HEAT_CABLES.get(HeatInsulators.WOOL), 1);
		}
	};

	public static final CreativeModeTab TAB_GEAR = new CreativeModeTab("gear"){
		@Override
		public ItemStack makeIcon(){
			return smallGear.withMaterial(GearFactory.findMaterial("copper"), 1);
		}
	};

	public static final Rarity BOBO_RARITY = Rarity.EPIC;

	public static CheatWandRotary debugGearWriter;
	public static CheatWandHeat debugHeatWriter;
	public static Item dustSalt;
	//	public static MashedPotato mashedPotato;
	public static HandCrank handCrank;
	public static OmniMeter omnimeter;
	public static Vacuum vacuum;
	public static MagentaBread magentaBread;
	public static EdibleBlob edibleBlob;
	public static RainIdol rainIdol;
	public static Item pureQuartz;
	public static Item brightQuartz;
	public static Item lensArray;
	public static SquidHelmet squidHelmet;
	public static PigZombieChestsplate pigZombieChestplate;
	public static CowLeggings cowLeggings;
	public static ChickenBoots chickenBoots;
	public static ChaosRod chaosRod;
	public static ArmorGoggles armorGoggles;
	public static ArmorPropellerPack propellerPack;
	public static StaffTechnomancy staffTechnomancy;
	public static BeamCage beamCage;
	//	public static PrototypePistol pistol;
//	public static PrototypeWatch watch;
	public static Item adamant;
	public static Item sulfur;
	public static Item vanadiumOxide;
	public static PhilStone philosopherStone;
	public static PhilStone practitionerStone;
	public static Item alchCrystal;
	public static Item alchemySalt;
	public static Phial phialGlass;
	public static FlorenceFlask florenceFlaskGlass;
	public static Shell shellGlass;
	public static Phial phialCrystal;
	public static FlorenceFlask florenceFlaskCrystal;
	public static Shell shellCrystal;
	public static LiechWrench liechWrench;
	public static LeydenJar leydenJar;
	public static Nitroglycerin nitroglycerin;
	public static PoisonVodka poisonVodka;
	public static Item solidQuicksilver;
	public static Item solidFusas;
	public static Item solidEldrine;
	public static Item solidStasisol;
	public static Item solidVoltus;
	public static Item solidElemEnchant;
	public static Item solidElemExpansion;
	public static Item solidDensus;
	public static Item solidAntiDensus;
	public static Item solidFortis;
	public static Item solidVitriol;
	public static Item solidMuriatic;
	public static Item solidRegia;
	public static Item solidCavorite;
	public static Item solidChlorine;
	public static Item solidSO2;
	public static Item bedrockDust;
	public static FlyingMachine flyingMachine;
	public static TeslaRay teslaRay;
	public static Slag slag;
	//	public static LinkingTool linkingTool;
	public static DampingPowder dampingPowder;
	public static BoboRod boboRod;
	public static RecallDevice recallDevice;
	public static SpringGun springGun;
	public static Whirligig whirligig;
	public static PathSigil sigilAlch;
	public static PathSigil sigilTech;
	public static PathSigil sigilWitch;
	public static ArmorEnviroBoots armorEnviroBoots;
	public static ArmorToolbelt armorToolbelt;
	public static Item bloodSampleEmpty;
	public static BloodSample bloodSample;
	public static BloodSample separatedBloodSample;
	public static PotionExtension potionExtension;
	public static Syringe syringe;
	public static Item mushroomDust;
	public static WheezewortSeeds wheezewortSeeds;
	public static SoulCluster soulCluster;
	public static SoulCluster soulShard;
	public static Embryo embryo;
	public static GeneticSpawnEgg geneticSpawnEgg;
	public static Item mutagen;
	public static VillagerBrain villagerBrain;
	public static BrainHarvester brainHarvester;
	public static Item hopperHawkSpawnEgg;

	public static OreProfileItem oreGravel;
	public static OreProfileItem oreClump;

	public static Item ironDust;
	public static Item goldDust;
	public static Item copperDust;
	public static Item tinDust;

	public static Axle axle;
	public static Clutch clutch;
	public static Clutch invClutch;
	public static BasicGear smallGear;
	public static ToggleGear toggleGear;
	public static ToggleGear invToggleGear;
	public static LargeGear largeGear;
	public static AxleMount axleMount;
	public static GearFacade gearFacadeStoneBrick;
	public static GearFacade gearFacadeCobble;
	public static GearFacade gearFacadeIron;
	public static GearFacade gearFacadeGlass;

	public static final ArrayList<Item> toRegister = new ArrayList<>();

	public static void init(){
		debugGearWriter = new CheatWandRotary();
		handCrank = new HandCrank();
		debugHeatWriter = new CheatWandHeat();
		toRegister.add(dustSalt = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("dust_salt"));
//		mashedPotato = new MashedPotato();
		omnimeter = new OmniMeter();
		vacuum = new Vacuum();
		magentaBread = new MagentaBread();
		edibleBlob = new EdibleBlob();
		rainIdol = new RainIdol();
		toRegister.add(pureQuartz = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("pure_quartz"));
		toRegister.add(brightQuartz = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("bright_quartz"));
		toRegister.add(lensArray = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("lens_array"));
		squidHelmet = new SquidHelmet();
		pigZombieChestplate = new PigZombieChestsplate();
		cowLeggings = new CowLeggings();
		chickenBoots = new ChickenBoots();
		chaosRod = new ChaosRod();
		armorGoggles = new ArmorGoggles();
		staffTechnomancy = new StaffTechnomancy();
		beamCage = new BeamCage();
//		pistol = new PrototypePistol();
//		watch = new PrototypeWatch();
		toRegister.add(adamant = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("adamant"));
		toRegister.add(sulfur = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("sulfur"));
		toRegister.add(vanadiumOxide = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("vanadium_oxide"));
		philosopherStone = new PhilStone(false);
		practitionerStone = new PhilStone(true);
		toRegister.add(alchCrystal = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("alch_crystal"));
		toRegister.add(alchemySalt = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("waste_salt"));
		phialGlass = new Phial(false);
		florenceFlaskGlass = new FlorenceFlask(false);
		shellGlass = new Shell(false);
		phialCrystal = new Phial(true);
		florenceFlaskCrystal = new FlorenceFlask(true);
		shellCrystal = new Shell(true);
		liechWrench = new LiechWrench();
		leydenJar = new LeydenJar();
		nitroglycerin = new Nitroglycerin();
		poisonVodka = new PoisonVodka();
		toRegister.add(solidQuicksilver = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_quicksilver"));
		toRegister.add(solidFusas = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_fusas"));
		toRegister.add(solidEldrine = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_eldrine"));
		toRegister.add(solidStasisol = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_stasisol"));
		toRegister.add(solidVoltus = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_voltus"));
		toRegister.add(solidElemEnchant = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_elem_enchantment"));
		toRegister.add(solidElemExpansion = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_elem_expansion"));
		toRegister.add(solidDensus = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_densus"));
		toRegister.add(solidAntiDensus = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_anti_densus"));
		toRegister.add(solidFortis = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_fortis"));
		toRegister.add(solidVitriol = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_vitriol"));
		toRegister.add(solidMuriatic = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_muriatic"));
		toRegister.add(solidRegia = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_regia"));
		toRegister.add(solidCavorite = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_cavorite"));
		toRegister.add(solidSO2 = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_sulfur_dioxide"));
		toRegister.add(solidChlorine = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("solid_chlorine"));
		toRegister.add(bedrockDust = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("dust_bedrock"));
		flyingMachine = new FlyingMachine();
		teslaRay = new TeslaRay();
		slag = new Slag();
//		linkingTool = new LinkingTool();
		dampingPowder = new DampingPowder();
		boboRod = new BoboRod();
		recallDevice = new RecallDevice();
		springGun = new SpringGun();
		whirligig = new Whirligig();
		axle = new Axle();
		clutch = new Clutch(false);
		invClutch = new Clutch(true);
		smallGear = new BasicGear();
		toggleGear = new ToggleGear(false);
		invToggleGear = new ToggleGear(true);
		largeGear = new LargeGear();
		axleMount = new AxleMount();
		gearFacadeStoneBrick = new GearFacade(GearFacade.FacadeBlock.STONE_BRICK);
		gearFacadeCobble = new GearFacade(GearFacade.FacadeBlock.COBBLE);
		gearFacadeIron = new GearFacade(GearFacade.FacadeBlock.IRON);
		gearFacadeGlass = new GearFacade(GearFacade.FacadeBlock.GLASS);
		oreGravel = (OreProfileItem) new OreProfileItem(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("ore_gravel");
		oreClump = (OreProfileItem) new OreProfileItem(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("ore_clump");
		toRegister.add(ironDust = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("dust_iron"));
		toRegister.add(goldDust = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("dust_gold"));
		toRegister.add(copperDust = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("dust_copper"));
		toRegister.add(tinDust = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("dust_tin"));
		sigilAlch = new PathSigil(EnumPath.ALCHEMY);
		sigilTech = new PathSigil(EnumPath.TECHNOMANCY);
		sigilWitch = new PathSigil(EnumPath.WITCHCRAFT);
		propellerPack = new ArmorPropellerPack();
		armorEnviroBoots = new ArmorEnviroBoots();
		armorToolbelt = new ArmorToolbelt();
		toRegister.add(bloodSampleEmpty = new Item(new Item.Properties().stacksTo(1).tab(TAB_CROSSROADS)).setRegistryName("blood_sample_empty"));
		bloodSample = new BloodSample();
		separatedBloodSample = new BloodSample("separated_blood_sample");
		potionExtension = new PotionExtension();
		syringe = new Syringe();
		toRegister.add(mushroomDust = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("mushroom_dust"));
		wheezewortSeeds = new WheezewortSeeds();
		soulCluster = new SoulCluster(true);
		soulShard = new SoulCluster(false);
		embryo = new Embryo();
		geneticSpawnEgg = new GeneticSpawnEgg();
		toRegister.add(mutagen = new Item(new Item.Properties().tab(TAB_CROSSROADS)).setRegistryName("mutagen"));
		villagerBrain = new VillagerBrain();
		brainHarvester = new BrainHarvester();
		toRegister.add(hopperHawkSpawnEgg = new SpawnEggItem(EntityHopperHawk.type, 0x555555, 0x999999, (new Item.Properties()).tab(CreativeModeTab.TAB_MISC)).setRegistryName("hopper_hawk_spawn_egg"));
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientInit(){
		//Coloring
		ItemColors itemColor = Minecraft.getInstance().getItemColors();
		//Alchemy containers
		itemColor.register((ItemStack stack, int layer) -> layer == 0 ? AbstractGlassware.getColorRGB(stack) : -1, phialGlass, florenceFlaskGlass, shellGlass, phialCrystal, florenceFlaskCrystal, shellCrystal);

		//Gears and ore processing dusts
		ItemColor oreItemColoring = (ItemStack stack, int tintIndex) -> {
			if(tintIndex == 0){
				return -1;
			}
			OreSetup.OreProfile mat = OreProfileItem.getProfile(stack);
			return mat == null ? -1 : mat.getColor().getRGB();
		};
		itemColor.register(oreItemColoring, oreGravel, oreClump, axle, smallGear, largeGear, clutch, invClutch, toggleGear, invToggleGear, axleMount);

		//Genetic spawn egg
		ItemColor eggItemColoring = (ItemStack stack, int tintIndex) -> {
			//Lookup the mob's vanilla egg, copy the colors
			//If it doesn't have an egg, fallback to defaults
			if(stack.getItem() instanceof GeneticSpawnEgg){
				EntityTemplate template = ((GeneticSpawnEgg) stack.getItem()).getEntityTypeData(stack);
				EntityType<?> type = template.getEntityType();
				if(type != null){
					SpawnEggItem vanillaEgg = SpawnEggItem.byId(type);
					if(vanillaEgg != null){
						return vanillaEgg.getColor(tintIndex);
					}
				}
			}
			//Fallback to defaults
			//Which are hideous, but that's what you get for not registering spawn eggs
			return tintIndex == 0 ? Color.CYAN.getRGB() : Color.GREEN.getRGB();
		};
		itemColor.register(eggItemColoring, geneticSpawnEgg);

		//Properties
		//Whirligig rotation
		ItemProperties.register(whirligig, new ResourceLocation("angle"), (ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) -> {
			if(entity == null || entity.getUseItem() != stack){
				return 0;
			}
			//The following gets the angle in degrees of the blades based on:
			//Ticks since started rotating
			//Speed (wind level) at current time
			//Assumption that speed decreased since start at -WIND_USE_RATE/tick
			//Assumption that angle started at 0
			//TODO re-work this formula, make it more reliable, cap at wind = 0
			int useTime = entity.getTicksUsingItem();//Method is poorly MCP mapped- actually gives number of ticks since started using
			float currSpeed = (float) whirligig.getWindLevel(stack) / 20F;//Converted to rad/t
			float deaccel = (float) Whirligig.WIND_USE_RATE / 20F;//Converted to rad/t/t
			float angle = (currSpeed * useTime + deaccel * useTime * useTime / 2F) % (2F * (float) Math.PI);
			angle = (float) Math.toDegrees(angle);
			return angle;
		});
		//Technomancy armor
		ItemPropertyFunction technoArmorPropertyGetter = (ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) -> TechnomancyArmor.isReinforced(stack) ? TechnomancyArmor.hasDurability(stack) ? 2F : 1F : 0F;
		ItemProperties.register(armorGoggles, new ResourceLocation("protection"), technoArmorPropertyGetter);
		ItemProperties.register(propellerPack, new ResourceLocation("protection"), technoArmorPropertyGetter);
		ItemProperties.register(armorToolbelt, new ResourceLocation("protection"), technoArmorPropertyGetter);
		ItemProperties.register(armorEnviroBoots, new ResourceLocation("protection"), technoArmorPropertyGetter);
		//Rotting samples
		ItemPropertyFunction rottingPropertyGetter = (ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) -> stack.getItem() instanceof IPerishable && ((IPerishable) stack.getItem()).isSpoiled(stack, world) ? 1F : 0F;
		ItemProperties.register(bloodSample, new ResourceLocation("spoiled"), rottingPropertyGetter);
		ItemProperties.register(separatedBloodSample, new ResourceLocation("spoiled"), rottingPropertyGetter);
		ItemProperties.register(potionExtension, new ResourceLocation("spoiled"), rottingPropertyGetter);
		ItemProperties.register(embryo, new ResourceLocation("spoiled"), rottingPropertyGetter);
		//Syringe treatment
		ItemPropertyFunction syringePropertyGetter = (ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) -> CRItems.syringe.isTreated(stack) ? 1 : 0;
		ItemProperties.register(syringe, new ResourceLocation("treated"), syringePropertyGetter);
	}

	/**
	 * Registers dispenser behaviours for any item where the behaviour isn't being registered by the item itself for whatever reason
	 */
	public static void registerDispenserOverrides(){
		registerDispenserOverride(EmbryoLab.DISPENSE_ONTO_EMBRYO_LAB, CRItems.separatedBloodSample, Items.NAME_TAG, CRItems.soulCluster, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
	}

	/**
	 * Registers a dispenser behaviour for an item where the item will attempt to do the override behaviour.
	 * If the override behaviour is not successful, it performs the behaviour registered to the item before the method call
	 * This is used for adding dispenser functionality to an item without removing some existing other dispenser functionality on the same item
	 * @param overrideBehaviour The new behaviour to add to the item. If not successful when executed, the fallback behaviour will be used
	 * @param items All items to override the dispenser behaviour for
	 */
	private static void registerDispenserOverride(OptionalDispenseItemBehavior overrideBehaviour, Item... items){
		for(Item item : items){
			DispenserBlock.registerBehavior(item, new FallbackDispenseBehaviour(overrideBehaviour, DispenserSubclass.INSTANCE.getDispenseMethod(new ItemStack(item))));
		}
	}

	private static class DispenserSubclass extends DispenserBlock{

		/**
		 * This class solely exists to get access to the private static dispense behaviour map
		 * There is a protected non-static accessor we create an instance of this subclass to access
		 */
		public static final DispenserSubclass INSTANCE = new DispenserSubclass(CRBlocks.getRockProperty());

		private DispenserSubclass(Properties dummyProp){
			super(dummyProp);
		}

		@Override
		public DispenseItemBehavior getDispenseMethod(ItemStack stack){
			return super.getDispenseMethod(stack);
		}
	}

	private static class FallbackDispenseBehaviour implements DispenseItemBehavior{

		private final OptionalDispenseItemBehavior overrideBehaviour;
		private final DispenseItemBehavior fallbackBehaviour;

		public FallbackDispenseBehaviour(@Nullable OptionalDispenseItemBehavior overrideBehaviour, @Nonnull DispenseItemBehavior fallbackBehaviour){
			this.overrideBehaviour = overrideBehaviour;
			this.fallbackBehaviour = fallbackBehaviour;
		}

		@Override
		public ItemStack dispense(BlockSource source, ItemStack stack){
			if(overrideBehaviour != null){
				overrideBehaviour.setSuccess(false);
				ItemStack result = overrideBehaviour.dispense(source, stack);
				if(overrideBehaviour.isSuccess()){
					return result;
				}
			}
			return fallbackBehaviour.dispense(source, stack);
		}
	}
}