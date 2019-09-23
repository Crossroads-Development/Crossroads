package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.packets.ILongReceiver;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Most Crossroads machines extend this class, which provides boilerplate for simple rotary, heat, and fluid support that can be enabled via overriding a few methods and setting a few fields
 */
public abstract class ModuleTE extends TileEntity implements ITickableTileEntity, IInfoTE, ILongReceiver{

	//Rotary
	protected final double[] motData = new double[4];
	// 0: angle, 1: clientW
	// Initialized by the constructor of AngleAxleHandler, making its use conditional upon the use of AngleAxleHandler
	protected float[] angleW = null;
	//Heat
	protected boolean initHeat = false;
	protected double temp;
	protected final FluidStack[] fluids = new FluidStack[fluidTanks()];
	protected final TankProperty[] fluidProps = new TankProperty[fluidTanks()];

	/**
	 * @return Whether to enable the default heat helpers. Should not change at runtime
	 */
	protected abstract boolean useHeat();

	/**
	 * @return Whether to enable the default rotary helpers. Should not change at runtime
	 */
	protected abstract boolean useRotary();

	/**
	 * @return How many fluid tanks this machine has. Should not change at runtime, cannot be negative
	 */
	protected abstract int fluidTanks();

	/**
	 * @return An array of length fluidTanks() defining properties for each tank
	 */
	protected abstract TankProperty[] createFluidTanks();

	protected AxleHandler createAxleHandler(){
		return new AxleHandler();
	}

	protected HeatHandler createHeatHandler(){
		return new HeatHandler();
	}

	public ModuleTE(TileEntityType<? extends ModuleTE> type){
		super(type);
		if(useHeat()){
			heatHandler = createHeatHandler();
		}else{
			heatHandler = null;
		}
		if(useRotary()){
			axleHandler = createAxleHandler();
		}else{
			axleHandler = null;
		}
		for(int i = 0; i < fluids.length; i++){
			fluids[i] = FluidStack.EMPTY;
		}
	}

	@Override
	public void tick(){
		if(world.isRemote){
			if(useRotary() && angleW != null){
				angleW[0] += angleW[1] * 9D / Math.PI;
			}
		}else{
			if(useHeat() && !initHeat){
				heatHandler.init();
			}
		}
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		/* TODO localize
		if(useHeat()){
			chat.add("Temp: " + MiscUtil.betterRound(temp, 3) + "°C");
			chat.add("Biome Temp: " + HeatUtil.convertBiomeTemp(world, pos) + "°C");
		}
		if(useRotary()){
			chat.add("Speed: " + MiscUtil.betterRound(motData[0], 3));
			chat.add("Energy: " + MiscUtil.betterRound(motData[1], 3));
			chat.add("Power: " + MiscUtil.betterRound(motData[2], 3));
			chat.add("I: " + getMoInertia() + ", Rotation Ratio: " + axleHandler.getRotationRatio());
		}
		*/
	}

	/**
	 * Machines that override useRotary() probably want to override this
	 * @return The moment of inertia of the internal axle
	 */
	protected double getMoInertia(){
		return 0;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		for(int i = 0; i < 4; i++){
			nbt.putDouble("mot_" + i, motData[i]);
		}
		if(angleW != null){
			nbt.putFloat("ang_w_0", angleW[0]);
			nbt.putFloat("ang_w_1", angleW[1]);
		}

		nbt.putBoolean("init_heat", initHeat);
		nbt.putDouble("temp", temp);

		for(int i = 0; i < fluids.length; i++){
			if(fluids[i] != null){
				CompoundNBT fluidNBT = new CompoundNBT();
				fluids[i].writeToNBT(fluidNBT);
				nbt.put("fluid_" + i, fluidNBT);
			}
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		for(int i = 0; i < 4; i++){
			motData[i] = nbt.getDouble("mot_" + i);
		}
		if(angleW != null){
			angleW[0] = nbt.getFloat("ang_w_0");
			angleW[1] = nbt.getFloat("ang_w_1");
		}

		initHeat = nbt.getBoolean("init_heat");
		temp = nbt.getDouble("temp");

		for(int i = 0; i < fluids.length; i++){
			if(nbt.contains("fluid_" + i)){
				fluids[i] = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_" + i));
			}
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		if(angleW != null){
			nbt.putFloat("ang_w_0", angleW[0]);
			nbt.putFloat("ang_w_1", angleW[1]);
		}
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == 0 && angleW != null){
			float angle = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
			angleW[0] = Math.abs(angle - angleW[0]) > 5F ? angle : angleW[0];
			angleW[1] = Float.intBitsToFloat((int) (message >>> 32L));
		}
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
		public int fill(FluidStack resource, FluidAction action){
			if(tank < 0){
				//Try each tank, stop when reaching the first one that allows this fluid
				for(int i = 0; i < fluids.length; i++){
					if(!resource.isEmpty() && isFluidValid(i, resource) && (fluids[i].isEmpty() || fluids[i].isFluidEqual(resource))){
						int change = Math.min(fluidProps[i].capacity - fluids[i].getAmount(), resource.getAmount());
						if(action == FluidAction.EXECUTE){
							int prevAmount = fluids[i].getAmount();
							fluids[i] = resource.copy();
							fluids[i].setAmount(prevAmount + change);
							markDirty();
						}
						return change;
					}
				}
			}else{
				if(!resource.isEmpty() && isFluidValid(tank, resource) && (fluids[tank].isEmpty() || fluids[tank].isFluidEqual(resource))){
					int change = Math.min(fluidProps[tank].capacity - fluids[tank].getAmount(), resource.getAmount());
					if(action == FluidAction.EXECUTE){
						int prevAmount = fluids[tank].getAmount();
						fluids[tank] = resource.copy();
						fluids[tank].setAmount(prevAmount + change);
						markDirty();
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

						if(action == FluidAction.EXECUTE){
							fluids[i].shrink(change);
							markDirty();
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
					markDirty();
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
			if(maxDrain == 0){
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
							markDirty();
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
					markDirty();
				}

				return content;
			}

			return FluidStack.EMPTY;
		}

		@Override
		public int getTanks(){
			return fluids.length;
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tank){
			return fluids[tank];
		}

		@Override
		public int getTankCapacity(int tank){
			return fluidProps[tank].capacity;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
			return fluidProps[tank].canFill && fluidProps[tank].canAccept.test(stack.getFluid());
		}
	}

	protected static class TankProperty{

		protected final int capacity;
		protected final boolean canFill;
		protected final boolean canDrain;
		protected final Predicate<Fluid> canAccept;

		/**
		 * @param capacity The capacity of this tank
		 * @param canFill Whether this tank can be filled by pipes
		 * @param canDrain Whether this tank can be drained by pipes
		 */
		public TankProperty(int capacity, boolean canFill, boolean canDrain){
			this(capacity, canFill, canDrain, f -> true);
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
				temp = HeatUtil.convertBiomeTemp(world, pos);
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

		public double rotRatio;
		public byte updateKey;
		public boolean renderOffset;
		public IAxisHandler axis;

		@Override
		public double[] getMotionData(){
			return motData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
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
		public void markChanged(){
			markDirty();
		}

		@Override
		public float getAngle(float partialTicks){
			return 0;
		}

		@Override
		public boolean shouldManageAngle(){
			return false;
		}

		@Override
		public void disconnect(){
			axis = null;
		}
	}

	protected class AngleAxleHandler extends AxleHandler{

		public AngleAxleHandler(){
			angleW = new float[2];
		}

		@Override
		public boolean shouldManageAngle(){
			return true;
		}

		@Override
		public float getAngle(float partialTicks){
			return axis == null ? 0 : axis.getAngle(rotRatio, partialTicks, renderOffset, 22.5F);
		}
	}
}
