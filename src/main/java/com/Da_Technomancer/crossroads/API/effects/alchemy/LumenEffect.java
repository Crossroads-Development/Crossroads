package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBiomeUpdateToClient;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactiveSpotTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biomes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import java.util.function.Predicate;

public class LumenEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		BlockState oldState = world.getBlockState(pos);

		//quicksilver makes it create a block instead of transmuting blocks
		if(reags != null && reags.getQty(EnumReagents.QUICKSILVER.id()) > 0 && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, BlockDistilledWater.getDistilledWater().getBlock().getDefaultState());
			return;
		}

		Chunk c = world.getChunk(pos);
		if(world.getBiome(pos) != Biomes.DEEP_OCEAN){
			c.getBiomeArray()[(pos.getZ() & 15) << 4 | (pos.getX() & 15)] = (byte) Biome.getIdForBiome(Biomes.DEEP_OCEAN);
			CrossroadsPackets.network.sendToDimension(new SendBiomeUpdateToClient(pos, (byte) Biome.getIdForBiome(Biomes.DEEP_OCEAN)), world.provider.getDimension());
		}

		if(oldState.getBlock().isAir(oldState, world, pos) || oldState.getBlockHardness(world, pos) < 0){
			return;
		}

		for(Predicate<BlockState> pred : AetherEffect.CRYS_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.SEA_LANTERN.getDefaultState()){
					world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<BlockState> pred : AetherEffect.FLUD_GROUP){
			if(pred.test(oldState)){
				if(oldState != BlockDistilledWater.getDistilledWater().getBlock().getDefaultState() && oldState.getBlock() != CrossroadsBlocks.reactiveSpot){
					world.setBlockState(pos, CrossroadsBlocks.reactiveSpot.getDefaultState());
					TileEntity te = world.getTileEntity(pos);
					if(te instanceof ReactiveSpotTileEntity){
						((ReactiveSpotTileEntity) te).setTarget(BlockDistilledWater.getDistilledWater().getBlock().getDefaultState());
					}
				}
				return;
			}
		}
		for(Predicate<BlockState> pred : AetherEffect.ROCK_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.PRISMARINE.getDefaultState()){
					world.setBlockState(pos, Blocks.PRISMARINE.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<BlockState> pred : AetherEffect.SOIL_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.CLAY.getDefaultState()){
					world.setBlockState(pos, Blocks.CLAY.getDefaultState());
				}
				return;
			}
		}
	}
}
