package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BlastFurnaceContainer;
import com.Da_Technomancer.crossroads.tileentities.rotary.BlastFurnaceTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BlastFurnaceScreen extends MachineGUI<BlastFurnaceContainer, BlastFurnaceTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/blast_furnace_gui.png");

	public BlastFurnaceScreen(BlastFurnaceContainer cont, PlayerInventory playerInv, ITextComponent text){
		super(cont, playerInv, text);
	}

	@Override
	public void init(){
		super.init();
		te.fluidManagers[0].initScreen( (width - xSize) / 2, (height - ySize) / 2, 63, 70);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize);
		blit(guiLeft + 25, guiTop + 38, 176, 0, 38 * te.progRef.get() / BlastFurnaceTileEntity.REQUIRED_PRG, 14);
		drawRect(guiLeft + 50, guiTop + 36 - te.carbRef.get() * 16 / BlastFurnaceTileEntity.CARBON_LIMIT, guiLeft + 52, guiTop + 36, 0xFF000000);

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
}
