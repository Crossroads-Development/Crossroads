package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AetherEffect implements IAlchEffect{

	@Override
	public void doEffectAdv(World world, BlockPos pos, double amount, EnumMatterPhase phase, ReagentStack[] contents){
		doTransmute(world, pos, contents[3] == null ? 0 : contents[3].getAmount() / amount, contents[13] == null ? 0 : contents[13].getAmount() / amount);
	}

	@Override
	public void doEffect(World world, BlockPos pos, double amount, EnumMatterPhase phase){
		
	}
	
	public static void doTransmute(World world, BlockPos pos, double sulfurRatio, double quicksilverRatio){
		IBlockState oldState = world.getBlockState(pos);
		float fromHardness = oldState.getBlockHardness(world, pos);
		if(oldState.getBlock().isAir(oldState, world, pos)){
			return;
		}
		
		if(fromHardness < 0){
			if(oldState.getBlock() == Blocks.BEDROCK){
				fromHardness = 100;
			}else{
				return;
			}
		}
		
		if(sulfurRatio > 0.8D || sulfurRatio * 32D >= fromHardness){
			IBlockState newState = Blocks.AIR.getDefaultState();
			
			switch((int) (quicksilverRatio * 10D)){
				case 0:
					break;
				case 1:
					newState = Blocks.WATER.getDefaultState();
					break;
				case 2:
					newState = Blocks.ICE.getDefaultState();
					break;
				case 3:
					newState = Blocks.SAND.getDefaultState();
					break;
				case 4:
					newState = Blocks.DIRT.getDefaultState();
					break;
				case 5:
					newState = Blocks.CLAY.getDefaultState();
					break;
				case 6:
					newState = Blocks.STONE.getDefaultState();
					break;
				case 7:
					newState = Blocks.OBSIDIAN.getDefaultState();
					break;
				default:
					newState = Blocks.LAVA.getDefaultState();
					break;
			}
			
			world.setBlockState(pos, newState);
		}
	}
}
