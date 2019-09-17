package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBiomeUpdateToClient;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.crafting.BlockRecipePredicate;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactiveSpotTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.world.biome.Biomes;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AetherEffect implements IAlchEffect{

	protected static final ArrayList<Predicate<BlockState>> SOIL_GROUP = new ArrayList<>();
	protected static final ArrayList<Predicate<BlockState>> ROCK_GROUP = new ArrayList<>();
	protected static final ArrayList<Predicate<BlockState>> FLUD_GROUP = new ArrayList<>();//Was going to be named FLUID_GROUP, but the other two fields had the same name lengths and I couldn't resist
	protected static final ArrayList<Predicate<BlockState>> CRYS_GROUP = new ArrayList<>();

	static{
		SOIL_GROUP.add(new MaterialPredicate(Material.GROUND));
		SOIL_GROUP.add(new MaterialPredicate(Material.SAND));
		SOIL_GROUP.add(new MaterialPredicate(Material.SNOW));
		SOIL_GROUP.add(new MaterialPredicate(Material.CRAFTED_SNOW));
		SOIL_GROUP.add(new MaterialPredicate(Material.CLAY));
		SOIL_GROUP.add(new MaterialPredicate(Material.GRASS));
		ROCK_GROUP.add(new MaterialPredicate(Material.ROCK));
		ROCK_GROUP.add(new MaterialPredicate(Material.PACKED_ICE));
		FLUD_GROUP.add(new MaterialPredicate(Material.WATER));
		FLUD_GROUP.add(new MaterialPredicate(Material.ICE));
		FLUD_GROUP.add(new MaterialPredicate(Material.LAVA));
		CRYS_GROUP.add(new MaterialPredicate(Material.GLASS));
		CRYS_GROUP.add(new BlockRecipePredicate(CrossroadsBlocks.blockPureQuartz.getDefaultState(), false));
	}

	private static void doTransmute(BlockState oldState, World world, BlockPos pos){
		Chunk c = world.getChunk(pos);
		if(world.getBiome(pos) != Biomes.PLAINS){
			c.getBiomeArray()[(pos.getZ() & 15) << 4 | (pos.getX() & 15)] = (byte) Biome.getIdForBiome(Biomes.PLAINS);
			CrossroadsPackets.network.sendToDimension(new SendBiomeUpdateToClient(pos, Biomes.PLAINS.getRegistryName()), world.provider.getDimension());
		}

		if(oldState.getBlock().isAir(oldState, world, pos) || oldState.getBlockHardness(world, pos) < 0){
			return;
		}

		for(Predicate<BlockState> pred : CRYS_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.GLASS.getDefaultState()){
					world.setBlockState(pos, Blocks.GLASS.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<BlockState> pred : FLUD_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.WATER.getDefaultState() && oldState.getBlock() != CrossroadsBlocks.reactiveSpot){
					world.setBlockState(pos, CrossroadsBlocks.reactiveSpot.getDefaultState());
					TileEntity te = world.getTileEntity(pos);
					if(te instanceof ReactiveSpotTileEntity){
						((ReactiveSpotTileEntity) te).setTarget(Blocks.WATER.getDefaultState());
					}
				}
				return;
			}
		}
		for(Predicate<BlockState> pred : ROCK_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.STONE.getDefaultState()){
					world.setBlockState(pos, Blocks.STONE.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<BlockState> pred : SOIL_GROUP){
			if(pred.test(oldState)){
				BlockState upState = world.getBlockState(pos.offset(Direction.UP));
				if(upState.getBlock().isAir(upState, world, pos.offset(Direction.UP))){
					if(oldState != Blocks.GRASS.getDefaultState()){
						world.setBlockState(pos, Blocks.GRASS.getDefaultState());
					}
				}else if(oldState != Blocks.DIRT.getDefaultState()){
					world.setBlockState(pos, Blocks.DIRT.getDefaultState());
				}
				return;
			}
		}
	}

	protected static class MaterialPredicate implements Predicate<BlockState>{

		private final Material m;

		public MaterialPredicate(Material m){
			this.m = m;
		}

		@Override
		public boolean test(BlockState toCheck){
			return toCheck.getMaterial() == m && !(toCheck.getBlock() instanceof ITileEntityProvider);
		}
	}
	
	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap contents){
		BlockState oldState = world.getBlockState(pos);

		//quicksilver makes it create a block instead of transmuting blocks
		if(contents.getQty(EnumReagents.QUICKSILVER.id()) != 0 && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, Blocks.GRASS.getDefaultState());
			return;
		}

		doTransmute(oldState, world, pos);
	}
}
