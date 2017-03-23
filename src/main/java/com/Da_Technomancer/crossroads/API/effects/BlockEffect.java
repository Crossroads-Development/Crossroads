package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEffect implements IEffect{

	private final IBlockState block;

	public BlockEffect(IBlockState block){
		this.block = block;
	}

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		if(worldIn.getBlockState(pos).getBlock() == Blocks.BARRIER){
			return;
		}
		worldIn.setBlockState(pos, block, 3);
		SoundType soundtype = block.getBlock().getSoundType(block, worldIn, pos, null);
		worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
	}
}
