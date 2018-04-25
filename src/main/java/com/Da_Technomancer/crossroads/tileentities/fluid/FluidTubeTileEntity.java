package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class FluidTubeTileEntity extends TileEntity implements ITickable, IIntReceiver{

	/**
	 * 0: Locked
	 * 1: Normal
	 * 2: Out
	 * 3: In
	 */
	private Integer[] connectMode = null;
	private final boolean[] hasMatch = new boolean[6];

	private static final int CAPACITY = 2000;
	private FluidStack content = null;

	public void markSideChanged(int index){
		init();
		markDirty();
		ModPackets.network.sendToAllAround(new SendIntToClient(index, hasMatch[index] ? connectMode[index] : 0, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	public Integer[] getConnectMode(boolean forRender){
		init();
		if(forRender && !world.isRemote){
			Integer[] out = new Integer[6];
			for(int i = 0; i < 6; i++){
				out[i] = hasMatch[i] ? connectMode[i] : 0;
			}
			return out;
		}
		return connectMode;
	}

	public void receiveInt(int identifier, int message, @Nullable EntityPlayerMP sender){
		if(identifier < 6){
			init();
			connectMode[identifier] = message;
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	private void init(){
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

		for(EnumFacing dir : EnumFacing.values()){
			int ind = dir.getIndex();
			TileEntity te = world.getTileEntity(pos.offset(dir));

			if(connectMode[ind] != 0){
				if(te == null || !te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())){
					if(hasMatch[ind]){
						hasMatch[ind] = false;
						markSideChanged(ind);
					}
				}else if(!hasMatch[ind]){
					hasMatch[ind] = true;
					markSideChanged(ind);
				}
			}

			if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())){
				transfer(te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite()), ind);
			}
		}
	}

	private void transfer(IFluidHandler handler, int dir){
		if(connectMode[dir] == 2){
			if(content != null){
				content.amount -= handler.fill(content, true);
				if(content.amount <= 0){
					content = null;
				}
				markDirty();
			}
		}else if(connectMode[dir] == 3){
			if(content == null){
				content = handler.drain(CAPACITY, true);
				markDirty();
			}else{
				FluidStack drained = handler.drain(new FluidStack(content.getFluid(), CAPACITY - content.amount), true);
				if(drained != null){
					content.amount += drained.amount;
					markDirty();
				}
			}
		}else if(connectMode[dir] == 1){

			// the FluidTankProperties are completely ignored due to them being
			// unreliable.
			// Alternate methods of obtaining the information are used

			// False means either draining in not allowed or the tank is empty
			boolean canDrain = handler.drain(1, false) != null;

			if(!canDrain && content == null){
				return;
			}
			// False means either the tank is full or filling is disallowed with the
			// liquid in this pipe
			boolean canFill = handler.fill(content == null ? handler.drain(1, false) : content, false) != 0;

			if(!canDrain && !canFill){
				// if both are false, there is nothing to be done.
				return;
			}

			if(!canDrain){
				// content != null
				content.amount -= handler.fill(content, true);

				if(content.amount <= 0){
					content = null;
				}

				markDirty();

				//It's possible the connected machine does allow draining but was just empty. This checks for that.
				if(handler.drain(1, false) == null){
					return;
				}
			}
			// content can = null

			// If this pipe and the tank are full, there is nothing to be done anyway
			if(!canFill && CAPACITY != (content == null ? 0 : content.amount)){
				if(content == null){
					content = handler.drain(CAPACITY, true);
					if(content != null && content.amount == 0){
						content = null;
					}
				}else{
					FluidStack drained = handler.drain(new FluidStack(content.getFluid(), CAPACITY - content.amount), true);
					content.amount += drained == null ? 0 : drained.amount;
				}
				if(content != null && content.amount <= 0){
					content = null;
				}
				markDirty();
				if(handler.fill(content, false) == 0){
					return;
				}
			}

			// content can = null

			// KNOWN: canFill & canDrain tank & pipe, tank and pipe are not BOTH
			// full, tank and pipe are not BOTH empty, capacity and contents of
			// pipe.

			FluidStack fakeFullDrained = handler.drain(Integer.MAX_VALUE, false);
			long tankContent = fakeFullDrained == null ? 0 : fakeFullDrained.amount;
			long tankCapacity = tankContent + handler.fill(content == null ? new FluidStack(fakeFullDrained.getFluid(), Integer.MAX_VALUE) : new FluidStack(content.getFluid(), Integer.MAX_VALUE), false);

			long total = (content == null ? 0 : content.amount) + tankContent;

			Fluid fluid = content == null ? fakeFullDrained.getFluid() : content.getFluid();

			long targetOtherContent = Math.round(((double) total * tankCapacity) / ((double) (CAPACITY + tankCapacity)));
			int targetContent = (int) (total - targetOtherContent);

			content = null;

			if(fluid != null){
				if(targetOtherContent - tankContent >= 0){
					handler.fill(new FluidStack(fluid, (int) (targetOtherContent - tankContent)), true);
				}else{
					handler.drain((int) (tankContent - targetOtherContent), true);
				}

				if(targetContent > 0){
					content = new FluidStack(fluid, targetContent);
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		connectMode = new Integer[] {0, 0, 0, 0, 0, 0};
		for(int i = 0; i < 6; i++){
			connectMode[i] = Math.max(0, nbt.hasKey("mode_" + i) ? nbt.getInteger("mode_" + i) : 1);
			hasMatch[i] = nbt.getBoolean("match_" + i);
		}
		content = FluidStack.loadFluidStackFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(content != null){
			content.writeToNBT(nbt);
		}
		if(connectMode != null){
			for(int i = 0; i < 6; i++){
				nbt.setInteger("mode_" + i, connectMode[i]);
				nbt.setBoolean("match_" + i, hasMatch[i]);
			}
		}

		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound out = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			out.setInteger("mode_" + i, hasMatch[i] ? connectMode[i] : 0);
		}
		return out;
	}

	private final IFluidHandler mainHandler = new MainFluidHandler();
	private final IFluidHandler inHandler = new InFluidHandler();
	private final IFluidHandler outHandler = new OutFluidHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side){
		init();
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (side == null || connectMode[side.getIndex()] != 0)){
			return side == null || connectMode[side.getIndex()] == 1 ? (T) mainHandler : connectMode[side.getIndex()] == 2 ? (T) outHandler : (T) inHandler;
		}

		return super.getCapability(capability, side);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side){
		init();
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (side == null || connectMode[side.getIndex()] != 0)){
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
}
