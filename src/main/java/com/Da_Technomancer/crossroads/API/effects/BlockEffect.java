package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.ModConfig;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
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
		String[] bannedBlocks = ModConfig.getConfigStringList(ModConfig.destroyBlacklist, false);
		String id = worldIn.getBlockState(pos).getBlock().getRegistryName().toString();
		for(String s : bannedBlocks){
			if(s.equals(id)){
				return;
			}
		}
		worldIn.setBlockState(pos, block, 3);
		SoundType soundtype = block.getBlock().getSoundType(block, worldIn, pos, null);
		worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
	}
}
