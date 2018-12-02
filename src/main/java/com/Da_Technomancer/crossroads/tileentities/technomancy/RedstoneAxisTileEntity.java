package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class RedstoneAxisTileEntity extends MasterAxisTileEntity{

	@Override
	protected void runCalc(){
		EnumFacing facing = getFacing();

		double baseSpeed = RedstoneUtil.getPowerAtPos(world, pos);
		double sumIRot = 0;
		sumEnergy = RotaryUtil.getTotalEnergy(rotaryMembers);

		double cost = sumIRot * Math.pow(baseSpeed, 2) / 2D;
		TileEntity backTE = world.getTileEntity(pos.offset(facing.getOpposite()));
		double availableEnergy = Math.abs(sumEnergy) + Math.abs(backTE != null && backTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing) ? backTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[1] : 0);
		if(availableEnergy - cost < 0){
			baseSpeed = 0;
			cost = 0;
		}
		availableEnergy -= cost;

		for(IAxleHandler gear : rotaryMembers){
			double newEnergy;

			// set w
			gear.getMotionData()[0] = gear.getRotationRatio() * baseSpeed;
			// set energy
			newEnergy = Math.signum(gear.getMotionData()[0]) * Math.pow(gear.getMotionData()[0], 2) * gear.getMoInertia() / 2D;
			gear.getMotionData()[1] = newEnergy;
			sumEnergy += newEnergy;
			// set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20;
			// set lastE
			gear.getMotionData()[3] = newEnergy;

			gear.markChanged();
		}

		if(backTE != null && backTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing)){
			backTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[1] = availableEnergy * RotaryUtil.posOrNeg(backTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[1], 1);
		}

		runAngleCalc();
	}
}
