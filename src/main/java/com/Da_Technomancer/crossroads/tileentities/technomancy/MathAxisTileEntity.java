package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CrossroadsProperties;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.rotary.*;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.function.DoubleBinaryOperator;

public class MathAxisTileEntity extends MasterAxisTileEntity implements IIntReceiver{

	private Mode mode = Mode.SUM;
	private final IAxisHandler mathAxisHandler = new AxisHandler();
	private final ISlaveAxisHandler slaveHandler = new SlaveAxisHandler();

	@Override
	protected Direction getFacing(){
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CrossroadsBlocks.mathAxis){
				invalidate();
				return Direction.NORTH;
			}
			facing = state.get(CrossroadsProperties.HORIZ_FACING);
		}
		return facing;
	}

	public void setMode(Mode mode){
		this.mode = mode;
		if(!world.isRemote){
			world.setBlockState(pos, world.getBlockState(pos).with(CrossroadsProperties.ARRANGEMENT, mode.getFormat()), 2);
		}
		markDirty();
	}

	@Override
	protected AxisTypes getType(){
		return AxisTypes.FIXED;
	}

	public Mode getMode(){
		return mode;
	}

	@Override
	public void receiveInt(byte identifier, int message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == 0 && message >= 0 && message < Mode.values().length){
			setMode(Mode.values()[message]);
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return newState.getBlock() != oldState.getBlock();
	}

	@Override
	public void disconnect(){
		super.disconnect();
		facing = null;
	}

	@Override
	protected void runCalc(){
		Direction side1 = getFacing().getOpposite();
		TileEntity te1 = world.getTileEntity(pos.offset(side1));
		double in1 = te1 != null && te1.hasCapability(Capabilities.AXLE_CAPABILITY, side1.getOpposite()) ? te1.getCapability(Capabilities.AXLE_CAPABILITY, side1.getOpposite()).getMotionData()[0] : 0;
		double in2 = 0;

		if(mode.format == Arrangement.DOUBLE){
			TileEntity te2 = world.getTileEntity(pos.offset(Direction.UP));
			in2 = te2 != null && te2.hasCapability(Capabilities.AXLE_CAPABILITY, Direction.DOWN) ? te2.getCapability(Capabilities.AXLE_CAPABILITY, Direction.DOWN).getMotionData()[0] : 0;
		}

		double targetSpeed = mode.getFunction().applyAsDouble(in1, in2);
		double sumIRot = 0;
		sumEnergy = RotaryUtil.getTotalEnergy(rotaryMembers);

		for(IAxleHandler gear : rotaryMembers){
			sumIRot += gear.getMoInertia() * Math.pow(gear.getRotationRatio(), 2);
		}

		double cost = sumIRot * Math.pow(targetSpeed, 2) / 2D;
		TileEntity batteryTE = world.getTileEntity(pos.offset(Direction.DOWN));
		IAxleHandler sourceAxle = batteryTE == null ? null : batteryTE.getCapability(Capabilities.AXLE_CAPABILITY, Direction.UP);

		double availableEnergy = Math.abs(sumEnergy);
		if(sourceAxle != null){
			availableEnergy += Math.abs(sourceAxle.getMotionData()[1]);
		}

		if(availableEnergy - cost < 0){
			targetSpeed = 0;
		}else{
			availableEnergy -= cost;
		}
		for(IAxleHandler gear : rotaryMembers){
			double newEnergy;

			// set w
			gear.getMotionData()[0] = gear.getRotationRatio() * targetSpeed;
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
		}

		runAngleCalc();
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		mode = Mode.values()[nbt.getInt("mode")];
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("mode", mode.ordinal());
		return nbt;
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}

		ticksExisted++;

		if(ticksExisted % UPDATE_TIME == 20 || forceUpdate){
			handler.requestUpdate();
		}

		forceUpdate = RotaryUtil.getMasterKey() != lastKey;

		lastKey = RotaryUtil.getMasterKey();
	}

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.SLAVE_AXIS_CAPABILITY && (side == getFacing().getOpposite() || (mode.format == Arrangement.DOUBLE && side == Direction.UP))){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXIS_CAPABILITY && (side == null || side == getFacing())){
			return (T) mathAxisHandler;
		}
		if(cap == Capabilities.SLAVE_AXIS_CAPABILITY && (side == getFacing().getOpposite() || (mode.format == Arrangement.DOUBLE && side == Direction.UP))){
			return (T) slaveHandler;
		}

		return super.getCapability(cap, side);
	}

	protected class SlaveAxisHandler implements ISlaveAxisHandler{

		private boolean oneTrig = false;
		private boolean twoTrig = false;

		@Override
		public void trigger(Direction side){
			if(mode.format == Arrangement.SINGLE){
				if(side == getFacing().getOpposite() && !locked && !rotaryMembers.isEmpty()){
					runCalc();
					triggerSlaves();
				}
			}else if(side == getFacing().getOpposite()){
				if(twoTrig){
					twoTrig = false;
					oneTrig = false;
					if(!locked && !rotaryMembers.isEmpty()){
						runCalc();
						triggerSlaves();
					}
				}else{
					oneTrig = true;
				}
			}else if(oneTrig){
				oneTrig = false;
				twoTrig = false;
				if(!locked && !rotaryMembers.isEmpty()){
					runCalc();
					triggerSlaves();
				}
			}else{
				twoTrig = true;
			}
		}

		@Override
		public HashSet<ISlaveAxisHandler> getContainedAxes(){
			HashSet<ISlaveAxisHandler> out = new HashSet<>();
			for(Pair<ISlaveAxisHandler, Direction> slave : slaves){
				out.add(slave.getLeft());
			}
			return out;
		}

		@Override
		public boolean isInvalid(){
			return tileEntityInvalid;
		}
	}
	protected class AxisHandler extends MasterAxisTileEntity.AxisHandler{

		@Override
		public void addAxisToList(ISlaveAxisHandler handler, Direction side){
			if(RotaryUtil.contains(slaveHandler, handler)){
				world.destroyBlock(pos, true);
				return;
			}
			slaves.add(Pair.of(handler, side));
		}
	}

	public enum Arrangement implements IStringSerializable{
		SINGLE(),
		DOUBLE();

		@Override
		public String getName(){
			return name().toLowerCase();
		}
	}

	public enum Mode{

		SUM(Arrangement.DOUBLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/sum.png"), (in1, in2) -> in1 + in2),
		SUB(Arrangement.DOUBLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/sub.png"), (in1, in2) -> in1 - in2),
		MULT(Arrangement.DOUBLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/mult.png"), (in1, in2) -> in1 * in2),
		DIV(Arrangement.DOUBLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/div.png"), (in1, in2) -> in2 == 0 ? 0 : in1 / in2),
		POW(Arrangement.DOUBLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/pow.png"), Math::pow),
		SQRT(Arrangement.SINGLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/sqrt.png"), (in1, in2) -> in1 < 0 ? 0 : Math.sqrt(in1)),
		SIN(Arrangement.SINGLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/sin.png"), (in1, in2) -> Math.sin(in1)),
		COS(Arrangement.SINGLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/cos.png"), (in1, in2) -> Math.cos(in1)),
		TAN(Arrangement.SINGLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/tan.png"), (in1, in2) -> Math.tan(in1)),
		ASIN(Arrangement.SINGLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/asin.png"), (in1, in2) -> in1 * in1 > 1 ? 0 : Math.asin(in1)),
		ACOS(Arrangement.SINGLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/acos.png"), (in1, in2) -> in1 * in1 > 1 ? 0 : Math.acos(in1)),
		ATAN(Arrangement.SINGLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/atan.png"), (in1, in2) -> Math.atan(in1)),
		LOG(Arrangement.SINGLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/log.png"), (in1, in2) -> in1 <= 0 ? 0 : Math.log(in1)),//Base e
		MOD(Arrangement.DOUBLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/mod.png"), (in1, in2) -> in2 == 0 ? 0 : in1 % in2),
		MIN(Arrangement.DOUBLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/min.png"), Math::min),
		MAX(Arrangement.DOUBLE, new ResourceLocation(Crossroads.MODID, "textures/gui/math/max.png"), Math::max);

		private final Arrangement format;
		private final ResourceLocation sprite;
		private final DoubleBinaryOperator function;

		Mode(Arrangement format, ResourceLocation sprite, DoubleBinaryOperator function){
			this.format = format;
			this.sprite = sprite;
			this.function = function;
		}

		public Arrangement getFormat(){
			return format;
		}

		public ResourceLocation getSprite(){
			return sprite;
		}

		public DoubleBinaryOperator getFunction(){
			return function;
		}
	}
}
