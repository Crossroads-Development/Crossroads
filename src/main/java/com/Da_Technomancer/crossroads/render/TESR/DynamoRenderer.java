package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.DynamoTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.LazyOptional;

public class DynamoRenderer extends TileEntityRenderer<DynamoTileEntity>{

	protected DynamoRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(DynamoTileEntity dynamo, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		if(dynamo.getBlockState().getBlock() != CRBlocks.dynamo){
			return;
		}

		Direction facing = dynamo.getBlockState().get(CRProperties.HORIZ_FACING);
		LazyOptional<IAxleHandler> axle = dynamo.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(!axle.isPresent()){
			return;
		}

		matrix.translate(0.5D, 0.5D, 0.5D);
		matrix.rotate(Vector3f.YP.rotationDegrees(270F - facing.getHorizontalAngle()));
		matrix.rotate(Vector3f.ZP.rotationDegrees(90));
		matrix.rotate(Vector3f.YP.rotationDegrees(-facing.getAxisDirection().getOffset() * axle.orElseThrow(NullPointerException::new).getAngle(partialTicks)));
		CRModels.drawAxle(matrix, buffer, combinedLight, GearFactory.findMaterial("iron").getColor());

		matrix.translate(0, 0.45626D, 0);
		matrix.scale(0.7F, 0.7F, 0.7F);
		CRModels.draw8Gear(matrix, buffer.getBuffer(RenderType.getSolid()), CRRenderUtil.convertColor(GearFactory.findMaterial("copper").getColor()), combinedLight);
	}
}
