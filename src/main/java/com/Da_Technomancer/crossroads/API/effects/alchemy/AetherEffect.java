package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBiomeUpdateToClient;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactiveSpotTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.IChunk;

public class AetherEffect implements IAlchEffect{

	private static final Tag<Block> SOIL_GROUP = new BlockTags.Wrapper(new ResourceLocation(Crossroads.MODID, "alchemy_soil"));
	private static final Tag<Block> ROCK_GROUP = new BlockTags.Wrapper(new ResourceLocation(Crossroads.MODID, "alchemy_rock"));
	private static final Tag<Block> FLUD_GROUP = new BlockTags.Wrapper(new ResourceLocation(Crossroads.MODID, "alchemy_fluid"));//Was going to be named FLUID_GROUP, but the other two fields had the same name lengths and I couldn't resist
	private static final Tag<Block> CRYS_GROUP = new BlockTags.Wrapper(new ResourceLocation(Crossroads.MODID, "alchemy_crystal"));

	protected Block soilBlock(){
		return Blocks.GRASS_BLOCK;
	}

	protected Block rockBlock(){
		return Blocks.STONE;
	}

	protected Block fluidBlock(){
		return Blocks.WATER;
	}

	protected Block crystalBlock(){
		return Blocks.GLASS;
	}

	protected Biome biome(){
		return Biomes.PLAINS;
	}

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap contents){
		BlockState oldState = world.getBlockState(pos);

		//quicksilver makes it create a block instead of transmuting blocks
		if(contents.getQty(EnumReagents.QUICKSILVER.id()) != 0 && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, soilBlock().getDefaultState());
			return;
		}

		IChunk c = world.getChunk(pos);
		if(world.getBiome(pos) != biome()){
			c.getBiomes()[(pos.getZ() & 15) << 4 | (pos.getX() & 15)] = biome();
			CRPackets.sendPacketToDimension(world, new SendBiomeUpdateToClient(pos, biome().getRegistryName()));
		}

		if(oldState.getBlock().isAir(oldState, world, pos) || oldState.getBlockHardness(world, pos) < 0){
			return;
		}

		if(CRYS_GROUP.contains(oldState.getBlock())){
			if(oldState != crystalBlock().getDefaultState()){
				world.setBlockState(pos, crystalBlock().getDefaultState());
			}
		}else if(FLUD_GROUP.contains(oldState.getBlock())){
			if(oldState != fluidBlock().getDefaultState() && oldState.getBlock() != CRBlocks.reactiveSpot){
				world.setBlockState(pos, CRBlocks.reactiveSpot.getDefaultState());
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof ReactiveSpotTileEntity){
					((ReactiveSpotTileEntity) te).setTarget(fluidBlock().getDefaultState());
				}
			}
		}else if(ROCK_GROUP.contains(oldState.getBlock())){
			if(oldState != rockBlock().getDefaultState()){
				world.setBlockState(pos, rockBlock().getDefaultState());
			}
		}else if(SOIL_GROUP.contains(oldState.getBlock())){
			//Special case for grass vs dirt
			BlockPos upPos = pos.up();
			if(soilBlock() == Blocks.GRASS_BLOCK && !world.getBlockState(upPos).isAir(world, upPos)){
				if(oldState != Blocks.DIRT.getDefaultState()){
					world.setBlockState(pos, Blocks.DIRT.getDefaultState());
				}
			}else if(oldState != soilBlock().getDefaultState()){
				world.setBlockState(pos, soilBlock().getDefaultState());
			}
		}
	}
}
