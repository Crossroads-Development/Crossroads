package com.Da_Technomancer.crossroads.client.TESR;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.crossroads.client.TESR.models.ModelDrill;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class RotaryDrillRenderer extends TileEntitySpecialRenderer<RotaryDrillTileEntity>{

	private static final ModelDrill model = new ModelDrill();
	private static final ResourceLocation texture = new ResourceLocation(Main.MODID, "textures/model/drill.png");

	@Override
	public void render(RotaryDrillTileEntity drill, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(!(drill.getWorld().getBlockState(drill.getPos()).getBlock() instanceof RotaryDrill) || !drill.getWorld().isBlockLoaded(drill.getPos(), false)){
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		EnumFacing dir = drill.getWorld().getBlockState(drill.getPos()).getValue(EssentialsProperties.FACING);

		switch(dir){
			case DOWN:{
				GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translate(-.5F, -1.5F, .5F);
				break;
			}
			case UP:{
				GlStateManager.translate(.5F, -.5F, .5F);
				break;
			}
			case EAST:{
				GlStateManager.rotate(270F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translate(-.5F, -.5F, .5F);
				break;
			}
			case WEST:{
				GlStateManager.rotate(90F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translate(.5F, -1.5F, .5F);
				break;
			}
			case NORTH:{
				GlStateManager.rotate(270F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translate(.5F, -1.5F, .5F);
				break;
			}
			case SOUTH:{
				GlStateManager.rotate(90F, 1.0F, 0.0F, 0.0F);
				GlStateManager.translate(.5F, -.5F, -.5F);
				break;
			}
		}
		GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0F, -2F, 0F);
		GlStateManager.rotate(drill.getAngle() * dir.getAxisDirection().getOffset(), 0F, 1F, 0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		if(drill.isGolden()){
			GlStateManager.color(1F, 1F, 0.15F);
		}
		model.render();
		if(drill.isGolden()){
			GlStateManager.color(1, 1, 1);
		}
		GlStateManager.popMatrix();

	}

}
