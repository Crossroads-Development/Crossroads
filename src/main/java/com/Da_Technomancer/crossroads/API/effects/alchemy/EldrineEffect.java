package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
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

import java.util.function.Predicate;

public class EldrineEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		IBlockState oldState = world.getBlockState(pos);

		//quicksilver makes it create a block instead of transmuting blocks
		if(reags != null && reags.getQty(EnumReagents.QUICKSILVER.id()) != 0 && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState());
			return;
		}


		Chunk c = world.getChunk(pos);
		if(world.getBiome(pos) != Biomes.HELL){
			c.getBiomeArray()[(pos.getZ() & 15) << 4 | (pos.getX() & 15)] = (byte) Biome.getIdForBiome(Biomes.HELL);
			ModPackets.network.sendToDimension(new SendBiomeUpdateToClient(pos, (byte) Biome.getIdForBiome(Biomes.HELL)), world.provider.getDimension());
		}

		if(oldState.getBlock().isAir(oldState, world, pos) || oldState.getBlockHardness(world, pos) < 0){
			return;
		}

		for(Predicate<IBlockState> pred : AetherEffect.CRYS_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.GLOWSTONE.getDefaultState()){
					world.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.FLUD_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.LAVA.getDefaultState() && oldState.getBlock() != ModBlocks.reactiveSpot){
					world.setBlockState(pos, ModBlocks.reactiveSpot.getDefaultState());
					TileEntity te = world.getTileEntity(pos);
					if(te instanceof ReactiveSpotTileEntity){
						((ReactiveSpotTileEntity) te).setTarget(Blocks.LAVA.getDefaultState());
					}
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.ROCK_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.NETHERRACK.getDefaultState()){
					world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.SOIL_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.SOUL_SAND.getDefaultState()){
					world.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState());
				}
				return;
			}
		}
	}
}
