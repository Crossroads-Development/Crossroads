package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.EmbryoLabTileEntity;
import com.Da_Technomancer.crossroads.gui.container.EmbryoLabContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;

public class EmbryoLabScreen extends MachineScreen<EmbryoLabContainer, EmbryoLabTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/embryo_lab_gui.png");

	public EmbryoLabScreen(EmbryoLabContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
		imageHeight = 246;
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderTexture(0, TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		if(te.template == null){
			font.draw(matrix, MiscUtil.localize("container.crossroads.embryo_lab.empty"), 8, 25, 0x404040);
		}else{
			ArrayList<Component> lines = new ArrayList<>();
			te.template.addTooltip(lines, 13);
			for(int i = 0; i < lines.size(); i++){
				Component line = lines.get(i);
				font.draw(matrix, line.getString(), 8, 25 + i * 10, 0x404040);
			}
		}
	}
}
