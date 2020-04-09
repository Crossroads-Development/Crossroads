package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LightEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				//Break light sources nearby
				int range = (int) Math.sqrt(power) / 2;//0 to 4 radius
				BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(pos);
				for(int i = -range; i <= range; i++){
					for(int j = -range; j <= range; j++){
						for(int k = -range; k <= range; k++){
							checkPos.setPos(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
							BlockState state = worldIn.getBlockState(checkPos);
							if(state.getLightValue() > 0 && state.getBlockHardness(worldIn, checkPos) < 0.5F){
								worldIn.destroyBlock(checkPos, true);
							}
						}
					}
				}
			}else{
				//Spawn light clusters
				BlockState state = worldIn.getBlockState(pos);
				if(state.isAir(worldIn, pos)){
					worldIn.setBlockState(pos, CRBlocks.lightCluster.getDefaultState());
				}else if(dir != null && state.getBlock() != CRBlocks.lightCluster && state.getLightValue() == 0 && state.isOpaqueCube(worldIn, pos)){//Don't spawn clusters against other light sources
					BlockPos offsetPos = pos.offset(dir);
					state = worldIn.getBlockState(offsetPos);
					if(state.isAir(worldIn, offsetPos)){
						worldIn.setBlockState(offsetPos, CRBlocks.lightCluster.getDefaultState());
					}
				}
			}
		}
	}
}
