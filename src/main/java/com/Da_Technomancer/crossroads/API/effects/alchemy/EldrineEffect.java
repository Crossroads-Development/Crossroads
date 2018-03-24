package com.Da_Technomancer.crossroads.API.effects.alchemy;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EldrineEffect implements IAlchEffect{

	@Override
	public void doEffect(World world, BlockPos pos, double amount,double heat, EnumMatterPhase phase){
		IBlockState oldState = world.getBlockState(pos);
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
				if(oldState != Blocks.LAVA.getDefaultState()){
					world.setBlockState(pos, Blocks.LAVA.getDefaultState());
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
	
	@Override
	public void doEffectAdv(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase, @Nullable ReagentStack[] contents){
		IBlockState oldState = world.getBlockState(pos);
		if(contents != null && contents[13] != null && oldState.getBlock().isAir(oldState, world, pos)){
			world.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState());
			return;
		}
		doEffect(world, pos, amount, temp, phase);
	}
}
