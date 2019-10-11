package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.SaltReactorContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.SaltReactorTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SaltReactorScreen extends MachineGUI<SaltReactorContainer, SaltReactorTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/salt_reactor_gui.png");

	public SaltReactorScreen(SaltReactorContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();
		te.fluidManagers[0].initScreen((width - xSize) / 2, (height - ySize) / 2, 10, 70);
		te.fluidManagers[1].initScreen((width - xSize) / 2, (height - ySize) / 2, 70, 70);
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
