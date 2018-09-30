package com.Da_Technomancer.crossroads.API.redstone;

import com.Da_Technomancer.crossroads.API.Capabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneUtil{

	/**
	 * Gets the redstone power (including adv. redstone) at a position on all sides
	 * @param worldIn The world (virtual server side only)
	 * @param pos The position of the measuring block
	 * @return The measured redstone power
	 */
	public static double getPowerAtPos(World worldIn, BlockPos pos){
		double output = 0;

		for(EnumFacing side : EnumFacing.VALUES){
			output = Math.max(output, getPowerOnSide(worldIn, pos, side));
		}

		return output;
	}

	/**
	 * Measures the redstone power (including adv. redstone) on one specific side
	 * @param worldIn The world (virtual server side only)
	 * @param pos The position of the measuring block
	 * @param side The direction to measure in
	 * @return The measured redstone power
	 */
	public static double getPowerOnSide(World worldIn, BlockPos pos, EnumFacing side){
		TileEntity te = worldIn.getTileEntity(pos.offset(side));
		if(te != null && te.hasCapability(Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY, side.getOpposite())){
			return te.getCapability(Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY, side.getOpposite()).getOutput(false);
		}else{
			return worldIn.getRedstonePower(pos.offset(side), side);
		}
	}

	/**
	 * For use by the backside of Ratiators
	 * @param worldIn The world (virtual server side only)
	 * @param pos The position of the measuring block
	 * @param side The side to be measured
	 * @return The measured redstone power
	 */
	public static double getMeasuredPower(World worldIn, BlockPos pos, EnumFacing side){
		BlockPos offsetPos = pos.offset(side);

		TileEntity te = worldIn.getTileEntity(offsetPos);

		if(te != null && te.hasCapability(Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY, side.getOpposite())){
			return te.getCapability(Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY, side.getOpposite()).getOutput(true);
		}

		IBlockState state = worldIn.getBlockState(offsetPos);

		if(state.hasComparatorInputOverride()){
			return state.getComparatorInputOverride(worldIn, offsetPos);
		}
		int possibleOut = worldIn.getRedstonePower(offsetPos, side);
		if(possibleOut != 0){
			return possibleOut;

		}

		if(worldIn.isBlockNormalCube(offsetPos, false)){
			offsetPos = pos.offset(side, 2);
			state = worldIn.getBlockState(offsetPos);
			te = worldIn.getTileEntity(offsetPos);
			if(te != null && te.hasCapability(Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY, side.getOpposite())){
				return te.getCapability(Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY, side.getOpposite()).getOutput(true);
			}

			if(state.hasComparatorInputOverride()){
				return state.getComparatorInputOverride(worldIn, offsetPos);
			}
		}
		return 0;
	}
}
