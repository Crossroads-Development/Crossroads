package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.gui.container.*;
import com.Da_Technomancer.crossroads.tileentities.RedstoneKeyboardTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamBoilerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.fluid.WaterCentrifugeTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.FireboxTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.SmelterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.MillstoneTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypePortTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneRegistryTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

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
	public static final int ALCHEMY_CHART_GUI = 11;
	public static final int FAKE_CRAFTER_GUI = 12;
	public static final int STAMP_MILL_GUI = 13;
	public static final int FAT_COLLECTOR_GUI = 14;
	public static final int SALT_REACTOR_GUI = 15;
	public static final int RADIATOR_GUI = 16;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
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
			case REDSTONE_REGISTRY_GUI://Shares Container with the Redstone Keyboard because its Container function is identical.
			case REDSTONE_KEYBOARD_GUI:
				return new RedstoneKeyboardContainer();
			case WATER_CENTRIFUGE_GUI:
				return new WaterCentrifugeContainer(player.inventory, (WaterCentrifugeTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
			case CRAFTER_GUI:
				return new DetailedCrafterContainer(player.inventory, new BlockPos(x, y, z), false);
			case PROTOTYPING_GUI:
				return new PrototypingTableContainer(player.inventory, ((PrototypingTableTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case PROTOTYPE_PORT_GUI:
				return new PrototypePortContainer(((PrototypePortTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case ALCHEMY_CHART_GUI:
				return new AlchemyChartContainer(player, world);
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
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		switch(ID){
			case MILLSTONE_GUI:
				return new MillstoneGuiContainer(player.inventory, ((MillstoneTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case FIREBOX_GUI:
				return new FireboxGuiContainer(player.inventory, ((FireboxTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case SMELTER_GUI:
				return new SmelterGuiContainer(player.inventory, ((SmelterTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case STEAM_BOILER_GUI:
				return new SteamBoilerGuiContainer(player.inventory, (SteamBoilerTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
			case COLOR_CHART_GUI:
				return new ColorChartGuiContainer(player, world, new BlockPos(x, y, z));
			case REDSTONE_KEYBOARD_GUI:
				return new RedstoneKeyboardGuiContainer(((RedstoneKeyboardTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case WATER_CENTRIFUGE_GUI:
				return new WaterCentrifugeGuiContainer(player.inventory, (WaterCentrifugeTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
			case CRAFTER_GUI:
				return new DetailedCrafterGuiContainer(player.inventory, new BlockPos(x, y, z), false);
			case PROTOTYPING_GUI:
				return new PrototypingTableGuiContainer(player.inventory, ((PrototypingTableTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case PROTOTYPE_PORT_GUI:
				return new PrototypePortGuiContainer(((PrototypePortTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case REDSTONE_REGISTRY_GUI:
				return new RedstoneRegistryGuiContainer(((RedstoneRegistryTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case ALCHEMY_CHART_GUI:
				return new AlchemyChartGuiContainer(player, world);
			case FAKE_CRAFTER_GUI:
				return new DetailedCrafterGuiContainer(player.inventory, new BlockPos(x, y, z), true);
			case STAMP_MILL_GUI:
				return new StampMillGuiContainer(player.inventory, ((StampMillTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case FAT_COLLECTOR_GUI:
				return new FatCollectorGuiContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case SALT_REACTOR_GUI:
				return new SaltReactorGuiContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
			case RADIATOR_GUI:
				return new RadiatorGuiContainer(player.inventory, (InventoryTE) world.getTileEntity(new BlockPos(x, y, z)));
		}

		return null;
	}

}
