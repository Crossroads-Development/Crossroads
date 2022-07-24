package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.electric.Dynamo;
import com.Da_Technomancer.crossroads.blocks.technomancy.LodestoneDynamo;
import com.Da_Technomancer.crossroads.items.item_sets.GearFactory;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.LazyOptional;

public class DynamoRenderer implements BlockEntityRenderer<ModuleTE>{

	protected DynamoRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(ModuleTE dynamo, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
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
