package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.witchcraft.AutoInjectorTileEntity;
import com.Da_Technomancer.crossroads.gui.container.AutoInjectorContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;

public class AutoInjectorScreen extends MachineScreen<AutoInjectorContainer, AutoInjectorTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/auto_injector_gui.png");

	public AutoInjectorScreen(AutoInjectorContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		matrix.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		MobEffect effect = MobEffect.byId(menu.effectRef.get());
		String typeStr;
		if(effect == null){
			typeStr = MiscUtil.localize("container.crossroads.auto_injector.empty");
		}else{
			typeStr = MiscUtil.localize("container.crossroads.auto_injector.contents", effect.getDisplayName().getString(), menu.intensityRef.get() + 1);
		}
		String durationStr = MiscUtil.localize("container.crossroads.auto_injector.qty", menu.durationRef.get() / 20, AutoInjectorTileEntity.DURATION_CAPACITY / 20);
		String doseStr = MiscUtil.localize("container.crossroads.auto_injector.dose_qty", menu.doseRef.get() / 20);
		matrix.drawString(font, typeStr, 8, 25, 0x404040, false);
		matrix.drawString(font, durationStr, 8, 35, 0x404040, false);
		matrix.drawString(font, doseStr, 8, 45, 0x404040, false);

		//Draw dose indicator
		matrix.blit(TEXTURE, 149, 59 - 3 - 39 * menu.doseRef.get() / AutoInjectorTileEntity.DURATION_CAPACITY, 188, 0, 10, 5);

		//Draw fullness indicator
		if(effect != null){
			int color = effect.getColor();
			RenderSystem.setShaderColor(((color >>> 16) & 0xFF) / 255F, ((color >>> 8) & 0xFF) / 255F, ((color) & 0xFF) / 255F, 1F);
			int renderHeight = 39 * menu.durationRef.get() / AutoInjectorTileEntity.DURATION_CAPACITY;
			matrix.blit(TEXTURE, 136, 59 - renderHeight, 176, 39 - renderHeight, 12, renderHeight);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
	}
}
