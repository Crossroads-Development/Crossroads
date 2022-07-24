package com.Da_Technomancer.crossroads.blocks.rotary.mechanisms;

import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.api.rotary.IMechanism;
import com.Da_Technomancer.crossroads.api.rotary.IMechanismAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.IMechanismProperty;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.item_sets.GearFactory;
import com.Da_Technomancer.crossroads.items.item_sets.OreSetup;
import com.Da_Technomancer.crossroads.render.CRRenderTypes;
import com.Da_Technomancer.crossroads.render.tesr.CRModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
		VoxelShape core = Block.box(6, 6, 6, 10, 10, 10);
		SHAPES_SIDE[0] = Shapes.or(core, Block.box(6, 0, 6, 10, 3, 10), Block.box(7, 3, 7, 9, 6, 9));//DOWN
		SHAPES_SIDE[1] = Shapes.or(core, Block.box(6, 13, 6, 10, 16, 10), Block.box(7, 10, 7, 9, 13, 9));//UP
		SHAPES_SIDE[2] = Shapes.or(core, Block.box(6, 6, 0, 10, 10, 3), Block.box(7, 7, 3, 9, 9, 6));//NORTH
		SHAPES_SIDE[3] = Shapes.or(core, Block.box(6, 6, 13, 10, 10, 16), Block.box(7, 7, 10, 9, 9, 13));//SOUTH
		SHAPES_SIDE[4] = Shapes.or(core, Block.box(0, 6, 6, 3, 10, 10), Block.box(3, 7, 7, 6, 9, 9));//WEST
		SHAPES_SIDE[5] = Shapes.or(core, Block.box(13, 6, 6, 16, 10, 10), Block.box(10, 7, 7, 13, 9, 9));//EAST
		SHAPES_END[0] = Block.box(6, 0, 6, 10, 2, 10);
		SHAPES_END[1] = Block.box(6, 14, 6, 10, 16, 10);
		SHAPES_END[2] = Block.box(6, 6, 0, 10, 10, 2);
		SHAPES_END[3] = Block.box(6, 6, 14, 10, 10, 16);
		SHAPES_END[4] = Block.box(0, 6, 6, 2, 10, 10);
		SHAPES_END[5] = Block.box(14, 6, 6, 16, 10, 10);
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
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
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
		return side == null ? Shapes.empty() : axis == null || side.getAxis() == axis ? SHAPES_END[side.get3DDataValue()] : SHAPES_SIDE[side.get3DDataValue()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, PoseStack matrix, MultiBufferSource buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(side == null){
			return;
		}

		matrix.mulPose(side.getOpposite().getRotation());//Apply orientation

		Direction.Axis selfAxis = side.getAxis();
		VertexConsumer builder = buffer.getBuffer(RenderType.solid());
		int[] matCol = CRRenderUtil.convertColor(mat instanceof GearFactory.GearMaterial ? ((GearFactory.GearMaterial) mat).getColor() : Color.WHITE);
		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_MOUNT_TEXTURE);
		TextureAtlasSprite octSprite = CRRenderUtil.getTextureSprite(CRRenderTypes.AXLE_MOUNT_OCT_TEXTURE);

		if(axis == null || selfAxis == axis){
			//The axle is pointing into this mount

			matrix.translate(0, -7F / 16F, 0);
			matrix.pushPose();
			matrix.scale(OCT_SCALE, 1 - 0.001F, OCT_SCALE);
			CRModels.draw8Core(builder, matrix, matCol, matCol, combinedLight, octSprite);
			matrix.popPose();

			//Wall mount
			matrix.translate(0, -0.5F / 16F, 0);
			CRModels.drawBox(matrix, builder, combinedLight, matCol, 4F / 16F, 0.5F / 16F, 4F / 16F, sprite.getU0(), sprite.getV0(), sprite.getU(8), sprite.getV(8), sprite.getU(8), sprite.getV0(), sprite.getU1(), sprite.getV(2), sprite.getU(8), sprite.getV0(), sprite.getU1(), sprite.getV(2));
		}else{
			//The axle is pointing alongside this mount

			//Rotate to face along the axle
			if(selfAxis == Direction.Axis.X ? axis == Direction.Axis.Y : axis != Direction.Axis.X){
				matrix.mulPose(Vector3f.YP.rotationDegrees(90));
			}

			//Render pointing along the X axis, on the bottom side

			//Octagon core
			float antiZFightModifier = side.get3DDataValue() * 0.001F;//Small scale applied based on side to prevent z-fighting

			matrix.pushPose();
			matrix.mulPose(Vector3f.ZP.rotationDegrees(90));
			matrix.scale(OCT_SCALE + antiZFightModifier, 1 + antiZFightModifier, OCT_SCALE + antiZFightModifier);
			CRModels.draw8Core(builder, matrix, matCol, matCol, combinedLight, octSprite);
			matrix.popPose();

			//Shaft
			matrix.translate(0, -OCT_SCALE / 2F - ROD_HEIGHT / 2F, 0);
			CRModels.drawBox(matrix, builder, combinedLight, matCol, 1F / 16F, ROD_HEIGHT / 2F, 1F / 16F, sprite.getU0(), sprite.getV0(), sprite.getU(8), sprite.getV(8), sprite.getU0(), sprite.getV(8), sprite.getU(2), sprite.getV1(), sprite.getU0(), sprite.getV(8), sprite.getU(2), sprite.getV1());

			//Wall mount
			matrix.translate(0, -ROD_HEIGHT / 2F - 1.5F / 16F, 0);
			CRModels.drawBox(matrix, builder, combinedLight, matCol, 2F / 16F, 1.5F / 16F, 2F / 16F, sprite.getU0(), sprite.getV0(), sprite.getU(8), sprite.getV(8), sprite.getU(8), sprite.getV0(), sprite.getU1(), sprite.getV(2), sprite.getU(8), sprite.getV0(), sprite.getU1(), sprite.getV(2));
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
