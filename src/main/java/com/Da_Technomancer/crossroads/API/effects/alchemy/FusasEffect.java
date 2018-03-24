package com.Da_Technomancer.crossroads.API.effects.alchemy;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.fluids.ModFluids;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FusasEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, double amount,double heat, EnumMatterPhase phase){
		IBlockState oldState = world.getBlockState(pos);
		if(oldState.getBlock().isAir(oldState, world, pos) || oldState.getBlockHardness(world, pos) < 0){
			return;
		}

		for(Predicate<IBlockState> pred : AetherEffect.CRYS_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.SEA_LANTERN.getDefaultState()){
					world.setBlockState(pos, Blocks.SEA_LANTERN.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.FLUD_GROUP){
			if(pred.test(oldState)){
				if(oldState != ModFluids.distilledWater.getDefaultState()){
					world.setBlockState(pos, ModFluids.distilledWater.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.ROCK_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.PRISMARINE.getDefaultState()){
					world.setBlockState(pos, Blocks.PRISMARINE.getDefaultState());
				}
				return;
			}
		}
		for(Predicate<IBlockState> pred : AetherEffect.SOIL_GROUP){
			if(pred.test(oldState)){
				if(oldState != Blocks.CLAY.getDefaultState()){
					world.setBlockState(pos, Blocks.CLAY.getDefaultState());
				}
				return;
			}
		}
	}
	
	@Override
	public void doEffectAdv(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase, @Nullable ReagentStack[] contents){
		IBlockState oldState = world.getBlockState(pos);
		if(contents != null && contents[13] != null && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, ModFluids.distilledWater.getDefaultState());
			return;
		}
		doEffect(world, pos, amount, temp, phase);
	}
}
