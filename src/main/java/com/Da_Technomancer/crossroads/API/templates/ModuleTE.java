package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

public abstract class ModuleTE extends TileEntity implements ITickable, IInfoTE{

	//Rotary
	protected final double[] motData = new double[4];
	//Heat
	protected boolean initHeat = false;
	protected double temp;
	protected final FluidStack[] fluids = new FluidStack[fluidTanks()];
	/**
	 * Machines overriding the fluidTanks() method should set each tank capacity in the constructor. DO NOT LEAVE THEM AS NULL
	 */
	protected final IFluidTankProperties[] fluidProps = new IFluidTankProperties[fluidTanks()];

	/**
	 * @return How many fluid tanks this machine has. Should not change at runtime, cannot be negative
	 */
	protected int fluidTanks(){
		return 0;
	}

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

	public ModuleTE(){
		super();
		if(useHeat()){
			heatHandler = new HeatHandler();
		}else{
			heatHandler = null;
		}
		if(useRotary()){
			axleHandler = new AxleHandler();
		}else{
			axleHandler = null;
		}
	}

	@Override
	public void update(){
		if(!world.isRemote && useHeat() && !initHeat){
			heatHandler.init();
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		if(useHeat()){
			chat.add("Temp: " + MiscUtil.betterRound(temp, 3) + "°C");
			chat.add("Biome Temp: " + HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
		}
		if(useRotary()){
			chat.add("Speed: " + MiscUtil.betterRound(motData[0], 3));
			chat.add("Energy: " + MiscUtil.betterRound(motData[1], 3));
			chat.add("Power: " + MiscUtil.betterRound(motData[2], 3));
			chat.add("I: " + getMoInertia() + ", Rotation Ratio: " + axleHandler.getRotationRatio());
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		for(int i = 0; i < 4; i++){
			nbt.setDouble("mot_" + i, motData[i]);
		}

		nbt.setBoolean("init_heat", initHeat);
		nbt.setDouble("temp", temp);

		for(int i = 0; i < fluids.length; i++){
			if(fluids[i] != null){
				NBTTagCompound fluidNBT = new NBTTagCompound();
				fluids[i].writeToNBT(fluidNBT);
				nbt.setTag("fluid_" + i, fluidNBT);
			}
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		for(int i = 0; i < 4; i++){
			motData[i] = nbt.getDouble("mot_" + i);
		}

		initHeat = nbt.getBoolean("init_heat");
		temp = nbt.getDouble("temp");

		for(int i = 0; i < fluids.length; i++){
			if(nbt.hasKey("fluid_" + i)){
				fluids[i] = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid_" + i));
			}
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return getCapability(cap, side) != null || super.hasCapability(cap, side);
	}

	protected final HeatHandler heatHandler;
	protected final AxleHandler axleHandler;

	protected class FluidHandler implements IFluidHandler{

		protected final int tank;

		/**
		 * @param tank The index of the FluidStack this is allowed to access. Setting a negative value will allow viewing of all tanks. Must be less than fluidTanks()
		 */
		public FluidHandler(int tank){
			this.tank = tank;
		}

		@Override
		public IFluidTankProperties[] getTankProperties(){
			if(tank < 0){
				return fluidProps;
			}

			return new IFluidTankProperties[] {fluidProps[tank]};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(tank < 0){
				//Try each tank, stop when reaching the first one that allows this fluid
				for(int i = 0; i < fluids.length; i++){
					if(resource != null && fluidProps[i].canFillFluidType(resource) && (fluids[i] == null || fluids[i].isFluidEqual(resource))){
						int change = Math.min(fluidProps[i].getCapacity() - (fluids[i] == null ? 0 : fluids[i].amount), resource.amount);
						if(doFill){
							int prevAmount = fluids[i] == null ? 0 : fluids[i].amount;
							fluids[i] = resource.copy();
							fluids[i].amount = prevAmount + change;
							markDirty();
						}
						return change;
					}
				}

			}else{
				if(resource != null && fluidProps[tank].canFillFluidType(resource) && (fluids[tank] == null || fluids[tank].isFluidEqual(resource))){
					int change = Math.min(fluidProps[tank].getCapacity() - (fluids[tank] == null ? 0 : fluids[tank].amount), resource.amount);
					if(doFill){
						int prevAmount = fluids[tank] == null ? 0 : fluids[tank].amount;
						fluids[tank] = resource.copy();
						fluids[tank].amount = prevAmount + change;
						markDirty();
					}
					return change;
				}
			}

			return 0;
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null){
				return null;
			}

			if(tank < 0){
				//Try each tank, stop when reaching the first one that allows this fluid
				for(int i = 0; i < fluids.length; i++){
					if(fluidProps[i].canDrain() && resource.isFluidEqual(fluids[i])){
						int change = Math.min(fluids[i].amount, resource.amount);

						if(doDrain){
							fluids[i].amount -= change;
							if(fluids[i].amount == 0){
								fluids[i] = null;
							}
							markDirty();
						}
						FluidStack out = resource.copy();
						out.amount = change;
						return out;
					}
				}

				return null;
			}else if(fluidProps[tank].canDrain() && resource.isFluidEqual(fluids[tank])){
				int change = Math.min(fluids[tank].amount, resource.amount);

				if(doDrain){
					fluids[tank].amount -= change;
					if(fluids[tank].amount == 0){
						fluids[tank] = null;
					}
					markDirty();
				}
				FluidStack out = resource.copy();
				out.amount = change;
				return out;
			}

			return null;
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(maxDrain == 0){
				return null;
			}

			if(tank < 0){
				//Try each tank, stop when reaching the first one that allows this fluid
				for(int i = 0; i < fluids.length; i++){
					if(fluidProps[i].canDrain() && fluids[i] != null){
						int change = Math.min(fluids[i].amount, maxDrain);
						FluidStack content = fluids[i].copy();
						content.amount = change;

						if(doDrain){
							fluids[i].amount -= change;
							if(fluids[i].amount == 0){
								fluids[i] = null;
							}
							markDirty();
						}

						return content;
					}
				}

				return null;
			}else if(fluidProps[tank].canDrain() && fluids[tank] != null){
				int change = Math.min(fluids[tank].amount, maxDrain);
				FluidStack content = fluids[tank].copy();
				content.amount = change;

				if(doDrain){
					fluids[tank].amount -= change;
					if(fluids[tank].amount == 0){
						fluids[tank] = null;
					}
					markDirty();
				}

				return content;
			}

			return null;
		}
	}

	protected class TankProperty implements IFluidTankProperties{

		protected final int tank;
		protected final int capacity;
		protected final boolean canFill;
		protected final boolean canDrain;
		protected final Predicate<Fluid> canAccept;

		/**
		 * @param tank The index of the fluidstack this relates to in the fluids array
		 * @param capacity The capacity of this tank
		 * @param canFill Whether this tank can be filled by pipes
		 * @param canDrain Whether this tank can be drained by pipes
		 */
		public TankProperty(int tank, int capacity, boolean canFill, boolean canDrain){
			this(tank, capacity, canFill, canDrain, null);
		}

		/**
		 * @param tank The index of the fluidstack this relates to in the fluids array
		 * @param capacity The capacity of this tank
		 * @param canFill Whether this tank can be filled by pipes
		 * @param canDrain Whether this tank can be drained by pipes
		 * @param canAccept A predicate controlling whether a fluid can be inserted into this tank. Ignored if canFill is false or if null
		 */
		public TankProperty(int tank, int capacity, boolean canFill, boolean canDrain, @Nullable Predicate<Fluid> canAccept){
			this.tank = tank;
			this.capacity = capacity;
			this.canFill = canFill;
			this.canDrain = canDrain;
			this.canAccept = canAccept;
		}

		@Nullable
		@Override
		public FluidStack getContents(){
			return fluids[tank];
		}

		@Override
		public int getCapacity(){
			return capacity;
		}

		@Override
		public boolean canFill(){
			return canFill;
		}

		@Override
		public boolean canDrain(){
			return canDrain;
		}

		@Override
		public boolean canFillFluidType(FluidStack fluidStack){
			return canFill && (fluidStack == null || canAccept == null || canAccept.test(fluidStack.getFluid()));
		}

		@Override
		public boolean canDrainFluidType(FluidStack fluidStack){
			return canDrain;
		}
	}

	protected class HeatHandler implements IHeatHandler{

		protected void init(){
			if(!initHeat){
				temp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				initHeat = true;
				markDirty();
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
			temp = tempIn;
			markDirty();
		}

		@Override
		public void addHeat(double heat){
			init();
			temp += heat;
			markDirty();
		}
	}

	protected class AxleHandler implements IAxleHandler{

		protected boolean connected;
		protected double rotRatio;
		protected byte updateKey;

		@Override
		public double[] getMotionData(){
			return motData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
			connected = true;
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
		public void markChanged(){
			markDirty();
		}

		@Override
		public boolean shouldManageAngle(){
			return false;
		}

		@Override
		public void disconnect(){
			connected = false;
		}
	}
}
