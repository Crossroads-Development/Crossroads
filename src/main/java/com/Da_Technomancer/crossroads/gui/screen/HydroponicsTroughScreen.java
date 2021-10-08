package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.HydroponicsTroughContainer;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.HydroponicsTroughTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class HydroponicsTroughScreen extends MachineGUI<HydroponicsTroughContainer, HydroponicsTroughTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/hydroponics_trough_gui.png");

	public HydroponicsTroughScreen(HydroponicsTroughContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();

		initFluidManager(0, 28, 66);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		blit(matrix, leftPos + 61, topPos + 33, 176, 0, 72, menu.progRef.get() * 18 / 100);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
