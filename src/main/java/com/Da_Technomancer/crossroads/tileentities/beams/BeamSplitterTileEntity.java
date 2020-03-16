package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.IWireConnect;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.BlockState;
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
public class BeamSplitterTileEntity extends BeamRenderTE implements IWireConnect{

	@ObjectHolder("beam_splitter")
	private static TileEntityType<BeamSplitterTileEntity> type = null;

	private int redstone = 0;
	private float circRedstone = 0;
	private Direction dir = null;

	public BeamSplitterTileEntity(){
		super(type);
	}

	private Direction getDir(){
		if(dir == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CRBlocks.beamSplitter){
				return Direction.NORTH;
			}
			dir = state.get(ESProperties.FACING);
		}
		return dir;
	}

	@Override
	public void resetBeamer(){
		super.resetBeamer();
		dir = null;
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
	protected void doEmit(BeamUnit out){
		//As it would turn out, the problem of meeting a quota for the sum of values drawn from a limited source while also approximately maintaining the source ratio is quite messy when all values must be integers
		//This is about as clean an implementation as is possible
		int toFill = Math.round(out.getPower() * calcRedsInput() / 15F);
		Direction facing = getDir();
		BeamUnit toDraw;
		BeamUnit remain;

		if(toFill == 0){
			if(beamer[facing.getIndex()].emit(BeamUnit.EMPTY, world)){
				refreshBeam(facing.getIndex());
			}
			if(beamer[facing.getOpposite().getIndex()].emit(out, world)){
				refreshBeam(facing.getOpposite().getIndex());
			}
			return;
		}

		if(toFill < out.getPower()){
			int[] output = out.mult(((double) toFill) / ((double) out.getPower()), true).getValues();//Use the floor formula as a starting point
			int[] stored = out.getValues();
			int available = 0;

			for(int i = 0; i < 4; i++){
				stored[i] -= output[i];
				available += stored[i];
				toFill -= output[i];
			}

			toFill = Math.min(toFill, available);
			available -= toFill;

			int source = 0;

			//Round-robin distribution of drawing additional power from storage to meet the quota
			//Ignoring the source element ratio, as toFill << RATES[storage] in most cases, making the effect on ratio minor
			for(int i = 0; i < toFill; i++){
				while(stored[source] == 0){
					source++;
				}
				output[source]++;
				stored[source]--;
				source++;
			}
			toDraw = new BeamUnit(output);
			remain = available == 0 ? BeamUnit.EMPTY : new BeamUnit(stored[0], stored[1], stored[2], stored[3]);
		}else{
			toDraw = out;
			remain = BeamUnit.EMPTY;
		}


		if(beamer[facing.getIndex()].emit(toDraw, world)){
			refreshBeam(facing.getIndex());
		}
		if(beamer[facing.getOpposite().getIndex()].emit(remain, world)){
			refreshBeam(facing.getOpposite().getIndex());
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] input = new boolean[] {true, true, true, true, true, true};
		Direction facing = getDir();
		input[facing.getIndex()] = false;
		input[facing.getOpposite().getIndex()] = false;
		return input;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] output = new boolean[6];
		Direction facing = getDir();
		output[facing.getIndex()] = true;
		output[facing.getOpposite().getIndex()] = true;
		return output;
	}


	@Override
	public void remove(){
		super.remove();
		redsOpt.invalidate();
	}

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

	private float calcRedsInput(){
		if(!builtConnections){
			buildConnections();
		}
		//Splitter caps signal at 15

		//Something we have to be careful of is that any circuit input (other than circuit connections directly from wire) will also create a normal redstone value
		//But the vanilla redstone value created is the rounded version of the circuit value.
		//So if the highest vanilla redstone signal is equal to the rounded highest circuit input,
		//we assume it is the output from the circuit and use the circuit value instead.
		//In very unusual cases, THIS ASSUMPTION COULD BE FALSE (ex. vanilla redstone dust with 13, circuit of 12.8, this code uses 12.8 when it should use 13)
		//This is a known edge case bug
		if(Math.round(circRedstone) == redstone){
			return Math.min(15, circRedstone);
		}
		return Math.min(Math.max(redstone, circRedstone), 15);
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

	@Override
	public boolean canConnect(Direction side, BlockState state){
		return true;
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
					circRedstone = Math.max(circRedstone, RedstoneUtil.sanitize(srcOpt.orElseThrow(NullPointerException::new).getOutput()));
				}else{
					//Remove invalid entries to speed up future checks
					sources.remove(i);
					i--;
				}
			}
			if(RedstoneUtil.didChange(prevCirc, circRedstone)){
				markDirty();
			}
		}
	}
} 
