package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.entity.EntityFlameCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

public class PhelostogenEffect implements IAlchEffect{

	private final Function<Integer, Integer> RADIUS_FINDER;
	
	public PhelostogenEffect(Function<Integer, Integer> radiusFinder){
		this.RADIUS_FINDER = radiusFinder;
	}
	
	@Override
	public void doEffectAdv(World world, BlockPos pos, int amount, double temp, EnumMatterPhase phase, ReagentMap contents){
		if(ModConfig.getConfigBool(ModConfig.phelEffect, false)){

			EntityFlameCore coreFlame = new EntityFlameCore(world);
			coreFlame.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			world.spawnEntity(coreFlame);
			coreFlame.setInitialValues(contents, temp, RADIUS_FINDER.apply(amount));
		}else{
			IBlockState prev = world.getBlockState(pos);
			if(prev.getBlock() == Blocks.FIRE){
				return;
			}
			String[] bannedBlocks = ModConfig.getConfigStringList(ModConfig.destroyBlacklist, false);
			String id = prev.getBlock().getRegistryName().toString();
			for(String s : bannedBlocks){
				if(s.equals(id)){
					return;
				}
			}

			world.setBlockState(pos, Blocks.FIRE.getDefaultState());
		}
	}

	@Override
	public void doEffect(World world, BlockPos pos, int amount, double temp, EnumMatterPhase phase){
		doEffectAdv(world, pos, amount, temp, phase, null);
	}
}
