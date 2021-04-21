package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BlastFurnaceContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.BlastFurnaceTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BlastFurnaceScreen extends MachineGUI<BlastFurnaceContainer, BlastFurnaceTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/blast_furnace_gui.png");

	public BlastFurnaceScreen(BlastFurnaceContainer cont, PlayerInventory playerInv, ITextComponent text){
		super(cont, playerInv, text);
	}

	@Override
	public void init(){
		super.init();
		initFluidManager(0, 63, 70);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		blit(matrix, leftPos + 25, topPos + 38, 176, 0, 38 * menu.progRef.get() / BlastFurnaceTileEntity.REQUIRED_PRG, 14);
		fill(matrix, leftPos + 50, topPos + 36 - menu.carbRef.get() * 16 / BlastFurnaceTileEntity.CARBON_LIMIT, leftPos + 52, topPos + 36, 0xFF000000);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
