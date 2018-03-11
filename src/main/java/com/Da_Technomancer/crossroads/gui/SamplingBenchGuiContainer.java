package com.Da_Technomancer.crossroads.gui;

import java.io.IOException;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.alchemy.IDynamicReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.gui.TexturedButtonGuiObject;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendStringToServer;
import com.Da_Technomancer.crossroads.gui.container.SamplingBenchContainer;
import com.Da_Technomancer.crossroads.tileentities.alchemy.SamplingBenchTileEntity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class SamplingBenchGuiContainer extends GuiContainer{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Main.MODID + ":textures/gui/container/sampling_bench_gui.png");
	private static final ResourceLocation QUILL_TEXTURES = new ResourceLocation(Main.MODID + ":textures/gui/container/quill.png");

	private final IInventory playerInv;
	private final SamplingBenchTileEntity te;
	private TexturedButtonGuiObject button;

	public SamplingBenchGuiContainer(IInventory playerInv, SamplingBenchTileEntity te){
		super(new SamplingBenchContainer(playerInv, te));
		this.playerInv = playerInv;
		this.te = te;
	}
	
	@Override
	public void initGui(){
		super.initGui();

		button = new TexturedButtonGuiObject((width - xSize) / 2, (height - ySize) / 2, 152, 19, 16, 16, QUILL_TEXTURES, 0, 0, 16, 16, 16, 16);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURES);

		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
		
		button.drawBack(partialTicks, mouseX, mouseY, fontRenderer);
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
		fontRenderer.drawString(s, 88 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, 72, 4210752);

		ReagentStack reag = te.reag;

		if(reag == null){
			fontRenderer.drawString("Empty", 88 - fontRenderer.getStringWidth("Empty") / 2, 18, 4210752);
		}else{
			String line = reag.getType().getName();
			fontRenderer.drawString(line, 88 - fontRenderer.getStringWidth(line) / 2, 18, 4210752);
			fontRenderer.drawString("Boiling: ", 32, 28, 4210752);
			line = reag.getType().getBoilingPoint() < -273 ? "Always" : (reag.getType().getBoilingPoint() + "°C");
			fontRenderer.drawString(line, 144 - fontRenderer.getStringWidth(line), 28, 4210752);
			fontRenderer.drawString("Melting: ", 32, 38, 4210752);
			line = reag.getType().getMeltingPoint() < -273 ? "Always" : (reag.getType().getMeltingPoint() + "°C");
			fontRenderer.drawString(line, 144 - fontRenderer.getStringWidth(line), 38, 4210752);
			fontRenderer.drawString("Soluble in: ", 32, 48, 4210752);
			line = reag.getType().soluteType() == null ? "None" : reag.getType().soluteType().toString();
			fontRenderer.drawString(line, 144 - fontRenderer.getStringWidth(line), 48, 4210752);
			fontRenderer.drawString("Disolves: ", 32, 58, 4210752);
			line = reag.getType().solventType() == null ? "None" : reag.getType().solventType().toString();
			fontRenderer.drawString(line, 144 - fontRenderer.getStringWidth(line), 58, 4210752);
		}
		
		button.drawFore(mouseX, mouseY, fontRenderer);
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if(this.button.mouseClicked(x, y, button) && te.reag != null && te.reag.getType().getName().equals(IDynamicReagent.UNKNOWN_NAME) && !te.paper.isEmpty()){
			ModPackets.network.sendToServer(new SendStringToServer("new_name", te.paper.getDisplayName(), te.getPos(), te.getWorld().provider.getDimension()));
		}
	}
}
