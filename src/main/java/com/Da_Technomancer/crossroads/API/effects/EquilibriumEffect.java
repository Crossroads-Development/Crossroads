package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.BeamHit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EquilibriumEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		//Effect in crystal master axis.

		if(!performTransmute(align, voi, power, beamHit)){
			Direction dir = beamHit.getDirection();
			Vec3 ray = beamHit.getRay();

			if(voi){
				//Void is effectively the same but opposite direction
				dir = dir.getOpposite();
				ray = ray.scale(-1);
			}

			boolean willPushEntity = !pushBlock(beamHit.getWorld(), beamHit.getPos(), dir.getOpposite(), Math.max(1, power / 4));

			if(willPushEntity){
				List<Entity> entities = beamHit.getNearbyEntities(Entity.class, 0.48D, ent -> !ent.isShiftKeyDown());
				for(Entity ent : entities){
					attractEntity(ent, beamHit.getHitPos(), ray, power / 32F, 0.2F);
				}
			}
		}
	}

	private static boolean pushBlock(Level world, BlockPos pos, Direction pushDir, int maxPush){
		Direction opposing = pushDir.getOpposite();
		BlockPos.MutableBlockPos checkPos = pos.mutable().move(opposing, 1);

		int totalPushed = 0;
		boolean doPush = false;
		BlockState state;
		do{
			checkPos.move(pushDir, 1);
			state = world.getBlockState(checkPos);
			if(state.isAir() || state.getPistonPushReaction() == PushReaction.IGNORE){
				doPush = totalPushed > 0;
				break;
			}
			if(state.getPistonPushReaction() == PushReaction.DESTROY){
				world.destroyBlock(checkPos, true);
				doPush = true;
				break;
			}
		}while(++totalPushed <= maxPush && canPush(world, checkPos, state));

		if(doPush){
			for(int i = 0; i < totalPushed; i++){
				world.setBlock(checkPos, world.getBlockState(checkPos.relative(opposing)), 3);
				checkPos.move(opposing, 1);
			}
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			CRSounds.playSoundServer(world, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 1F, 1F);
		}
		return doPush;
	}

	private static boolean canPush(Level world, BlockPos pos, BlockState state){
		if(state.isAir() || state.getBlock() == Blocks.OBSIDIAN || state.hasBlockEntity() || state.getDestroySpeed(world, pos) < 0){
			return false;
		}
		if(state.getBlock() instanceof PistonBaseBlock && state.hasProperty(PistonBaseBlock.EXTENDED)){
			return !state.getValue(PistonBaseBlock.EXTENDED);//Vanilla pistons report BLOCK even when retracted and movable
		}
		PushReaction reaction = state.getPistonPushReaction();
		return reaction == PushReaction.NORMAL || reaction == PushReaction.PUSH_ONLY;
	}

	private static void attractEntity(Entity entity, Vec3 beamPoint, Vec3 ray, float pushForce, float attractForceScale){
		//Attraction force; pulls entity towards beam
		Vec3 relEntPos = entity.position().subtract(beamPoint);
		Vec3 nearestBeamPoint = ray.scale(relEntPos.dot(ray)).add(beamPoint);
		Vec3 force = nearestBeamPoint.subtract(entity.position());
		force = force.scale(attractForceScale);
		//Push force; moves entity along ray direction
		force = force.add(ray.scale(pushForce));

		entity.setDeltaMovement(force);
		entity.hurtMarked = true;
		entity.fallDistance = 0;
	}
}
