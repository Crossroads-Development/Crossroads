package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickableTileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class FluidTubeTileEntity extends TileEntity implements ITickableTileEntity, IIntReceiver{

	protected static final int CAPACITY = 2000;

	/**
	 * 0: Locked
	 * 1: Normal
	 * 2: Out
	 * 3: In
	 */
	protected Integer[] connectMode = null;
	private final Boolean[] hasMatch = new Boolean[6];
	private FluidStack content = null;
	private IFluidHandler[] outHandlers = null;

	public void markSideChanged(int index){
		init();
		markDirty();
		ModPackets.network.sendToAllAround(new SendIntToClient((byte) index, hasMatch[index] != null && hasMatch[index] ? connectMode[index] : 0, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	public Integer[] getConnectMode(boolean forRender){
		init();
		if(forRender && !world.isRemote){
			Integer[] out = new Integer[6];
			for(int i = 0; i < 6; i++){
				out[i] = hasMatch[i] != null && hasMatch[i] ? connectMode[i] : 0;
			}
			return out;
		}
		return connectMode;
	}

	@Override
	public void receiveInt(byte identifier, int message, @Nullable ServerPlayerEntity sender){
		if(identifier < 6){
			init();
			connectMode[identifier] = message;
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	protected void init(){
		if(connectMode == null){
			connectMode = world.isRemote ? new Integer[] {0, 0, 0, 0, 0, 0} : new Integer[] {1, 1, 1, 1, 1, 1};
		}
	}

	@Override
	public void update(){
		init();

		if(world.isRemote){
			return;
		}

		IFluidHandler[] handlers = new IFluidHandler[6];
		outHandlers = new IFluidHandler[6];

		for(Direction dir : Direction.values()){
			int ind = dir.getIndex();

			if(connectMode[ind] != 0){
				TileEntity te = world.getTileEntity(pos.offset(dir));
				IFluidHandler handler;
				if(te != null && (handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())) != null){
					if(hasMatch[ind] == null){
						boolean canFill = handler.getTankProperties().length != 0 && handler.getTankProperties()[0].canFill();
						boolean canDrain = handler.getTankProperties().length != 0 && handler.getTankProperties()[0].canDrain();
						connectMode[ind] = canDrain ? canFill ? 1 : 3 : canFill ? 2 : 1;
						hasMatch[ind] = true;
						markSideChanged(ind);
					}
					if(!hasMatch[ind]){
						hasMatch[ind] = true;
						markSideChanged(ind);
					}

					handlers[ind] = handler;

					if(connectMode[ind] == 2){
						outHandlers[ind] = handler;
					}
				}else if(hasMatch[ind] != null && hasMatch[ind]){
					hasMatch[ind] = false;
					markSideChanged(ind);
				}
			}
		}


		//Before we can proceed, we need to know which fluid we're moving
		Fluid fluid = content == null ? null : content.getFluid();

		//We iterate over all input and bi connections until we find a fluid
		if(fluid == null){
			for(int i = 0; i < 6; i++){
				if(handlers[i] != null && connectMode[i] != 2){
					FluidStack drainSample = handlers[i].drain(1, false);
					if(drainSample != null){
						fluid = drainSample.getFluid();
						break;
					}
				}
			}
		}

		//If fluid is still null at this point, there must not be any fluid to move
		if(fluid == null){
			return;
		}

		long sumInput = 0;
		long sumOutput = 0;
		long sumBi = 0;
		long sumBiCap = 0;
		int[] biConts = new int[6];
		int[] biCaps = new int[6];

		final FluidStack fillTestStack = new FluidStack(fluid, Integer.MAX_VALUE);//We re-use the same fluidstack for capacity fill tests to save initializing the same stack each loop

		//In order to do all movements in one synchronized, order independent action, we need to build up information about all connections
		for(int i = 0; i < 6; i++){
			if(handlers[i] != null){
				switch(connectMode[i]){
					case 1://bi
						FluidStack drainSample = handlers[i].drain(Integer.MAX_VALUE, false);
						if(drainSample == null || drainSample.getFluid() == fluid){
							int fillSample = handlers[i].fill(fillTestStack, false);
							if(fillSample == 0 && drainSample == null){//It's blocking all interaction. The fluid doesn't match, or it just doesn't allow fluid movement
								handlers[i] = null;
							}else{
								biConts[i] = drainSample == null ? 0 : drainSample.amount;
								biCaps[i] = biConts[i] + fillSample;
								sumBi += biConts[i];
								sumBiCap += biCaps[i];
							}

						}else{
							handlers[i] = null;//Not the same fluid type
						}
						break;
					case 2://output
						int fillSample = handlers[i].fill(fillTestStack, false);
						if(fillSample == 0){
							handlers[i] = null;
						}else{
							sumOutput += fillSample;
						}
						break;
					case 3://input
						FluidStack testDrain = handlers[i].drain(Integer.MAX_VALUE, false);
						if(testDrain == null || testDrain.getFluid() != fluid){
							handlers[i] = null;
						}else{
							sumInput += testDrain.amount;
						}
						break;
				}
			}
		}

		if(sumOutput < sumInput){
			long biFill = Math.min(sumInput - sumOutput, CAPACITY + sumBiCap - sumBi - (content == null ? 0 : content.amount));
			long toDrain = biFill + sumOutput;
			biFill += sumBi + (content == null ? 0 : content.amount);
			double tarPressure = biFill;
			tarPressure /= CAPACITY + sumBiCap;
			for(int i = 0; i < 6; i++){
				if(handlers[i] != null){
					switch(connectMode[i]){
						case 1://bi
							biFill -= biConts[i];
							if((double) biConts[i] / biCaps[i] < tarPressure){
								biFill -= handlers[i].fill(new FluidStack(fluid, (int) (tarPressure * biCaps[i] - biConts[i])), true);
							}else{
								FluidStack drained = handlers[i].drain((int) (biConts[i] - tarPressure * biCaps[i]), true);
								biFill += drained == null ? 0 : drained.amount;
							}
							break;
						case 2://output
							handlers[i].fill(fillTestStack, true);//Completely fill
							break;
						case 3://input
							if(toDrain > 0){
								FluidStack drained = handlers[i].drain((int) toDrain, true);
								toDrain -= drained == null ? 0 : drained.amount;
							}
							break;
					}
				}
			}

			if(biFill <= 0){
				content = null;
				markDirty();
			}else{
				content = new FluidStack(fluid, (int) biFill);
			}
		}else{
			long fromBi = Math.min(sumOutput - sumInput, sumBi + (content == null ? 0 : content.amount));//The qty being drained from bi to fill out.
			long biFill = sumBi - fromBi + (content == null ? 0 : content.amount);
			double tarPressure = biFill;
			tarPressure /= CAPACITY + sumBiCap;
			long toFill = sumInput + fromBi;

			for(int i = 0; i < 6; i++){
				if(handlers[i] != null){
					switch(connectMode[i]){
						case 1://bi
							biFill -= biConts[i];
							if((double) biConts[i] / biCaps[i] < tarPressure){
								biFill -= handlers[i].fill(new FluidStack(fluid, (int) (tarPressure * biCaps[i] - biConts[i])), true);
							}else{
								FluidStack drained = handlers[i].drain((int) (biConts[i] - tarPressure * biCaps[i]), true);
								biFill += drained == null ? 0 : drained.amount;
							}
							break;
						case 2://output
							if(toFill > 0){
								toFill -= handlers[i].fill(new FluidStack(fluid, (int) toFill), true);
							}
							break;
						case 3://input
							handlers[i].drain(Integer.MAX_VALUE, true);
							break;
					}
				}
			}

			if(biFill <= 0){
				content = null;
				markDirty();
			}else{
				content = new FluidStack(fluid, (int) biFill);
			}
		}
	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);
		connectMode = new Integer[] {0, 0, 0, 0, 0, 0};
		for(int i = 0; i < 6; i++){
			connectMode[i] = Math.max(0, nbt.hasKey("mode_" + i) ? nbt.getInteger("mode_" + i) : 1);
			byte match = nbt.getByte("match_" + i);
			hasMatch[i] = match == 0 ? null : match != 1;
		}
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		super.writeToNBT(nbt);
		if(content != null){
			content.writeToNBT(nbt);
		}
		if(connectMode != null){
			for(int i = 0; i < 6; i++){
				nbt.setInteger("mode_" + i, connectMode[i]);
				nbt.setByte("match_" + i, hasMatch[i] == null ? 0 : hasMatch[i] ? (byte) 2 : (byte) 1);
			}
		}

		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT out = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			out.setInteger("mode_" + i, hasMatch[i] != null && hasMatch[i] ? connectMode[i] : 0);
		}
		return out;
	}

	protected final IFluidHandler mainHandler = new MainFluidHandler();
	protected final IFluidHandler inHandler = new InFluidHandler();
	protected final IFluidHandler outHandler = new OutFluidHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction side){
		init();
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && connectMode != null && (side == null || connectMode[side.getIndex()] != 0)){
			return side == null || connectMode[side.getIndex()] == 1 ? (T) mainHandler : connectMode[side.getIndex()] == 2 ? (T) outHandler : (T) inHandler;
		}

		return super.getCapability(capability, side);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable Direction side){
		init();
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && connectMode != null && (side == null || connectMode[side.getIndex()] != 0)){
			return true;
		}
		return super.hasCapability(capability, side);
	}

	private class OutFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(content, CAPACITY, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){

			if(resource != null && resource.isFluidEqual(content)){
				int change = Math.min(content.amount, resource.amount);
				Fluid fluid = content.getFluid();

				if(doDrain && change != 0){
					content.amount -= change;
					if(content.amount == 0){
						content = null;
					}
					markDirty();
				}

				return new FluidStack(fluid, change);
			}else{
				return null;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(content == null || maxDrain == 0){
				return null;
			}

			int change = Math.min(content.amount, maxDrain);
			Fluid fluid = content.getFluid();

			if(doDrain && change != 0){
				content.amount -= change;
				if(content.amount == 0){
					content = null;
				}
				markDirty();
			}

			return new FluidStack(fluid, change);
		}
	}

	private class InFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(content, CAPACITY, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource != null && (content == null || resource.isFluidEqual(content))){
				int change = Math.min(CAPACITY - (content == null ? 0 : content.amount), resource.amount);

				if(doFill && change != 0){
					content = new FluidStack(resource.getFluid(), (content == null ? 0 : content.amount) + change);
					markDirty();
				}

				return change;
			}else{
				return 0;
			}
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

	private class MainFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(content, CAPACITY)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(resource != null && (content == null || resource.isFluidEqual(content))){
				int change = Math.min(CAPACITY - (content == null ? 0 : content.amount), resource.amount);

				if(doFill && change != 0){
					content = new FluidStack(resource.getFluid(), (content == null ? 0 : content.amount) + change);
					markDirty();
				}

				return change;
			}else{
				return 0;
			}
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource != null && resource.isFluidEqual(content) && outHandlers != null){
				for(IFluidHandler outHandler : outHandlers){
					if(outHandler != null && outHandler.fill(resource, false) != 0){
						return null;//We refuse to allow extracting liquid out dual connections while this pipe could still push out output-only connections. We therefore prioritize output-only connections over dual connections (similarly to the sorting hopper)
					}
				}

				int change = Math.min(content.amount, resource.amount);
				Fluid fluid = content.getFluid();

				if(doDrain && change != 0){
					content.amount -= change;
					if(content.amount == 0){
						content = null;
					}
					markDirty();
				}

				return new FluidStack(fluid, change);
			}else{
				return null;
			}
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(content == null || maxDrain == 0 || outHandlers == null){
				return null;
			}

			for(IFluidHandler outHandler : outHandlers){
				if(outHandler != null && outHandler.fill(content, false) != 0){
					return null;//We refuse to allow extracting liquid out dual connections while this pipe could still push out output-only connections. We therefore prioritize output-only connections over dual connections (similarly to the sorting hopper)
				}
			}

			Fluid fluid = content.getFluid();
			int change = Math.min(content.amount, maxDrain);
			if(doDrain && change != 0){
				content.amount -= change;
				if(content.amount == 0){
					content = null;
				}
				markDirty();
			}

			return new FluidStack(fluid, change);
		}
	}
}
