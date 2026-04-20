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
		VertexConsumer builder = buffer.getBuffer(RenderType.solid());

		matrix.mulPose(side.getOpposite().getRotation());//Apply orientation
		float angle = handler.getAngle(partialTicks);
		matrix.translate(0, -0.4375D, 0);
		matrix.mulPose(Axis.YP.rotationDegrees(-(float) RotaryUtil.getCCWSign(side) * angle));

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_8_TEXTURE);
		TextureAtlasSprite spriteRim = CRRenderUtil.getTextureSprite(CRRenderTypes.GEAR_8_RIM_TEXTURE);
		float top = 0.0625F;

		//If inverted, renders the core as red
		if(inverted){
			int[] invertCol = new int[] {255, 0, 0, 255};

			float radius = 2F / 16F;
			float zFightOffset = 0.001F;//Vertical offset to prevent z-fighting
			//Texture coords
			float radiusT = radius * 16F;
			float uSt = CRRenderUtil.getScaledU(sprite, 8 - radiusT);
			float uEn = CRRenderUtil.getScaledU(sprite, 8 + radiusT);
			float vSt = CRRenderUtil.getScaledV(sprite, 8 - radiusT);
			float vEn = CRRenderUtil.getScaledV(sprite, 8 + radiusT);

			CRRenderUtil.addVertexBlock(builder, matrix, -radius, top + zFightOffset, radius, uSt, vEn, 0, 1, 0, combinedLight, invertCol);
			CRRenderUtil.addVertexBlock(builder, matrix, radius, top + zFightOffset, radius, uEn, vEn, 0, 1, 0, combinedLight, invertCol);
			CRRenderUtil.addVertexBlock(builder, matrix, radius, top + zFightOffset, -radius, uEn, vSt, 0, 1, 0, combinedLight, invertCol);
			CRRenderUtil.addVertexBlock(builder, matrix, -radius, top + zFightOffset, -radius, uSt, vSt, 0, 1, 0, combinedLight, invertCol);
		}

		int[] color = CRRenderUtil.convertColor(mat instanceof CRMaterialLibrary.GearMaterial ? ((CRMaterialLibrary.GearMaterial) mat).getColor() : Color.WHITE);

		if(te.redstoneIn != 0 ^ inverted){
			//Render normally when active
			CRModels.draw8Gear(matrix, builder, color, combinedLight, CRModels.generateZFightFactor(te.getBlockPos(), side == null ? 0 : side.ordinal()));
		}else{
			//Render without prongs
			float lHalf = 7F / 16F;//Half the side length of the octagon
			matrix.scale(2F * lHalf, 1, 2F * lHalf);
			CRModels.draw8Core(builder, matrix, color, combinedLight, sprite, spriteRim);
		}
	}
}
