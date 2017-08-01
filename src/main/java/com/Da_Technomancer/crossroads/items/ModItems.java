package com.Da_Technomancer.crossroads.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
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
	protected static final ArmorMaterial TECHNOMANCY = EnumHelper.addArmorMaterial("TECHNOMANCY", "chain", 0, new int[4], 0, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0);

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
	@Deprecated
	public static BasicItem diamondWire;
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
		diamondWire = new BasicItem("diamond_wire", "wireDiamond", false){
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
				tooltip.add("DEPRECATED");
				tooltip.add("This item is being removed. Dispose of any you have.");
			}
		};
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
	}

	@SideOnly(Side.CLIENT)
	public static void initModels(){
		for(Entry<Pair<Item, Integer>, ModelResourceLocation> modeling : toClientRegister.entrySet()){
			ModelLoader.setCustomModelResourceLocation(modeling.getKey().getLeft(), modeling.getKey().getRight(), modeling.getValue());
		}
		toClientRegister.clear();
	}
}