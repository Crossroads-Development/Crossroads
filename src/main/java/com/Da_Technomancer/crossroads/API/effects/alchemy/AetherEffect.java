package com.Da_Technomancer.crossroads.API.effects.alchemy;

import java.util.ArrayList;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBiomeUpdateToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.crafting.BlockRecipePredicate;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class AetherEffect implements IAlchEffect{

	protected static final ArrayList<Predicate<IBlockState>> SOIL_GROUP = new ArrayList<Predicate<IBlockState>>();
	protected static final ArrayList<Predicate<IBlockState>> ROCK_GROUP = new ArrayList<Predicate<IBlockState>>();
	protected static final ArrayList<Predicate<IBlockState>> FLUD_GROUP = new ArrayList<Predicate<IBlockState>>();//Was going to be named FLUID_GROUP, but the other two fields had the samed name lengths and I couldn't resist
	protected static final ArrayList<Predicate<IBlockState>> CRYS_GROUP = new ArrayList<Predicate<IBlockState>>();

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
		CRYS_GROUP.add(new BlockRecipePredicate(ModBlocks.blockPureQuartz.getDefaultState(), false));
	}

	@Override
	public void doEffect(World world, BlockPos pos, double amount,double heat, EnumMatterPhase phase){
		doTransmute(world, pos);
	}

	private static void doTransmute(World world, BlockPos pos){
		Chunk c = world.getChunkFromBlockCoords(pos);
		if(world.getBiome(pos) != Biomes.PLAINS){
			c.getBiomeArray()[(pos.getZ() & 15) << 4 | (pos.getX() & 15)] = (byte) Biome.getIdForBiome(Biomes.PLAINS);
			ModPackets.network.sendToDimension(new SendBiomeUpdateToClient(pos, (byte) Biome.getIdForBiome(Biomes.PLAINS)), world.provider.getDimension());
		}

		IBlockState oldState = world.getBlockState(pos);
		if(oldState.getBlock().isAir(oldState, world, pos) || oldState.getBlockHardness(world, pos) < 0){
			return;
		}

		for(Predicate<IBlockState> pred : CRYS_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.GLASS.getDefaultState()){
					world.setBlockState(pos, Blocks.GLASS.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : FLUD_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.WATER.getDefaultState()){
					world.setBlockState(pos, Blocks.WATER.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : ROCK_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.STONE.getDefaultState()){
					world.setBlockState(pos, Blocks.STONE.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : SOIL_GROUP){
			if(pred.test(oldState)){
				IBlockState upState = world.getBlockState(pos.offset(EnumFacing.UP));
				if(upState.getBlock().isAir(upState, world, pos.offset(EnumFacing.UP))){
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

	protected static class MaterialPredicate implements Predicate<IBlockState>{

		private final Material m;

		public MaterialPredicate(Material m){
			this.m = m;
		}

		@Override
		public boolean test(IBlockState toCheck){
			return toCheck.getMaterial() == m && !(toCheck.getBlock() instanceof ITileEntityProvider);
		}
	}
	
	@Override
	public void doEffectAdv(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase, @Nullable ReagentStack[] contents){
		IBlockState oldState = world.getBlockState(pos);
		if(contents != null && contents[13] != null && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, Blocks.DIRT.getDefaultState());
			return;
		}
		doEffect(world, pos, amount, temp, phase);
	}
}
