package com.Da_Technomancer.crossroads.blocks.rotary.mechanisms;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.rotary.*;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.item_sets.LargeGear;
import com.Da_Technomancer.crossroads.render.tesr.CRModels;
import com.Da_Technomancer.essentials.api.MathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

import static net.minecraft.world.level.block.Block.box;

/**
 * Large gears are represented by two mechanism types for the 3x3 gear:
 * - MechanismLargeGearCore, the middle block that handles most gear physics
 * - 8x MechanismLargeGearEdge, the blocks surrounding the core and which defer most logic back to the core
 */
public class MechanismLargeGearCore implements IMechanism<CRMaterialLibrary.GearMaterial>{

	public static final MechanismLargeGearCore INSTANCE = new MechanismLargeGearCore();

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		SHAPES[0] = box(0, 0, 0, 16, 2, 16);
		SHAPES[1] = box(0, 14, 0, 16, 16, 16);
		SHAPES[2] = box(0, 0, 0, 16, 16, 2);
		SHAPES[3] = box(0, 0, 14, 16, 16, 16);
		SHAPES[4] = box(0, 0, 0, 2, 16, 16);
		SHAPES[5] = box(14, 0, 0, 16, 16, 16);
	}

	@Override
	public double getInertia(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		// assume each gear is 1/8 of a cubic meter and has a radius of 1.5 meters.
		// mass is rounded to make things nicer for everyone
		if(mat instanceof CRMaterialLibrary.GearMaterial material){
			return MathUtil.preciseRound(9D * 1.125D * material.getDensity() / 8, 3);// 1.125 because r*r/2 so 1.5*1.5/2
		}else{
			return 0;
		}
	}

	@Override
	public boolean hasCap(BlockCapability<?, ?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return cap == CRCapabilities.AXLE_CAPABILITY && side == capSide;
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		if(mat == null || side == null){
			return;
		}

		if(lastRadius != 0){
			rotRatioIn *= lastRadius / 1.5D;
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

//		axis = masterIn;

		handler.setRotRatio(rotRatioIn);
//		LargeGearMasterTileEntity.this.renderOffset = renderOffset;
		handler.setUpdateKey(key);

//		Direction side = getFacing();
		final Level level = te.getLevel();
		final BlockPos worldPosition = te.getBlockPos();
		final BlockPos sidePosition = worldPosition.relative(side);//BlockPos the large gear is placed against
		final Direction oppositeSide = side.getOpposite();

		for(int i = 0; i < 6; i++){
			if(i != side.get3DDataValue() && i != oppositeSide.get3DDataValue()){
				final Direction facing = Direction.from3DDataValue(i);
				final Direction oppositeFacing = facing.getOpposite();
				// Adjacent gears
				BlockPos adjPos = worldPosition.relative(facing, 2);
				ICogHandler cogHandler;
				if((cogHandler = level.getCapability(CRCapabilities.COG_CAPABILITY, adjPos, side)) != null){
					cogHandler.connect(masterIn, key, -rotRatioIn, 1.5D, oppositeFacing, handler.renderOffset());
				}/*else if((cogHandler = level.getCapability(CRCapabilities.COG_CAPABILITY, adjPos, oppositeSide)) != null){
					//Check for large gears
					cogHandler.connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * rotRatioIn, 1.5D, side, handler.renderOffset());
				}*/

				// Diagonal gears
				BlockPos diagPos = sidePosition.relative(facing, 2);
				if((cogHandler = level.getCapability(CRCapabilities.COG_CAPABILITY, diagPos, oppositeFacing)) != null && RotaryUtil.canConnectThrough(level, worldPosition.relative(facing, 2), facing.getOpposite(), side)){
					cogHandler.connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, 1.5D, oppositeSide, handler.renderOffset());
				}

				//Underside gears
				BlockPos undersidePos = sidePosition.relative(facing, 1);
				if((cogHandler = level.getCapability(CRCapabilities.COG_CAPABILITY, undersidePos, facing)) != null){
					cogHandler.connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, 1.5D, oppositeSide, handler.renderOffset());
				}

				//Gears in the same blockspace as an edge
				BlockPos edgePos = worldPosition.relative(facing, 1);
				if((cogHandler = level.getCapability(CRCapabilities.COG_CAPABILITY, edgePos, facing)) != null){
					cogHandler.connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * rotRatioIn, 1.5D, side, handler.renderOffset());
				}
			}
		}
		//Connected block
		RotaryUtil.propagateAxially(level, sidePosition, oppositeSide, handler, masterIn, key, handler.renderOffset());

		//Axle slot
		if(te.getAxleAxis() == side.getAxis() && te.members[6] != null && te.members[6].hasCap(CRCapabilities.AXLE_CAPABILITY, side, te.mats[6], null, te.getAxleAxis(), te)){
			te.axleHandlers[6].propagate(masterIn, key, handler.getRotationRatio(), 0, handler.renderOffset());
		}
//		for(Direction.AxisDirection dir : Direction.AxisDirection.values()){
//			Direction axleDir = dir == Direction.AxisDirection.POSITIVE ? side : side.getOpposite();
//			BlockPos connectPos = worldPosition.relative(axleDir);
//
//			IAxisHandler axisHandler;
//			if((axisHandler = level.getCapability(CRCapabilities.AXIS_CAPABILITY, connectPos, axleDir.getOpposite())) != null){
//				axisHandler.trigger(masterIn, key);
//			}
//			IAxleHandler axleHandler;
//			if((axleHandler = level.getCapability(CRCapabilities.AXLE_CAPABILITY, connectPos, axleDir.getOpposite())) != null){
//				axleHandler.propagate(masterIn, key, rotRatioIn, 0, handler.renderOffset());
//			}
//		}
	}

	@Override
	public void connect(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, @Nonnull IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, Direction cogOrient, boolean renderOffset){
		//No-op
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, @Nullable MechanismTileEntity te){
		if(mat instanceof CRMaterialLibrary.GearMaterial oreProf){
			return CRItems.largeGear.withMaterial(oreProf, 1);
		}else{
			return ItemStack.EMPTY;
		}
	}

	/**
	 * Process for breaking a large gear is:
	 * - Player breaks an edge (or the core)
	 * - Edge destroys the core (skip this step if player broke core)
	 * - Core destroys all edge pieces
	 * But this breaks item dropping logic if the block the player broke removes itself during the breaking process, which this not-exactly-recursive process sometimes does
	 * So this field prevents the initiating block from being removed early
	 */
	protected static BlockPos dirtyBreakingHackPos = null;
	protected static BlockPos dirtyItemDropHackPos = null;
	protected static IMechanismProperty dirtyItemDropHackMaterial = null;

	@Override
	public void onRemoved(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		if(side != null && te != null){
			//Destroy all surrounding edge pieces
			Level world = te.getLevel();
			int sideIndex = side.get3DDataValue();
			BlockPos corePos = te.getBlockPos();
			if(dirtyBreakingHackPos == null){
				dirtyBreakingHackPos = corePos;
			}
			dirtyItemDropHackPos = corePos;
			dirtyItemDropHackMaterial = mat;
			for(MechanismLargeGearEdge.CorePosOffset offset : LargeGear.EDGE_OFFSET_POSITIONS[side.getAxis().ordinal()]){
				BlockPos edgePos = offset.edgePos(corePos);
				if(!edgePos.equals(dirtyBreakingHackPos) && world.getBlockEntity(edgePos) instanceof MechanismTileEntity edgeTE && edgeTE.members[sideIndex] == MechanismLargeGearEdge.INSTANCE){
					edgeTE.setMechanism(sideIndex, null, null, null, false);
					CRBlocks.mechanism.neighborChanged(world.getBlockState(edgePos), world, edgePos, CRBlocks.mechanism, corePos, false);
				}
			}
			if(corePos.equals(dirtyBreakingHackPos)){
				dirtyBreakingHackPos = null;
			}
		}
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side == null ? Shapes.empty() : SHAPES[side.get3DDataValue()];
	}

	@Override
	public void doRender(MechanismTileEntity te, PoseStack matrix, MultiBufferSource buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		if(side == null){
			return;
		}
//		matrix.translate(0.5D, 0.5D, 0.5D);
		IAxleHandler handler = te.axleHandlers[side.get3DDataValue()];
		matrix.mulPose(side.getOpposite().getRotation());
		final float angle = handler.getAngle(partialTicks);
		matrix.mulPose(Axis.YP.rotationDegrees(-(float) RotaryUtil.getCCWSign(side) * angle));
		matrix.translate(0, -0.4375D, 0);
		CRModels.draw24GearMilled(matrix, buffer, combinedLight, mat instanceof CRMaterialLibrary.GearMaterial material ? material.getColor() : Color.WHITE, CRModels.generateZFightFactor(te.getBlockPos(), side.ordinal()));
	}

	@Override
	public CRMaterialLibrary.GearMaterial readProperty(CompoundTag nbt){
		return CRMaterialLibrary.GearMaterial.read(nbt);
	}
}
