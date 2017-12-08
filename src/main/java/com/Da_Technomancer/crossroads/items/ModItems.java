package com.Da_Technomancer.crossroads.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.items.alchemy.FlorenceFlask;
import com.Da_Technomancer.crossroads.items.alchemy.LeydenJar;
import com.Da_Technomancer.crossroads.items.alchemy.LiechWrench;
import com.Da_Technomancer.crossroads.items.alchemy.PhilStone;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import com.Da_Technomancer.crossroads.items.technomancy.ModuleGoggles;
import com.Da_Technomancer.crossroads.items.technomancy.PrototypePistol;
import com.Da_Technomancer.crossroads.items.technomancy.PrototypeWatch;
import com.Da_Technomancer.crossroads.items.technomancy.StaffTechnomancy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModItems{

	public static final CreativeTabs tabCrossroads = new CreativeTabs(Main.MODID){
		@Override
		public ItemStack getTabIconItem(){
			return new ItemStack(omnimeter, 1);
		}
	};
	public static final CreativeTabs tabHeatCable = new CreativeTabs("heatCable"){
		@Override
		public ItemStack getTabIconItem(){
			return new ItemStack(HeatCableFactory.HEAT_CABLES.get(HeatInsulators.WOOL), 1);
		}
	};
	public static final CreativeTabs tabGear = new CreativeTabs("gear"){
		@Override
		public ItemStack getTabIconItem(){
			return new ItemStack(GearFactory.BASIC_GEARS.get(GearTypes.BRONZE));
		}
	};
	protected static final ArmorMaterial BOBO = EnumHelper.addArmorMaterial("BOBO", Main.MODID + ":bobo", 100, new int[4], 0, SoundEvents.ENTITY_HORSE_DEATH, 0F).setRepairItem(new ItemStack(Items.POISONOUS_POTATO));
	public static final ArmorMaterial TECHNOMANCY = EnumHelper.addArmorMaterial("TECHNOMANCY", "chain", 0, new int[4], 0, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0);

	public static DebugGearWriter debugGearWriter;
	public static DebugHeatWriter debugHeatWriter;
	public static BasicItem dustSalt;
	public static MashedPotato mashedPotato;
	public static HandCrank handCrank;
	public static ObsidianCuttingKit obsidianKit;
	public static BasicItem dustCopper;
	public static Thermometer thermometer;
	public static FluidGauge fluidGauge;
	public static Speedometer speedometer;
	public static OmniMeter omnimeter;
	public static Vacuum vacuum;
	public static MagentaBread magentaBread;
	public static ItemCandleLily itemCandleLilypad;
	public static EdibleBlob edibleBlob;
	public static RainIdol rainIdol;
	public static BasicItem pureQuartz;
	public static BasicItem luminescentQuartz;
	public static BasicItem lensArray;
	public static BasicItem invisItem;
	public static SquidHelmet squidHelmet;
	public static PigZombieChestsplate pigZombieChestplate;
	public static CowLeggings cowLeggings;
	public static ChickenBoots chickenBoots;
	public static ChaosRod chaosRod;
	public static BasicItem voidCrystal;
	public static ModuleGoggles moduleGoggles;
	public static StaffTechnomancy staffTechnomancy;
	public static BeamCage beamCage;
	public static PrototypePistol pistol;
	public static PrototypeWatch watch;
	public static BasicItem adamant;
	public static BasicItem sulfur;
	public static BasicItem vanadium;
	public static BasicItem vanadiumVOxide;
	public static PhilStone philosopherStone;
	public static BasicItem practitionerStone;
	public static BasicItem alchCrystal;
	public static BasicItem wasteSalt;
	public static FlorenceFlask florenceFlask;
	public static BasicItem wrench;
	public static LiechWrench liechWrench;
	public static LeydenJar leydenJar;

	/**
	 * Registers the model location for items. Item: item; Integer: the meta value to register for; ModelResourceLocation: The location to map to. 
	 */
	public static final HashMap<Pair<Item, Integer>, ModelResourceLocation> toClientRegister = new HashMap<Pair<Item, Integer>, ModelResourceLocation>();
	public static final ArrayList<Item> toRegister = new ArrayList<Item>();

	/**
	 * Convenience method to add an Item to the toClientRegister map. 
	 * @param item
	 * @return
	 */
	public static <T extends Item> T itemAddQue(T item){
		toClientRegister.put(Pair.of(item, 0), new ModelResourceLocation(item.getRegistryName(), "inventory"));
		return item;
	}

	public static final void init(){
		debugGearWriter = new DebugGearWriter();
		handCrank = new HandCrank();
		debugHeatWriter = new DebugHeatWriter();
		dustCopper = new BasicItem("dust_copper", "dustCopper");
		dustSalt = new BasicItem("dust_salt", "dustSalt");
		obsidianKit = new ObsidianCuttingKit();
		mashedPotato = new MashedPotato();
		thermometer = new Thermometer();
		fluidGauge = new FluidGauge();
		speedometer = new Speedometer();
		omnimeter = new OmniMeter();
		vacuum = new Vacuum();
		magentaBread = new MagentaBread();
		itemCandleLilypad = new ItemCandleLily();
		edibleBlob = new EdibleBlob();
		rainIdol = new RainIdol();
		pureQuartz = new BasicItem("pure_quartz", "gemQuartz");
		luminescentQuartz = new BasicItem("luminescent_quartz");
		lensArray = new BasicItem("lens_array");
		invisItem = new BasicItem("invis_item", null, false);
		squidHelmet = new SquidHelmet();
		pigZombieChestplate = new PigZombieChestsplate();
		cowLeggings = new CowLeggings();
		chickenBoots = new ChickenBoots();
		chaosRod = new ChaosRod();
		voidCrystal = new BasicItem("void_crystal");
		moduleGoggles = new ModuleGoggles();
		staffTechnomancy = new StaffTechnomancy();
		beamCage = new BeamCage();
		pistol = new PrototypePistol();
		watch = new PrototypeWatch();
		adamant = new BasicItem("adamant");
		sulfur = new BasicItem("sulfur", "dustSulfur");
		vanadium = new BasicItem("vanadium");
		vanadiumVOxide = new BasicItem("vanadium_5_oxide");
		philosopherStone = new PhilStone();
		practitionerStone = new BasicItem("prac_stone");
		alchCrystal = new BasicItem("alch_crystal");
		wasteSalt = new BasicItem("waste_salt");
		florenceFlask = new FlorenceFlask();
		if(ModConfig.getConfigBool(ModConfig.addWrench, false)){
			wrench = new BasicItem("wrench"){
				@Override
				public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player){
					return true;
				}
			};
		}
		liechWrench = new LiechWrench();
		leydenJar = new LeydenJar();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels(){
		for(Entry<Pair<Item, Integer>, ModelResourceLocation> modeling : toClientRegister.entrySet()){
			ModelLoader.setCustomModelResourceLocation(modeling.getKey().getLeft(), modeling.getKey().getRight(), modeling.getValue());
		}
		toClientRegister.clear();
	}
}