package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeamSiphonTileEntity extends BeamRenderTE{

	@ObjectHolder("beam_siphon")
	public static BlockEntityType<BeamSiphonTileEntity> TYPE = null;

	public BeamSiphonTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction dir = null;

	private Direction getDir(){
		if(dir == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.beamSiphon){
				return Direction.NORTH;
			}
			dir = state.getValue(ESProperties.FACING);
		}
		return dir;
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		dir = null;
	}

	@Override
	public CompoundTag m_6945_(CompoundTag nbt){
		super.m_6945_(nbt);
		redsHandler.write(nbt);
		return nbt;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		redsHandler.read(nbt);
	}

	public int getPowerInput(){
		return Math.max(0, Math.round(CircuitUtil.combineRedsSources(redsHandler)));
	}

	@Override
	protected void doEmit(BeamUnit out){
		int toFill = getPowerInput();
		Direction facing = getDir();
		BeamUnit toDraw;
		BeamUnit remain;

		if(out.isEmpty() || toFill == 0){
			toDraw = BeamUnit.EMPTY;
			remain = out;
		}else{
			toDraw = new BeamUnit(MiscUtil.withdrawExact(out.getValues(), toFill));
			remain = new BeamUnit(out.getEnergy() - toDraw.getEnergy(), out.getPotential() - toDraw.getPotential(), out.getStability() - toDraw.getStability(), out.getVoid() - toDraw.getVoid());
		}

		if(beamer[facing.get3DDataValue()].emit(toDraw, level)){
			refreshBeam(facing.get3DDataValue());
		}
		if(beamer[facing.getOpposite().get3DDataValue()].emit(remain, level)){
			refreshBeam(facing.getOpposite().get3DDataValue());
		}
//		if(toFill < out.getPower()){
//			int[] output = out.mult(((double) toFill) / ((double) out.getPower()), true).getValues();//Use the floor formula as a starting point
//			int[] stored = out.getValues();
//			int available = 0;
//
//			for(int i = 0; i < 4; i++){
//				stored[i] -= output[i];
//				available += stored[i];
//				toFill -= output[i];
//			}
//
//			toFill = Math.min(toFill, available);
////			available -= toFill;
//
//			int source = 0;
//
//			//Round-robin distribution of drawing additional power from storage to meet the quota
//			//Ignoring the source element ratio, as toFill << RATES[storage] in most cases, making the effect on ratio minor
//			for(int i = 0; i < toFill; i++){
//				while(stored[source] == 0){
//					source++;
//				}
//				output[source]++;
//				stored[source]--;
//				source++;
//			}
//			toDraw = new BeamUnit(output);
//			remain = new BeamUnit(stored[0], stored[1], stored[2], stored[3]);
//		}else{
//			toDraw = out;
//			remain = BeamUnit.EMPTY;
//		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] input = new boolean[] {true, true, true, true, true, true};
		Direction facing = getDir();
		input[facing.get3DDataValue()] = false;
		input[facing.getOpposite().get3DDataValue()] = false;
		return input;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] output = new boolean[6];
		Direction facing = getDir();
		output[facing.get3DDataValue()] = true;
		output[facing.getOpposite().get3DDataValue()] = true;
		return output;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		redsOpt.invalidate();
	}

	private void updateSignalState(){
		setChanged();
		BlockState state = getBlockState();
		int powerLevel = getPowerInput();
		if(powerLevel > 0){
			if(!state.getValue(ESProperties.REDSTONE_BOOL)){
				level.setBlockAndUpdate(worldPosition, state.setValue(ESProperties.REDSTONE_BOOL, true));
			}
		}else if(state.getValue(ESProperties.REDSTONE_BOOL)){
			level.setBlockAndUpdate(worldPosition, state.setValue(ESProperties.REDSTONE_BOOL, false));
		}
	}

	public final CircuitUtil.InputCircHandler redsHandler = new CircuitUtil.InputCircHandler();
	private final LazyOptional<IRedstoneHandler> redsOpt = CircuitUtil.makeBaseCircuitOptional(this, redsHandler, 0, this::updateSignalState);

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir){
		if(cap == RedstoneUtil.REDSTONE_CAPABILITY){
			return (LazyOptional<T>) redsOpt;
		}
		return super.getCapability(cap, dir);
	}
} 
