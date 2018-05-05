package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBiomeUpdateToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactiveSpotTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class StasisolEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, double amount,double heat, EnumMatterPhase phase){
		Chunk c = world.getChunkFromBlockCoords(pos);
		if(world.getBiome(pos) != Biomes.ICE_PLAINS){
			c.getBiomeArray()[(pos.getZ() & 15) << 4 | (pos.getX() & 15)] = (byte) Biome.getIdForBiome(Biomes.ICE_PLAINS);
			ModPackets.network.sendToDimension(new SendBiomeUpdateToClient(pos, (byte) Biome.getIdForBiome(Biomes.ICE_PLAINS)), world.provider.getDimension());
		}

		IBlockState oldState = world.getBlockState(pos);

		if(oldState.getBlock().isAir(oldState, world, pos) || oldState.getBlockHardness(world, pos) < 0){
			return;
		}

		for(Predicate<IBlockState> pred : AetherEffect.CRYS_GROUP){
			if(pred.test(oldState)){
				if(oldState != ModBlocks.blockPureQuartz.getDefaultState()){
					world.setBlockState(pos, ModBlocks.blockPureQuartz.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.FLUD_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.ICE.getDefaultState() && oldState.getBlock() != ModBlocks.reactiveSpot){
					world.setBlockState(pos, ModBlocks.reactiveSpot.getDefaultState());
					TileEntity te = world.getTileEntity(pos);
					if(te instanceof ReactiveSpotTileEntity){
						((ReactiveSpotTileEntity) te).setTarget(Blocks.ICE.getDefaultState());
					}
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.ROCK_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.PACKED_ICE.getDefaultState()){
					world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.SOIL_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.SNOW.getDefaultState()){
					world.setBlockState(pos, Blocks.SNOW.getDefaultState());
				}
				return;
			}
		}
	}
	
	@Override
	public void doEffectAdv(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase, @Nullable ReagentStack[] contents){
		IBlockState oldState = world.getBlockState(pos);
		if(contents != null && contents[13] != null && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState());
			return;
		}
		doEffect(world, pos, amount, temp, phase);
	}
}
