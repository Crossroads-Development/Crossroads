package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.IceboxContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.IceboxTileEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class IceboxScreen extends MachineGUI<IceboxContainer, IceboxTileEntity>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID + ":textures/gui/container/icebox_gui.png");

	public IceboxScreen(IceboxContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);

		xSize = 176;
		ySize = 136;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize);

		int burnTime = te.coolProg.get();
		int k = burnTime == 0 ? 0 : 1 + (burnTime * 13 / 100);
		if(k != 0){
			blit(i + 81, j + 19 - k, 176, 13 - k, 14, k);
		}
	}

}
