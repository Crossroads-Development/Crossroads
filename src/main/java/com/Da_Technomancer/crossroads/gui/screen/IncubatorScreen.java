package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.IncubatorTileEntity;
import com.Da_Technomancer.crossroads.gui.container.IncubatorContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IncubatorScreen extends MachineScreen<IncubatorContainer, IncubatorTileEntity>{

	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "textures/gui/container/incubator_gui.png");

	public IncubatorScreen(IncubatorContainer container, Inventory playerInv, Component name){
		super(container, playerInv, name);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		matrix.blit(TEXTURE, leftPos + 43, topPos + 35, 176, 0, menu.progressRef.get() * 54 / IncubatorTileEntity.REQUIRED, 10);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}

	@Override
	protected void renderExtraLabels(GuiGraphics matrix, int mouseX, int mouseY){
		//Replacing the default heat label
		//This version is 3 decimal places and colors correct digits green
		if(menu.heatRef != null){
			int rawTemp = menu.heatRef.get();
			//Render traditional temperature overlay in top-right
			String s = MiscUtil.localize("container.crossroads.boilerplate.temp", rawTemp / 1000);
			matrix.drawString(font, s, imageWidth - 8 - font.width(s), 6, 0x404040, false);

			//Special overlay in the middle
			rawTemp = Math.max(0, Math.min(rawTemp, 99_999));
			boolean inRange = rawTemp >= 1000 * IncubatorTileEntity.MIN_TEMP && rawTemp <= IncubatorTileEntity.MAX_TEMP * 1000;
			for(int i = 0; i < 6; i++){
				String charStr;
				int color;
				if(i == 3){
					charStr = ".";
					color = 0;
				}else{
					int digit = rawTemp % 10;
					rawTemp /= 10;
					charStr = "" + digit;
					color = digit == 3 && inRange ? 0x00FF00 : 0xA00000;
				}
				matrix.drawString(font, charStr, imageWidth / 2 + (2 - i) * 8, 6, color, false);
			}
		}
	}

	@Override
	protected void renderLabels(GuiGraphics matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		//Time ticker
		if(menu.timeRef != null){
			int total = menu.timeRef.get();
			int minutes = total / 60;
			int seconds = total %= 60;
			float fractionOfBaseTime = (float) total / IncubatorTileEntity.REQUIRED;
			int color = fractionOfBaseTime < 0.2 ? 0x0000A0 : fractionOfBaseTime < 0.4 ? 0x00A000 : fractionOfBaseTime < 0.8 ? 0xA0A000 : fractionOfBaseTime < 1 ? 0xA00800 : 0xA00000;
			String s = MiscUtil.localize("container.crossroads.incubator.time", minutes, seconds);
			matrix.drawString(font, s, imageWidth / 2 - font.width(s) / 2, 16, color, false);
		}
	}
}
