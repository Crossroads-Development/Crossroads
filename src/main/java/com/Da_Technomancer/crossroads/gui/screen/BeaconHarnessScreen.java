package com.Da_Technomancer.crossroads.gui.screen;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.BeaconHarnessContainer;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import net.minecraft.network.chat.Component;

public class BeaconHarnessScreen extends AbstractContainerScreen<BeaconHarnessContainer>{

	private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Crossroads.MODID, "textures/gui/container/beacon_harness_gui.png");

	public BeaconHarnessScreen(BeaconHarnessContainer cont, Inventory playerInv, Component name){
		super(cont, playerInv, name);
		imageWidth = 300;
		imageHeight = 300;
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		super.render(matrix, mouseX, mouseY, partialTicks);
		renderTooltip(matrix, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
//		super.drawGuiContainerBackgroundLayer(matrix, partialTicks, mouseX, mouseY);

		RenderSystem.color4f(1, 1, 1, 1);
		Minecraft.getInstance().getTextureManager().bind(GUI_TEXTURES);

		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight, 512, 512);
		int cycle = menu.cycleRef.get();
		boolean running = cycle >= 0;

		if(running){
			//Selector arrow
			matrix.pushPose();
			matrix.translate(leftPos + 150, topPos + 150, 0);
			matrix.mulPose(Vector3f.ZP.rotationDegrees(360F * cycle / BeaconHarnessTileEntity.LOOP_TIME));
			matrix.translate(81, -8, 150);
			blit(matrix, 0, 0, 300, 0, 64, 16, 512, 512);
			matrix.popPose();
		}
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY){
		//Not rendering the inventory labels
		//super.drawGuiContainerForegroundLayer(matrix, mouseX, mouseY);

		int cycle = menu.cycleRef.get();
		boolean running = cycle >= 0;

		String s;
		if(running){
			s = MiscUtil.localize("container.crossroads.beacon_harness.cycle", cycle, BeaconHarnessTileEntity.LOOP_TIME);
		}else{
			s = MiscUtil.localize("container.crossroads.beacon_harness.cooldown", -cycle - 1);
		}
		font.draw(matrix, s, 100, 110, 0x404040);

		boolean safety = menu.te == null || menu.te.isSafetyPeriod(cycle);
		BeamUnit output = running ? BeaconHarnessTileEntity.getOutput(cycle) : BeamUnit.EMPTY;
		String input;
		if(safety){
			input = MiscUtil.localize("container.crossroads.beacon_harness.input.none");
		}else if(output.getEnergy() == 0){
			input = MiscUtil.localize("container.crossroads.beacon_harness.input.energy");
		}else if(output.getPotential() == 0){
			input = MiscUtil.localize("container.crossroads.beacon_harness.input.potential");
		}else{
			input = MiscUtil.localize("container.crossroads.beacon_harness.input.stability");
		}

		font.draw(matrix, input, 100, 130, 0x404040);

		font.draw(matrix, MiscUtil.localize("container.crossroads.beacon_harness.output.energy", output.getEnergy()), 100, 150, 0x404040);
		font.draw(matrix, MiscUtil.localize("container.crossroads.beacon_harness.output.potential", output.getPotential()), 100, 170, 0x404040);
		font.draw(matrix, MiscUtil.localize("container.crossroads.beacon_harness.output.stability", output.getStability()), 100, 190, 0x404040);
	}
}
