package com.Da_Technomancer.crossroads.API.rotary;

import com.Da_Technomancer.crossroads.API.MiscOp;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DefaultAxleHandler implements IAxleHandler{

	/** I keep changing my mind about how to determine whether gears can connect diagonally through a block. 
	 * Implementers of IAxleHandler should use this to determine whether they can connect diagonally through a block. 
	 * @param world The World.
	 * @param pos The BlockPos of the block space that is being connected through.
	 * @param fromDir The direction from pos that the caller is located.
	 * @param toDir The direction from pos that the end point of the connection is located.
	 * @return Whether a connection is allowed. Does not verify that the start/endpoints are valid. 
	 */
	public static boolean canConnectThrough(World world, BlockPos pos, EnumFacing fromDir, EnumFacing toDir){
		IBlockState state = world.getBlockState(pos);
		return !state.getBlock().isNormalCube(state, world, pos);
	}
	
	private double[] motionData = new double[4];
	private double[] physData = new double[2];

	@Override
	public double[] getMotionData(){
		return motionData;
	}

	@Override
	public double[] getPhysData(){
		return physData;
	}

	@Override
	public void resetAngle(){
		
	}

	@Override
	public double getAngle(){
		return 0;
	}

	@Override
	public void addEnergy(double energy, boolean allowInvert, boolean absolute){
		if(allowInvert && absolute){
			motionData[1] += energy;
		}else if(allowInvert){
			motionData[1] += energy * MiscOp.posOrNeg(motionData[1]);
		}else if(absolute){
			int sign = (int) MiscOp.posOrNeg(motionData[1]);
			motionData[1] += energy;
			if(sign != 0 && MiscOp.posOrNeg(motionData[1]) != sign){
				motionData[1] = 0;
			}
		}else{
			int sign = (int) MiscOp.posOrNeg(motionData[1]);
			motionData[1] += energy * MiscOp.posOrNeg(motionData[1]);
			if(MiscOp.posOrNeg(motionData[1]) != sign){
				motionData[1] = 0;
			}
		}
	}

	@Override
	public void propogate(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius){
		
	}

	@Override
	public double getRotationRatio(){
		return 0;
	}
	
	@Override
	public void markChanged(){
		
	}
}
