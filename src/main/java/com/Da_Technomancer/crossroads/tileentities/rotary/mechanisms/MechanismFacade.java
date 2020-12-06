package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.*;
import com.Da_Technomancer.crossroads.items.itemSets.GearFacade;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.render.TESR.CRModels;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MechanismFacade implements IMechanism<GearFacade.FacadeBlock>{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		SHAPES[0] = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);
		SHAPES[1] = Block.makeCuboidShape(0, 14, 0, 16, 16, 16);
		SHAPES[2] = Block.makeCuboidShape(0, 0, 0, 16, 16, 2);
		SHAPES[3] = Block.makeCuboidShape(0, 0, 14, 16, 16, 16);
		SHAPES[4] = Block.makeCuboidShape(0, 0, 0, 2, 16, 16);
		SHAPES[5] = Block.makeCuboidShape(14, 0, 0, 16, 16, 16);
	}

	@Override
	public double getInertia(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		return 0;
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		//Delegate to the axle, if there is one
		return side != null && side == capSide && cap == Capabilities.AXLE_CAPABILITY;
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, MechanismTileEntity.SidedAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//Delegate to the axle, if there is one

		if(side == null){
			return;
		}

		if(lastRadius != 0){
			return;//Only axial connections should ever occur for this mechanism
		}

		//If true, this has already been checked.
		if(key == handler.updateKey){
//			//If true, there is rotation conflict.
//			if(handler.rotRatio != rotRatioIn){
//				masterIn.lock();
//			}
			return;
		}

//		if(masterIn.addToList(handler)){
//			return;
//		}

//		handler.rotRatio = rotRatioIn;
		handler.updateKey = key;

		TileEntity sideTE = te.getWorld().getTileEntity(te.getPos().offset(side));

		//Connected block
		if(sideTE != null){
			LazyOptional<IAxisHandler> axisOpt = sideTE.getCapability(Capabilities.AXIS_CAPABILITY, side.getOpposite());
			if(axisOpt.isPresent()){
				axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
			}
			LazyOptional<IAxleHandler> axleOpt = sideTE.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite());
			if(axleOpt.isPresent()){
				axleOpt.orElseThrow(NullPointerException::new).propagate(masterIn, key, rotRatioIn, 0, handler.renderOffset);
			}
		}

		//Axle slot
		if(te.getAxleAxis() == side.getAxis() && te.members[6] != null && te.members[6].hasCap(Capabilities.AXLE_CAPABILITY, side, te.mats[6], null, te.getAxleAxis(), te)){
			te.axleHandlers[6].propagate(masterIn, key, rotRatioIn, 0, handler.renderOffset);
		}
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat){
		if(mat instanceof GearFacade.FacadeBlock){
			return GearFacade.withMaterial((GearFacade.FacadeBlock) mat, 1);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side == null ? SHAPES[0] : SHAPES[side.getIndex()];
	}

	@Override
	public void doRender(MechanismTileEntity te, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(side == null){
			return;
		}

		TextureAtlasSprite sprite = CRRenderUtil.getTextureSprite(mat instanceof GearFacade.FacadeBlock ? ((GearFacade.FacadeBlock) mat).getTexture() : GearFacade.FacadeBlock.STONE_BRICK.getTexture());

		matrix.rotate(side.getRotation());
		matrix.translate(0, 7F / 16F, 0);

		//Render along the top
		IVertexBuilder builder = buffer.getBuffer(RenderType.getCutoutMipped());
		float antiZFightScale = 0.0001F * side.getIndex();
		CRModels.drawBox(matrix, builder, combinedLight, new int[] {255, 255, 255, 255}, 0.5F - antiZFightScale, 1F / 16F, 0.5F - antiZFightScale, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getInterpolatedV(2), sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getInterpolatedV(2));
	}

	@Override
	public GearFacade.FacadeBlock deserializeProperty(int serial){
		return GearFacade.FacadeBlock.deserialize(serial);
	}

	@Override
	public GearFacade.FacadeBlock loadProperty(String name){
		return GearFacade.FacadeBlock.loadProperty(name);
	}

	@Override
	public boolean requiresSupport(){
		return false;
	}
}
