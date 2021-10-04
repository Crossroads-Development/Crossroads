package com.Da_Technomancer.crossroads.render.TESR;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.BloodCentrifugeTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.LazyOptional;

import java.awt.*;

public class BloodCentrifugeRenderer extends TileEntityRenderer<BloodCentrifugeTileEntity>{

	protected BloodCentrifugeRenderer(TileEntityRendererDispatcher dispatcher){
		super(dispatcher);
	}

	@Override
	public void render(BloodCentrifugeTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay){
		BlockState state = te.getBlockState();
		if(state.getBlock() != CRBlocks.bloodCentrifuge){
			return;
		}
		LazyOptional<IAxleHandler> axle = te.getCapability(Capabilities.AXLE_CAPABILITY, null);
		int sampleCount = state.getValue(CRProperties.CONTENTS);
		IVertexBuilder builder = buffer.getBuffer(RenderType.solid());

		matrix.translate(.5F, .5F, .5F);
		//Rotate
		matrix.mulPose(Vector3f.YP.rotationDegrees((float) RotaryUtil.getCCWSign(Direction.UP) * axle.orElseThrow(NullPointerException::new).getAngle(partialTicks)));

		Color ironCol = GearFactory.findMaterial("iron").getColor();
		Color tinCol = GearFactory.findMaterial("tin").getColor();

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
			matrix.mulPose(Vector3f.YP.rotationDegrees(180));
			matrix.pushPose();
			matrix.translate(0, -0.2F, supportLen - 0.05F);
			matrix.mulPose(Vector3f.ZP.rotationDegrees(45));
			matrix.scale(0.5F, 0.5F, 0.5F);
			Minecraft.getInstance().getItemRenderer().renderStatic(getSampleItemstack(), ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrix, buffer);
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
