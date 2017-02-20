package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLightningToClient;

import net.minecraft.block.material.Material;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ChargeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		worldIn.spawnEntity(new EntityLightningBolt(worldIn, pos.getX(), pos.getY(), pos.getZ(), false));
		ModPackets.network.sendToAllAround(new SendLightningToClient(pos), new TargetPoint(worldIn.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		
		if(worldIn.getBlockState(pos).getMaterial() == Material.ROCK){
			worldIn.setBlockState(pos, Blocks.REDSTONE_BLOCK.getDefaultState());
		}
	}
	
	public static class VoidChargeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			if(worldIn.getBlockState(pos).getBlock() == Blocks.REDSTONE_BLOCK){
				worldIn.setBlockState(pos, Blocks.STONE.getDefaultState());
			}
		}
	}
}
