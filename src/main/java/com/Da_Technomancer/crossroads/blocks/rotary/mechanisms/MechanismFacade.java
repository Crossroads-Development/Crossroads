package com.Da_Technomancer.crossroads.blocks.rotary.mechanisms;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.rotary.*;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.item_sets.GearFacade;
import com.Da_Technomancer.crossroads.render.tesr.CRModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.client.model.data.ModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanismFacade implements IMechanism<GearFacade.FacadeBlock>{

	public static final MechanismFacade INSTANCE = new MechanismFacade();

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		SHAPES[0] = Block.box(0, 0, 0, 16, 2, 16);
		SHAPES[1] = Block.box(0, 14, 0, 16, 16, 16);
		SHAPES[2] = Block.box(0, 0, 0, 16, 16, 2);
		SHAPES[3] = Block.box(0, 0, 14, 16, 16, 16);
		SHAPES[4] = Block.box(0, 0, 0, 2, 16, 16);
		SHAPES[5] = Block.box(14, 0, 0, 16, 16, 16);
	}

	@Override
	public double getInertia(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		return 0;
	}

	@Override
	public boolean hasCap(BlockCapability<?, ?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		//Delegate to the axle, if there is one
		return side != null && side == capSide && cap == CRCapabilities.AXLE_CAPABILITY;
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//Delegate to the axle, if there is one

		if(side == null){
			return;
		}

		if(lastRadius != 0){
			return;//Only axial connections should ever occur for this mechanism
		}

		//If true, this has already been checked.
		if(key == handler.getUpdateKey()){
//			//If true, there is rotation conflict.
//			if(handler.rotRatio != rotRatioIn){
//				masterIn.lock();
//			}
			return;
		}

//		if(masterIn.addToList(handler)){
//			return;
//		}

		handler.setRotRatio(rotRatioIn);
		handler.setUpdateKey(key);

		//Connected block
		RotaryUtil.propagateAxially(te.getLevel(), te.getBlockPos().relative(side), side.getOpposite(), handler, masterIn, key, handler.renderOffset());

		//Axle slot
		if(te.getAxleAxis() == side.getAxis() && te.members[6] != null && te.members[6].hasCap(CRCapabilities.AXLE_CAPABILITY, side, te.mats[6], null, te.getAxleAxis(), te)){
			te.axleHandlers[6].propagate(masterIn, key, rotRatioIn, 0, handler.renderOffset());
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat){
		if(mat instanceof GearFacade.FacadeBlock){
			return new ItemStack(CRItems.gearFacade);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side == null ? SHAPES[0] : SHAPES[side.get3DDataValue()];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void doRender(MechanismTileEntity te, PoseStack matrix, MultiBufferSource buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(side == null){
			return;
		}

		TextureAtlasSprite sprite;
		ModelData modelData = te.getLevel().getModelDataManager().getAt(te.getBlockPos());
		if(modelData == null){
			modelData = ModelData.EMPTY;
		}
		if(mat instanceof GearFacade.FacadeBlock facade){
			sprite = Minecraft.getInstance().getBlockRenderer().getBlockModel(facade.getBlockState()).getParticleIcon(modelData);
		}else{
			sprite = Minecraft.getInstance().getBlockRenderer().getBlockModel(Blocks.STONE_BRICKS.defaultBlockState()).getParticleIcon(modelData);
		}
//		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(mat instanceof GearFacade.FacadeBlock ? ((GearFacade.FacadeBlock) mat).getTexture() : GearFacade.FacadeBlock.STONE_BRICK.getTexture());

		matrix.mulPose(side.getRotation());
		matrix.translate(0, 7F / 16F, 0);

		//Render along the top
		VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
		float antiZFightScale = 0.0001F * (1 + side.get3DDataValue());
		CRModels.drawBox(matrix, builder, combinedLight, new int[] {255, 255, 255, 255}, 0.5F - antiZFightScale, 1F / 16F, 0.5F - antiZFightScale, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), sprite.getU0(), sprite.getV0(), sprite.getU1(), CRRenderUtil.getScaledV(sprite, 2), sprite.getU0(), sprite.getV0(), sprite.getU1(), CRRenderUtil.getScaledV(sprite, 2));
	}

	@Override
	public GearFacade.FacadeBlock readProperty(CompoundTag nbt){
		return GearFacade.FacadeBlock.read(nbt);
	}

	@Override
	public boolean requiresSupport(){
		return false;
	}
}
