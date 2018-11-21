package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelGearOctagon;
import com.Da_Technomancer.crossroads.tileentities.alchemy.DynamoTileEntity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class DynamoRenderer extends TileEntitySpecialRenderer<DynamoTileEntity>{

	private final ModelGearOctagon modelOct = new ModelGearOctagon();
	private final ResourceLocation textureGear = new ResourceLocation(Main.MODID, "textures/model/gear_oct.png");

	@Override
	public void render(DynamoTileEntity dynamo, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(!dynamo.getWorld().isBlockLoaded(dynamo.getPos(), false) || dynamo.getWorld().getBlockState(dynamo.getPos()).getBlock() != ModBlocks.dynamo){
			return;
		}

		EnumFacing facing = dynamo.getWorld().getBlockState(dynamo.getPos()).getValue(Properties.HORIZ_FACING);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate(x + .5F, y + .5F, z + .5F);
		GlStateManager.rotate(270F - facing.getHorizontalAngle(), 0, 1, 0);
		GlStateManager.rotate(90, 0, 0, 1);
		GlStateManager.rotate(-(float) ((dynamo.nextAngle - dynamo.angle) * partialTicks + dynamo.angle), 0F, 1F, 0F);
		ModelAxle.render(GearFactory.findMaterial("Iron").getColor());
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0.5F, 0);
		GlStateManager.scale(0.7D, 0.7D, 0.7D);
		modelOct.render(textureGear, GearFactory.findMaterial("Copper").getColor());
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
