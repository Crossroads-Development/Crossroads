package com.Da_Technomancer.crossroads.blocks.rotary.mechanisms;

import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.Capabilities;
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
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

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
			RotaryUtil.increaseMasterKey(true);
		}
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return ((cap == Capabilities.COG_CAPABILITY && (te.redstoneIn != 0 ^ inverted)) || cap == Capabilities.AXLE_CAPABILITY) && side == capSide;
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

		BlockEntity sideTE = te.getLevel().getBlockEntity(te.getBlockPos().relative(side));

		//Don't connect via cogs if disabled
		if((te.redstoneIn != 0) ^ inverted){
			//Other internal gears
			for(int i = 0; i < 6; i++){
				if(i != side.get3DDataValue() && i != side.getOpposite().get3DDataValue() && te.members[i] != null && te.members[i].hasCap(Capabilities.COG_CAPABILITY, Direction.from3DDataValue(i), te.mats[i], Direction.from3DDataValue(i), te.getAxleAxis(), te)){
					te.axleHandlers[i].propagate(masterIn, key, RotaryUtil.getDirSign(side, Direction.from3DDataValue(i)) * handler.getRotationRatio(), .5D, !handler.renderOffset());
				}
			}

			for(int i = 0; i < 6; i++){
				if(i != side.get3DDataValue() && i != side.getOpposite().get3DDataValue()){
					Direction facing = Direction.from3DDataValue(i);
					// Adjacent gears
					BlockEntity adjTE = te.getLevel().getBlockEntity(te.getBlockPos().relative(facing));
					if(adjTE != null){
						LazyOptional<ICogHandler> cogOpt;
						if((cogOpt = adjTE.getCapability(Capabilities.COG_CAPABILITY, side)).isPresent()){
							cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -handler.getRotationRatio(), .5D, facing.getOpposite(), handler.renderOffset());
						}else if((cogOpt = adjTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent()){
							//Check for large gears
							cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * handler.getRotationRatio(), .5D, side, handler.renderOffset());
						}
					}

					// Diagonal gears
					BlockEntity diagTE = te.getLevel().getBlockEntity(te.getBlockPos().relative(facing).relative(side));
					LazyOptional<ICogHandler> cogOpt;
					if(diagTE != null && (cogOpt = diagTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent() && RotaryUtil.canConnectThrough(te.getLevel(), te.getBlockPos().relative(facing), facing.getOpposite(), side)){
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * handler.getRotationRatio(), .5D, side.getOpposite(), handler.renderOffset());
					}

					if(sideTE != null && (cogOpt = sideTE.getCapability(Capabilities.COG_CAPABILITY, facing)).isPresent()){
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, .5D, side.getOpposite(), handler.renderOffset());
					}
				}
			}
		}

		//Connected block
		if(sideTE != null){
			LazyOptional<IAxisHandler> axisOpt = sideTE.getCapability(Capabilities.AXIS_CAPABILITY, side.getOpposite());
			if(axisOpt.isPresent()){
				axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
			}
			LazyOptional<IAxleHandler> axleOpt = sideTE.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite());
			if(axleOpt.isPresent()){
				axleOpt.orElseThrow(NullPointerException::new).propagate(masterIn, key, handler.getRotationRatio(), 0, handler.renderOffset());
			}
		}

		//Axle slot
		if(te.getAxleAxis() == side.getAxis() && te.members[6] != null && te.members[6].hasCap(Capabilities.AXLE_CAPABILITY, side, te.mats[6], null, te.getAxleAxis(), te)){
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
		matrix.mulPose(Axis.YP.rotationDegrees(- (float) RotaryUtil.getCCWSign(side) * angle));

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
			float uSt = sprite.getU(8 - radiusT);
			float uEn = sprite.getU(8 + radiusT);
			float vSt = sprite.getV(8 - radiusT);
			float vEn = sprite.getV(8 + radiusT);
			
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
