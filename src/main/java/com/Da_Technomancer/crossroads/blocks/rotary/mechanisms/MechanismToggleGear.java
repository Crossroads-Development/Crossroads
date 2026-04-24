package com.Da_Technomancer.crossroads.blocks.rotary.mechanisms;

import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.*;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.tesr.CRModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.BlockCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class MechanismToggleGear extends MechanismSmallGear{

	private final boolean inverted;

	public MechanismToggleGear(boolean inverted){
		this.inverted = inverted;
	}

	@Override
	public void onRedstoneChange(double prevValue, double newValue, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, double energy, double speed, MechanismTileEntity te){
		if((newValue == 0) ^ (prevValue == 0)){
			CRSounds.playSoundServer(te.getLevel(), te.getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, (newValue != 0) ^ inverted ? 0.6F : 0.5F);
			RotaryUtil.increaseMasterKey(true, te.getLevel());
		}
	}

	@Override
	public boolean hasCap(BlockCapability<?, ?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return ((cap == CRCapabilities.COG_CAPABILITY && (te.redstoneIn != 0 ^ inverted)) || cap == CRCapabilities.AXLE_CAPABILITY) && side == capSide;
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//This mechanism should never be in the axle slot
		if(side == null){
			return;
		}

		if(lastRadius != 0){
			rotRatioIn *= lastRadius * 2D;
		}

		//If true, this has already been checked.
		if(key == handler.getUpdateKey()){
			//If true, there is rotation conflict.
			if(handler.getRotationRatio() != rotRatioIn){
				masterIn.lock();
			}
			return;
		}

		if(masterIn.addToList(handler)){
			return;
		}

		handler.setRotRatio(rotRatioIn);
		handler.setUpdateKey(key);

		//Don't connect via cogs if disabled
		if((te.redstoneIn != 0) ^ inverted){
			propagateCogs(mat, side, axis, te, handler, masterIn, key, rotRatioIn, lastRadius);
		}

		//Connected block
		BlockPos sidePos = te.getBlockPos().relative(side);
		RotaryUtil.propagateAxially(te.getLevel(), sidePos, side.getOpposite(), handler, masterIn, key, handler.renderOffset());

		//Axle slot
		if(te.getAxleAxis() == side.getAxis() && te.members[6] != null && te.members[6].hasCap(CRCapabilities.AXLE_CAPABILITY, side, te.mats[6], null, te.getAxleAxis(), te)){
			te.axleHandlers[6].propagate(masterIn, key, handler.getRotationRatio(), 0, handler.renderOffset());
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat){
		if(mat instanceof CRMaterialLibrary.GearMaterial){
			return inverted ? CRItems.invToggleGear.withMaterial((CRMaterialLibrary.OreProfile) mat, 1) : CRItems.toggleGear.withMaterial((CRMaterialLibrary.OreProfile) mat, 1);
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, PoseStack matrix, MultiBufferSource buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(side == null){
			return;
		}

		IAxleHandler handler = te.axleHandlers[side.get3DDataValue()];


		matrix.mulPose(side.getOpposite().getRotation());//Apply orientation
		float angle = handler.getAngle(partialTicks);
		matrix.translate(0, -0.4375D, 0);
		matrix.mulPose(Axis.YP.rotationDegrees(-(float) RotaryUtil.getCCWSign(side) * angle));
		final float lHalf = 7F / 16F;//Half the side length of the octagon

		//Renders an extra layer over the regular gear model to indicate this is a toggle gear
		VertexConsumer builderCutout = buffer.getBuffer(RenderType.cutout());
		TextureAtlasSprite sprite = inverted ? CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_8_TOGGLE_INV_TEXTURE) : CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_8_TOGGLE_TEXTURE);

		float zFightOffset = 0.001F;//Vertical offset to prevent z-fighting
		float recessY = (1F/8F - 1/16F) * lHalf;
		float backY = (-1F/8F) * lHalf;
		//Texture coords
		float uSt = sprite.getU0();
		float uEn = sprite.getU1();
		float vSt = sprite.getV0();
		float vEn = sprite.getV1();
		final float halfSideWidth = lHalf;
		int[] col = {255, 255, 255, 255};
		//Front
		CRRenderUtil.addVertexBlock(builderCutout, matrix, -halfSideWidth, recessY + zFightOffset, halfSideWidth, uSt, vEn, 0, 1, 0, combinedLight, col);
		CRRenderUtil.addVertexBlock(builderCutout, matrix, halfSideWidth, recessY + zFightOffset, halfSideWidth, uEn, vEn, 0, 1, 0, combinedLight, col);
		CRRenderUtil.addVertexBlock(builderCutout, matrix, halfSideWidth, recessY + zFightOffset, -halfSideWidth, uEn, vSt, 0, 1, 0, combinedLight, col);
		CRRenderUtil.addVertexBlock(builderCutout, matrix, -halfSideWidth, recessY + zFightOffset, -halfSideWidth, uSt, vSt, 0, 1, 0, combinedLight, col);

		//Back
		CRRenderUtil.addVertexBlock(builderCutout, matrix, -halfSideWidth, backY - zFightOffset, halfSideWidth, uSt, vEn, 0, 1, 0, combinedLight, col);
		CRRenderUtil.addVertexBlock(builderCutout, matrix, -halfSideWidth, backY - zFightOffset, -halfSideWidth, uSt, vSt, 0, 1, 0, combinedLight, col);
		CRRenderUtil.addVertexBlock(builderCutout, matrix, halfSideWidth, backY - zFightOffset, -halfSideWidth, uEn, vSt, 0, 1, 0, combinedLight, col);
		CRRenderUtil.addVertexBlock(builderCutout, matrix, halfSideWidth, backY - zFightOffset, halfSideWidth, uEn, vEn, 0, 1, 0, combinedLight, col);

		int[] color = CRRenderUtil.convertColor(mat instanceof CRMaterialLibrary.GearMaterial ? ((CRMaterialLibrary.GearMaterial) mat).getColor() : Color.WHITE);

		VertexConsumer builder = buffer.getBuffer(RenderType.solid());
		if(te.redstoneIn != 0 ^ inverted){
			//Render normally when active
			CRModels.draw8GearMilled(matrix, builder, color, combinedLight, CRModels.generateZFightFactor(te.getBlockPos(), side == null ? 0 : side.ordinal()));
		}else{
			//Render without prongs
			CRModels.draw8CoreMilled(matrix, builder, color, combinedLight, CRModels.generateZFightFactor(te.getBlockPos(), side == null ? 0 : side.ordinal()));
		}
	}
}
