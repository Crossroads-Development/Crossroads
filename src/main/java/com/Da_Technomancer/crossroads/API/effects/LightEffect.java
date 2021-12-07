package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class LightEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				//Break light sources nearby
				int range = (int) Math.sqrt(power) / 2;//0 to 4 radius
				BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
				for(int i = -range; i <= range; i++){
					for(int j = -range; j <= range; j++){
						for(int k = -range; k <= range; k++){
							checkPos.set(pos.getX() + i, pos.getY() + j, pos.getZ() + k);
							BlockState state = worldIn.getBlockState(checkPos);
							if(state.getLightEmission() > 0 && state.getDestroySpeed(worldIn, checkPos) < 0.5F){
								worldIn.destroyBlock(checkPos, true);
							}
						}
					}
				}
			}else{
				//Spawn light clusters
				BlockState state = worldIn.getBlockState(pos);
				if(state.isAir()){
					worldIn.setBlockAndUpdate(pos, CRBlocks.lightCluster.defaultBlockState());
				}else if(dir != null && state.getBlock() != CRBlocks.lightCluster && state.getLightEmission() == 0 && state.isSolidRender(worldIn, pos)){//Don't spawn clusters against other light sources
					BlockPos offsetPos = pos.relative(dir);
					state = worldIn.getBlockState(offsetPos);
					if(state.isAir()){
						worldIn.setBlockAndUpdate(offsetPos, CRBlocks.lightCluster.defaultBlockState());
					}
				}
			}
		}
	}
}
