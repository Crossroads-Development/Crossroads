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
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);


		//Time meter
		blit(matrix, leftPos + 55, topPos + 34, 176, 0, Math.min(66, menu.timeRef.get() * 66 / StampMillTileEntity.TIME_LIMIT), 9);
		//Progress meter
		blit(matrix, leftPos + 55, topPos + 45, 176, 0, (int) Math.min(66, menu.progRef.get() * 66 / StampMillTileEntity.REQUIRED), 9);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
