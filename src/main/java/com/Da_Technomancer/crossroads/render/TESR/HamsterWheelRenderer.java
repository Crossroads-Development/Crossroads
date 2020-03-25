package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.tileentities.technomancy.HamsterWheelTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

public class HamsterWheelRenderer extends TileEntityRenderer<HamsterWheelTileEntity>{

	private static final ResourceLocation textureHam = new ResourceLocation(Crossroads.MODID, "textures/model/hamster.png");

	@Override
	public void render(HamsterWheelTileEntity wheel, double x, double y, double z, float partialTicks, int destroyStage){
		World world = wheel.getWorld();
		BlockPos pos = wheel.getPos();
		if(!world.isBlockLoaded(pos) || world.getBlockState(pos).getBlock() != CRBlocks.hamsterWheel){
			return;
		}
		Direction facing = world.getBlockState(pos).get(CRProperties.HORIZ_FACING);

		GlStateManager.pushMatrix();
		GlStateManager.pushLightingAttributes();
		GlStateManager.disableLighting();
		GlStateManager.translated(x + .5D, y + .5D, z + .5D);
		GlStateManager.rotated(-facing.getHorizontalAngle() + 180, 0, 1, 0);
		GlStateManager.rotated(90, 1, 0, 0);

		float angle = wheel.nextAngle - wheel.angle;
		angle *= partialTicks;
		angle += wheel.angle;
		angle *= -facing.getAxisDirection().getOffset();

		//Feet
		GlStateManager.pushMatrix();
		GlStateManager.translated(-.2D, -.25D, .30D);
		float peakAngle = 60;
		float degreesPerCycle = 50;
		float feetAngle = Math.abs((4 * peakAngle * Math.abs(angle) / degreesPerCycle) % (4 * peakAngle) - (2 * peakAngle)) - peakAngle;
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 2; j++){
				GlStateManager.pushMatrix();
				GlStateManager.translated(j == 0 ? 0 : .4D, i == 0 ? -.065D : .065D, 0);
				GlStateManager.scaled(.4D, .07D, .49D);
				GlStateManager.rotated(i + j % 2 == 0 ? feetAngle : -feetAngle, 0, 1, 0);
				CRModels.drawAxle(textureHam, textureHam, Color.LIGHT_GRAY);
				GlStateManager.popMatrix();
			}
		}

		GlStateManager.popMatrix();

		//Wheel
		GlStateManager.pushMatrix();
		GlStateManager.rotated(angle, 0F, 1F, 0F);

		//Axle Support
		GlStateManager.pushMatrix();
		GlStateManager.translated(0, -.4375D, 0);
		GlStateManager.scaled(1, .8D, 1);
		GlStateManager.rotated(90, 1, 0, 0);
		CRModels.drawAxle(GearFactory.findMaterial("iron").getColor());
		GlStateManager.popMatrix();

		float lHalf = .375F;

		for(int i = 0; i < 8; i++){
			GlStateManager.pushMatrix();
			GlStateManager.rotated(45F * (float) i, 0, 1, 0);
			GlStateManager.translated(lHalf, -.25F, 0);
			GlStateManager.scaled(.41D, i % 2 == 0 ? .5D : .45D, 7.5D * lHalf);

			CRModels.drawAxle(Color.GRAY);
			GlStateManager.popMatrix();
		}

		GlStateManager.color3f(1, 1, 1);
		GlStateManager.popMatrix();

		GlStateManager.enableLighting();
		GlStateManager.popAttributes();
		GlStateManager.popMatrix();
	}
}
