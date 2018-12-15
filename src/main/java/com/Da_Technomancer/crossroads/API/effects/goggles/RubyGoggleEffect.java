package com.Da_Technomancer.crossroads.API.effects.goggles;

import com.Da_Technomancer.crossroads.render.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RubyGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat, RayTraceResult ray){
		if(world.getTotalWorldTime() % 5 == 0){
			Entity entHit = null;
			Vec3d start = new Vec3d(player.posX - Math.cos(Math.toRadians(player.getRotationYawHead())) * 0.18D, player.posY + player.getEyeHeight() + 0.03D, player.posZ - Math.sin(Math.toRadians(player.getRotationYawHead())) * 0.18D);
			Vec3d end = start;

			for(double d = 0; d < 16; d += 0.2D){
				Vec3d tar = player.getPositionEyes(0).add(0, 0.2D, 0).add(player.getLookVec().scale(d));
				List<Entity> ents = world.getEntitiesInAABBexcluding(player, new AxisAlignedBB(tar.x - 0.1D, tar.y - 0.1D, tar.z - 0.1D, tar.x + 0.1D, tar.y + 0.1D, tar.z + 0.1D), EntitySelectors.IS_ALIVE);
				if(!ents.isEmpty()){
					entHit = ents.get((int) (Math.random() * ents.size()));
					end = tar;
					break;
				}
				IBlockState state = world.getBlockState(new BlockPos(tar));
				if(!state.getBlock().isAir(state, world, new BlockPos(tar))){
					break;
				}
				end = tar;
			}

			if(entHit != null){
				entHit.setFire(3);
			}else{
				world.setBlockState(new BlockPos(end), Blocks.FIRE.getDefaultState());
			}

			RenderUtil.addBeam(world.provider.getDimension(), start.x, start.y, start.z, (int) Math.sqrt(end.squareDistanceTo(start)), player.rotationPitch, player.rotationYawHead, (byte) 1, Color.RED.getRGB());
		}
	}
}