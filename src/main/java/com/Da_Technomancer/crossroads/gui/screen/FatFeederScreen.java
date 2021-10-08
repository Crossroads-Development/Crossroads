package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.FatFeederContainer;
import com.Da_Technomancer.crossroads.tileentities.fluid.FatFeederTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class FatFeederScreen extends MachineGUI<FatFeederContainer, FatFeederTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/fat_feeder_gui.png");

	public FatFeederScreen(FatFeederContainer cont, Inventory playerInv, Component text){
		super(cont, playerInv, text);
	}

	@Override
	public void init(){
		super.init();
		initFluidManager(0, 80, 71);
	}


	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
