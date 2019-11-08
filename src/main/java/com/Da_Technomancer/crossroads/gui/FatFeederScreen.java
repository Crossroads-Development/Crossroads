package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.FatFeederContainer;
import com.Da_Technomancer.crossroads.tileentities.fluid.FatFeederTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class FatFeederScreen extends MachineGUI<FatFeederContainer, FatFeederTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/fat_feeder_gui.png");

	public FatFeederScreen(FatFeederContainer cont, PlayerInventory playerInv, ITextComponent text){
		super(cont, playerInv, text);
	}

	@Override
	public void init(){
		super.init();
		te.fluidManagers[0].initScreen((width - xSize) / 2, (height - ySize) / 2, 80, 71);
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
		blit((width - xSize) / 2, (height - ySize) / 2, 0, 0, xSize, ySize);
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
}
