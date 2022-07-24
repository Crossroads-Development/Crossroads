package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.rotary.BlastFurnaceTileEntity;
import com.Da_Technomancer.crossroads.gui.container.BlastFurnaceContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlastFurnaceScreen extends MachineScreen<BlastFurnaceContainer, BlastFurnaceTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/blast_furnace_gui.png");

	public BlastFurnaceScreen(BlastFurnaceContainer cont, Inventory playerInv, Component text){
		super(cont, playerInv, text);
	}

	@Override
	public void init(){
		super.init();
		initFluidManager(0, 63, 70);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderTexture(0, TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		blit(matrix, leftPos + 25, topPos + 38, 176, 0, 38 * menu.progRef.get() / BlastFurnaceTileEntity.REQUIRED_PRG, 14);
		fill(matrix, leftPos + 50, topPos + 36 - menu.carbRef.get() * 16 / BlastFurnaceTileEntity.CARBON_LIMIT, leftPos + 52, topPos + 36, 0xFF000000);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
