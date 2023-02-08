package com.Da_Technomancer.crossroads.effects.overheat_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.heat.HeatInsulators;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class IceEffect implements HeatInsulators.IOverheatEffect{

	@Override
	public void doEffect(Level worldIn, BlockPos pos){
		BlockState block = Blocks.WATER.defaultBlockState();
		BlockState prev = worldIn.getBlockState(pos);
		SoundType soundtype = block.getBlock().getSoundType(block, worldIn, pos, null);
		if(prev == block){
			return;
		}

		if(CRConfig.isProtected(worldIn, pos, worldIn.getBlockState(pos))){
			return;
		}

		if(worldIn.dimensionType().ultraWarm()){
			worldIn.playSound(null, pos, SoundEvents.GENERIC_BURN, SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
			worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			return;
		}
		worldIn.setBlock(pos, block, 3);
		worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
	}
}
