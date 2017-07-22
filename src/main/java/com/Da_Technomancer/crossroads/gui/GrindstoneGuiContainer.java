package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.container.GrindstoneContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.GrindstoneTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GrindstoneGuiContainer extends GuiContainer{

	private IInventory playerInv;
	private GrindstoneTileEntity te;
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/grindstone_gui.png");

	public GrindstoneGuiContainer(IInventory playerInv, GrindstoneTileEntity te){
		super(new GrindstoneContainer(playerInv, te));
		this.playerInv = playerInv;
		this.te = te;

		xSize = 176;
		ySize = 166;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		drawTexturedModalRect(guiLeft + 66, guiTop + 35, 176, 0, 44, getScaledProgress());
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = I18n.format("container.grindstone");
		fontRenderer.drawString(s, 88 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, 72, 4210752);
	}

	private int getScaledProgress(){
		return (int) Math.ceil(te.getProgress() * 17 / GrindstoneTileEntity.REQUIRED);
	}

}
