package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.gui.container.*;
import com.Da_Technomancer.crossroads.tileentities.RedstoneKeyboardTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.SamplingBenchTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.FuelHeaterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingChamberTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.GrindstoneTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypePortTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypingTableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneRegistryTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{

	public static final int GRINDSTONE_GUI = 0;
	public static final int COAL_HEATER_GUI = 1;
	public static final int HEATING_CHAMBER_GUI = 2;
	public static final int COLOR_CHART_GUI = 4;
	public static final int REDSTONE_KEYBOARD_GUI = 5;
	public static final int CRAFTER_GUI = 6;
	public static final int PROTOTYPING_GUI = 7;
	public static final int PROTOTYPE_PORT_GUI = 8;
	public static final int REDSTONE_REGISTRY_GUI = 9;
	public static final int SAMPLING_BENCH_GUI = 10;
	public static final int ALCHEMY_CHART_GUI = 11;
	public static final int FAKE_CRAFTER_GUI = 12;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		switch(ID){
			case GRINDSTONE_GUI:
				return new GrindstoneContainer(player.inventory, ((GrindstoneTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case COAL_HEATER_GUI:
				return new CoalHeaterContainer(player.inventory, ((FuelHeaterTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case HEATING_CHAMBER_GUI:
				return new HeatingChamberContainer(player.inventory, ((HeatingChamberTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case COLOR_CHART_GUI:
				return new ColorChartContainer(player, world, new BlockPos(x, y, z));
			case REDSTONE_KEYBOARD_GUI:
				return new RedstoneKeyboardContainer();
			case CRAFTER_GUI:
				return new DetailedCrafterContainer(player.inventory, new BlockPos(x, y, z), false);
			case PROTOTYPING_GUI:
				return new PrototypingTableContainer(player.inventory, ((PrototypingTableTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case PROTOTYPE_PORT_GUI:
				return new PrototypePortContainer(((PrototypePortTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case REDSTONE_REGISTRY_GUI:
				return new RedstoneKeyboardContainer();//Shares Container with the RedstoneKeyboard because their Container function is identical.
			case SAMPLING_BENCH_GUI:
				return new SamplingBenchContainer(player.inventory, ((SamplingBenchTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case ALCHEMY_CHART_GUI:
				return new AlchemyChartContainer(player, world);
			case FAKE_CRAFTER_GUI:
				return new DetailedCrafterContainer(player.inventory, new BlockPos(x, y, z), true);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		switch(ID){
			case GRINDSTONE_GUI:
				return new GrindstoneGuiContainer(player.inventory, ((GrindstoneTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case COAL_HEATER_GUI:
				return new CoalHeaterGuiContainer(player.inventory, ((FuelHeaterTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case HEATING_CHAMBER_GUI:
				return new HeatingChamberGuiContainer(player.inventory, ((HeatingChamberTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case COLOR_CHART_GUI:
				return new ColorChartGuiContainer(player, world, new BlockPos(x, y, z));
			case REDSTONE_KEYBOARD_GUI:
				return new RedstoneKeyboardGuiContainer(((RedstoneKeyboardTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case CRAFTER_GUI:
				return new DetailedCrafterGuiContainer(player.inventory, new BlockPos(x, y, z), false);
			case PROTOTYPING_GUI:
				return new PrototypingTableGuiContainer(player.inventory, ((PrototypingTableTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case PROTOTYPE_PORT_GUI:
				return new PrototypePortGuiContainer(((PrototypePortTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case REDSTONE_REGISTRY_GUI:
				return new RedstoneRegistryGuiContainer(((RedstoneRegistryTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case SAMPLING_BENCH_GUI:
				return new SamplingBenchGuiContainer(player.inventory, ((SamplingBenchTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case ALCHEMY_CHART_GUI:
				return new AlchemyChartGuiContainer(player, world);
			case FAKE_CRAFTER_GUI:
				return new DetailedCrafterGuiContainer(player.inventory, new BlockPos(x, y, z), true);
		}

		return null;
	}

}
