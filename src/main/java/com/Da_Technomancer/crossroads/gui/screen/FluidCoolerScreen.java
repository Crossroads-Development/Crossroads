package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineScreen;
import com.Da_Technomancer.crossroads.blocks.heat.FluidCoolingChamberTileEntity;
import com.Da_Technomancer.crossroads.gui.container.FluidCoolerContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FluidCoolerScreen extends MachineScreen<FluidCoolerContainer, FluidCoolingChamberTileEntity>{

	private static final ResourceLocation TEXTURE = new ResourceLocation(Crossroads.MODID, "textures/gui/container/fluid_cooler_gui.png");

	public FluidCoolerScreen(FluidCoolerContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
	}

	@Override
	protected void init(){
		super.init();
		initFluidManager(0, 10, 70);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		RenderSystem.setShaderTexture(0, TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if(menu.totalHeatRef.get() > 0){
			blit(matrix, leftPos + 24, topPos + 32, 176, 0, (int) Math.ceil(46F * menu.releasedHeatRef.get() / menu.totalHeatRef.get()), 16);
		}

		super.renderBg(matrix, partialTicks, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY){
		super.renderLabels(matrix, mouseX, mouseY);

		//Render a label for maximum temperature
		if(menu.totalHeatRef.get() >= 0){
			int maxTemp = menu.maxTempRef.get();
			String s0 = MiscUtil.localize("tt.crossroads.fcc.max_temp.0", maxTemp);
			String s1 = MiscUtil.localize("tt.crossroads.fcc.max_temp.1", maxTemp);
			boolean overMax = maxTemp - menu.heatRef.get() < 0;
			font.draw(matrix, s0, imageWidth - 8 - font.width(s0), 24, overMax ? 0x800000 : 0x008000);
			font.draw(matrix, s1, imageWidth - 8 - font.width(s1), 32, overMax ? 0x800000 : 0x008000);
		}
	}
}
