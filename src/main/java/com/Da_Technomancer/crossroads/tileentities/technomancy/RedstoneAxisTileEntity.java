package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.AxisTypes;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.TickPriority;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class RedstoneAxisTileEntity extends MasterAxisTileEntity{

	@ObjectHolder("redstone_axis")
	private static TileEntityType<RedstoneAxisTileEntity> type = null;

	private int redstone;
	private float circRedstone;

	public RedstoneAxisTileEntity(){
		super(type);
	}

	@Override
	protected void runCalc(){
		Direction facing = getFacing();

		double baseSpeed = getSetting();
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

	private float getSetting(){
		if(!builtConnections){
			buildConnections();
		}
		return Math.max(redstone, circRedstone);
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
		nbt.putFloat("circ_reds", circRedstone);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		redstone = nbt.getInt("reds");
		circRedstone = nbt.getFloat("circ_reds");
	}

	@Override
	public void remove(){
		super.remove();
		redsOpt.invalidate();
	}

	//Everything below this line is generic input-only circuit code; Note to self: Find a way to centralize this mess

	private LazyOptional<IRedstoneHandler> redsOpt = LazyOptional.of(CircHandler::new);
	private WeakReference<LazyOptional<IRedstoneHandler>> redsRef = new WeakReference<>(redsOpt);

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir){
		if(cap == RedstoneUtil.REDSTONE_CAPABILITY){
			return (LazyOptional<T>) redsOpt;
		}
		return super.getCapability(cap, dir);
	}

	private ArrayList<WeakReference<LazyOptional<IRedstoneHandler>>> sources = new ArrayList<>(1);
	private boolean builtConnections = false;

	public void buildConnections() {
		//Rebuild the sources list
		if(!world.isRemote){
			builtConnections = true;
			ArrayList<WeakReference<LazyOptional<IRedstoneHandler>>> preSrc = new ArrayList<>(sources.size());
			preSrc.addAll(sources);
			//Wipe old sources
			sources.clear();

			for(Direction checkDir : Direction.values()){
				TileEntity te = world.getTileEntity(pos.offset(checkDir));
				IRedstoneHandler otherHandler;
				if(te != null && (otherHandler = BlockUtil.get(te.getCapability(RedstoneUtil.REDSTONE_CAPABILITY, checkDir.getOpposite()))) != null){
					otherHandler.requestSrc(redsRef, 0, checkDir.getOpposite(), checkDir);
				}
			}

			//if sources changed, schedule an update
			if(sources.size() != preSrc.size() || !sources.containsAll(preSrc)){
				world.getPendingBlockTicks().scheduleTick(pos, ESBlocks.redstoneTransmitter, RedstoneUtil.DELAY, TickPriority.NORMAL);
			}
		}
	}

	private class CircHandler implements IRedstoneHandler{

		@Override
		public float getOutput(){
			return 0;
		}

		@Override
		public void findDependents(WeakReference<LazyOptional<IRedstoneHandler>> weakReference, int i, Direction fromSide, Direction nominalSide){
			LazyOptional<IRedstoneHandler> srcOption = weakReference.get();
			if(srcOption != null && srcOption.isPresent()){
				IRedstoneHandler srcHandler = BlockUtil.get(srcOption);
				srcHandler.addDependent(redsRef, nominalSide);
				if(!sources.contains(weakReference)){
					sources.add(weakReference);
				}
			}
		}

		@Override
		public void requestSrc(WeakReference<LazyOptional<IRedstoneHandler>> weakReference, int i, Direction direction, Direction direction1){
			//No-op
		}

		@Override
		public void addSrc(WeakReference<LazyOptional<IRedstoneHandler>> weakReference, Direction direction){
			if(!sources.contains(weakReference)){
				sources.add(weakReference);
				notifyInputChange(weakReference);
			}
		}

		@Override
		public void addDependent(WeakReference<LazyOptional<IRedstoneHandler>> weakReference, Direction direction){
			//No-op
		}

		@Override
		public void notifyInputChange(WeakReference<LazyOptional<IRedstoneHandler>> weakReference){
			float prevCirc = circRedstone;
			circRedstone = 0;
			for(int i = 0; i < sources.size(); i++){
				WeakReference<LazyOptional<IRedstoneHandler>> src = sources.get(i);
				LazyOptional<IRedstoneHandler> srcOpt;
				if((srcOpt = src.get()) != null && srcOpt.isPresent()){
					circRedstone = Math.max(circRedstone, Math.round(RedstoneUtil.sanitize(srcOpt.orElseThrow(NullPointerException::new).getOutput())));
				}else{
					//Remove invalid entries to speed up future checks
					sources.remove(i);
					i--;
				}
			}
			if(circRedstone != prevCirc){
				markDirty();
			}
		}
	}
}
