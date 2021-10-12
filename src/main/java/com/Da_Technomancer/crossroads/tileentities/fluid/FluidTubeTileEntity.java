package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
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

import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

@ObjectHolder(Crossroads.MODID)
public class FluidTubeTileEntity extends BlockEntity implements ITickableTileEntity, ConduitBlock.IConduitTE<EnumTransferMode>{

	@ObjectHolder("fluid_tube")
	private static BlockEntityType<FluidTubeTileEntity> type = null;

	protected static final int CAPACITY = 2000;

	//Caching of instances
	//Pipes may change blockstate often, so as much caching of handler and optional references as possible is done
	private final IFluidHandler mainHandlerIns = new BiFluidHandler();
	private final IFluidHandler inHandlerIns = new InFluidHandler();
	private final IFluidHandler outHandlerIns = new OutFluidHandler();
	private final IFluidHandler innerHandlerIns = new InnerFluidHandler();
	private final NonNullSupplier<IFluidHandler> mainHandler = () -> mainHandlerIns;
	private final NonNullSupplier<IFluidHandler> inHandler = () -> inHandlerIns;
	private final NonNullSupplier<IFluidHandler> outHandler = () -> outHandlerIns;
	private final NonNullSupplier<IFluidHandler> innerHandler = () -> innerHandlerIns;

	protected boolean[] matches = new boolean[6];
	protected EnumTransferMode[] modes = ConduitBlock.IConduitTE.genModeArray(EnumTransferMode.INPUT);

	//Cache of neighboring optionals
	@SuppressWarnings("unchecked")
	private LazyOptional<IFluidHandler>[] otherOpts = new LazyOptional[] {LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty()};
	//The optionals of this tube, in order both, in, out
	@SuppressWarnings("unchecked")
	private LazyOptional<IFluidHandler>[] internalOpts = new LazyOptional[] {LazyOptional.of(mainHandler), LazyOptional.of(inHandler), LazyOptional.of(outHandler), LazyOptional.of(innerHandler)};

	@Nonnull
	private FluidStack content = FluidStack.EMPTY;

	public FluidTubeTileEntity(BlockPos pos, BlockState state){
		this(type);
	}

	protected FluidTubeTileEntity(BlockEntityType<? extends FluidTubeTileEntity> type, BlockPos pos, BlockState state){
		super(type, pos, state);
	}

	@Override
	public void clearCache(){
		super.clearCache();
		//Invalidate and regenerate all the optionals
		for(LazyOptional<IFluidHandler> internalOpt : internalOpts){
			internalOpt.invalidate();
		}
		internalOpts[0] = LazyOptional.of(mainHandler);
		internalOpts[1] = LazyOptional.of(inHandler);
		internalOpts[2] = LazyOptional.of(outHandler);
		internalOpts[3] = LazyOptional.of(innerHandler);
	}

	/*
	 * Fluid routing algorithm:
	 *
	 * Each tube routes fluids independently
	 * Fluid routing within a tube is done in two stages each tick:
	 * 1. 1-way fluid movement through sides in input/output mode. Done by first querying neighbors for amount of fluid they are willing to output/accept and do holistically
	 * 2. 2-way fluid movement through sides in both mode. Done by querying neighbors for capacity and contents, and doing averaging. Only allowed with other CR tubes and implementers of Forge's tank interface
	 *
	 * These tubes will deny any attempt to withdraw fluid through a both side while the tube would be able to output through any output side. This causes output only connections to be prioritized over both connections, and reduces tick-order dependency.
	 * They will also recognize if the both-direction neighbors are other CR fluid tubes that would refuse extraction, and react accordingly.
	 *
	 * Neighboring handlers are cached through their LazyOptionals
	 */

	@Override
	public void tick(){
		if(level.isClientSide){
			return;
		}

		//First, we collect all available fluid handlers, and tabulate transfer data for later transfers
		FluidStack origStack = content.copy();
		//Found bi-directional connection handlers, stored with all null values at the end
		IFluidHandler[] biHandlers = new IFluidHandler[6];
		int biHandlerCount = 0;//Number of found bi-handlers
		//Found in-directional connection handlers, stored with all null values at the end
		IFluidHandler[] inHandlers = new IFluidHandler[6];
		int inHandlerCount = 0;//Number of found in-handlers
		//Found out-directional connection handlers, stored with all null values at the end
		IFluidHandler[] outHandlers = new IFluidHandler[6];
		int outHandlerCount = 0;//Number of found out-handlers
		int totalCapacity = 0;//Total capacity of all bi-direction handlers
		int totalFluid = 0;//Total fluid found in all bi-direction handlers
		int totalInFluid = 0;//Total fluid available to extract from in-connections
		int totalOutFluid = 0;//Total fluid possible to insert into out-connections
		//Defines the fluid type to be moved for connections- we only transfer 1 fluid type per tick
		//May be empty- at every opportunity, we attempt to get a real value
		FluidStack fluidRef = content.copy();
		final int LARGE_NUM = 32_000;//Chosen arbitrarily

		//STAGE 1: Collect handlers and tabulate fluid information

		BlockState state = getBlockState();

		//Collect all handlers, using or updating cache, and build fluid total data
		for(int i = 0; i < 6; i++){
			EnumTransferMode mode = state.getValue(CRProperties.CONDUIT_SIDES_FULL[i]);//Set mode in this direction on the blockstate
			boolean hasMatch;//Whether there is a valid connection direction
			//Skip disabled directions
			if(!mode.isConnection()){
				continue;
			}

			IFluidHandler otherHandler = null;
			//Use the cache if possible
			if(otherOpts[i].isPresent()){
				otherHandler = otherOpts[i].orElseThrow(NullPointerException::new);
			}else{
				//Cache is invalid- get from world
				Direction dir = Direction.from3DDataValue(i);
				BlockEntity otherTe = level.getBlockEntity(worldPosition.relative(dir));
				//Update cache
				if(otherTe != null && (otherOpts[i] = otherTe.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())).isPresent()){
					otherHandler = otherOpts[i].orElseThrow(NullPointerException::new);
				}
			}

			hasMatch = otherHandler != null;

			//Collect behaviour data from handlers, and sort them into 3 arrays for interaction in stage 2
			if(hasMatch){
				switch(mode){
					case BOTH:
						//Verify that this handler is a type we are allowed to do bi-direction with (tank or pipe main handler)
						//This would be simpler if BiFluidHandler implemented IFluidTank, but BiFluidHandler doesn't meet the contract due to being able to refuse extraction (and also isn't a tank...)
						BiFluidHandler biHandler = null;
						IFluidTank tankHandler = null;
						if(otherHandler instanceof BiFluidHandler){
							biHandler = (BiFluidHandler) otherHandler;
						}else if(otherHandler instanceof IFluidTank){
							tankHandler = (IFluidTank) otherHandler;
						}else{
							//We are not allowed to do bi-direction with this neighbor. Disconnect on this side
							mode = EnumTransferMode.NONE;
							break;
						}

						if(biHandler != null && !biHandler.allowExtract()){
							//The other handler is another fluid tube in BOTH mode, which is not currently allowing extraction
							//Skip it, but do not invalidate this connection
							break;
						}

						FluidStack handlerFluid = (biHandler != null) ? biHandler.getFluidInTank(0) : tankHandler.getFluid();

						//Update/check fluidRef
						if(fluidRef.isEmpty()){
							fluidRef = biHandler != null ? otherHandler.getFluidInTank(0).copy() : tankHandler.getFluid().copy();
						}else if(!handlerFluid.isEmpty() && !BlockUtil.sameFluid(handlerFluid, fluidRef)){
							//The handler has a different fluid than we are moving. Skip
							break;
						}

						//Add the data of this handler to the totals, and add it to the array of handlers to act on in stage 2
						biHandlers[biHandlerCount] = otherHandler;
						biHandlerCount++;
						totalFluid += handlerFluid.getAmount();
						totalCapacity += biHandler != null ? CAPACITY : tankHandler.getCapacity();
						break;
					case INPUT:
						FluidStack drained;
						if(fluidRef.isEmpty()){
							//Drain any fluid, and define our fluidRef based on it
							drained = otherHandler.drain(LARGE_NUM, IFluidHandler.FluidAction.SIMULATE);
							fluidRef = drained.copy();
						}else{
							fluidRef.setAmount(LARGE_NUM);
							drained = otherHandler.drain(fluidRef.copy(), IFluidHandler.FluidAction.SIMULATE);
						}

						//If drained is empty, there is no point including this handler
						if(!drained.isEmpty()){
							inHandlers[inHandlerCount] = otherHandler;
							inHandlerCount++;
							totalInFluid += drained.getAmount();
						}
						break;
					case OUTPUT:
						//Unfortunately, if we do not already have a valid fluidRef, we can not test outputs at all
						//This is because there is no fill method that doesn't require already knowing the fluid to be moved
						//We store all the found output handlers- regardless of validity- and test them as a batch later
						outHandlers[outHandlerCount] = otherHandler;
						outHandlerCount++;
						break;
				}
			}

			//Update hasMatch
			setData(i, hasMatch, mode);
		}

		if(fluidRef.isEmpty()){
			//If we didn't manage to find any fluid, there is nothing to do
			return;
		}

		//We have to test our outHandlers after we are certain we have a fluidRef
		//Typical algorithm for quickly filtering an array
		int toTestCount = outHandlerCount;
		outHandlerCount = 0;
		int[] passedIndices = new int[6];//All indices in the original outHandlers that passed
		fluidRef.setAmount(LARGE_NUM);
		for(int i = 0; i < toTestCount; i++){
			int inserted = outHandlers[i].fill(fluidRef.copy(), IFluidHandler.FluidAction.SIMULATE);
			if(inserted > 0){
				//Valid- keep in the list
				passedIndices[outHandlerCount] = i;
				outHandlerCount++;
				totalOutFluid += inserted;
			}
		}
		for(int i = 0; i < 6; i++){
			if(i < outHandlerCount){
				outHandlers[i] = outHandlers[passedIndices[i]];
			}else{
				outHandlers[i] = null;//Remove all handlers that failed
			}
		}

		//STAGE 2: Transfer fluids

		//We want to move as much fluid through in/out connections as possible, and balance the remainder through both
		//These calculations are based around assuming other handlers act in a predictable way compared to their simulated results
		//We trust but verify- we do extractions before insertions, so that if an extraction failed, we don't dupe fluid
		//If an insertion fails, we are unable to re-insert extracted fluids back into the source. In that case, we may end with more fluid than CAPACITY

		//Perform extractions from IN sides
		//Calculations
		int totalSpace = totalOutFluid + CAPACITY + totalCapacity - totalFluid - content.getAmount();//Space to put fluid that might be extracted
		totalInFluid = Math.min(totalInFluid, totalSpace);//Don't extract more fluid than we have space for

		//Execution
		int drained = 0;
		for(int i = 0; i < inHandlerCount; i++){
			if(drained < totalInFluid){
				fluidRef.setAmount(totalInFluid - drained);
				drained += inHandlers[i].drain(fluidRef.copy(), IFluidHandler.FluidAction.EXECUTE).getAmount();
			}else{
				break;//We finished
			}
		}

		//Perform insertions into OUT sides
		//Calculations
		//If everything went as expected, drained == totalInFluid. We use drained instead just in case
		int availableFluid = drained + content.getAmount() + totalFluid;//Fluid we have to insert
		int toFill = Math.min(totalOutFluid, availableFluid);//Don't output more fluid than we have access to

		int filled = 0;
		//Execution
		for(int i = 0; i < outHandlerCount; i++){
			if(filled < toFill){
				fluidRef.setAmount(toFill - filled);
				filled += outHandlers[i].fill(fluidRef.copy(), IFluidHandler.FluidAction.EXECUTE);
			}else{
				break;//We finished
			}
		}

		//If everything went as expected, filled == toFill. We use filled instead just in case
		availableFluid -= filled;//Filled fluid is no longer available

		//Perform balancing through BOTH sides
		if(biHandlerCount != 0){
			//Calculations
			totalCapacity += CAPACITY;//Capacity for balancing includes this
			double pressure = (double) availableFluid / totalCapacity;
			availableFluid -= totalFluid;//Available fluid from this point tracks the amount of fluid that should be in this pipe
			//We know bi-handlers will behave, so we don't need as much sanity checking and special casing

			//Execution
			for(int i = 0; i < biHandlerCount; i++){
				FluidStack prev = biHandlers[i].getFluidInTank(0);
				int capacity = biHandlers[i].getTankCapacity(0);
				int desired = (int) (capacity * pressure);
				if(desired > prev.getAmount()){
					fluidRef.setAmount(desired - prev.getAmount());
					availableFluid -= biHandlers[i].fill(fluidRef.copy(), IFluidHandler.FluidAction.EXECUTE);
				}else if(desired < prev.getAmount()){
					availableFluid += biHandlers[i].drain(prev.getAmount() - desired, IFluidHandler.FluidAction.EXECUTE).getAmount();
				}
			}
		}

		//All remaining fluid goes into this tube
		FluidStack prevStack = content;
		if(availableFluid <= 0 || fluidRef.isEmpty()){
			content = FluidStack.EMPTY;
		}else{
			content = fluidRef.copy();
			content.setAmount(availableFluid);
		}
		if(!BlockUtil.sameFluid(content, prevStack)){
			setChanged();
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		ConduitBlock.IConduitTE.readConduitNBT(nbt, this);
		content = FluidStack.loadFluidStackFromNBT(nbt);
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		ConduitBlock.IConduitTE.writeConduitNBT(nbt, this);
		if(!content.isEmpty()){
			content.writeToNBT(nbt);
		}

		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		for(LazyOptional<?> opt : internalOpts){
			opt.invalidate();
		}
		internalOpts[0].invalidate();
		internalOpts[1].invalidate();
		internalOpts[2].invalidate();
		internalOpts[3].invalidate();
	}

	protected boolean canConnect(Direction side){
		//Clooge so redstone tubes work
		return side == null || modes[side.get3DDataValue()].isConnection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(side == null){
				return (LazyOptional<T>) internalOpts[3];//Inner handler
			}else if(canConnect(side)){
				switch(modes[side.get3DDataValue()]){
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

	@Nonnull
	@Override
	public boolean[] getHasMatch(){
		return matches;
	}

	@Nonnull
	@Override
	public EnumTransferMode[] getModes(){
		return modes;
	}

	@Nonnull
	@Override
	public EnumTransferMode deserialize(String name){
		return ConduitBlock.IConduitTE.deserializeEnumMode(name);
	}

	@Override
	public boolean hasMatch(int side, EnumTransferMode mode){
		Direction face = Direction.from3DDataValue(side);
		BlockEntity neighTE = level.getBlockEntity(worldPosition.relative(face));
		if(neighTE != null){
			LazyOptional<IFluidHandler> opt = neighTE.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face.getOpposite());
			return opt.isPresent();
		}
		return false;
	}

	/**
	 * Used for bi-directional connections.
	 * Will refuse drain calls if an output only side would be allowed to move fluid (see algorithm comment above)
	 */
	private class BiFluidHandler implements IFluidHandler{

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
			return innerHandlerIns.fill(resource, action);
		}

		protected boolean allowExtract(){
			if(content.isEmpty()){
				return true;//As returning false will disable fluid balancing in neighboring tubes, we can not return false here
			}

			//Try all neighbors- see we are in out mode on a side, and that side will accept any of our fluid
			for(int i = 0; i < 6; i++){
				//We use the TE modes instead of blockstate modes because by checking the handler cache, we effectively also check hasMatch and speed things up a little
				if(modes[i] == EnumTransferMode.OUTPUT){
					//Use the handler cache- it's much faster than checking the world, and the cache and world would only differ during the tick things are being changed.
					if(otherOpts[i].isPresent() && otherOpts[i].orElseThrow(NullPointerException::new).fill(content, FluidAction.SIMULATE) != 0){
						return false;
					}
				}
			}

			return true;
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action){
			if(allowExtract()){
				return innerHandlerIns.drain(resource, action);
			}else{
				return FluidStack.EMPTY;
			}
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			if(allowExtract()){
				return innerHandlerIns.drain(maxDrain, action);
			}else{
				return FluidStack.EMPTY;
			}
		}
	}

	/**
	 * Used for output-only connections
	 */
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
			return innerHandlerIns.drain(resource, action);
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			return innerHandlerIns.drain(maxDrain, action);
		}
	}

	/**
	 * Used for input-only connections
	 */
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
			return innerHandlerIns.fill(resource, action);
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

	/**
	 * Used for null side handler, and other handlers define behaviour in terms of this one
	 */
	private class InnerFluidHandler implements IFluidHandler{

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
			if(filled != 0 && action.execute()){
				if(content.isEmpty()){
					content = resource.copy();
					content.setAmount(filled);
				}else{
					content.grow(filled);
				}
				setChanged();
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
				setChanged();
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
				setChanged();
			}
			return removed;
		}
	}
}
