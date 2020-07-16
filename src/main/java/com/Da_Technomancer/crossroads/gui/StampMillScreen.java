package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.StampMillContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class StampMillScreen extends MachineGUI<StampMillContainer, StampMillTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/stamp_mill_gui.png");

	public StampMillScreen(StampMillContainer cont, PlayerInventory playerInv, ITextComponent text){
		super(cont, playerInv, text);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);

		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);


		//Time meter
		blit(matrix, guiLeft + 55, guiTop + 34, 176, 0, Math.min(66, container.timeRef.get() * 66 / StampMillTileEntity.TIME_LIMIT), 9);
		//Progress meter
		blit(matrix, guiLeft + 55, guiTop + 45, 176, 0, (int) Math.min(66, container.progRef.get() * 66 / StampMillTileEntity.REQUIRED), 9);

		super.drawGuiContainerBackgroundLayer(matrix, partialTicks, mouseX, mouseY);
	}
}
