package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeamSiphonTileEntity extends BeamRenderTE{

	@ObjectHolder("beam_siphon")
	private static TileEntityType<BeamSiphonTileEntity> type = null;

	public BeamSiphonTileEntity(){
		super(type);
	}

	private int redstone;

	private Direction dir = null;

	private Direction getDir(){
		if(dir == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.beamSiphon){
				return Direction.NORTH;
			}
			dir = state.get(ESProperties.FACING);
		}
		return dir;
	}

	@Override
	public void updateContainingBlockInfo(){
		super.updateContainingBlockInfo();
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
		nbt.putFloat("circ_reds", redsHandler.getCircRedstone());
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		redstone = nbt.getInt("reds");
		redsHandler.setCircRedstone(nbt.getFloat("circ_reds"));
	}

	@Override
	protected void doEmit(BeamUnit out){
		//As it would turn out, the problem of meeting a quota for the sum of values drawn from a limited source while also approximately maintaining the source ratio is quite messy when all values must be integers
		//This is about as clean an implementation as is possible
		int toFill = Math.round(CircuitUtil.combineRedsSources(redsHandler, redstone));
		Direction facing = getDir();
		BeamUnit toDraw;
		BeamUnit remain;

		if(out.isEmpty() || toFill == 0){
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
//			available -= toFill;

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
			remain = new BeamUnit(stored[0], stored[1], stored[2], stored[3]);
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

	public CircuitUtil.CircHandler redsHandler = new CircuitUtil.CircHandler();
	private LazyOptional<IRedstoneHandler> redsOpt = CircuitUtil.makeBaseCircuitOptional(this, redsHandler, 0);

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir){
		if(cap == RedstoneUtil.REDSTONE_CAPABILITY){
			return (LazyOptional<T>) redsOpt;
		}
		return super.getCapability(cap, dir);
	}
} 
