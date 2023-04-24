package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRReflection;
import com.Da_Technomancer.crossroads.api.EnumPath;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.witchcraft.EmbryoLab;
import com.Da_Technomancer.crossroads.entity.EntityHopperHawk;
import com.Da_Technomancer.crossroads.items.alchemy.*;
import com.Da_Technomancer.crossroads.items.item_sets.*;
import com.Da_Technomancer.crossroads.items.technomancy.*;
import com.Da_Technomancer.crossroads.items.witchcraft.*;
import com.Da_Technomancer.essentials.api.ReflectionUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public static final Rarity CREATIVE_RARITY = Rarity.RARE;

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
	public static BloodCompass bloodCompass;
	public static Item mainspring;

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

	public static final HashMap<String, Item> toRegister = new HashMap<>();

	public static void init(){
		debugGearWriter = new CheatWandRotary();
		handCrank = new HandCrank();
		debugHeatWriter = new CheatWandHeat();
		dustSalt = MiscUtil.putReturn(toRegister, "dust_salt", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
//		mashedPotato = new MashedPotato();
		omnimeter = new OmniMeter();
		vacuum = new Vacuum();
		magentaBread = new MagentaBread();
		edibleBlob = new EdibleBlob();
		rainIdol = new RainIdol();
		pureQuartz = MiscUtil.putReturn(toRegister, "pure_quartz", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		brightQuartz = MiscUtil.putReturn(toRegister, "bright_quartz", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		lensArray = MiscUtil.putReturn(toRegister, "lens_array", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
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
		adamant = MiscUtil.putReturn(toRegister, "adamant", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		sulfur = MiscUtil.putReturn(toRegister, "sulfur", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		vanadiumOxide = MiscUtil.putReturn(toRegister, "vanadium_oxide", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		philosopherStone = new PhilStone(false);
		practitionerStone = new PhilStone(true);
		alchCrystal = MiscUtil.putReturn(toRegister, "alch_crystal", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		alchemySalt = MiscUtil.putReturn(toRegister, "waste_salt", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
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
		solidQuicksilver = MiscUtil.putReturn(toRegister, "solid_quicksilver", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidFusas = MiscUtil.putReturn(toRegister, "solid_fusas", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidEldrine = MiscUtil.putReturn(toRegister, "solid_eldrine", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidStasisol = MiscUtil.putReturn(toRegister, "solid_stasisol", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidVoltus = MiscUtil.putReturn(toRegister, "solid_voltus", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidElemEnchant = MiscUtil.putReturn(toRegister, "solid_elem_enchantment", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidElemExpansion = MiscUtil.putReturn(toRegister, "solid_elem_expansion", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidDensus = MiscUtil.putReturn(toRegister, "solid_densus", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidAntiDensus = MiscUtil.putReturn(toRegister, "solid_anti_densus", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidFortis = MiscUtil.putReturn(toRegister, "solid_fortis", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidVitriol = MiscUtil.putReturn(toRegister, "solid_vitriol", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidMuriatic = MiscUtil.putReturn(toRegister, "solid_muriatic", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidRegia = MiscUtil.putReturn(toRegister, "solid_regia", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidCavorite = MiscUtil.putReturn(toRegister, "solid_cavorite", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidSO2 = MiscUtil.putReturn(toRegister, "solid_sulfur_dioxide", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		solidChlorine = MiscUtil.putReturn(toRegister, "solid_chlorine", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		bedrockDust = MiscUtil.putReturn(toRegister, "dust_bedrock", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
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
		oreGravel = (OreProfileItem) MiscUtil.putReturn(toRegister, "ore_gravel", new OreProfileItem(new Item.Properties().tab(TAB_CROSSROADS)));
		oreClump = (OreProfileItem) MiscUtil.putReturn(toRegister, "ore_clump", new OreProfileItem(new Item.Properties().tab(TAB_CROSSROADS)));
		ironDust = MiscUtil.putReturn(toRegister, "dust_iron", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		goldDust = MiscUtil.putReturn(toRegister, "dust_gold", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		copperDust = MiscUtil.putReturn(toRegister, "dust_copper", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		tinDust = MiscUtil.putReturn(toRegister, "dust_tin", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		sigilAlch = new PathSigil(EnumPath.ALCHEMY);
		sigilTech = new PathSigil(EnumPath.TECHNOMANCY);
		sigilWitch = new PathSigil(EnumPath.WITCHCRAFT);
		propellerPack = new ArmorPropellerPack();
		armorEnviroBoots = new ArmorEnviroBoots();
		armorToolbelt = new ArmorToolbelt();
		bloodSampleEmpty = new BloodSampleEmpty();
		bloodSample = new BloodSample();
		separatedBloodSample = new BloodSample("separated_blood_sample");
		potionExtension = new PotionExtension();
		syringe = new Syringe();
		mushroomDust = MiscUtil.putReturn(toRegister, "mushroom_dust", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		wheezewortSeeds = new WheezewortSeeds();
		soulCluster = new SoulCluster(true);
		soulShard = new SoulCluster(false);
		embryo = new Embryo();
		geneticSpawnEgg = new GeneticSpawnEgg();
		mutagen = MiscUtil.putReturn(toRegister, "mutagen", new Item(new Item.Properties().tab(TAB_CROSSROADS)));
		villagerBrain = new VillagerBrain();
		brainHarvester = new BrainHarvester();
		hopperHawkSpawnEgg = MiscUtil.putReturn(toRegister, "hopper_hawk_spawn_egg", new ForgeSpawnEggItem(() -> EntityHopperHawk.type, 0x555555, 0x999999, (new Item.Properties()).tab(CreativeModeTab.TAB_MISC)));
		bloodCompass = new BloodCompass();
		mainspring = MiscUtil.putReturn(toRegister, "mainspring", new Item(new Item.Properties().tab(TAB_CROSSROADS)){
			@Override
			public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level world, List<Component> tooltip, TooltipFlag flag){
				tooltip.add(Component.translatable("tt.crossroads.mainspring"));
			}
		});

		registerDispenserOverrides();
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientInit(){
		//Properties
		//Whirligig rotation
		ItemProperties.register(whirligig, new ResourceLocation("angle"), (ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int unmapped) -> {
			if(entity == null || entity.getUseItem() != stack){
				return 0;
			}
			//Take 3: Just using animated textures and switching frametime based on discrete speed levels
			float currSpeed = (float) whirligig.getWindLevel(stack);//rad/s
			return currSpeed >= 7 ? 2 : currSpeed >= 5 ? 3 : currSpeed >= 3 ? 4 : currSpeed >= 1 ? 6 : currSpeed > 0 ? 10 : 0;
			/*
			//We use item properties for animation, which you're really not supposed to do
			//While a proper continuous angle function is more accurate on paper, due to the nature of how this method is called,
			//it is better to use a discrete piecewise linear function, which doesn't run the risk of rounding issues or 'skipping' frames

			int useTime = entity.getTicksUsingItem();//Method is poorly MCP mapped- actually gives number of ticks since started using
			float currSpeed = (float) whirligig.getWindLevel(stack);//rad/s
			float deaccel = (float) Whirligig.WIND_USE_RATE / 20F;//rad/s/t
			float startSpeed = currSpeed + useTime * deaccel;//speed when started using, rad/s
			int[] speedTicks = new int[5];//ticks at speed where each position advancements requires 2, 3, 4, 6, 10 ticks
			int[] speedCutoffs = {7, 5, 3, 1, 0};
			speedTicks[0] = Math.max(0, (int) Math.min(useTime, ((startSpeed)-speedCutoffs[0]) / deaccel));
			speedTicks[1] = Math.max(0, (int) Math.min(useTime-speedTicks[0], (Math.min(startSpeed, speedCutoffs[0])-speedCutoffs[1]) / deaccel));
			speedTicks[2] = Math.max(0, (int) Math.min(useTime-speedTicks[0]-speedTicks[1], (Math.min(startSpeed, speedCutoffs[1])-speedCutoffs[2]) / deaccel));
			speedTicks[3] = Math.max(0, (int) Math.min(useTime-speedTicks[0]-speedTicks[1]-speedTicks[2], (Math.min(startSpeed, speedCutoffs[2])-speedCutoffs[2]) / deaccel));
			speedTicks[4] = Math.max(0, (int) Math.min(useTime-speedTicks[0]-speedTicks[1]-speedTicks[2]-speedTicks[3], (Math.min(startSpeed, speedCutoffs[3])-speedCutoffs[2]) / deaccel));
			int totalPosition = speedTicks[0] / 2 + speedTicks[1] / 3 + speedTicks[2] / 4 + speedTicks[3] / 6 + speedTicks[4] / 10;
			return (45 * totalPosition) % 360;
			*/
			/*
			//The following gets the angle in degrees of the blades based on:
			//Ticks since started rotating
			//Speed (wind level) at current time
			//Assumption that speed decreased since start at -WIND_USE_RATE/tick
			//Assumption that angle started at 0
			int useTime = entity.getTicksUsingItem();//Method is poorly MCP mapped- actually gives number of ticks since started using
			float currSpeed = (float) whirligig.getWindLevel(stack) / 20F;//Converted to rad/t
			float deaccel = (float) Whirligig.WIND_USE_RATE / 20F;//Converted to rad/t/t
			float angle = (currSpeed * useTime + deaccel * useTime * useTime / 2F) % (2F * (float) Math.PI);
			angle = (float) Math.toDegrees(angle);
			return angle;
			*/
		});
		//Technomancy armor
		ItemPropertyFunction technoArmorPropertyGetter = (ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int unmapped) -> TechnomancyArmor.isReinforced(stack) ? TechnomancyArmor.hasDurability(stack) ? 2F : 1F : 0F;
		ItemProperties.register(armorGoggles, new ResourceLocation("protection"), technoArmorPropertyGetter);
		ItemProperties.register(propellerPack, new ResourceLocation("protection"), technoArmorPropertyGetter);
		ItemProperties.register(armorToolbelt, new ResourceLocation("protection"), technoArmorPropertyGetter);
		ItemProperties.register(armorEnviroBoots, new ResourceLocation("protection"), technoArmorPropertyGetter);
		//Rotting samples
		ItemPropertyFunction rottingPropertyGetter = (ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int unmapped) -> stack.getItem() instanceof IPerishable && IPerishable.isSpoiled(stack, world) ? 1F : 0F;
		ItemProperties.register(bloodSample, new ResourceLocation("spoiled"), rottingPropertyGetter);
		ItemProperties.register(separatedBloodSample, new ResourceLocation("spoiled"), rottingPropertyGetter);
		ItemProperties.register(potionExtension, new ResourceLocation("spoiled"), rottingPropertyGetter);
		ItemProperties.register(embryo, new ResourceLocation("spoiled"), rottingPropertyGetter);
		//Syringe treatment
		ItemPropertyFunction syringePropertyGetter = (ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int unmapped) -> CRItems.syringe.isTreated(stack) ? 1 : 0;
		ItemProperties.register(syringe, new ResourceLocation("treated"), syringePropertyGetter);
		//Blood compass
		ItemProperties.register(bloodCompass, new ResourceLocation("angle"), new CompassItemPropertyFunction((world, stack, player) -> bloodCompass.getTarget(stack, player, world)));
	}

	/**
	 * Registers dispenser behaviours for any item where the behaviour isn't being registered by the item itself for whatever reason
	 */
	public static void registerDispenserOverrides(){
		registerDispenserOverride(EmbryoLab.DISPENSE_ONTO_EMBRYO_LAB, CRItems.separatedBloodSample, CRItems.bloodSample, Items.NAME_TAG, CRItems.soulCluster, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
	}

	/**
	 * Registers a dispenser behaviour for an item where the item will attempt to do the override behaviour.
	 * If the override behaviour is not successful, it performs the behaviour registered to the item before the method call
	 * This is used for adding dispenser functionality to an item without removing some existing other dispenser functionality on the same item
	 * @param overrideBehaviour The new behaviour to add to the item. If not successful when executed, the fallback behaviour will be used
	 * @param items All items to override the dispenser behaviour for
	 */
	private static void registerDispenserOverride(OptionalDispenseItemBehavior overrideBehaviour, Item... items){
		final Field DISPENSER_MAP = ReflectionUtil.reflectField(CRReflection.DISPENSER_BEHAVIOR_MAP);
		Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY;
		if(DISPENSER_MAP != null){
			try{
				DISPENSER_REGISTRY = (Map<Item, DispenseItemBehavior>) DISPENSER_MAP.get(null);
			}catch(IllegalAccessException | ClassCastException e){
				DISPENSER_REGISTRY = new HashMap<>(0);
				Crossroads.logger.log(Level.ERROR, "Failed to register a dispenser override", e);
			}
		}else{
			DISPENSER_REGISTRY = new HashMap<>(0);
		}
		for(Item item : items){
			DispenserBlock.registerBehavior(item, new FallbackDispenseBehaviour(overrideBehaviour, DISPENSER_REGISTRY.get(item)));
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