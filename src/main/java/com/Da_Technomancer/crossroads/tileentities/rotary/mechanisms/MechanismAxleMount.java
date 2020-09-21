package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IMechanismProperty;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.render.TESR.CRModels;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class MechanismAxleMount implements IMechanism<GearFactory.GearMaterial>{

	private static final VoxelShape[] SHAPES_SIDE = new VoxelShape[6];
	private static final VoxelShape[] SHAPES_END = new VoxelShape[6];
	static{
		VoxelShape core = Block.makeCuboidShape(6, 6, 6, 10, 10, 10);
		SHAPES_SIDE[0] = VoxelShapes.or(core, Block.makeCuboidShape(6, 0, 6, 10, 3, 10), Block.makeCuboidShape(7, 3, 7, 9, 6, 9));//DOWN
		SHAPES_SIDE[1] = VoxelShapes.or(core, Block.makeCuboidShape(6, 13, 6, 10, 16, 10), Block.makeCuboidShape(7, 10, 7, 9, 13, 9));//UP
		SHAPES_SIDE[2] = VoxelShapes.or(core, Block.makeCuboidShape(6, 6, 0, 10, 10, 3), Block.makeCuboidShape(7, 7, 3, 9, 9, 6));//NORTH
		SHAPES_SIDE[3] = VoxelShapes.or(core, Block.makeCuboidShape(6, 6, 13, 10, 10, 16), Block.makeCuboidShape(7, 7, 10, 9, 9, 13));//SOUTH
		SHAPES_SIDE[4] = VoxelShapes.or(core, Block.makeCuboidShape(0, 6, 6, 3, 10, 10), Block.makeCuboidShape(3, 7, 7, 6, 9, 9));//WEST
		SHAPES_SIDE[5] = VoxelShapes.or(core, Block.makeCuboidShape(13, 6, 6, 16, 10, 10), Block.makeCuboidShape(10, 7, 7, 13, 9, 9));//EAST
		SHAPES_END[0] = Block.makeCuboidShape(6, 0, 6, 10, 2, 10);
		SHAPES_END[1] = Block.makeCuboidShape(6, 14, 6, 10, 16, 10);
		SHAPES_END[2] = Block.makeCuboidShape(6, 6, 0, 10, 10, 2);
		SHAPES_END[3] = Block.makeCuboidShape(6, 6, 14, 10, 10, 16);
		SHAPES_END[4] = Block.makeCuboidShape(0, 6, 6, 2, 10, 10);
		SHAPES_END[5] = Block.makeCuboidShape(14, 6, 6, 16, 10, 10);
	}
	private static final float OCT_SCALE = (2F + 2F * (float) Math.sqrt(2)) / 16F;
	private static final float ROD_HEIGHT = (4F - (float) Math.sqrt(2)) / 16F;

	@Override
	public double getInertia(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		return 0;
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return false;
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//No-op
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat){
		if(mat instanceof GearFactory.GearMaterial){
			return CRItems.axleMount.withMaterial((OreSetup.OreProfile) mat, 1);
		}else{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side == null ? VoxelShapes.empty() : axis == null || side.getAxis() == axis ? SHAPES_END[side.getIndex()] : SHAPES_SIDE[side.getIndex()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(side == null){
			return;
		}

		matrix.rotate(side.getOpposite().getRotation());//Apply orientation

		Direction.Axis selfAxis = side.getAxis();
		IVertexBuilder builder = buffer.getBuffer(RenderType.getSolid());
		int[] matCol = CRRenderUtil.convertColor(mat instanceof GearFactory.GearMaterial ? ((GearFactory.GearMaterial) mat).getColor() : Color.WHITE);
		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_MOUNT_TEXTURE);
		TextureAtlasSprite octSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_MOUNT_OCT_TEXTURE);

		if(axis == null || selfAxis == axis){
			//The axle is pointing into this mount

			matrix.translate(0, -7F / 16F, 0);
			matrix.push();
			matrix.scale(OCT_SCALE, 1 - 0.001F, OCT_SCALE);
			CRModels.draw8Core(builder, matrix, matCol, matCol, combinedLight, octSprite);
			matrix.pop();

			//Wall mount
			matrix.translate(0, -0.5F / 16F, 0);
			CRModels.drawBox(matrix, builder, combinedLight, matCol, 4F / 16F, 0.5F / 16F, 4F / 16F, sprite.getMinU(), sprite.getMinV(), sprite.getInterpolatedU(8), sprite.getInterpolatedV(8), sprite.getInterpolatedU(8), sprite.getMinV(), sprite.getMaxU(), sprite.getInterpolatedV(2), sprite.getInterpolatedU(8), sprite.getMinV(), sprite.getMaxU(), sprite.getInterpolatedV(2));
		}else{
			//The axle is pointing alongside this mount

			//Rotate to face along the axle
			if(selfAxis == Direction.Axis.X ? axis == Direction.Axis.Y : axis != Direction.Axis.X){
				matrix.rotate(Vector3f.YP.rotationDegrees(90));
			}

			//Render pointing along the X axis, on the bottom side

			//Octagon core
			float antiZFightModifier = side.getIndex() * 0.001F;//Small scale applied based on side to prevent z-fighting

			matrix.push();
			matrix.rotate(Vector3f.ZP.rotationDegrees(90));
			matrix.scale(OCT_SCALE + antiZFightModifier, 1 + antiZFightModifier, OCT_SCALE + antiZFightModifier);
			CRModels.draw8Core(builder, matrix, matCol, matCol, combinedLight, octSprite);
			matrix.pop();

			//Shaft
			matrix.translate(0, -OCT_SCALE / 2F - ROD_HEIGHT / 2F, 0);
			CRModels.drawBox(matrix, builder, combinedLight, matCol, 1F / 16F, ROD_HEIGHT / 2F, 1F / 16F, sprite.getMinU(), sprite.getMinV(), sprite.getInterpolatedU(8), sprite.getInterpolatedV(8), sprite.getMinU(), sprite.getInterpolatedV(8), sprite.getInterpolatedU(2), sprite.getMaxV(), sprite.getMinU(), sprite.getInterpolatedV(8), sprite.getInterpolatedU(2), sprite.getMaxV());

			//Wall mount
			matrix.translate(0, -ROD_HEIGHT / 2F - 1.5F / 16F, 0);
			CRModels.drawBox(matrix, builder, combinedLight, matCol, 2F / 16F, 1.5F / 16F, 2F / 16F, sprite.getMinU(), sprite.getMinV(), sprite.getInterpolatedU(8), sprite.getInterpolatedV(8), sprite.getInterpolatedU(8), sprite.getMinV(), sprite.getMaxU(), sprite.getInterpolatedV(2), sprite.getInterpolatedU(8), sprite.getMinV(), sprite.getMaxU(), sprite.getInterpolatedV(2));
		}
	}

	@Override
	public GearFactory.GearMaterial deserializeProperty(int serial){
		return GearFactory.GearMaterial.deserialize(serial);
	}

	@Override
	public GearFactory.GearMaterial loadProperty(String name){
		return GearFactory.findMaterial(name);
	}
}
