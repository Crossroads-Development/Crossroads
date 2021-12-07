package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.essentials.packets.ILongReceiver;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Most Crossroads machines extend this class, which provides the ability to enable highly configurable support for most things (fluids, heat, rotary) with only a few overrides
 * Machines that also use ItemStacks or UIs override the subclass, InventoryTE
 * I'd call this class boilerplate, except its 500+ lines
 */
public abstract class ModuleTE extends BlockEntity implements ITickableTileEntity, IInfoTE, ILongReceiver{

	//Rotary
	protected double energy = 0;
	// 0: angle, 1: clientW
	//Heat
	protected boolean initHeat = false;
	protected double temp;
	protected final FluidStack[] fluids = new FluidStack[fluidTanks()];
	protected final TankProperty[] fluidProps = new TankProperty[fluidTanks()];

	/**
	 * @return Whether to enable the default heat helpers. Should not change at runtime
	 */
	protected boolean useHeat(){
		return false;
	}

	/**
	 * @return Whether to enable the default rotary helpers. Should not change at runtime
	 */
	protected boolean useRotary(){
		return false;
	}

	/**
	 * Must be overriden if using tank properties for fluid support
	 * @return How many fluid tanks this machine has. Should not change at runtime, cannot be negative
	 */
	protected int fluidTanks(){
		return 0;
	}

	protected AxleHandler createAxleHandler(){
		return new AxleHandler();
	}

	protected HeatHandler createHeatHandler(){
		return new HeatHandler();
	}

	/**
	 * Creates a new fluid handler that can traverse all fluid inventories.
	 * Used for null-side fluid capability return and fluid slots in UIs
	 * @return A new fluid handler that can traverse all externally visible fluids in this block
	 */
	protected IFluidHandler createGlobalFluidHandler(){
		return new FluidHandler(-1);
	}

	public ModuleTE(BlockEntityType<? extends ModuleTE> type, BlockPos pos, BlockState state){
		super(type, pos, state);
		if(useHeat()){
			heatHandler = createHeatHandler();
			heatOpt = LazyOptional.of(() -> heatHandler);
		}else{
			heatHandler = null;
		}
		if(useRotary()){
			axleHandler = createAxleHandler();
			axleOpt = LazyOptional.of(() -> axleHandler);
		}else{
			axleHandler = null;
		}
		if(fluids.length != 0){
			globalFluidHandler = createGlobalFluidHandler();
			globalFluidOpt = LazyOptional.of(() -> globalFluidHandler);
		}else{
			globalFluidHandler = null;
		}

		Arrays.fill(fluids, FluidStack.EMPTY);
	}

	@Override
	public void serverTick(){
		if(useHeat() && !initHeat){
			heatHandler.init();
		}
		ITickableTileEntity.super.serverTick();
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(useHeat()){
			HeatUtil.addHeatInfo(chat, temp, HeatUtil.convertBiomeTemp(level, worldPosition));
		}
		if(useRotary()){
			RotaryUtil.addRotaryInfo(chat, axleHandler, true);
		}
	}

	/**
	 * Machines that override useRotary() probably want to override this
	 * @return The moment of inertia of the internal axle
	 */
	protected double getMoInertia(){
		return 0;
	}

	@Override
	public CompoundTag m_6945_(CompoundTag nbt){
		super.m_6945_(nbt);
		nbt.putDouble("mot_1", energy);

		nbt.putBoolean("init_heat", initHeat);
		nbt.putDouble("temp", temp);

		for(int i = 0; i < fluids.length; i++){
			if(fluids[i] != null){
				CompoundTag fluidNBT = new CompoundTag();
				fluids[i].writeToNBT(fluidNBT);
				nbt.put("fluid_" + i, fluidNBT);
			}
		}
		return nbt;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		energy = nbt.getDouble("mot_1");

		initHeat = nbt.getBoolean("init_heat");
		temp = nbt.getDouble("temp");

		for(int i = 0; i < fluids.length; i++){
			if(nbt.contains("fluid_" + i)){
				fluids[i] = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_" + i));
			}
		}
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		//Placeholder for future use
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		if(heatOpt != null){
			heatOpt.invalidate();
		}
		if(axleOpt != null){
			axleOpt.invalidate();
		}
		if(globalFluidOpt != null){
			globalFluidOpt.invalidate();
		}
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		if(identifier == 0){
			//No-op (removed code)
			//Reserving identifier 0 for future use
		}
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		//Return the global optional for internal-side (null) checks
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side == null && globalFluidOpt != null){
			return (LazyOptional<T>) globalFluidOpt;
		}
		if(cap == Capabilities.HEAT_CAPABILITY && side == null && useHeat()){
			return (LazyOptional<T>) heatOpt;
		}
		if(cap == Capabilities.AXLE_CAPABILITY && side == null && useRotary()){
			return (LazyOptional<T>) axleOpt;
		}
		return super.getCapability(cap, side);
	}

	protected HeatHandler heatHandler;
	protected LazyOptional<IHeatHandler> heatOpt;
	protected AxleHandler axleHandler;
	protected LazyOptional<IAxleHandler> axleOpt;
	protected IFluidHandler globalFluidHandler;
	protected LazyOptional<IFluidHandler> globalFluidOpt;

	protected class FluidHandler implements IFluidHandler{

		protected final int tank;

		/**
		 * @param tank The index of the FluidStack this is allowed to access. Setting a negative value will allow viewing of all tanks. Must be less than fluidTanks()
		 */
		public FluidHandler(int tank){
			this.tank = tank;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action){
			if(tank < 0){
				//Try each tank, stop when reaching the first one that allows this fluid
				for(int i = 0; i < fluids.length; i++){
					if(!resource.isEmpty() && isFluidValid(i, resource) && (fluids[i].isEmpty() || fluids[i].isFluidEqual(resource))){
						int change = Math.min(fluidProps[i].capacity - fluids[i].getAmount(), resource.getAmount());
						if(action == FluidAction.EXECUTE && change > 0){
							int prevAmount = fluids[i].getAmount();
							fluids[i] = resource.copy();
							fluids[i].setAmount(prevAmount + change);
							setChanged();
						}
						return change;
					}
				}
			}else{
				if(!resource.isEmpty() && isFluidValid(tank, resource) && (fluids[tank].isEmpty() || fluids[tank].isFluidEqual(resource))){
					int change = Math.min(fluidProps[tank].capacity - fluids[tank].getAmount(), resource.getAmount());
					if(action == FluidAction.EXECUTE && change >= 0){
						int prevAmount = fluids[tank].getAmount();
						fluids[tank] = resource.copy();
						fluids[tank].setAmount(prevAmount + change);
						setChanged();
					}
					return change;
				}
			}

			return 0;
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action){
			if(resource.isEmpty()){
				return FluidStack.EMPTY;
			}

			if(tank < 0){
				//Try each tank, stop when reaching the first one that allows this fluid
				for(int i = 0; i < fluids.length; i++){
					if(fluidProps[i].canDrain && resource.isFluidEqual(fluids[i])){
						int change = Math.min(fluids[i].getAmount(), resource.getAmount());

						if(action == FluidAction.EXECUTE && change >= 0){
							fluids[i].shrink(change);
							setChanged();
						}
						FluidStack out = resource.copy();
						out.setAmount(change);
						return out;
					}
				}

				return FluidStack.EMPTY;
			}else if(fluidProps[tank].canDrain && resource.isFluidEqual(fluids[tank])){
				int change = Math.min(fluids[tank].getAmount(), resource.getAmount());

				if(action == FluidAction.EXECUTE){
					fluids[tank].shrink(change);
					setChanged();
				}
				FluidStack out = resource.copy();
				out.setAmount(change);
				return out;
			}

			return FluidStack.EMPTY;
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			if(maxDrain <= 0){
				return FluidStack.EMPTY;
			}

			if(tank < 0){
				//Try each tank, stop when reaching the first one that allows this fluid
				for(int i = 0; i < fluids.length; i++){
					if(fluidProps[i].canDrain && !fluids[i].isEmpty()){
						int change = Math.min(fluids[i].getAmount(), maxDrain);
						FluidStack content = fluids[i].copy();
						content.setAmount(change);

						if(action == FluidAction.EXECUTE){
							fluids[i].shrink(change);
							setChanged();
						}

						return content;
					}
				}

				return FluidStack.EMPTY;
			}else if(fluidProps[tank].canDrain && !fluids[tank].isEmpty()){
				int change = Math.min(fluids[tank].getAmount(), maxDrain);
				FluidStack content = fluids[tank].copy();
				content.setAmount(change);

				if(action == FluidAction.EXECUTE){
					fluids[tank].shrink(change);
					setChanged();
				}

				return content;
			}

			return FluidStack.EMPTY;
		}

		@Override
		public int getTanks(){
			return tank < 0 ? fluids.length : 1;
		}

		protected int calcTank(int rawTank){
			if(tank < 0){
				return rawTank;
			}else{
				return tank;
			}
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tank){
			return fluids[calcTank(tank)];
		}

		@Override
		public int getTankCapacity(int tank){
			return fluidProps[calcTank(tank)].capacity;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
			return fluidProps[calcTank(tank)].canFill && fluidProps[calcTank(tank)].canAccept.test(stack.getFluid());
		}
	}

	/**
	 * A version of the FluidHandler which also acts as an IFluidTank- allowing pipes to do bidirectional access and having a stricter contract
	 * Does not allow accessing multiple internal tanks
	 */
	protected class FluidTankHandler extends FluidHandler implements IFluidTank{

		/**
		 * @param tank The index of the FluidStack this is allowed to access. Does not allow setting a negative value or accessing more than one tank. Must be less than fluidTanks()
		 */
		public FluidTankHandler(int tank){
			super(tank);
			assert tank >= 0;
		}

		@Nonnull
		@Override
		public FluidStack getFluid(){
			return fluids[tank];
		}

		@Override
		public int getFluidAmount(){
			return fluids[tank].getAmount();
		}

		@Override
		public int getCapacity(){
			return fluidProps[tank].capacity;
		}

		@Override
		public boolean isFluidValid(FluidStack stack){
			return fluidProps[tank].canFill && fluidProps[tank].canAccept.test(stack.getFluid());
		}
	}

	protected static class TankProperty{

		public final int capacity;
		public final boolean canFill;
		public final boolean canDrain;
		public final Predicate<Fluid> canAccept;

		/**
		 * @param capacity The capacity of this tank
		 * @param canFill Whether this tank can be filled by pipes
		 * @param canDrain Whether this tank can be drained by pipes
		 */
		public TankProperty(int capacity, boolean canFill, boolean canDrain){
			this(capacity, canFill, canDrain, f -> canFill);
		}

		/**
		 * @param capacity The capacity of this tank
		 * @param canFill Whether this tank can be filled by pipes
		 * @param canDrain Whether this tank can be drained by pipes
		 * @param canAccept A predicate controlling whether a fluid can be inserted into this tank
		 */
		public TankProperty(int capacity, boolean canFill, boolean canDrain, @Nonnull Predicate<Fluid> canAccept){
			this.capacity = capacity;
			this.canFill = canFill;
			this.canDrain = canDrain;
			this.canAccept = canAccept;
		}
	}

	protected class HeatHandler implements IHeatHandler{

		public void init(){
			if(!initHeat){
				temp = HeatUtil.convertBiomeTemp(level, worldPosition);
				initHeat = true;
				setChanged();
			}
		}

		@Override
		public double getTemp(){
			init();
			return temp;
		}

		@Override
		public void setTemp(double tempIn){
			initHeat = true;
			temp = Math.max(HeatUtil.ABSOLUTE_ZERO, tempIn);
			setChanged();
		}

		@Override
		public void addHeat(double heat){
			init();
			temp = Math.max(HeatUtil.ABSOLUTE_ZERO, temp + heat);
			setChanged();
		}
	}

	protected class AxleHandler implements IAxleHandler{

		public double rotRatio;
		public byte updateKey;
		public boolean renderOffset;
		public IAxisHandler axis;

		@Override
		public double getSpeed(){
			return axis == null ? 0 : rotRatio * axis.getBaseSpeed();
		}

		@Override
		public double getEnergy(){
			return energy;
		}

		@Override
		public void setEnergy(double newEnergy){
			energy = newEnergy;
			setChanged();
		}

		@Override
		public void propagate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			this.renderOffset = renderOffset;
			updateKey = key;
			axis = masterIn;
		}

		@Override
		public double getMoInertia(){
			return ModuleTE.this.getMoInertia();
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public float getAngle(float partialTicks){
			return axis == null ? 0 : axis.getAngle(rotRatio, partialTicks, renderOffset, 22.5F);
		}

		@Override
		public void disconnect(){
			axis = null;
		}
	}
}
