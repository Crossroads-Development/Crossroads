package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.EmbryoLabContainer;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.EmbryoLabTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;

public class EmbryoLabScreen extends MachineGUI<EmbryoLabContainer, EmbryoLabTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/embryo_lab_gui.png");

	public EmbryoLabScreen(EmbryoLabContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
		imageHeight = 246;
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(MatrixStack matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		if(te.template == null){
			font.draw(matrix, MiscUtil.localize("container.crossroads.embryo_lab.empty"), 8, 25, 0x404040);
		}else{
			ArrayList<ITextComponent> lines = new ArrayList<>();
			te.template.addTooltip(lines, 13);
			for(int i = 0; i < lines.size(); i++){
				ITextComponent line = lines.get(i);
				font.draw(matrix, line.getString(), 8, 25 + i * 10, 0x404040);
			}
		}
	}
}
