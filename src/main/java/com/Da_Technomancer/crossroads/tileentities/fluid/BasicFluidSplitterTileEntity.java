package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class BasicFluidSplitterTileEntity extends ModuleTE{

	public static final int[] MODES = {1, 2, 3};
	private int mode = 1;

	public int increaseMode(){
		mode++;
		mode %= MODES.length;
		markDirty();
		return mode;
	}

	public BasicFluidSplitterTileEntity(){
		super();
		fluidProps[0] = new TankProperty(0, CAPACITY, false, true);//Bottom
		fluidProps[1] = new TankProperty(1, CAPACITY, false, true);//Top
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}

	private static final int CAPACITY = 2_000;

	private final FluidHandler downHandler = new FluidHandler(0);
	private final FluidHandler upHandler = new FluidHandler(1);
	private final InHandler inHandler = new InHandler();
	private final FluidHandler centerHandler = new FluidHandler(-1);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(side == null){
				return (T) centerHandler;
			}

			return side.getAxis() != Direction.Axis.Y ? (T) inHandler : side == Direction.UP ? (T) upHandler : (T) downHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("mode", mode);
		nbt.putInt("transfered", transfered);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		mode = nbt.getInt("mode");
		transfered = nbt.getInt("transfered");
	}

	protected int getPortion(){
		return MODES[mode];
	}

	protected int getBase(){
		return 4;
	}

	private int transfered = 0;

	private class InHandler implements IFluidHandler{

		private final TankProperty[] properties = new TankProperty[] {new TankProperty(0, CAPACITY, true, false), new TankProperty(1, CAPACITY, true, false)};

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return properties;
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource == null){
				return 0;
			}

			int portion = getPortion();
			int base = getBase();

			int accepted = Math.max(0, Math.min(resource.amount, portion == 0 ? fluids[1] != null && !fluids[1].isFluidEqual(resource) ? 0 : CAPACITY - (fluids[1] == null ? 0 : fluids[1].amount) : portion == base ? fluids[0] != null && !fluids[0].isFluidEqual(resource) ? 0 : CAPACITY - (fluids[0] == null ? 0 : fluids[0].amount) : Math.min(fluids[0] != null && !fluids[0].isFluidEqual(resource) ? 0 : ((base * (CAPACITY - (fluids[0] == null ? 0 : fluids[0].amount))) / portion), fluids[1] != null && !fluids[1].isFluidEqual(resource) ? 0 : ((base * (CAPACITY - (fluids[1] == null ? 0 : fluids[1].amount))) / (base - portion)))));
			int goDown = (portion * (accepted / base)) + (transfered >= portion ? 0 : Math.min(portion - transfered, accepted % base)) + Math.max(0, Math.min(portion, (accepted % base) + transfered - base));
			int goUp = accepted - goDown;

			if(doFill && accepted != 0){
				fluids[0] = new FluidStack(resource.getFluid(), goDown + (fluids[0] == null ? 0 : fluids[0].amount), resource.tag);
				fluids[1] = new FluidStack(resource.getFluid(), goUp + (fluids[1] == null ? 0 : fluids[1].amount), resource.tag);
				transfered += accepted % base;
				transfered %= base;
			}

			return accepted;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			return null;
		}
	}
} 
