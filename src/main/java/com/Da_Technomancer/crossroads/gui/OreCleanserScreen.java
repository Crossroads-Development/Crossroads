package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.OreCleanserContainer;
import com.Da_Technomancer.crossroads.tileentities.fluid.OreCleanserTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class OreCleanserScreen extends MachineGUI<OreCleanserContainer, OreCleanserTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/ore_cleanser_gui.png");

	public OreCleanserScreen(OreCleanserContainer cont, PlayerInventory playerInv, ITextComponent name){
		super(cont, playerInv, name);
	}

	@Override
	public void init(){
		super.init();
		te.fluidManagers[0].initScreen((width - xSize) / 2, (height - ySize) / 2, 8, 70);
		te.fluidManagers[1].initScreen((width - xSize) / 2, (height - ySize) / 2, 62, 70);
	}


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);

		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		blit(i, j, 0, 0, xSize, ySize);
		blit(guiLeft + 25, guiTop + 21, 176, 0, 36 * te.progRef.get() / 50, 10);

		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
}
