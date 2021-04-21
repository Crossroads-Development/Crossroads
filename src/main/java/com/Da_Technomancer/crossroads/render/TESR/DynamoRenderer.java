package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.electric.Dynamo;
import com.Da_Technomancer.crossroads.blocks.technomancy.LodestoneDynamo;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.LazyOptional;

public class DynamoRenderer extends TileEntityRenderer<ModuleTE>{

	protected DynamoRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(ModuleTE dynamo, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		Block block = dynamo.getBlockState().getBlock();
		if(!(block instanceof Dynamo) && !(block instanceof LodestoneDynamo)){
			return;
		}

		Direction facing = dynamo.getBlockState().getValue(CRProperties.HORIZ_FACING);
		LazyOptional<IAxleHandler> axle = dynamo.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(!axle.isPresent()){
			return;
		}

		matrix.translate(0.5D, 0.5D, 0.5D);
		matrix.mulPose(Vector3f.YP.rotationDegrees(270F - facing.toYRot()));
		matrix.mulPose(Vector3f.ZP.rotationDegrees(90));
		matrix.mulPose(Vector3f.YP.rotationDegrees(-facing.getAxisDirection().getStep() * axle.orElseThrow(NullPointerException::new).getAngle(partialTicks)));
		CRModels.drawAxle(matrix, buffer, combinedLight, GearFactory.findMaterial("iron").getColor());

		matrix.translate(0, 0.45626D, 0);
		matrix.scale(0.7F, 0.7F, 0.7F);

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_8_TEXTURE);
		float lHalf = 7F / 16F;//Half the side length of the octagon

		//Renders the core of the gear, leaving only the prongs
		matrix.scale(2F * lHalf, 1, 2F * lHalf);
		CRModels.draw8Core(buffer.getBuffer(RenderType.solid()), matrix, CRRenderUtil.convertColor(GearFactory.findMaterial("copper").getColor()), combinedLight, sprite);
	}
}
