package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.ISpinReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendSpinToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;

public class AdditionAxisTileEntity extends AbstractMathAxisTE implements ISpinReceiver{

	//TODO completely broken

	private EnumFacing.Axis axis;

	@Override
	public void receiveSpin(int identifier, float clientW, float angle){
		//No point synchronizing angle. 

		if(identifier == 0){
			lastInPos = clientW;
		}else if(identifier == 1){
			lastInNeg = clientW;
		}
	}

	//On the server side these serve as a record of what was sent to the render, but on the render this is the received data for rendering.
	public float lastInPos;
	public float lastInNeg;
	public double angleOne;
	public double angleTwo;
	public double angleThree;

	@Override
	protected void runCalc(){
		super.runCalc();

		EnumFacing side1 = getInOne();
		TileEntity te1 = world.getTileEntity(pos.offset(side1));
		double in1 = te1 != null && te1.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side1.getOpposite()) ? te1.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side1.getOpposite()).getMotionData()[0] : 0;
		double in2;
		EnumFacing side2 = getInTwo();
		TileEntity te2 = world.getTileEntity(pos.offset(side2));
		in2 = te2 != null && te2.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side2.getOpposite()) ? te2.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side2.getOpposite()).getMotionData()[0] : 0;

		in1 *= -1D;

		if(Math.abs(lastInPos - in1) >= CLIENT_SPEED_MARGIN){
			lastInPos = (float) in1;
			ModPackets.network.sendToAllAround(new SendSpinToClient(0, lastInPos, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
		if(Math.abs(in2 - lastInNeg) >= CLIENT_SPEED_MARGIN){
			lastInNeg = (float) in2;
			ModPackets.network.sendToAllAround(new SendSpinToClient(1, lastInNeg, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}

		runAngleCalc();
	}

	@Override
	protected double getOutSpeed(double speed1, double speed2){
		return speed1 + speed2;
	}

	@Override
	protected EnumFacing getInOne(){
		if(axis == null){
			if(world.getBlockState(pos).getBlock() != ModBlocks.additionAxis){
				return EnumFacing.DOWN;
			}
			axis = world.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.Z : Axis.X;
		}
		return EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, axis);
	}

	@Nullable
	@Override
	protected EnumFacing getInTwo(){
		if(axis == null){
			if(world.getBlockState(pos).getBlock() != ModBlocks.additionAxis){
				return EnumFacing.DOWN;
			}
			axis = world.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.Z : Axis.X;
		}
		return EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, axis);
	}

	@Override
	protected EnumFacing getOut(){
		return EnumFacing.UP;
	}

	@Override
	protected EnumFacing getBattery(){
		return EnumFacing.DOWN;
	}

	@Override
	protected void cleanDirCache(){
		axis = null;
	}

	@Override
	public void update(){
		if(world.isRemote){
			angleOne += Math.toDegrees(lastInPos / 20D);
			angleTwo += Math.toDegrees(lastInNeg / 20D);
			angleThree += Math.toDegrees((lastInNeg - lastInPos) / 20D);
			return;
		}
		super.update();
	}
}
