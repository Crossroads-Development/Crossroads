package com.Da_Technomancer.crossroads.render.tesr;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.witchcraft.BloodCentrifugeTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import java.awt.*;

public class BloodCentrifugeRenderer implements BlockEntityRenderer<BloodCentrifugeTileEntity>{

	protected BloodCentrifugeRenderer(BlockEntityRendererProvider.Context dispatcher){
		super();
	}

	@Override
	public void render(BloodCentrifugeTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		LazyOptional<IAxleHandler> axle = te.getCapability(Capabilities.AXLE_CAPABILITY, null);
		if(state.getBlock() != CRBlocks.bloodCentrifuge || !axle.isPresent()){
			return;
		}
		int sampleCount = state.getValue(CRProperties.CONTENTS);
		VertexConsumer builder = buffer.getBuffer(RenderType.solid());

		matrix.translate(.5F, .5F, .5F);
		//Rotate
		matrix.mulPose(Axis.YP.rotationDegrees((float) RotaryUtil.getCCWSign(Direction.UP) * axle.orElseThrow(NullPointerException::new).getAngle(partialTicks)));

		Color ironCol = CRMaterialLibrary.findMaterial("iron").getColor();
		Color tinCol = CRMaterialLibrary.findMaterial("tin").getColor();

		//Rotating axle
		CRModels.drawAxle(matrix, buffer, combinedLight, ironCol);

		float supportHeight = 0.05F;
		float supportLen = 0.2F;
		float supportWid = 0.1F;
		TextureAtlasSprite sSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_MOUNT_TEXTURE);

		//Draw sample 'holder'
		matrix.translate(0, 0.4F, 0);
		CRModels.drawBox(matrix, builder, combinedLight, new int[] {tinCol.getRed(), tinCol.getGreen(), tinCol.getBlue(), tinCol.getAlpha()}, supportWid, supportHeight, supportLen, sSprite.getU0(), sSprite.getV0(), sSprite.getU(supportWid * 32D), sSprite.getV(supportLen * 32D), sSprite.getU0(), sSprite.getV0(), sSprite.getU(supportLen * 32D), sSprite.getV(supportHeight * 32D), sSprite.getU0(), sSprite.getV0(), sSprite.getU(supportWid * 32D), sSprite.getV(supportHeight * 32D));

		//Draw sample(s), if present
		for(int i = 0; i < sampleCount; i++){
			matrix.mulPose(Axis.YP.rotationDegrees(180));
			matrix.pushPose();
			matrix.translate(0, -0.2F, supportLen - 0.05F);
			matrix.mulPose(Axis.ZP.rotationDegrees(45));
			matrix.scale(0.5F, 0.5F, 0.5F);
			Minecraft.getInstance().getItemRenderer().renderStatic(getSampleItemstack(), ItemDisplayContext.FIXED, combinedLight, combinedOverlay, matrix, buffer, te.getLevel(), (int) te.getBlockPos().asLong());
			matrix.popPose();
		}
	}

	private static ItemStack SAMPLE_ITEMSTACK = ItemStack.EMPTY;

	private static ItemStack getSampleItemstack(){
		if(SAMPLE_ITEMSTACK.isEmpty()){//Lazy-load the value in case the item hasn't been initialized immediately
			SAMPLE_ITEMSTACK = new ItemStack(CRItems.bloodSample, 1);
		}
		return SAMPLE_ITEMSTACK;
	}
}
