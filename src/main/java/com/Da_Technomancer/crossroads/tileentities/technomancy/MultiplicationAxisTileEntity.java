package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.ISpinReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendSpinToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxle;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;

public class MultiplicationAxisTileEntity extends AbstractMathAxisTE implements ISpinReceiver{

	private EnumFacing facing;

	@Override
	public void receiveSpin(int identifier, float clientW, float angle){
		//No point in syncing angle. 

		if(identifier == 0){
			lastInOne = clientW;
		}else if(identifier == 1){
			lastInTwo = clientW;
		}
	}

	//On the server side these serve as a record of what was sent to the client, but on the client this is the received data for rendering. 
	public float lastInOne;
	public float lastInTwo;
	public double angleOne;
	public double angleTwo;
	public double angleThree;
	public double angleTwoPos;

	@Override
	protected void runCalc(){
		super.runCalc();

		EnumFacing side1 = getInOne();
		TileEntity te1 = world.getTileEntity(pos.offset(side1));
		double in1 = te1 != null && te1.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side1.getOpposite()) ? te1.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side1.getOpposite()).getMotionData()[0] : 0;
		if(side1.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && te1 instanceof IAxle){
			in1 *= -1D;
		}
		double in2;
		EnumFacing side2 = getInTwo();
		TileEntity te2 = world.getTileEntity(pos.offset(side2));
		in2 = te2 != null && te2.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side2.getOpposite()) ? te2.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side2.getOpposite()).getMotionData()[0] : 0;
		if(side2.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && te2 instanceof IAxle){
			in2 *= -1D;
		}

		if(facing.getAxisDirection() == AxisDirection.NEGATIVE){
			in1 *= -1D;
		}
		if(Math.abs(in1 - lastInOne) >= CLIENT_SPEED_MARGIN){
			lastInOne = (float) in1;
			ModPackets.network.sendToAllAround(new SendSpinToClient(0, lastInOne, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
		if(Math.abs(in2 - lastInTwo) >= CLIENT_SPEED_MARGIN){
			lastInTwo = (float) in2;
			ModPackets.network.sendToAllAround(new SendSpinToClient(1, lastInTwo, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}

		runAngleCalc();
	}

	@Override
	protected double getOutSpeed(double speed1, double speed2){
		return world.getBlockState(pos).getValue(EssentialsProperties.REDSTONE_BOOL) ? speed2 == 0 ? 0 : speed1 / speed2 : speed1 * speed2;
	}

	@Override
	protected EnumFacing getInOne(){
		if(facing == null){
			if(!world.getBlockState(pos).getProperties().containsKey(EssentialsProperties.FACING)){
				return EnumFacing.DOWN;
			}
			facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
		}
		return facing.getOpposite();
	}

	@Nullable
	@Override
	protected EnumFacing getInTwo(){
		return EnumFacing.UP;
	}

	@Override
	protected EnumFacing getOut(){
		if(facing == null){
			if(!world.getBlockState(pos).getProperties().containsKey(EssentialsProperties.FACING)){
				return EnumFacing.DOWN;
			}
			facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
		}
		return facing;
	}

	@Override
	protected EnumFacing getBattery(){
		return EnumFacing.DOWN;
	}

	@Override
	protected void cleanDirCache(){
		facing = null;
	}


	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setFloat("lastOne", lastInOne);
		nbt.setFloat("lastTwo", lastInTwo);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		lastInOne = nbt.getFloat("lastOne");
		lastInTwo = nbt.getFloat("lastTwo");
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setFloat("lastOne", lastInOne);
		nbt.setFloat("lastTwo", lastInTwo);
		return nbt;
	}

	@Override
	public void update(){
		if(world.isRemote){
			boolean divide = world.getBlockState(pos).getValue(EssentialsProperties.REDSTONE_BOOL);
			angleOne += Math.toDegrees((divide ? (lastInTwo == 0 ? 0 : divide ? -lastInOne / lastInTwo : -lastInOne * lastInTwo) : lastInOne) / 20D);
			angleTwo += Math.toDegrees(lastInTwo / 20D);
			angleTwoPos += Math.toDegrees(Math.abs(lastInTwo) / 20D);
			angleThree += Math.toDegrees((divide ? lastInOne : (lastInTwo == 0 ? 0 : divide ? -lastInOne / lastInTwo : -lastInOne * lastInTwo)) / 20D);
			return;
		}

		super.update();
	}
}
