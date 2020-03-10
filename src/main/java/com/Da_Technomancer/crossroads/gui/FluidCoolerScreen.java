package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.FluidCoolerContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.FluidCoolingChamberTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class FluidCoolerScreen extends MachineGUI<FluidCoolerContainer, FluidCoolingChamberTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/fluid_cooler_gui.png");

	public FluidCoolerScreen(FluidCoolerContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
	}

	@Override
	protected void init(){
		super.init();
		initFluidManager(0, 10, 70);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize);


		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
}
