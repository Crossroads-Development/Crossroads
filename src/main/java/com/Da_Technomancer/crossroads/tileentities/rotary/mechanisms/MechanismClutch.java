package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.IMechanismProperty;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.render.TESR.CRModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class MechanismClutch extends MechanismAxle{
	
	private final boolean inverted;

	public MechanismClutch(boolean inverted){
		this.inverted = inverted;
	}

	private static final VoxelShape[] SHAPES_CLUTCH = new VoxelShape[3];
	static{
		SHAPES_CLUTCH[0] = Shapes.or(SHAPES[0], Block.box(8, 4, 4, 16, 12, 12));
		SHAPES_CLUTCH[1] = Shapes.or(SHAPES[1], Block.box(4, 8, 4, 12, 16, 4));
		SHAPES_CLUTCH[2] = Shapes.or(SHAPES[2], Block.box(4, 4, 8, 12, 12, 16));
	}

	@Override
	public void onRedstoneChange(double prevValue, double newValue, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, double energy, double speed, MechanismTileEntity te){
		if((newValue == 0) ^ (prevValue == 0)){
			te.getLevel().playSound(null, te.getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, (newValue != 0) ^ inverted ? 0.6F : 0.5F);
			RotaryUtil.increaseMasterKey(true);
		}
	}

	@Override
	public double getCircuitSignal(IMechanismProperty mat, Direction.Axis axis, double energy, double speed, MechanismTileEntity te){
		return speed;
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return cap == Capabilities.AXLE_CAPABILITY && side == null && capSide.getAxis() == axis && (te.redstoneIn != 0 ^ inverted || capSide.getAxisDirection() == Direction.AxisDirection.NEGATIVE);
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//This mechanism should always be in the axle slot
		if(side != null){
			return;
		}

		if(rotRatioIn == 0){
			rotRatioIn = 1;
		}


		//If true, this has already been checked.
		if(key == handler.updateKey){
			//If true, there is rotation conflict.
			if(handler.rotRatio != rotRatioIn){
				masterIn.lock();
			}
			return;
		}

		if(masterIn.addToList(handler)){
			return;
		}

		handler.rotRatio = rotRatioIn;
		handler.updateKey = key;

		
		
		for(Direction.AxisDirection direct : Direction.AxisDirection.values()){
			if(direct == Direction.AxisDirection.POSITIVE && te.redstoneIn == 0 ^ inverted){
				continue;
			}

			Direction endDir = Direction.get(direct, axis);
			
			if(te.members[endDir.get3DDataValue()] != null){
				//Do internal connection
				if(te.members[endDir.get3DDataValue()].hasCap(Capabilities.AXLE_CAPABILITY, endDir, te.mats[endDir.get3DDataValue()], endDir, axis, te)){
					te.axleHandlers[endDir.get3DDataValue()].propagate(masterIn, key, rotRatioIn, 0, handler.renderOffset);
				}
			}else{
				//Connect externally
				BlockEntity endTE = te.getLevel().getBlockEntity(te.getBlockPos().relative(endDir));
				Direction oEndDir = endDir.getOpposite();
				if(endTE != null){
					LazyOptional<IAxisHandler> axisOpt = endTE.getCapability(Capabilities.AXIS_CAPABILITY, oEndDir);
					if(axisOpt.isPresent()){
						axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
					}

					LazyOptional<IAxleHandler> axleOpt = endTE.getCapability(Capabilities.AXLE_CAPABILITY, oEndDir);
					if(axleOpt.isPresent()){
						axleOpt.orElseThrow(NullPointerException::new).propagate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
					}
				}
			}
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat){
		if(mat instanceof GearFactory.GearMaterial){
			return inverted ? CRItems.invClutch.withMaterial((OreSetup.OreProfile) mat, 1) : CRItems.clutch.withMaterial((OreSetup.OreProfile) mat, 1);
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side != null || axis == null ? Shapes.empty() : SHAPES_CLUTCH[axis.ordinal()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, PoseStack matrix, MultiBufferSource buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(axis == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[6];

		//Orientation
		if(axis != Direction.Axis.Y){
			Quaternion rotation = (axis == Direction.Axis.X ? Vector3f.ZN : Vector3f.XP).rotationDegrees(90);
			matrix.mulPose(rotation);
		}
		
		//Clutch mechanism
		TextureAtlasSprite endSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_ENDS_TEXTURE);
		TextureAtlasSprite sideSprite = CRRenderUtil.getTextureSprite(inverted ? CRRenderTypes.CLUTCH_SIDE_INVERTED_TEXTURE : CRRenderTypes.CLUTCH_SIDE_TEXTURE);
		VertexConsumer builder = buffer.getBuffer(RenderType.solid());
		float size = 0.25F;
		float height = 0.5001F;

		//Ends
		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, -size, endSprite.getU0(), endSprite.getV0(), 0, -1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, -size, endSprite.getU1(), endSprite.getV0(), 0, -1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, size, endSprite.getU1(), endSprite.getV1(), 0, -1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, size, endSprite.getU0(), endSprite.getV1(), 0, -1, 0, combinedLight);

		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, size, endSprite.getU0(), endSprite.getV1(), 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, size, endSprite.getU1(), endSprite.getV1(), 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, -size, endSprite.getU1(), endSprite.getV0(), 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, -size, endSprite.getU0(), endSprite.getV0(), 0, 1, 0, combinedLight);

		//Sides
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, -size, sideSprite.getU0(), sideSprite.getV1(), 0, 0, -1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, -size, sideSprite.getU1(), sideSprite.getV1(), 0, 0, -1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, -size, sideSprite.getU1(), sideSprite.getV0(), 0, 0, -1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, -size, sideSprite.getU0(), sideSprite.getV0(), 0, 0, -1, combinedLight);

		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, size, sideSprite.getU1(), sideSprite.getV0(), 0, 0, 1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, size, sideSprite.getU0(), sideSprite.getV0(), 0, 0, 1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, size, sideSprite.getU0(), sideSprite.getV1(), 0, 0, 1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, size, sideSprite.getU1(), sideSprite.getV1(), 0, 0, 1, combinedLight);

		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, size, sideSprite.getU0(), sideSprite.getV0(), -1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, size, sideSprite.getU0(), sideSprite.getV1(), -1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, -size, sideSprite.getU1(), sideSprite.getV1(), -1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, -size, sideSprite.getU1(), sideSprite.getV0(), -1, 0, 0, combinedLight);

		CRRenderUtil.addVertexBlock(builder, matrix, size, height, -size, sideSprite.getU0(), sideSprite.getV1(), 1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, size, sideSprite.getU1(), sideSprite.getV1(), 1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, size, sideSprite.getU1(), sideSprite.getV0(), 1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, -size, sideSprite.getU0(), sideSprite.getV0(), 1, 0, 0, combinedLight);

		//Axle
		float angle = handler.getAngle(partialTicks);
		matrix.mulPose(Vector3f.YP.rotationDegrees(angle));
		CRModels.drawAxle(matrix, buffer, combinedLight, mat instanceof GearFactory.GearMaterial ? ((GearFactory.GearMaterial) mat).getColor() : Color.WHITE);
	}
}
