package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.AdvancementTracker;
import com.Da_Technomancer.crossroads.api.EnumPath;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendPathUnlockToServer;
import com.Da_Technomancer.crossroads.gui.container.DetailedCrafterContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;

public class DetailedCrafterScreen extends AbstractContainerScreen<DetailedCrafterContainer>{

	private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "textures/gui/container/detailed_crafter.png");
	private boolean canUnlockPath;//Cache so we don't re-check this every frame
	/**
	 * Dirty kludge. When unlocking a path in this UI, user action is on the client, but the actual unlocking happens on the server
	 * We need time for the unlock to be communicated to the server, then the result communicated back to the client
	 * Actual time required depends on network ping, but we approximate on the client immediately and then update once at recheckPathTime
	 * Not a perfect solution- we could calculate it properly on the client first and remove any time delay and fix an edge case with bad connections
	 */
	private long recheckPathTime = Long.MAX_VALUE;

	public DetailedCrafterScreen(DetailedCrafterContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
		imageWidth = 176;
		imageHeight = 166;
	}

	@Override
	protected void init(){
		super.init();
		AdvancementTracker.listen();//We use the path advancement
		canUnlockPath = EnumPath.canUnlock(Minecraft.getInstance().player);
	}

	@Override
	public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks){
		if(System.currentTimeMillis() > recheckPathTime){
			recheckPathTime = Long.MAX_VALUE;//Disable rechecking
			canUnlockPath = EnumPath.canUnlock(Minecraft.getInstance().player);
		}

		//renderBackground(matrix, mouseX, mouseY, partialTicks);
		super.render(matrix, mouseX, mouseY, partialTicks);
		renderTooltip(matrix, mouseX, mouseY);

		//Path tooltips
		if(mouseY - topPos >= 60 && mouseY - topPos < 76 && mouseX - leftPos >= 108){
			EnumPath selectedPath = null;
			if(mouseX - leftPos < 124){
				selectedPath = EnumPath.ALCHEMY;
			}else if(mouseX - leftPos < 140){
				selectedPath = EnumPath.TECHNOMANCY;
			}else if(mouseX - leftPos < 156){
				selectedPath = EnumPath.WITCHCRAFT;
			}
			if(selectedPath != null){
				ArrayList<Component> tooltip = new ArrayList<>(2);
				tooltip.add(Component.translatable("path." + selectedPath.toString()));
				if(!selectedPath.isUnlocked(Minecraft.getInstance().player)){
					if(canUnlockPath){
						tooltip.add(Component.translatable("container.detailed_crafter.can_unlock"));
					}else if(EnumPath.totalUnlockedPathsCount(Minecraft.getInstance().player) > 0 && CRConfig.multiPathMode.get() == EnumPath.MultiPathMode.SINGLE){
						tooltip.add(Component.translatable("container.detailed_crafter.perma_locked"));
					}else{
						tooltip.add(Component.translatable("container.detailed_crafter.locked"));
					}
				}
				matrix.renderComponentTooltip(font, tooltip, mouseX, mouseY);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		if(canUnlockPath && button == 0 && mouseY - topPos >= 60 && mouseY - topPos < 76){
			EnumPath selectedPath = null;
			if(mouseX - leftPos < 124){
				selectedPath = EnumPath.ALCHEMY;
			}else if(mouseX - leftPos < 140){
				selectedPath = EnumPath.TECHNOMANCY;
			}else if(mouseX - leftPos < 156){
				selectedPath = EnumPath.WITCHCRAFT;
			}
			if(selectedPath != null && !selectedPath.isUnlocked(Minecraft.getInstance().player)){
				CRPackets.sendPacketToServer(new SendPathUnlockToServer(selectedPath));
				canUnlockPath = CRConfig.multiPathMode.get() == EnumPath.MultiPathMode.UNLIMITED;//Rough guess, should be right 99% of the time
				recheckPathTime = System.currentTimeMillis() + 1000;//Verify after time for packets to happen
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int mouseX, int mouseY){
		//Background
		RenderSystem.setShaderColor(1, 1, 1, 1);
		matrix.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		//Render paths
		if(EnumPath.ALCHEMY.isUnlocked(minecraft.player)){
			matrix.blit(BACKGROUND, leftPos + 108, topPos + 60, 176, 16, 16, 16);
		}else if(canUnlockPath && (System.currentTimeMillis() + 300) % 1500 < 1050){
			matrix.blit(BACKGROUND, leftPos + 108, topPos + 60, 192, 16, 16, 16);
		}
		if(EnumPath.TECHNOMANCY.isUnlocked(minecraft.player)){
			matrix.blit(BACKGROUND, leftPos + 124, topPos + 60, 176, 0, 16, 16);
		}else if(canUnlockPath && (System.currentTimeMillis() + 150) % 1500 < 1050){
			matrix.blit(BACKGROUND, leftPos + 124, topPos + 60, 192, 0, 16, 16);
		}
		if(EnumPath.WITCHCRAFT.isUnlocked(minecraft.player)){
			matrix.blit(BACKGROUND, leftPos + 140, topPos + 60, 176, 32, 16, 16);
		}else if(canUnlockPath && System.currentTimeMillis() % 1500 < 1050){
			matrix.blit(BACKGROUND, leftPos + 140, topPos + 60, 192, 32, 16, 16);
		}
	}
}
