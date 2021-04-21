package com.Da_Technomancer.crossroads.gui;

import com.Da_Technomancer.crossroads.API.templates.MachineGUI;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.OreCleanserContainer;
import com.Da_Technomancer.crossroads.tileentities.fluid.OreCleanserTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
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
		initFluidManager(0, 8, 70);
		initFluidManager(1, 62, 70);
	}


	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		Minecraft.getInstance().getTextureManager().bind(TEXTURE);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		blit(matrix, leftPos + 25, topPos + 21, 176, 0, 36 * menu.progRef.get() / 50, 10);

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}
}
