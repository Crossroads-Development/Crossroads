package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.API.rotary.AxisTypes;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
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

	private int redstone;

	public RedstoneAxisTileEntity(){
		super(type);
	}

	@Override
	protected void runCalc(){
		Direction facing = getFacing();

		double baseSpeed = CircuitUtil.combineRedsSources(redsHandler, redstone);
		double sumIRot = 0;
		sumEnergy = RotaryUtil.getTotalEnergy(rotaryMembers);

		for(IAxleHandler gear : rotaryMembers){
			sumIRot += gear.getMoInertia() * Math.pow(gear.getRotationRatio(), 2);
		}

		double cost = sumIRot * Math.pow(baseSpeed, 2) / 2D;
		TileEntity backTE = world.getTileEntity(pos.offset(facing.getOpposite()));
		LazyOptional<IAxleHandler> backOpt = backTE == null ? LazyOptional.empty() : backTE.getCapability(Capabilities.AXLE_CAPABILITY, facing);
		IAxleHandler sourceAxle = backOpt.isPresent() ? backOpt.orElseThrow(NullPointerException::new) : null;
		double availableEnergy = Math.abs(sumEnergy);
		if(sourceAxle != null){
			availableEnergy += Math.abs(sourceAxle.getMotionData()[1]);
		}
		if(availableEnergy - cost < 0){
			baseSpeed = 0;
			cost = 0;
		}
		availableEnergy -= cost;
		sumEnergy = 0;

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

		if(sourceAxle != null){
			sourceAxle.getMotionData()[1] = availableEnergy * RotaryUtil.posOrNeg(sourceAxle.getMotionData()[1], 1);
			sourceAxle.markChanged();
		}

		runAngleCalc();
	}

	@Override
	protected AxisTypes getAxisType(){
		return AxisTypes.FIXED;
	}

	public void setRedstone(int redstone){
		if(this.redstone != redstone){
			this.redstone = redstone;
			markDirty();
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("reds", redstone);
		nbt.putFloat("circ_reds", redsHandler.getCircRedstone());
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		redstone = nbt.getInt("reds");
		redsHandler.setCircRedstone(nbt.getFloat("circ_reds"));
	}

	@Override
	public void remove(){
		super.remove();
		redsOpt.invalidate();
	}

	public CircuitUtil.CircHandler redsHandler = new CircuitUtil.CircHandler();
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
