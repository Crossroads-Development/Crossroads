package com.Da_Technomancer.crossroads.blocks.rotary.mechanisms;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.rotary.*;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

import static net.minecraft.world.level.block.Block.box;

/**
 * Large gears are represented by two mechanism types for the 3x3 gear:
 * - MechanismLargeGearCore, the middle block that handles most gear physics
 * - 8x MechanismLargeGearEdge, the blocks surrounding the core and which defer most logic back to the core
 */
public class MechanismLargeGearEdge implements IMechanism<MechanismLargeGearEdge.CorePosOffset>{

	public static final MechanismLargeGearEdge INSTANCE = new MechanismLargeGearEdge();

	private static final VoxelShape[] COL_SHAPES = new VoxelShape[6];

	static{
		//Create a collision shape for each facing. A small strip is missing to prevent it being considered "solid" for things like torches, while still blocking basically all entity movement
		//Note: could be changed to have one pixel missing instead of a strip if needed
		COL_SHAPES[0] = box(0, 0, 0, 15.9D, 2, 16);
		COL_SHAPES[1] = box(0, 14, 0, 15.9D, 16, 16);
		COL_SHAPES[2] = box(0, 0, 0, 15.9D, 16, 2);
		COL_SHAPES[3] = box(0, 0, 14, 15.9D, 16, 16);
		COL_SHAPES[4] = box(0, 0, 0, 2, 15.9D, 16);
		COL_SHAPES[5] = box(14, 0, 0, 15.9D, 16, 16);
	}

	@Override
	public double getInertia(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		return 0;
	}

	@Override
	public boolean hasCap(BlockCapability<?, ?> cap, Direction capSide, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		return cap == CRCapabilities.COG_CAPABILITY && capSide == side && (mat instanceof CorePosOffset offsetPos && offsetPos.isEdge());
	}

	@Override
	public void connect(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, @Nonnull IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, Direction cogOrient, boolean renderOffset){
		if(mat instanceof CorePosOffset coreOffset){
			BlockPos coreOffsetPos = coreOffset.offsetPos;
			if(cogOrient == Direction.getNearest(-coreOffsetPos.getX(), -coreOffsetPos.getY(), -coreOffsetPos.getZ())){
				IAxleHandler axle = getCoreHandler(mat, side, axis, te);
				if(axle != null){
					axle.propagate(masterIn, key, rotationRatioIn, lastRadius, !renderOffset);
				}
			}
		}
	}

	@Override
	public void propagate(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler, IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
		//No-op
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te, IMechanismAxleHandler handler){
		IAxleHandler coreAxleHandler = getCoreHandler(mat, side, axis, te);
		if(coreAxleHandler != null){
			RotaryUtil.addRotaryInfo(chat, coreAxleHandler, false, player);
		}
	}

	private IAxleHandler getCoreHandler(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		if(side != null && mat instanceof CorePosOffset coreOffset){
			BlockEntity coreTE = te.getLevel().getBlockEntity(coreOffset.corePos(te.getBlockPos()));
			if(coreTE instanceof MechanismTileEntity coreMechTE && coreMechTE.members[side.get3DDataValue()] instanceof MechanismLargeGearCore){
				return coreMechTE.axleHandlers[side.get3DDataValue()];
			}
		}
		return null;
	}

	@Nonnull
	@Override
	public ItemStack getDrop(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, @Nullable MechanismTileEntity te){
		if(mat instanceof CorePosOffset corePosOffset && side != null && te != null){
			Level level = te.getLevel();
			BlockPos corePos = corePosOffset.corePos(te.getBlockPos());
			if(level.getBlockEntity(corePos) instanceof MechanismTileEntity coreTE && coreTE.members[side.get3DDataValue()] instanceof MechanismLargeGearCore coreMechanism){
				//Drops the same item as the core
				return coreMechanism.getDrop(coreTE.mats[side.get3DDataValue()], side, te.getAxleAxis(), te);
			}else if(corePos.equals(MechanismLargeGearCore.dirtyItemDropHackPos)){
				//Fallback to large gear of material saved by last destroyed core at what we expect our core position to be
				return MechanismLargeGearCore.INSTANCE.getDrop(MechanismLargeGearCore.dirtyItemDropHackMaterial, side, null, null);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void onRemoved(IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis, MechanismTileEntity te){
		if(mat instanceof CorePosOffset corePosOffset && side != null && te != null){
			BlockPos edgePos = te.getBlockPos();
			BlockPos corePos = corePosOffset.corePos(edgePos);
			Level world = te.getLevel();
			if(MechanismLargeGearCore.dirtyBreakingHackPos == null){
				MechanismLargeGearCore.dirtyBreakingHackPos = edgePos;
			}
			if(!corePos.equals(MechanismLargeGearCore.dirtyBreakingHackPos) && world.getBlockEntity(corePos) instanceof MechanismTileEntity coreTE && coreTE.members[side.get3DDataValue()] instanceof MechanismLargeGearCore){
				//Destroy the core - core will handle removing the other edges
				coreTE.setMechanism(side.get3DDataValue(), null, null, null, false);
				CRBlocks.mechanism.neighborChanged(world.getBlockState(corePos), world, corePos, CRBlocks.mechanism, te.getBlockPos(), false);
			}
			if(edgePos.equals(MechanismLargeGearCore.dirtyBreakingHackPos)){
				MechanismLargeGearCore.dirtyBreakingHackPos = null;
			}
		}
	}

	@Override
	public VoxelShape getBoundingBox(@Nullable Direction side, @Nullable Direction.Axis axis){
		return side == null ? Shapes.empty() : COL_SHAPES[side.get3DDataValue()];
	}

	@Override
	public void doRender(MechanismTileEntity te, PoseStack matrix, MultiBufferSource buffer, int combinedLight, float partialTicks, IMechanismProperty mat, @Nullable Direction side, @Nullable Direction.Axis axis){
		//No-op - rendering is done by the core
	}

	@Override
	public CorePosOffset readProperty(CompoundTag nbt){
		return CorePosOffset.read(nbt);
	}

	@Override
	public boolean requiresSupport(){
		return false;
	}

	/**
	 * The IMechanismProperty for large gear edges
	 * Represents the location of the associated large gear core relative to the edge
	 * @param offsetPos Vector offset of the core position relative to the edge position
	 */
	public record CorePosOffset(@Nonnull BlockPos offsetPos) implements IMechanismProperty{

		public CorePosOffset(@Nonnull BlockPos offsetPos){
			this.offsetPos = offsetPos.immutable();
		}

		public boolean isEdge(){
			return offsetPos.distManhattan(BlockPos.ZERO) == 1;
		}

		public BlockPos corePos(BlockPos edgePos){
			return edgePos.offset(offsetPos);
		}

		public BlockPos edgePos(BlockPos corePos){
			return corePos.subtract(offsetPos);
		}

		public static CorePosOffset read(CompoundTag nbt){
			return new CorePosOffset(nbt.contains("offset_pos") ? BlockPos.of(nbt.getLong("offset_pos")) : BlockPos.ZERO);
		}

		@Override
		public void write(CompoundTag nbt){
			nbt.putLong("offset_pos", offsetPos.asLong());
		}
	}
}
