package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.FormulationVatContainer;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.FormulationVatTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class FormulationVatScreen extends MachineGUI<FormulationVatContainer, FormulationVatTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/formulation_vat_gui.png");

	public FormulationVatScreen(FormulationVatContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();

		initFluidManager(0, 10, 70);
		initFluidManager(1, 70, 70);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		blit(matrix, leftPos + 24, topPos + 35, 176, 0, menu.craftProgress.get() * 46 / FormulationVatTileEntity.REQUIRED, 19);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
