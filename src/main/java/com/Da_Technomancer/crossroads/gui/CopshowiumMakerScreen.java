package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.CopshowiumMakerContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CopshowiumCreationChamberTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CopshowiumMakerScreen extends MachineGUI<CopshowiumMakerContainer, CopshowiumCreationChamberTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/radiator_gui.png");

	public CopshowiumMakerScreen(CopshowiumMakerContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();
		te.fluidManagers[0].initScreen((width - xSize) / 2, (height - ySize) / 2, 10, 70, container.fluidManagerRefs[0][0], container.fluidManagerRefs[0][1]);
		te.fluidManagers[1].initScreen((width - xSize) / 2, (height - ySize) / 2, 70, 70, container.fluidManagerRefs[1][0], container.fluidManagerRefs[1][1]);
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
