package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.beams.*;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class BeamReflectorSensitiveTileEntity extends BeamReflectorTileEntity{

	public static final BlockEntityType<BeamReflectorSensitiveTileEntity> TYPE = CRTileEntity.createType(BeamReflectorSensitiveTileEntity::new, CRBlocks.beamReflectorSensitive);

	public BeamReflectorSensitiveTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	protected BeamHelper createBeamManager(Direction dir){
		return new SensitiveBeamHelper(dir, worldPosition);
	}

	/**
	 * A variant of BeamHelper where the beams can collide with entities
	 * Raytracing for entities is cpu intensive compared to normal BeamHelper
	 */
	private static class SensitiveBeamHelper extends BeamHelper{

		private final Vec3 startVec;
		private final Vec3 rayVec;

		public SensitiveBeamHelper(@Nonnull Direction dir, @Nonnull BlockPos pos){
			super(dir, pos);
			startVec = Vec3.atCenterOf(pos);
			rayVec = Vec3.atLowerCornerOf(dir.getNormal());
		}

		@Override
		public boolean emit(@Nonnull BeamUnit mag, Level world){
			BeamHit beamHit;
			if(mag.isEmpty()){
				//Optimization: skip full raytracing for empty beams, and fallback to normal block-collision only
				beamHit = BeamUtil.rayTraceBeamSimple(mag, world, pos, dir, BeamUtil.MAX_DISTANCE, true);
			}else{
				beamHit = BeamUtil.rayTraceBeams(mag, world, startVec, startVec, rayVec, null, pos, BeamUtil.MAX_DISTANCE, true);
			}


			//Handle receiving the beam or beam effect
			Direction effectDir = beamHit.getDirection();
			BlockEntity te = world.getBlockEntity(beamHit.getPos());
			LazyOptional<IBeamHandler> opt;
			if(te != null && (opt = te.getCapability(Capabilities.BEAM_CAPABILITY, effectDir)).isPresent()){
				opt.orElseThrow(NullPointerException::new).setBeam(mag);
			}else{
				EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
				if(!world.isOutsideBuildHeight(beamHit.getPos())){
					align.getEffect().doBeamEffect(align, mag.getVoid() != 0, Math.min(BeamUtil.MAX_EFFECT_POWER, mag.getPower()), beamHit);
				}
			}

			//Update the values used for rendering
			return updateBeamRender(mag, (int) Math.round(beamHit.getHitPos().subtract(startVec).length()));
		}
	}
}
