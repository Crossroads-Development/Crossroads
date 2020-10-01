package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CircuitUtil extends RedstoneUtil{

	public static float combineRedsSources(CircHandler handler){
		if(!handler.builtConnections){
			handler.buildConnections();
		}
		return sanitize(RedstoneUtil.chooseInput(handler.getCircRedstone(), handler.getWorldRedstone()));
	}

	public static LazyOptional<IRedstoneHandler> makeBaseCircuitOptional(TileEntity te, CircHandler handler, float startingRedstone){
		LazyOptional<IRedstoneHandler> optional = LazyOptional.of(() -> handler);
		handler.setup(optional, te, startingRedstone);
		return optional;
	}

	public static void updateFromWorld(CircHandler handler, Block updatingBlock){
		//Check for circuit input changes
		//Simple optimization- if the block update is just signal strength changing, we don't need to rebuild connections
		if(updatingBlock != Blocks.REDSTONE_WIRE && !(updatingBlock instanceof RedstoneDiodeBlock)){
			handler.buildConnections();
		}

		//Check for changing vanilla redstone signal
		handler.updateWorldRedstone();
	}

	/**
	 * This is a useful bare-bones implementation of IRedstoneHandler that only receives a circuit/redstone signal, and does not transmit it
	 * It is strongly suggested to use this class- and associated helpers- when applicable
	 */
	public static class CircHandler implements IRedstoneHandler{

		/**
		 * Stores all circuit sources
		 * Each entry is one source
		 * It is possible for a source to be repeated, but with different directions
		 * The first entry in each pair is the source handler (access controlled by weak reference for preventing memory leaks and lazyoptional for invalidation checking)
		 * The second entry in each pair is the direction the connection came from
		 *
		 * World redstone will not be checked in any direction with a valid source
		 */
		private ArrayList<Pair<WeakReference<LazyOptional<IRedstoneHandler>>, Direction>> sources = new ArrayList<>(1);
		private boolean builtConnections = false;
		private WeakReference<LazyOptional<IRedstoneHandler>> redsRef;
		private float circRedstone;
		private int worldRedstone;
		private TileEntity te;

		public void setup(LazyOptional<IRedstoneHandler> circuitOpt, TileEntity te, float initCircRedstone){
			redsRef = new WeakReference<>(circuitOpt);
			circRedstone = initCircRedstone;
			this.te = te;
		}

		public float getCircRedstone(){
			return circRedstone;
		}

		public int getWorldRedstone(){
			return worldRedstone;
		}

		/**
		 * Loads from an NBT tag
		 * @param nbt An NBT tag that this is saved to
		 */
		public void read(BlockState state, CompoundNBT nbt){
			circRedstone = nbt.getFloat("circ_reds");
			worldRedstone = nbt.getInt("reds");
		}

		/**
		 * Saves the state to an NBT tag
		 * @param nbt An NBT tag to write to. Will be modified
		 */
		public void write(CompoundNBT nbt){
			nbt.putFloat("circ_reds", circRedstone);
			nbt.putInt("reds", worldRedstone);
		}

		/**
		 * Measures and recalculates the overall redstone value from the vanilla redstone system
		 * Ignores redstone input on any side with a circuit input
		 */
		public void updateWorldRedstone(){
			int prevWorldReds = worldRedstone;
			worldRedstone = 0;
			Direction[] dirsToCheck = Direction.values();
			for(Pair<WeakReference<LazyOptional<IRedstoneHandler>>, Direction> src : sources){
				LazyOptional<IRedstoneHandler> srcOpt;
				if((srcOpt = src.getLeft().get()) != null && srcOpt.isPresent()){
					dirsToCheck[src.getRight().getIndex()] = null;//Mark any direction with a circuit input as not to be checked
				}
			}

			World world = te.getWorld();
			BlockPos pos = te.getPos();
			for(Direction dir : dirsToCheck){
				if(dir != null && world != null){
					worldRedstone = Math.max(worldRedstone, CircuitUtil.getRedstoneOnSide(world, pos, dir));
				}
			}
			worldRedstone = CircuitUtil.clampToVanilla(worldRedstone);//Sanitize the input. Sometimes vanilla adds redstone sources that break the 15 power cap (they usually are fixed quickly)

			if(prevWorldReds != worldRedstone){
				te.markDirty();
			}
		}

		private void buildConnections() {
			//Rebuild the sources list
			World world;
			if(te != null && (world = te.getWorld()) != null && !world.isRemote){
				BlockPos pos = te.getPos();
				builtConnections = true;
				ArrayList<Pair<WeakReference<LazyOptional<IRedstoneHandler>>, Direction>> preSrc = new ArrayList<>(sources.size());
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

			//update our world redstone value, to correctly ignore sides with circuits
			updateWorldRedstone();
		}

		@Override
		public float getOutput(){
			return combineRedsSources(this);
		}

		@Override
		public void findDependents(WeakReference<LazyOptional<IRedstoneHandler>> weakReference, int i, Direction fromSide, Direction nominalSide){
			LazyOptional<IRedstoneHandler> srcOption = weakReference.get();
			if(srcOption != null && srcOption.isPresent()){
				IRedstoneHandler srcHandler = BlockUtil.get(srcOption);
				srcHandler.addDependent(redsRef, nominalSide);
				Pair<WeakReference<LazyOptional<IRedstoneHandler>>, Direction> srcEntry = Pair.of(weakReference, fromSide);
				if(!sources.contains(srcEntry)){
					sources.add(srcEntry);
				}
			}
		}

		@Override
		public void requestSrc(WeakReference<LazyOptional<IRedstoneHandler>> weakReference, int i, Direction direction, Direction direction1){
			//No-op
		}

		@Override
		public void addSrc(WeakReference<LazyOptional<IRedstoneHandler>> weakReference, Direction direction){
			Pair<WeakReference<LazyOptional<IRedstoneHandler>>, Direction> srcEntry = Pair.of(weakReference, direction);
			if(!sources.contains(srcEntry)){
				sources.add(srcEntry);
				notifyInputChange(weakReference);
				//update our world redstone value, to correctly ignore sides with circuits
				updateWorldRedstone();
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
				WeakReference<LazyOptional<IRedstoneHandler>> src = sources.get(i).getLeft();
				LazyOptional<IRedstoneHandler> srcOpt;
				if((srcOpt = src.get()) != null && srcOpt.isPresent()){
					circRedstone = RedstoneUtil.chooseInput(circRedstone, RedstoneUtil.sanitize(srcOpt.orElseThrow(NullPointerException::new).getOutput()));
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
