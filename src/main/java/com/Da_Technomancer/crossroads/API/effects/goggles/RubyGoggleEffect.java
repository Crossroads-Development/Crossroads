package com.Da_Technomancer.crossroads.API.effects.goggles;

import com.Da_Technomancer.crossroads.API.beams.BeamUtil;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RubyGoggleEffect implements IGoggleEffect{

	private static final int RANGE = 32;

	@Override
	public void armorTick(Level world, Player player, ArrayList<Component> chat, BlockHitResult ray){
		if(world.getGameTime() % 5 == 0){
			Entity entHit = null;
			Vec3 start = new Vec3(player.getX() - Math.cos(Math.toRadians(player.getYHeadRot())) * 0.18D, player.getY() + player.getEyeHeight() + 0.03D, player.getZ() - Math.sin(Math.toRadians(player.getYHeadRot())) * 0.18D);
			Vec3 end = start;
			Vec3 look = player.getLookAngle();
			Direction collisionDir = Direction.getNearest(look.x, look.y, look.z);
			for(double d = 0; d < RANGE; d += 0.2D){
				Vec3 tar = player.getEyePosition(0).add(0, 0.2D, 0).add(look.scale(d));
				List<Entity> ents = world.getEntities(player, new AABB(tar.x - 0.1D, tar.y - 0.1D, tar.z - 0.1D, tar.x + 0.1D, tar.y + 0.1D, tar.z + 0.1D), EntitySelector.ENTITY_STILL_ALIVE);
				if(!ents.isEmpty()){
					entHit = ents.get((int) (Math.random() * ents.size()));
					end = tar;
					break;
				}
				BlockPos tarPos = new BlockPos(tar);
				BlockState state = world.getBlockState(tarPos);
				if(BeamUtil.solidToBeams(state, world, tarPos, collisionDir, 1)){
					break;
				}
				end = tar;
			}

			BlockPos endPos = new BlockPos(end);
			if(entHit != null){
				entHit.setSecondsOnFire(3);
			}else if(world.getBlockState(endPos).isAir()){
				world.setBlockAndUpdate(endPos, Blocks.FIRE.defaultBlockState());
			}

			CRRenderUtil.addBeam(world, start.x, start.y, start.z, (int) Math.sqrt(end.distanceToSqr(start)), player.getXRot(), player.yHeadRot, (byte) 1, Color.RED.getRGB());
		}
	}
}