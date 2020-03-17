package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CircuitUtil extends RedstoneUtil{

	public static float combineRedsSources(CircHandler handler, int worldRedstone){
		if(!handler.builtConnections){
			handler.buildConnections();
		}

		//Something we have to be careful of is that any circuit input (other than circuit connections directly from wire) will also create a normal redstone value
		//But the vanilla redstone value created is the rounded version of the circuit value.
		//So if the highest vanilla redstone signal is equal to the rounded highest circuit input,
		//we assume it is the output from the circuit and use the circuit value instead.
		//In very unusual cases, THIS ASSUMPTION COULD BE FALSE (ex. vanilla redstone dust with 13, circuit of 12.8, this code uses 12.8 when it should use 13)
		//This is a known edge case bug

		if(worldRedstone > handler.circRedstone && Math.round(handler.circRedstone) == worldRedstone){
			return handler.circRedstone;
		}
		return Math.max(handler.circRedstone, worldRedstone);
	}

	public static LazyOptional<IRedstoneHandler> makeBaseCircuitOptional(TileEntity te, CircHandler handler, float startingRedstone){
		LazyOptional<IRedstoneHandler> optional = LazyOptional.of(() -> handler);
		handler.setup(optional, te, startingRedstone);
		return optional;
	}

	public static void updateFromWorld(CircHandler handler, Block updatingBlock){
		//Simple optimization- if the block update is just signal strength changing, we don't need to rebuild connections
		if(updatingBlock != Blocks.REDSTONE_WIRE && !(updatingBlock instanceof RedstoneDiodeBlock)){
			handler.buildConnections();
		}
	}

	/**
	 * This is a useful bare-bones implementation of IRedstoneHandler that only receives a circuit signal, and does not transmit it
	 * It is strongly suggested to use this class- and associated helpers- when applicable
	 */
	public static class CircHandler implements IRedstoneHandler{

		private ArrayList<WeakReference<LazyOptional<IRedstoneHandler>>> sources = new ArrayList<>(1);
		private boolean builtConnections = false;
		private WeakReference<LazyOptional<IRedstoneHandler>> redsRef;
		private float circRedstone;
		private TileEntity te;

		public void setup(LazyOptional<IRedstoneHandler> circuitOpt, TileEntity te, float initCircRedstone){
			redsRef = new WeakReference<>(circuitOpt);
			circRedstone = initCircRedstone;
			this.te = te;
		}

		public float getCircRedstone(){
			return circRedstone;
		}

		public void setCircRedstone(float newVal){
			circRedstone = newVal;
		}

		private void buildConnections() {
			//Rebuild the sources list
			World world;
			if(te != null && (world = te.getWorld()) != null && !world.isRemote){
				BlockPos pos = te.getPos();
				builtConnections = true;
				ArrayList<WeakReference<LazyOptional<IRedstoneHandler>>> preSrc = new ArrayList<>(sources.size());
				preSrc.addAll(sources);
				//Wipe old sources
				sources.clear();

				for(Direction checkDir : Direction.values()){
					TileEntity checkTE = world.getTileEntity(pos.offset(checkDir));
					IRedstoneHandler otherHandler;
					if(checkTE != null && (otherHandler = BlockUtil.get(checkTE.getCapability(RedstoneUtil.REDSTONE_CAPABILITY, checkDir.getOpposite()))) != null){
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
		public float getOutput(){
			return getCircRedstone();
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
					circRedstone = Math.max(circRedstone, Math.round(RedstoneUtil.sanitize(srcOpt.orElseThrow(NullPointerException::new).getOutput())));
				}else{
					//Remove invalid entries to speed up future checks
					sources.remove(i);
					i--;
				}
			}
			if(CircuitUtil.didChange(prevCirc, circRedstone)){
				te.markDirty();
			}
		}
	}
}
