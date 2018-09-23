package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.tileentities.technomancy.HamsterWheelTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;

public class HamsterWheelRenderer extends TileEntitySpecialRenderer<HamsterWheelTileEntity>{

	private final ResourceLocation textureAx = new ResourceLocation(Main.MODID, "textures/model/axle.png");
	private final ResourceLocation textureHam = new ResourceLocation(Main.MODID, "textures/model/hamster.png");
	private final ModelAxle modelAxle = new ModelAxle();

	@Override
	public void render(HamsterWheelTileEntity wheel, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		World world = wheel.getWorld();
		BlockPos pos = wheel.getPos();
		if(!world.isBlockLoaded(pos, false) || world.getBlockState(pos).getBlock() != ModBlocks.hamsterWheel){
			return;
		}
		EnumFacing facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING);

		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5D, y + .5D, z + .5D);
		GlStateManager.rotate(-facing.getHorizontalAngle() + 180, 0, 1, 0);
		GlStateManager.rotate(90, 1, 0, 0);

		float angle = wheel.nextAngle - wheel.angle;
		angle *= partialTicks;
		angle += wheel.angle;
		angle *= -facing.getAxisDirection().getOffset();

		//Feet
		GlStateManager.pushMatrix();
		GlStateManager.translate(-.2D, -.25D, .30D);
		float peakAngle = 60;
		float degreesPerCycle = 50;
		float feetAngle = Math.abs((4 * peakAngle * Math.abs(angle) / degreesPerCycle) % (4 * peakAngle) - (2 * peakAngle)) - peakAngle;
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 2; j++){
				GlStateManager.pushMatrix();
				GlStateManager.translate(j == 0 ? 0 : .4D, i == 0 ? -.065D : .065D, 0);
				GlStateManager.scale(.4D, .07D, .49D);
				GlStateManager.rotate(i + j % 2 == 0 ? feetAngle : -feetAngle, 0, 1, 0);
				modelAxle.render(textureHam, textureHam, Color.WHITE);
				GlStateManager.popMatrix();
			}
		}

		GlStateManager.popMatrix();

		//Wheel
		GlStateManager.pushMatrix();
		GlStateManager.rotate(angle, 0F, 1F, 0F);

		//Axle Support
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, -.4375D, 0);
		GlStateManager.scale(1, .8D, 1);
		GlStateManager.rotate(90, 1, 0, 0);
		modelAxle.render(textureAx, textureAx, Color.WHITE);
		GlStateManager.popMatrix();

		float lHalf = .375F;

		for(int i = 0; i < 8; i++){
			GlStateManager.pushMatrix();
			GlStateManager.rotate(45F * (float) i, 0, 1, 0);
			GlStateManager.translate(lHalf, -.25F, 0);
			GlStateManager.scale(.41D, i % 2 == 0 ? .5D : .45D, 7.5D * lHalf);

			modelAxle.render(textureAx, textureAx, Color.LIGHT_GRAY);
			GlStateManager.popMatrix();
		}

		GlStateManager.color(1, 1, 1);
		GlStateManager.popMatrix();

		GlStateManager.enableLighting();
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
}
