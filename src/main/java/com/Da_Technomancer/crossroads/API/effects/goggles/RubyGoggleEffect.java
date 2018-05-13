package com.Da_Technomancer.crossroads.API.effects.goggles;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLooseBeamToClient;
import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RubyGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat, RayTraceResult ray){
		if(world.getTotalWorldTime() % 5 == 0){
			Entity entHit = null;
			Vec3d end = null;

			for(double d = 0; d < 16; d += 0.2D){
				Vec3d tar = player.getPositionEyes(0).addVector(0, 0.2D, 0).add(player.getLookVec().scale(d));
				List<Entity> ents = world.getEntitiesInAABBexcluding(player, new AxisAlignedBB(tar.subtract(0.1, 0.1, 0.1), tar.addVector(0.1, 0.1, 0.1)), EntitySelectors.IS_ALIVE);
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

			NBTTagCompound beamNBT = new NBTTagCompound();
			new LooseBeamRenderable(player.posX, player.posY + player.getEyeHeight() + 0.2D, player.posZ, (int) Math.sqrt(end.squareDistanceTo(player.getPositionEyes(0))), player.rotationPitch, player.rotationYawHead, (byte) 1, Color.RED.getRGB()).saveToNBT(beamNBT);
			ModPackets.network.sendToAllAround(new SendLooseBeamToClient(beamNBT), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512));
		}
	}
}