package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
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

	//Caching of instances
	private final IFluidHandler mainHandlerIns = new MainFluidHandler();
	private final IFluidHandler inHandlerIns = new InFluidHandler();
	private final IFluidHandler outHandlerIns = new OutFluidHandler();
	private final NonNullSupplier<IFluidHandler> mainHandler = () -> mainHandlerIns;
	private final NonNullSupplier<IFluidHandler> inHandler = () -> inHandlerIns;
	private final NonNullSupplier<IFluidHandler> outHandler = () -> outHandlerIns;

	//Cache of neighboring optionals
	@SuppressWarnings("unchecked")
	private LazyOptional<IFluidHandler>[] otherOpts = new LazyOptional[] {LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty()};
	//The optionals of this tube, in order both, in, out
	@SuppressWarnings("unchecked")
	private LazyOptional<IFluidHandler>[] internalOpts = new LazyOptional[] {LazyOptional.of(mainHandler), LazyOptional.of(inHandler), LazyOptional.of(outHandler)};

	@Nonnull
	private FluidStack content = FluidStack.EMPTY;

	public FluidTubeTileEntity(){
		super(type);
	}

	/**
	 * Updates all the cached lazyoptionals on a side
	 * @param side The index of the side that was changed
	 */
	public void toggleConfigure(int side){
		//Invalidate and regenerate all the optionals
		for(int i = 0; i < internalOpts.length; i++){
			internalOpts[i].invalidate();
		}
		internalOpts[0] = LazyOptional.of(mainHandler);
		internalOpts[1] = LazyOptional.of(inHandler);
		internalOpts[2] = LazyOptional.of(outHandler);
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}

		//Available handlers are interacted with in two stages: first all 1-way connections are handled, then all 2-way connections
		//First, we collect all available fluid handlers (and perform the 1-way connections in this stage)
		FluidStack origStack = content.copy();
		IFluidHandler[] handlers = new IFluidHandler[6];
		IFluidHandler[] biHandlers = new IFluidHandler[6];
		int biHandlerCount = 0;
		int totalCapacity = 0;
		int totalFluid = 0;
		FluidStack fluidRef = content.copy();//Defines the fluid type to be balanced for 2-way connections

		EnumTransferMode[] modes = new EnumTransferMode[6];
		BlockState state = getBlockState();

		for(int i = 0; i < 6; i++){
			modes[i] = state.get(CRProperties.CONDUIT_SIDES_FULL[i]);
			boolean hasMatch;

			//Skip disabled directions
			if(!modes[i].isConnection()){
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

			hasMatch = handlers[i] != null;

			//Perform 1-way transfers
			if(hasMatch){
				switch(modes[i]){
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
							CRBlocks.fluidTube.forceMode(world, pos, getBlockState(), Direction.byIndex(i), EnumTransferMode.INPUT);
						}
						break;
					case INPUT:
						if(content.isEmpty()){
							mainHandlerIns.fill(handlers[i].drain(CAPACITY, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
						}else{
							FluidStack toDrain = content.copy();
							toDrain.setAmount(CAPACITY - content.getAmount());
							mainHandlerIns.drain(handlers[i].drain(toDrain, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
						}
						break;
					case OUTPUT:
						if(!content.isEmpty()){
							content.shrink(handlers[i].fill(content, IFluidHandler.FluidAction.EXECUTE));
						}
						break;
				}
			}

			//Update hasMatch
			if(getBlockState().get(CRProperties.HAS_MATCH_SIDES[i]) != hasMatch){
				state = state.with(CRProperties.HAS_MATCH_SIDES[i], hasMatch);
				world.setBlockState(pos, state);
				updateContainingBlockInfo();
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
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		if(!content.isEmpty()){
			content.writeToNBT(nbt);
		}

		return nbt;
	}

	@Override
	public void remove(){
		super.remove();
		for(LazyOptional<?> opt : internalOpts){
			opt.invalidate();
		}
		internalOpts[0].invalidate();
		internalOpts[1].invalidate();
		internalOpts[2].invalidate();
	}

	protected boolean canConnect(Direction side){
		//Clooge so redstone tubes work
		return side == null || getBlockState().get(CRProperties.CONDUIT_SIDES_FULL[side.getIndex()]).isConnection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && canConnect(side)){
			if(side == null){
				return (LazyOptional<T>) internalOpts[0];//Main handler
			}else{
				switch(getBlockState().get(CRProperties.CONDUIT_SIDES_FULL[side.getIndex()])){
					case INPUT:
						return (LazyOptional<T>) internalOpts[1];
					case OUTPUT:
						return (LazyOptional<T>) internalOpts[2];
					case BOTH:
						return (LazyOptional<T>) internalOpts[0];
					case NONE:
						return LazyOptional.empty();
				}
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
			return mainHandlerIns.drain(resource, action);
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			return mainHandlerIns.drain(maxDrain, action);
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
			return mainHandlerIns.fill(resource, action);
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
