package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.EmbryoLabTileEntity;
import com.Da_Technomancer.crossroads.gui.container.EmbryoLabContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;

public class EmbryoLabScreen extends MachineScreen<EmbryoLabContainer, EmbryoLabTileEntity>{

	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "textures/gui/container/embryo_lab_gui.png");

	public EmbryoLabScreen(EmbryoLabContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
		imageHeight = 246;
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		if(te.template == null){
			matrix.drawString(font, MiscUtil.localize("container.crossroads.embryo_lab.empty"), 8, 25, 0x404040, false);
		}else{
			ArrayList<Component> lines = new ArrayList<>();
			te.template.addTooltip(lines, te.getLevel());
			int yOffset = 0;
			for(Component line : lines){
				for(FormattedCharSequence formattedcharsequence : font.split(line, 30)){
					matrix.drawString(font, formattedcharsequence, 8, 25 + yOffset, 0x404040, false);
					yOffset += 9;
				}
				yOffset += 1;
			}
		}
	}
}
