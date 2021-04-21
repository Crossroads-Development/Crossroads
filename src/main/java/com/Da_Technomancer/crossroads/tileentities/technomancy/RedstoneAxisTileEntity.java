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
		if(!level.isClientSide){
			BlockState state = getBlockState();
			int targetProp = targetSpeed > 0 ? 1 : targetSpeed < 0 ? 2 : 0;
			if(state.getValue(CRProperties.POWER_LEVEL) != targetProp){
				level.setBlock(worldPosition, state.setValue(CRProperties.POWER_LEVEL, targetProp), 2);
			}
		}
	}

	@Override
	protected void runCalc(){
		Direction facing = getFacing();
		double targetBaseSpeed = CircuitUtil.combineRedsSources(redsHandler);
		double[] energyCalcResults = RotaryUtil.getTotalEnergy(rotaryMembers, true);
		double sumIRot = energyCalcResults[3];//Sum of every gear's moment of inertia time rotation ratio squared

		double cost = sumIRot * Math.pow(targetBaseSpeed, 2) / 2D;//Total energy required to hold the output at the requested base speed
		TileEntity backTE = level.getBlockEntity(worldPosition.relative(facing.getOpposite()));
		LazyOptional<IAxleHandler> backOpt = backTE == null ? LazyOptional.empty() : backTE.getCapability(Capabilities.AXLE_CAPABILITY, facing);
		IAxleHandler sourceAxle = backOpt.isPresent() ? backOpt.orElseThrow(NullPointerException::new) : null;
		double availableEnergy = Math.abs(energyCalcResults[0]);
		//Add energy from the gear on the back. Don't double count if it's in this gear network
		if(rotaryMembers.contains(sourceAxle)){
			sourceAxle = null;
		}
		if(sourceAxle != null){
			availableEnergy += Math.abs(sourceAxle.getEnergy());
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

		energyChange = cost - sumEnergy;
		energyLossChange = energyCalcResults[1];
		sumEnergy = cost;
		availableEnergy -= cost;
		//Note the sumIRot check; the normal formula doesn't work for 0 mass system, and we can assume we're on the target speed in that condition
		//Re-calculated from the actual energy instead of using either the RotaryUtil calculated value or the targetBaseSpeed, neither of which recognize both energy changes and speed control
		baseSpeed = sumIRot == 0 ? targetBaseSpeed : Math.signum(targetBaseSpeed) * Math.sqrt(Math.abs(sumEnergy) * 2D / sumIRot);

		for(IAxleHandler gear : rotaryMembers){
			double gearSpeed = baseSpeed * gear.getRotationRatio();
			gear.setEnergy(Math.signum(gearSpeed) * Math.pow(gearSpeed, 2) * gear.getMoInertia() / 2D);
		}

		if(sourceAxle != null){
			sourceAxle.setEnergy(sourceAxle.getEnergy() < 0 ? -availableEnergy : availableEnergy);
		}

		updateWorldState(targetBaseSpeed);

		runAngleCalc();
	}

	@Override
	protected AxisTypes getAxisType(){
		return AxisTypes.FIXED;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		redsHandler.write(nbt);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		redsHandler.read(state, nbt);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		redsOpt.invalidate();
	}

	public final CircuitUtil.InputCircHandler redsHandler = new CircuitUtil.InputCircHandler();
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
