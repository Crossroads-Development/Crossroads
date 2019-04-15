package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.ModConfig;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEffect implements IEffect{

	private final IBlockState block;

	public BlockEffect(IBlockState block){
		this.block = block;
	}

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
		IBlockState prev = worldIn.getBlockState(pos);
		if(prev == block){
			return;
		}

		if(!MiscUtil.canBreak(worldIn.getBlockState(pos), false)){
			return;
		}
		worldIn.setBlockState(pos, block, 3);
		SoundType soundtype = block.getBlock().getSoundType(block, worldIn, pos, null);
		worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
	}
}
