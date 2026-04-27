package com.Da_Technomancer.crossroads.effects.goggles_effects;

import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.BeamUtil;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.api.technomancy.IGoggleEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RubyGoggleEffect implements IGoggleEffect{

	private static final int RANGE = 32;
	public static final String RUBY_GOGGLE_SOURCE_TYPE = "ruby_goggle";
	private static final BeamUnit NOMINAL_BEAM_UNIT = new BeamUnit(1, 0, 0, 0);

	@Override
	public void armorTick(Level world, Player player){
		if(world.getGameTime() % 2 == 0){
			//Goggles use a 'fake' energy beam - we use one for rendering and finding collision, but do a more limited set of effects and can't be captured by reflectors

//			Entity entHit = null;
			Vec3 start = new Vec3(player.getX() - Math.cos(Math.toRadians(player.getYHeadRot())) * 0.18D, player.getY() + player.getEyeHeight() + 0.03D, player.getZ() - Math.sin(Math.toRadians(player.getYHeadRot())) * 0.18D);
//			Vec3 end = start;
			BeamHit beamHit = BeamUtil.rayTraceBeams(NOMINAL_BEAM_UNIT, world, start, player.getEyePosition().add(0, 0.2D, 0), player.getLookAngle(), player, null, RANGE, new BeamHit.BeamSource(RUBY_GOGGLE_SOURCE_TYPE, true, false, world, null, player));
			List<LivingEntity> hitEntities = beamHit.getNearbyEntities(LivingEntity.class, BeamHit.WITHIN_BLOCK_RANGE, EntitySelector.NO_SPECTATORS);
			if(!hitEntities.isEmpty()){
				for(LivingEntity entHit : hitEntities){
					entHit.igniteForSeconds(3);
				}
			}else if(beamHit.getEndState().isAir()){
				world.setBlockAndUpdate(beamHit.getPos(), Blocks.FIRE.defaultBlockState());
			}else{
				//Set a fire w/ offset
				BlockPos offsetPos = beamHit.getPos().relative(beamHit.getDirection());
				BlockState state = world.getBlockState(offsetPos);
				if(state.isAir()){
					beamHit.getWorld().setBlockAndUpdate(offsetPos, Blocks.FIRE.defaultBlockState());
				}
			}

//			Vec3 look = player.getLookAngle();
//			Direction collisionDir = Direction.getNearest(look.x, look.y, look.z);
//			for(double d = 0; d < RANGE; d += 0.2D){
//				Vec3 tar = player.getEyePosition(0).add(0, 0.2D, 0).add(look.scale(d));
//				List<Entity> ents = world.getEntities(player, new AABB(tar.x - 0.1D, tar.y - 0.1D, tar.z - 0.1D, tar.x + 0.1D, tar.y + 0.1D, tar.z + 0.1D), EntitySelector.ENTITY_STILL_ALIVE);
//				if(!ents.isEmpty()){
//					entHit = ents.get((int) (Math.random() * ents.size()));
//					end = tar;
//					break;
//				}
//				BlockPos tarPos = MiscUtil.blockPos(tar);
//				BlockState state = world.getBlockState(tarPos);
//				if(BeamUtil.solidToBeams(state, world, tarPos, collisionDir, 1)){
//					break;
//				}
//				end = tar;
//			}
//
//			BlockPos endPos = MiscUtil.blockPos(end);
//			if(entHit != null){
//				entHit.igniteForSeconds(3);
//			}else if(world.getBlockState(endPos).isAir()){
//				world.setBlockAndUpdate(endPos, Blocks.FIRE.defaultBlockState());
//			}

			CRRenderUtil.addBeam(world, start.x, start.y, start.z, (int) Math.sqrt(beamHit.getHitPos().distanceToSqr(start)), player.getXRot(), player.yHeadRot, (byte) 1, NOMINAL_BEAM_UNIT.getRGB().getRGB(), (byte) 3);
		}
	}
}