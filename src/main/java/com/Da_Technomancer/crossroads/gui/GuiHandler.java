package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.gui.container.CoalHeaterContainer;
import com.Da_Technomancer.crossroads.gui.container.ColorChartContainer;
import com.Da_Technomancer.crossroads.gui.container.GrindstoneContainer;
import com.Da_Technomancer.crossroads.gui.container.HeatingChamberContainer;
import com.Da_Technomancer.crossroads.gui.container.RedstoneKeyboardContainer;
import com.Da_Technomancer.crossroads.gui.container.SlottedChestContainer;
import com.Da_Technomancer.crossroads.tileentities.SlottedChestTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.CoalHeaterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingChamberTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.GrindstoneTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneKeyboardTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler{

	public static final int GRINDSTONE_GUI = 0;
	public static final int COALHEATER_GUI = 1;
	public static final int HEATINGCHAMBER_GUI = 2;
	public static final int SLOTTEDCHEST_GUI = 3;
	public static final int COLORCHART_GUI = 4;
	public static final int REDSTONEKEYBOARD_GUI = 5;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		switch(ID){
			case GRINDSTONE_GUI:
				return new GrindstoneContainer(player.inventory, ((GrindstoneTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case COALHEATER_GUI:
				return new CoalHeaterContainer(player.inventory, ((CoalHeaterTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case HEATINGCHAMBER_GUI:
				return new HeatingChamberContainer(player.inventory, ((HeatingChamberTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case SLOTTEDCHEST_GUI:
				return new SlottedChestContainer(player.inventory, ((SlottedChestTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case COLORCHART_GUI:
				return new ColorChartContainer(player, world, new BlockPos(x, y, z));
			case REDSTONEKEYBOARD_GUI:
				return new RedstoneKeyboardContainer();
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		switch(ID){
			case GRINDSTONE_GUI:
				return new GrindstoneGuiContainer(player.inventory, ((GrindstoneTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case COALHEATER_GUI:
				return new CoalHeaterGuiContainer(player.inventory, ((CoalHeaterTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case HEATINGCHAMBER_GUI:
				return new HeatingChamberGuiContainer(player.inventory, ((HeatingChamberTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case SLOTTEDCHEST_GUI:
				return new SlottedChestGuiContainer(player.inventory, ((SlottedChestTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
			case COLORCHART_GUI:
				return new ColorChartGuiContainer(player, world, new BlockPos(x, y, z));
			case REDSTONEKEYBOARD_GUI:
				return new RedstoneKeyboardGuiContainer(((RedstoneKeyboardTileEntity) world.getTileEntity(new BlockPos(x, y, z))));
		}

		return null;
	}

}
