package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.AutoInjectorContainer;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.AutoInjectorTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class AutoInjectorScreen extends MachineGUI<AutoInjectorContainer, AutoInjectorTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/auto_injector_gui.png");

	public AutoInjectorScreen(AutoInjectorContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
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

		Effect effect = Effect.byId(menu.effectRef.get());
		String typeStr;
		if(effect == null){
			typeStr = MiscUtil.localize("container.crossroads.auto_injector.empty");
		}else{
			typeStr = MiscUtil.localize("container.crossroads.auto_injector.contents", effect.getDisplayName().getString(), menu.intensityRef.get() + 1);
		}
		String durationStr = MiscUtil.localize("container.crossroads.auto_injector.qty", menu.durationRef.get() / 20, AutoInjectorTileEntity.DURATION_CAPACITY / 20);
		String doseStr = MiscUtil.localize("container.crossroads.auto_injector.dose_qty", menu.doseRef.get() / 20);
		font.draw(matrix, typeStr, 8, 25, 0x404040);
		font.draw(matrix, durationStr, 8, 35, 0x404040);
		font.draw(matrix, doseStr, 8, 45, 0x404040);

		Minecraft.getInstance().getTextureManager().bind(TEXTURE);
		//Draw dose indicator
		blit(matrix, 149, 59 - 3 - 39 * menu.doseRef.get() / AutoInjectorTileEntity.DURATION_CAPACITY, 188, 0, 10, 5);

		//Draw fullness indicator
		if(effect != null){
			int color = effect.getColor();
			RenderSystem.color4f(((color >>> 16) & 0xFF) / 255F, ((color >>> 8) & 0xFF) / 255F, ((color) & 0xFF) / 255F, 1F);
			int renderHeight = 39 * menu.durationRef.get() / AutoInjectorTileEntity.DURATION_CAPACITY;
			blit(matrix, 136, 59 - renderHeight, 176, 39 - renderHeight, 12, renderHeight);
			RenderSystem.color4f(1, 1, 1, 1);
		}
	}
}
