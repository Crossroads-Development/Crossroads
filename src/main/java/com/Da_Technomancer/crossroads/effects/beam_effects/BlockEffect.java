package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.beams.IBeamEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEffect implements IBeamEffect{

	private final BlockState block;

	public BlockEffect(BlockState block){
		this.block = block;
	}

	@Override
	public void doEffect(Level worldIn, BlockPos pos){
		BlockState prev = worldIn.getBlockState(pos);
		if(prev == block){
			return;
		}

		if(CRConfig.isProtected(worldIn, pos, worldIn.getBlockState(pos))){
			return;
		}
		worldIn.setBlock(pos, block, 3);
		SoundType soundtype = block.getBlock().getSoundType(block, worldIn, pos, null);
		worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
	}
}
