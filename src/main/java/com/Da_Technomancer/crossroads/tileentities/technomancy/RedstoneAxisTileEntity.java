package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.API.rotary.AxisTypes;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class RedstoneAxisTileEntity extends MasterAxisTileEntity{

	@ObjectHolder("redstone_axis")
	private static TileEntityType<RedstoneAxisTileEntity> type = null;

	public RedstoneAxisTileEntity(){
		super(type);
	}

	/**
	 * Updates the blockstate in the world to render based on the target speed (not the actual induced speed).
	 * Only effective on the virtual server side
	 * @param targetSpeed The current target speed
	 */
	private void updateWorldState(double targetSpeed){
		if(!world.isRemote){
			BlockState state = getBlockState();
			int targetProp = targetSpeed > 0 ? 1 : targetSpeed < 0 ? 2 : 0;
			if(state.get(CRProperties.POWER_LEVEL) != targetProp){
				world.setBlockState(pos, state.with(CRProperties.POWER_LEVEL, targetProp), 2);
			}
		}
	}

	@Override
	protected void runCalc(){
		Direction facing = getFacing();

		double baseSpeed = CircuitUtil.combineRedsSources(redsHandler);
		double sumIRot = 0;//Sum of every gear's moment of inertia time rotation ratio squared
		sumEnergy = RotaryUtil.getTotalEnergy(rotaryMembers);

		for(IAxleHandler gear : rotaryMembers){
			sumIRot += gear.getMoInertia() * Math.pow(gear.getRotationRatio(), 2);
		}

		double cost = sumIRot * Math.pow(baseSpeed, 2) / 2D;//Total energy required to hold the output at the requested base speed
		TileEntity backTE = world.getTileEntity(pos.offset(facing.getOpposite()));
		LazyOptional<IAxleHandler> backOpt = backTE == null ? LazyOptional.empty() : backTE.getCapability(Capabilities.AXLE_CAPABILITY, facing);
		IAxleHandler sourceAxle = backOpt.isPresent() ? backOpt.orElseThrow(NullPointerException::new) : null;
		double availableEnergy = Math.abs(sumEnergy);
		if(sourceAxle != null){
			availableEnergy += Math.abs(sourceAxle.getMotionData()[1]);
		}
		if(Double.isNaN(availableEnergy)){
			availableEnergy = 0;//There's a NaN bug somewhere, and I can't find it. This should work
		}
//		if(sumIRot > 0 && availableEnergy - cost < 0){
//			baseSpeed = 0;
//			cost = 0;
//		}

		//As much energy as possible until it would reach the desired speed is pulled into the system
		cost = Math.min(cost, availableEnergy);
		if(Double.isNaN(cost)){
			cost = 0;//There's a NaN bug somewhere, and I can't find it. This should work
		}
		sumEnergy = cost;
		availableEnergy -= cost;

		for(IAxleHandler gear : rotaryMembers){
			// set w
			double newSpeed;
			if(sumIRot <= 0 || Double.isNaN(sumIRot)){
				newSpeed = baseSpeed * gear.getRotationRatio();
			}else{
				newSpeed = Math.signum(sumEnergy * gear.getRotationRatio()) * Math.sqrt(Math.abs(sumEnergy) * 2D * Math.pow(gear.getRotationRatio(), 2) / sumIRot);
			}
			gear.getMotionData()[0] = newSpeed;
			// set energy
			double newEnergy = Math.signum(newSpeed) * Math.pow(newSpeed, 2) * gear.getMoInertia() / 2D;
			gear.getMotionData()[1] = newEnergy;
			// set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20D;
			// set lastE
			gear.getMotionData()[3] = newEnergy;


			gear.markChanged();
		}

		if(sourceAxle != null){
			sourceAxle.getMotionData()[1] = sourceAxle.getMotionData()[1] < 0 ? -availableEnergy : availableEnergy;
			sourceAxle.markChanged();
		}

		updateWorldState(baseSpeed);

		runAngleCalc();
	}

	@Override
	protected AxisTypes getAxisType(){
		return AxisTypes.FIXED;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		redsHandler.write(nbt);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		redsHandler.read(state, nbt);
	}

	@Override
	public void remove(){
		super.remove();
		redsOpt.invalidate();
	}

	public CircuitUtil.InputCircHandler redsHandler = new CircuitUtil.InputCircHandler();
	private LazyOptional<IRedstoneHandler> redsOpt = CircuitUtil.makeBaseCircuitOptional(this, redsHandler, 0);

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir){
		if(cap == RedstoneUtil.REDSTONE_CAPABILITY){
			return (LazyOptional<T>) redsOpt;
		}
		return super.getCapability(cap, dir);
	}
}
