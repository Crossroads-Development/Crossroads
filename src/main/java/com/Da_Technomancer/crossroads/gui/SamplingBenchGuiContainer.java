package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.gui.container.SamplingBenchContainer;
import com.Da_Technomancer.crossroads.tileentities.alchemy.SamplingBenchTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class SamplingBenchGuiContainer extends GuiContainer{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID + ":textures/gui/container/sampling_bench_gui.png");

	private final IInventory playerInv;
	private final SamplingBenchTileEntity te;

	public SamplingBenchGuiContainer(IInventory playerInv, SamplingBenchTileEntity te){
		super(new SamplingBenchContainer(playerInv, te));
		this.playerInv = playerInv;
		this.te = te;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = te.getDisplayName().getUnformattedText();
		fontRenderer.drawString(s, 98 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, 72, 4210752);

		ReagentStack reag = te.reag;

		if(reag == null){
			fontRenderer.drawString("Empty", 98 - fontRenderer.getStringWidth("Empty") / 2, 18, 4210752);
		}else{
			String line = reag.getType().getName() + " (" + reag.getType().getIndex() + ")";
			fontRenderer.drawString(line, 98 - fontRenderer.getStringWidth(line) / 2, 18, 4210752);
			fontRenderer.drawString("Boiling: ", 32, 28, 4210752);
			line = reag.getType().getBoilingPoint() >= Short.MAX_VALUE - 10 ? "Never" : reag.getType().getBoilingPoint() < -273 ? "Always" : (reag.getType().getBoilingPoint() + "°C");
			fontRenderer.drawString(line, 164 - fontRenderer.getStringWidth(line), 28, 4210752);
			fontRenderer.drawString("Melting: ", 32, 38, 4210752);
			line = reag.getType().getMeltingPoint() >= Short.MAX_VALUE - 10 ? "Never" : reag.getType().getMeltingPoint() < -273 ? "Always" : (reag.getType().getMeltingPoint() + "°C");
			fontRenderer.drawString(line, 164 - fontRenderer.getStringWidth(line), 38, 4210752);
		}
	}
}
