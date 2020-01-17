package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelAxle;
import com.Da_Technomancer.crossroads.render.TESR.models.ModelGearOctagon;
import com.Da_Technomancer.crossroads.tileentities.electric.DynamoTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

public class DynamoRenderer extends TileEntityRenderer<DynamoTileEntity>{

	@Override
	public void render(DynamoTileEntity dynamo, double x, double y, double z, float partialTicks, int destroyStage){
		if(!dynamo.getWorld().isBlockLoaded(dynamo.getPos()) || dynamo.getWorld().getBlockState(dynamo.getPos()).getBlock() != CRBlocks.dynamo){
			return;
		}

		LazyOptional<IAxleHandler> axle = dynamo.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(!axle.isPresent()){
			return;
		}

		Direction facing = dynamo.getWorld().getBlockState(dynamo.getPos()).get(CRProperties.HORIZ_FACING);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translated(x + .5F, y + .5F, z + .5F);
		GlStateManager.rotated(270F - facing.getHorizontalAngle(), 0, 1, 0);
		GlStateManager.rotated(90, 0, 0, 1);
		GlStateManager.rotated(-facing.getAxisDirection().getOffset() * axle.orElseThrow(NullPointerException::new).getAngle(partialTicks), 0, 1, 0);
		ModelAxle.render(GearFactory.findMaterial("Iron").getColor());
		GlStateManager.pushMatrix();
		GlStateManager.translated(0, 0.5F, 0);
		GlStateManager.scaled(0.7D, 0.7D, 0.7D);
		ModelGearOctagon.render(GearFactory.findMaterial("Copper").getColor());
		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
