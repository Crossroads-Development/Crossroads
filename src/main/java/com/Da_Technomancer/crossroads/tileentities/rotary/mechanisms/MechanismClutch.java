package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.render.TESR.CRModels;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanismClutch extends MechanismAxle{
	
	private final boolean inverted;

	public MechanismClutch(boolean inverted){
		this.inverted = inverted;
	}

	private static final VoxelShape[] SHAPES_CLUTCH = new VoxelShape[3];
	static{
		VoxelShape core = Block.makeCuboidShape(4, 4, 4, 12, 12, 12);
		SHAPES_CLUTCH[0] = VoxelShapes.or(SHAPES[0], core);
		SHAPES_CLUTCH[1] = VoxelShapes.or(SHAPES[1], core);
		SHAPES_CLUTCH[2] = VoxelShapes.or(SHAPES[2], core);
	}

	@Override
	public void onRedstoneChange(double prevValue, double newValue, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, double[] motData, MechanismTileEntity te){
		if((newValue == 0) ^ (prevValue == 0)){
			te.getWorld().playSound(null, te.getPos(), SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, (newValue != 0) ^ inverted ? 0.6F : 0.5F);
			RotaryUtil.increaseMasterKey(true);
		}
	}

	@Override
	public double getCircuitSignal(GearFactory.GearMaterial mat, Direction.Axis axis, double[] motData, MechanismTileEntity te){
		return Math.abs(motData[0]);
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return cap == Capabilities.AXLE_CAPABILITY && side == null && capSide.getAxis() == axis && (te.redstoneIn != 0 ^ inverted || capSide.getAxisDirection() == Direction.AxisDirection.NEGATIVE);
	}

	@Override
	public void propogate(GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
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

			Direction endDir = Direction.getFacingFromAxis(direct, axis);
			
			if(te.members[endDir.getIndex()] != null){
				//Do internal connection
				if(te.members[endDir.getIndex()].hasCap(Capabilities.AXLE_CAPABILITY, endDir, te.mats[endDir.getIndex()], endDir, axis, te)){
					te.axleHandlers[endDir.getIndex()].propogate(masterIn, key, rotRatioIn, 0, handler.renderOffset);
				}
			}else{
				//Connect externally
				TileEntity endTE = te.getWorld().getTileEntity(te.getPos().offset(endDir));
				Direction oEndDir = endDir.getOpposite();
				if(endTE != null){
					LazyOptional<IAxisHandler> axisOpt = endTE.getCapability(Capabilities.AXIS_CAPABILITY, oEndDir);
					if(axisOpt.isPresent()){
						axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
					}

					LazyOptional<IAxleHandler> axleOpt = endTE.getCapability(Capabilities.AXLE_CAPABILITY, oEndDir);
					if(axleOpt.isPresent()){
						axleOpt.orElseThrow(NullPointerException::new).propogate(masterIn, key, handler.rotRatio, 0, handler.renderOffset);
					}
				}
			}
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(GearFactory.GearMaterial mat){
		return inverted ? CRItems.invClutch.withMaterial(mat, 1) : CRItems.clutch.withMaterial(mat, 1);
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side != null || axis == null ? VoxelShapes.empty() : SHAPES_CLUTCH[axis.ordinal()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, float partialTicks, GearFactory.GearMaterial mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(axis == null){
			return;
		}

		MechanismTileEntity.SidedAxleHandler handler = te.axleHandlers[6];

		//Orientation
		if(axis != Direction.Axis.Y){
			Quaternion rotation = (axis == Direction.Axis.X ? Vector3f.ZN : Vector3f.XP).rotationDegrees(90);
			matrix.rotate(rotation);
		}
		
		//Clutch mechanism
		TextureAtlasSprite endSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_ENDS_TEXTURE);
		TextureAtlasSprite sideSprite = CRRenderUtil.getTextureSprite(inverted ? CRRenderTypes.CLUTCH_SIDE_INVERTED_TEXTURE : CRRenderTypes.CLUTCH_SIDE_TEXTURE);
		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
		float size = 0.25F;
		float height = 0.4998F;

		//Ends
		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, -size, endSprite.getMinU(), endSprite.getMinV(), 0, -1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, -size, endSprite.getMaxU(), endSprite.getMinV(), 0, -1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, size, endSprite.getMaxU(), endSprite.getMaxV(), 0, -1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, size, endSprite.getMinU(), endSprite.getMaxV(), 0, -1, 0, combinedLight);

		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, size, endSprite.getMinU(), endSprite.getMaxV(), 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, size, endSprite.getMaxU(), endSprite.getMaxV(), 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, -size, endSprite.getMaxU(), endSprite.getMinV(), 0, 1, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, -size, endSprite.getMinU(), endSprite.getMinV(), 0, 1, 0, combinedLight);

		//Sides
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, -size, sideSprite.getMinU(), sideSprite.getMaxV(), 0, 0, -1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, -size, sideSprite.getMaxU(), sideSprite.getMaxV(), 0, 0, -1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, -size, sideSprite.getMaxU(), sideSprite.getMinV(), 0, 0, -1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, -size, sideSprite.getMinU(), sideSprite.getMinV(), 0, 0, -1, combinedLight);

		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, size, sideSprite.getMaxU(), sideSprite.getMinV(), 0, 0, 1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, size, sideSprite.getMinU(), sideSprite.getMinV(), 0, 0, 1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, size, sideSprite.getMinU(), sideSprite.getMaxV(), 0, 0, 1, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, size, sideSprite.getMaxU(), sideSprite.getMaxV(), 0, 0, 1, combinedLight);

		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, size, sideSprite.getMinU(), sideSprite.getMinV(), -1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, size, sideSprite.getMinU(), sideSprite.getMaxV(), -1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, height, -size, sideSprite.getMaxU(), sideSprite.getMaxV(), -1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, -size, 0, -size, sideSprite.getMaxU(), sideSprite.getMinV(), -1, 0, 0, combinedLight);

		CRRenderUtil.addVertexBlock(builder, matrix, size, height, -size, sideSprite.getMinU(), sideSprite.getMaxV(), 1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, height, size, sideSprite.getMaxU(), sideSprite.getMaxV(), 1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, size, sideSprite.getMaxU(), sideSprite.getMinV(), 1, 0, 0, combinedLight);
		CRRenderUtil.addVertexBlock(builder, matrix, size, 0, -size, sideSprite.getMinU(), sideSprite.getMinV(), 1, 0, 0, combinedLight);

		//Axle
		float angle = handler.getAngle(partialTicks);
		matrix.rotate(Vector3f.YP.rotationDegrees(angle));
		CRModels.drawAxle(matrix, buffer, combinedLight, mat.getColor());
	}
}
