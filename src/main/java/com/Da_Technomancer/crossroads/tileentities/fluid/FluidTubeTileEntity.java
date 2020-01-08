package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.fluid.FluidTube;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class FluidTubeTileEntity extends TileEntity implements ITickableTileEntity{

	@ObjectHolder("fluid_tube")
	private static TileEntityType<FluidTubeTileEntity> type = null;

	protected static final int CAPACITY = 2000;

	//Whether there exists a block to connect to on each side
	protected final boolean[] hasMatch = new boolean[6];
	//The setting of this block on each side. None means locked
	protected final EnumTransferMode[] configure = new EnumTransferMode[] {EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH};

	private final IFluidHandler mainHandler = new MainFluidHandler();
	private final IFluidHandler inHandler = new InFluidHandler();
	private final IFluidHandler outHandler = new OutFluidHandler();

	@SuppressWarnings("unchecked")
	private LazyOptional<IFluidHandler>[] otherOpts = new LazyOptional[] {LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty()};
	@SuppressWarnings("unchecked")
	private LazyOptional<IFluidHandler>[] internalOpts = new LazyOptional[] {LazyOptional.of(() -> mainHandler), LazyOptional.of(() -> mainHandler), LazyOptional.of(() -> mainHandler), LazyOptional.of(() -> mainHandler), LazyOptional.of(() -> mainHandler), LazyOptional.of(() -> mainHandler)};
	private final LazyOptional<IFluidHandler> centerOpt = LazyOptional.of(() -> mainHandler);

	private FluidStack content = null;
	private boolean init = false;

	public FluidTubeTileEntity(){
		super(type);
	}

	public void toggleConfigure(int side){
		switch(configure[side]){
			case INPUT:
				configure[side] = EnumTransferMode.NONE;
				internalOpts[side].invalidate();
				internalOpts[side] = LazyOptional.empty();
				break;
			case OUTPUT:
				configure[side] = EnumTransferMode.INPUT;
				internalOpts[side].invalidate();
				internalOpts[side] = LazyOptional.of(() -> inHandler);
				break;
			case NONE:
				configure[side] = EnumTransferMode.BOTH;
				internalOpts[side].invalidate();
				internalOpts[side] = LazyOptional.of(() -> mainHandler);
				break;
			case BOTH:
				configure[side] = EnumTransferMode.OUTPUT;
				internalOpts[side].invalidate();
				internalOpts[side] = LazyOptional.of(() -> outHandler);
				break;
		}
		//If another CR pipe is connected, force the other one to update it's state
		Direction dir = Direction.byIndex(side);
		TileEntity otherTE = world.getTileEntity(pos.offset(dir));
		if(otherTE instanceof FluidTubeTileEntity){
			((FluidTubeTileEntity) otherTE).recheckMode(dir);
			updateState();
		}
		updateState();
	}

	/**
	 * Forces this tube to calculate what transfer mode there should be on a side based on neighbor and adjust the configure. DOES NOT UPDATE STATE
	 * @param side The side to recheck
	 */
	protected void recheckMode(Direction side){
		int sideInd = side.getIndex();
		TileEntity neighbor = world.getTileEntity(pos.offset(side));
		LazyOptional<IFluidHandler> otherOpt;
		if(neighbor != null & (otherOpt = neighbor.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())).isPresent()){
			IFluidHandler otherHandler = otherOpt.orElseThrow(NullPointerException::new);
			if(otherHandler instanceof MainFluidHandler){
				//Other pipe with bi-directional mode. Go to both
				if(configure[sideInd] != EnumTransferMode.BOTH){
					configure[sideInd] = EnumTransferMode.BOTH;
					internalOpts[sideInd].invalidate();
					internalOpts[sideInd] = LazyOptional.of(() -> mainHandler);
				}
			}else if(otherHandler instanceof OutFluidHandler){
				//Other pipe in output mode. Go to input
				if(configure[sideInd] != EnumTransferMode.INPUT){
					configure[sideInd] = EnumTransferMode.INPUT;
					internalOpts[sideInd].invalidate();
					internalOpts[sideInd] = LazyOptional.of(() -> inHandler);
				}
			}else if(otherHandler instanceof InFluidHandler){
				//Other pipe in input mode. Go to output
				if(configure[sideInd] != EnumTransferMode.OUTPUT){
					configure[sideInd] = EnumTransferMode.OUTPUT;
					internalOpts[sideInd].invalidate();
					internalOpts[sideInd] = LazyOptional.of(() -> outHandler);
				}
			}else if(!(otherHandler instanceof IFluidTank)){//Tanks keep the current mode
				//Generic fluid handler. Keep the current mode, unless it's both, in which case go to output
				if(configure[sideInd] == EnumTransferMode.BOTH){
					configure[sideInd] = EnumTransferMode.OUTPUT;
					internalOpts[sideInd].invalidate();
					internalOpts[sideInd] = LazyOptional.of(() -> outHandler);
				}
			}
		}//else no connection- leave current mode
	}

	protected void updateState(){
		BlockState state = world.getBlockState(pos);
		BlockState newState = state;
		if(state.getBlock() instanceof FluidTube){
			for(int i = 0; i < 6; i++){
				newState = newState.with(CRProperties.CONDUIT_SIDES[i], hasMatch[i] ? configure[i] : EnumTransferMode.NONE);
			}
		}
		if(state != newState){
			world.setBlockState(pos, newState, 2);
		}
	}

	private void init(){
		if(!init){
			init = true;
			for(Direction dir : Direction.values()){
				recheckMode(dir);
			}
			updateState();
		}
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}
		init();

		//Available handlers are interacted with in two stages: first all 1-way connections are handled, then all 2-way connections
		//First, we collect all available fluid handlers (and perform the 1-way connections in this stage)
		FluidStack origStack = content.copy();
		IFluidHandler[] handlers = new IFluidHandler[6];
		IFluidHandler[] biHandlers = new IFluidHandler[6];
		int biHandlerCount = 0;
		int totalCapacity = 0;
		int totalFluid = 0;
		FluidStack fluidRef = content.copy();//Defines the fluid type to be balanced for 2-way connections

		for(int i = 0; i < 6; i++){
			//Skip disabled directions
			if(!configure[i].isConnection()){
				continue;
			}

			//Use the cache if possible
			if(otherOpts[i].isPresent()){
				handlers[i] = otherOpts[i].orElseThrow(NullPointerException::new);
			}else{
				Direction dir = Direction.byIndex(i);
				TileEntity otherTe = world.getTileEntity(pos.offset(dir));
				if(otherTe != null && (otherOpts[i] = otherTe.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())).isPresent()){
					handlers[i] = otherOpts[i].orElseThrow(NullPointerException::new);
				}
			}

			hasMatch[i] = handlers[i] != null;

			//Perform 1-way transfers
			if(hasMatch[i]){
				switch(configure[i]){
					case BOTH:
						//Several 2-way connections will be with handlers that only input OR output; they need to be handled as a 1-way connection of fluid flow will act strangely
						if(handlers[i] instanceof IFluidTank){
							if(fluidRef.isEmpty()){
								fluidRef = ((IFluidTank) handlers[i]).getFluid().copy();
							}
							if(((IFluidTank) handlers[i]).getFluid().isEmpty() || BlockUtil.sameFluid(fluidRef, ((IFluidTank) handlers[i]).getFluid())){
								biHandlers[biHandlerCount] = handlers[i];
								biHandlerCount++;
								totalCapacity += ((IFluidTank) handlers[i]).getCapacity();
								totalFluid += ((IFluidTank) handlers[i]).getFluidAmount();
							}
						}else if(handlers[i] instanceof MainFluidHandler){
							if(fluidRef.isEmpty()){
								fluidRef = handlers[i].getFluidInTank(0).copy();
							}
							if(handlers[i].getFluidInTank(0).isEmpty() || BlockUtil.sameFluid(fluidRef, handlers[i].getFluidInTank(0))){
								biHandlers[biHandlerCount] = handlers[i];
								biHandlerCount++;
								totalCapacity += CAPACITY;
								totalFluid += handlers[i].getFluidInTank(0).getAmount();
							}
						}else{
							//Actually should be a 1-way connection.
							recheckMode(Direction.byIndex(i));
							updateState();
						}
						break;
					case INPUT:
						if(content.isEmpty()){
							mainHandler.fill(handlers[i].drain(CAPACITY, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
						}else{
							FluidStack toDrain = content.copy();
							toDrain.setAmount(CAPACITY - content.getAmount());
							mainHandler.drain(handlers[i].drain(toDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
						}
						break;
					case OUTPUT:
						if(!content.isEmpty()){
							content.shrink(handlers[i].fill(content, IFluidHandler.FluidAction.EXECUTE));
						}
						break;
				}
			}
		}

		//A separate loop is needed for the second stage, as all handlers need to have been queried to do correct balancing

		//Everything in biHandlers is either a IFluidTank or another pipe MainFluidHandler, and can therefore be expected to have 1 tank and be well behaved
		totalCapacity += CAPACITY;
		totalFluid += content.getAmount();
		float pressure = (float) totalFluid / totalCapacity;
		totalFluid = content.getAmount();//From this point on, total fluid tracks the amount of fluid in this pipe to prevent rounding error based dupe bugs
		if(totalFluid != 0 && (content.isEmpty() || BlockUtil.sameFluid(content, fluidRef))){
			for(int i = 0; i < biHandlerCount; i++){
				IFluidHandler otherHand = biHandlers[i];
				int target = (int) (otherHand.getTankCapacity(0) * pressure);
				int otherCont = otherHand.getFluidInTank(0).getAmount();
				if(otherCont > target){
					totalFluid += otherHand.drain(otherCont - target, IFluidHandler.FluidAction.EXECUTE).getAmount();
				}else if(otherCont < target){
					FluidStack toFill = fluidRef.copy();
					toFill.setAmount(target - otherCont);
					totalFluid -= otherHand.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
				}
			}
			//Set this pipe's contents to the remainder
			//If one of the IFluidTank handlers isn't acting like a true tank, and didn't accept fluid, the end content could be above CAPACITY
			content = fluidRef;
			content.setAmount(totalFluid);
		}

		if(origStack.getAmount() != content.getAmount() || !BlockUtil.sameFluid(origStack, content)){
			markDirty();
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);
		init = nbt.getBoolean("init");
		for(int i = 0; i < 6; i++){
			hasMatch[i] = nbt.getBoolean("match_" + i);
			configure[i] = EnumTransferMode.fromString(nbt.getString("config_" + i));
			switch(configure[i]){
				case INPUT:
					internalOpts[i].invalidate();
					internalOpts[i] = LazyOptional.of(() -> inHandler);
					break;
				case OUTPUT:
					internalOpts[i].invalidate();
					internalOpts[i] = LazyOptional.of(() -> outHandler);
					break;
				case BOTH:
					//Default value- no change needed
					break;
				case NONE:
					internalOpts[i].invalidate();
					internalOpts[i] = LazyOptional.empty();
					break;
			}
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		if(content != null){
			content.writeToNBT(nbt);
		}
		nbt.putBoolean("init", init);
		for(int i = 0; i < 6; i++){
			nbt.putBoolean("match_" + i, hasMatch[i]);
			nbt.putString("config_" + i, configure[i].getName());
		}

		return nbt;
	}

	@Override
	public void remove(){
		super.remove();
		for(LazyOptional<?> opt : internalOpts){
			opt.invalidate();
		}
		centerOpt.invalidate();
	}

	protected boolean canConnect(Direction side){
		//Clooge so redstone tubes work
		return side == null || configure[side.getIndex()].isConnection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side){
		init();
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && canConnect(side)){
			if(side == null){
				return (LazyOptional<T>) centerOpt;
			}else{
				return (LazyOptional<T>) internalOpts[side.getIndex()];
			}
		}

		return super.getCapability(capability, side);
	}

	private class OutFluidHandler implements IFluidHandler{

		@Override
		public int getTanks(){
			return 1;
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tank){
			return tank == 0 ? content : FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank){
			return CAPACITY;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
			return true;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action){
			return 0;
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action){
			return mainHandler.drain(resource, action);
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			return mainHandler.drain(maxDrain, action);
		}
	}

	private class InFluidHandler implements IFluidHandler{

		@Override
		public int getTanks(){
			return 1;
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tank){
			return tank == 0 ? content : FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank){
			return CAPACITY;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
			return true;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action){
			return mainHandler.fill(resource, action);
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action){
			return FluidStack.EMPTY;
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			return FluidStack.EMPTY;
		}
	}

	private class MainFluidHandler implements IFluidHandler{

		@Override
		public int getTanks(){
			return 1;
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tank){
			return tank == 0 ? content : FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank){
			return CAPACITY;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
			return true;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action){
			if(!content.isEmpty() && !BlockUtil.sameFluid(content, resource)){
				return 0;
			}
			//The zero lower bound is because due to the fluid handling logic in tick(), there is a possibility of content.getAmount() being greater than CAPACITY
			int filled = Math.max(Math.min(resource.getAmount(), CAPACITY - content.getAmount()), 0);
			if(action.execute()){
				content.shrink(filled);
				markDirty();
			}
			return filled;
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action){
			if(content.isEmpty() || !BlockUtil.sameFluid(content, resource)){
				return FluidStack.EMPTY;
			}
			int drained = Math.min(resource.getAmount(), content.getAmount());
			FluidStack removed = content.copy();
			removed.setAmount(drained);
			if(action.execute()){
				content.shrink(drained);
				markDirty();
			}
			return removed;
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			if(content.isEmpty() || maxDrain <= 0){
				return FluidStack.EMPTY;
			}
			int drained = Math.min(maxDrain, content.getAmount());
			FluidStack removed = content.copy();
			removed.setAmount(drained);
			if(action.execute()){
				content.shrink(drained);
				markDirty();
			}
			return removed;
		}
	}
}
