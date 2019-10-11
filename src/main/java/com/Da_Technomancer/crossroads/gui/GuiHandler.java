package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.gui.container.*;
import com.Da_Technomancer.crossroads.tileentities.alchemy.HeatLimiterBasicTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamBoilerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.WaterCentrifugeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.FireboxTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.SmelterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.MillstoneTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.MathAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypePortTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneRegistryTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

@Deprecated
public class GuiHandler implements IGuiHandler{

	public static final int MILLSTONE_GUI = 0;
	public static final int FIREBOX_GUI = 1;
	public static final int SMELTER_GUI = 2;
	public static final int STEAM_BOILER_GUI = 3;
	public static final int COLOR_CHART_GUI = 4;
	public static final int REDSTONE_KEYBOARD_GUI = 5;
	public static final int CRAFTER_GUI = 6;
	public static final int PROTOTYPING_GUI = 7;
	public static final int PROTOTYPE_PORT_GUI = 8;
	public static final int REDSTONE_REGISTRY_GUI = 9;
	public static final int WATER_CENTRIFUGE_GUI = 10;
	public static final int HEAT_LIMITER_BASIC_GUI = 11;
	public static final int FAKE_CRAFTER_GUI = 12;
	public static final int STAMP_MILL_GUI = 13;
	public static final int FAT_COLLECTOR_GUI = 14;
	public static final int SALT_REACTOR_GUI = 15;
	public static final int RADIATOR_GUI = 16;
	public static final int CRUCIBLE_GUI = 17;
	public static final int ICEBOX_GUI = 18;
	public static final int FLUID_COOLER_GUI = 19;
	public static final int COPSHOWIUM_CHAMBER_GUI = 20;
	public static final int ORE_CLEANSER_GUI = 21;
	public static final int BLAST_FURNACE_GUI = 22;
	public static final int FAT_FEEDER_GUI = 23;
	public static final int BEAM_EXTRACTOR_GUI = 24;
	public static final int MATH_AXIS_GUI = 25;
	public static final int FLUID_TANK_GUI = 26;
	public static final int REAGENT_FILTER_GUI = 27;

	@Override
	public Object getServerGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z){
		switch(ID){
			case MILLSTONE_GUI:
				return new MillstoneContainer(player.inventory, ((MillstoneTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case FIREBOX_GUI:
				return new FireboxContainer(player.inventory, ((FireboxTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case SMELTER_GUI:
				return new SmelterContainer(player.inventory, ((SmelterTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case STEAM_BOILER_GUI:
				return new SteamBoilerContainer(player.inventory, (SteamBoilerTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
			case COLOR_CHART_GUI:
				return new ColorChartContainer(player, world, new BlockPos(x, y, z));
			case REDSTONE_REGISTRY_GUI:
			case MATH_AXIS_GUI:
			case HEAT_LIMITER_BASIC_GUI:
			case REDSTONE_KEYBOARD_GUI:
				return new BlankContainer();
			case WATER_CENTRIFUGE_GUI:
				return new WaterCentrifugeContainer(player.inventory, (WaterCentrifugeTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
			case CRAFTER_GUI:
				return new DetailedCrafterContainer(player.inventory, new BlockPos(x, y, z), false);
			case PROTOTYPING_GUI:
				return new PrototypingTableContainer(player.inventory, ((PrototypingTableTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case PROTOTYPE_PORT_GUI:
				return new PrototypePortContainer(((PrototypePortTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case FAKE_CRAFTER_GUI:
				return new DetailedCrafterContainer(player.inventory, new BlockPos(x, y, z), true);
			case STAMP_MILL_GUI:
				return new StampMillContainer(player.inventory, (StampMillTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
			case FAT_COLLECTOR_GUI:
				return new FatCollectorContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case SALT_REACTOR_GUI:
				return new SaltReactorContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case RADIATOR_GUI:
				return new RadiatorContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case CRUCIBLE_GUI:
				return new CrucibleContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case ICEBOX_GUI:
				return new IceboxContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case FLUID_COOLER_GUI:
				return new FluidCoolerContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case COPSHOWIUM_CHAMBER_GUI:
				return new CopshowiumCreationChamberContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case ORE_CLEANSER_GUI:
				return new OreCleanserContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case BLAST_FURNACE_GUI:
				return new BlastFurnaceContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case FAT_FEEDER_GUI:
			case FLUID_TANK_GUI:
				return new FatFeederContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case BEAM_EXTRACTOR_GUI:
				return new BeamExtractorContainer(player.inventory, (IInventory) world.getTileEntity(new BlockPos(x, y, z)));
			case REAGENT_FILTER_GUI:
				return new ReagentFilterContainer(player.inventory, (IInventory) world.getTileEntity(new BlockPos(x, y, z)));
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z){
		switch(ID){
			case MILLSTONE_GUI:
				return new MillstoneGuiContainer(player.inventory, ((MillstoneTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case FIREBOX_GUI:
				return new FireboxScreen(player.inventory, ((FireboxTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case SMELTER_GUI:
				return new SmelterScreen(player.inventory, ((SmelterTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case STEAM_BOILER_GUI:
				return new SteamBoilerGuiContainer(player.inventory, (SteamBoilerTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
			case COLOR_CHART_GUI:
				return new ColorChartGuiContainer(player, world, new BlockPos(x, y, z));
			case REDSTONE_KEYBOARD_GUI:
//				return new RedstoneKeyboardGuiContainer(((RedstoneKeyboardTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case WATER_CENTRIFUGE_GUI:
				return new WaterCentrifugeGuiContainer(player.inventory, (WaterCentrifugeTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
			case HEAT_LIMITER_BASIC_GUI:
				return new HeatLimiterBasicGuiContainer(((HeatLimiterBasicTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case CRAFTER_GUI:
				return new DetailedCrafterGuiContainer(player.inventory, new BlockPos(x, y, z), false);
			case PROTOTYPING_GUI:
				return new PrototypingTableGuiContainer(player.inventory, ((PrototypingTableTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case PROTOTYPE_PORT_GUI:
				return new PrototypePortGuiContainer(((PrototypePortTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case REDSTONE_REGISTRY_GUI:
				return new RedstoneRegistryGuiContainer(((RedstoneRegistryTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case FAKE_CRAFTER_GUI:
				return new DetailedCrafterGuiContainer(player.inventory, new BlockPos(x, y, z), true);
			case STAMP_MILL_GUI:
				return new StampMillGuiContainer(player.inventory, ((StampMillTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case FAT_COLLECTOR_GUI:
				return new FatCollectorGuiContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case SALT_REACTOR_GUI:
				return new SaltReactorScreen(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case RADIATOR_GUI:
				return new RadiatorGuiContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case CRUCIBLE_GUI:
				return new CrucibleScreen(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case ICEBOX_GUI:
				return new IceboxScreen(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case FLUID_COOLER_GUI:
				return new FluidCoolerScreen(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case COPSHOWIUM_CHAMBER_GUI:
				return new CopshowiumCreationChamberGuiContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case ORE_CLEANSER_GUI:
				return new OreCleanserGuiContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case BLAST_FURNACE_GUI:
				return new BlastFurnaceScreen(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case FAT_FEEDER_GUI:
				return new FatFeederGuiContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case BEAM_EXTRACTOR_GUI:
				return new BeamExtractorGuiContainer(player.inventory, (IInventory) world.getTileEntity(new BlockPos(x, y, z)));
			case MATH_AXIS_GUI:
				return new MathAxisGuiContainer(((MathAxisTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case FLUID_TANK_GUI:
				return new FluidTankGuiContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case REAGENT_FILTER_GUI:
				return new ReagentFilterGuiContainer(player.inventory, (IInventory) world.getTileEntity(new BlockPos(x, y, z)));
		}

		return null;
	}

}
