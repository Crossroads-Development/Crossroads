package com.Da_Technomancer.crossroads.API.effects.goggles;

import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RubyGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, PlayerEntity player, ArrayList<ITextComponent> chat, BlockRayTraceResult ray){
		if(world.getGameTime() % 5 == 0){
			Entity entHit = null;
			Vec3d start = new Vec3d(player.posX - Math.cos(Math.toRadians(player.getRotationYawHead())) * 0.18D, player.posY + player.getEyeHeight() + 0.03D, player.posZ - Math.sin(Math.toRadians(player.getRotationYawHead())) * 0.18D);
			Vec3d end = start;

			for(double d = 0; d < 16; d += 0.2D){
				Vec3d tar = player.getEyePosition(0).add(0, 0.2D, 0).add(player.getLookVec().scale(d));
				List<Entity> ents = world.getEntitiesInAABBexcluding(player, new AxisAlignedBB(tar.x - 0.1D, tar.y - 0.1D, tar.z - 0.1D, tar.x + 0.1D, tar.y + 0.1D, tar.z + 0.1D), EntityPredicates.IS_ALIVE);
				if(!ents.isEmpty()){
					entHit = ents.get((int) (Math.random() * ents.size()));
					end = tar;
					break;
				}
				BlockState state = world.getBlockState(new BlockPos(tar));
				if(!state.getBlock().isAir(state, world, new BlockPos(tar))){
					break;
				}
				end = tar;
			}

			BlockPos endPos = new BlockPos(end);
			if(entHit != null){
				entHit.setFire(3);
			}else if(world.getBlockState(endPos).isAir(world, endPos)){
				world.setBlockState(endPos, Blocks.FIRE.getDefaultState());
			}

			CRRenderUtil.addBeam(world, start.x, start.y, start.z, (int) Math.sqrt(end.squareDistanceTo(start)), player.rotationPitch, player.rotationYawHead, (byte) 1, Color.RED.getRGB());
		}
	}
}