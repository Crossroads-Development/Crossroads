package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CircuitUtil;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class BeamSplitterTileEntity extends BeamRenderTE{

	public static final BlockEntityType<BeamSplitterTileEntity> TYPE = CRTileEntity.createType(BeamSplitterTileEntity::new, CRBlocks.beamSplitter);

	private Direction dir = null;

	public BeamSplitterTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction getDir(){
		if(dir == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.beamSplitter){
				return Direction.NORTH;
			}
			dir = state.getValue(CRProperties.FACING);
		}
		return dir;
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		dir = null;
	}

	public float getPowerMultiplier(){
		return Math.max(0, Math.min(1F, CircuitUtil.combineRedsSources(redsHandler) / 15F));
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		redsHandler.write(nbt);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		redsHandler.read(nbt);
	}

	@Override
	protected void doEmit(BeamUnit out){
		int toFill = Math.round(out.getPower() * getPowerMultiplier());
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

	private void updateSignalState(){
		setChanged();
		BlockState state = getBlockState();
		float powerLevel = getPowerMultiplier();
		int prevPowerLevel = state.getValue(CRProperties.POWER_LEVEL);
		if(powerLevel <= 0 && prevPowerLevel != 0){
			level.setBlockAndUpdate(worldPosition, state.setValue(CRProperties.POWER_LEVEL, 0));
		}else if(powerLevel >= 1F && prevPowerLevel != 2){
			level.setBlockAndUpdate(worldPosition, state.setValue(CRProperties.POWER_LEVEL, 2));
		}else if(powerLevel > 0 && powerLevel < 1F && prevPowerLevel != 1){
			level.setBlockAndUpdate(worldPosition, state.setValue(CRProperties.POWER_LEVEL, 1));
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		redsOpt.invalidate();
	}

	public CircuitUtil.InputCircHandler redsHandler = new CircuitUtil.InputCircHandler();
	private LazyOptional<IRedstoneHandler> redsOpt = CircuitUtil.makeBaseCircuitOptional(this, redsHandler, 0, this::updateSignalState);

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir){
		if(cap == RedstoneUtil.REDSTONE_CAPABILITY){
			return (LazyOptional<T>) redsOpt;
		}
		return super.getCapability(cap, dir);
	}
} 
