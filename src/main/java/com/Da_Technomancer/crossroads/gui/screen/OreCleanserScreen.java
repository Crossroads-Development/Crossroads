package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.fluid.OreCleanserTileEntity;
import com.Da_Technomancer.crossroads.gui.container.OreCleanserContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OreCleanserScreen extends MachineScreen<OreCleanserContainer, OreCleanserTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/ore_cleanser_gui.png");

	public OreCleanserScreen(OreCleanserContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();
		initFluidManager(0, 8, 70);
		initFluidManager(1, 62, 70);
	}


	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderTexture(0, TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		blit(matrix, leftPos + 25, topPos + 21, 176, 0, 36 * menu.progRef.get() / 50, 10);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
