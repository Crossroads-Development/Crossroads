package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.BeamUnitStorage;
import com.Da_Technomancer.crossroads.api.beams.BeamUtil;
import com.Da_Technomancer.crossroads.api.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ClockworkStabilizerTileEntity extends BeamRenderTE{

	public static final BlockEntityType<ClockworkStabilizerTileEntity> TYPE = CRTileEntity.createType(ClockworkStabilizerTileEntity::new, CRBlocks.clockworkStabilizer);

	public static final double RATE = 0.2D;
	private BeamUnitStorage storage = new BeamUnitStorage();
	private Direction dir = null;

	public ClockworkStabilizerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	protected int getLimit(){
		return (int) (BeamUtil.POWER_LIMIT / RATE);//This block can store 5 times as much so it emits a full power beam at capacity
	}

	private Direction getDir(){
		if(dir == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.clockworkStabilizer){
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

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		storage.writeToNBT("stab_mag", nbt);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		storage = BeamUnitStorage.readFromNBT("stab_mag", nbt);
	}

	@Override
	protected void doEmit(BeamUnit toEmit){
		storage.addBeam(toEmit);

		//Enforce LIMIT
		if(storage.getPower() > getLimit()){
			BeamUnit stored = storage.getOutput();
			storage.clear();
			storage.addBeam(stored.mult((double) getLimit() / (double) stored.getPower(), true));
		}

		Direction dir = getDir();

		if(!storage.isEmpty()){
			double toWithdraw = RATE * storage.getPower();
			if(toWithdraw < 1){
				toWithdraw = 1;//Withdraw a minimum of 1, to prevent a small quantity getting 'stuck'
			}else{
				toWithdraw = Math.round(toWithdraw);
			}
			BeamUnit output = new BeamUnit(MiscUtil.withdrawExact(storage.getOutput().getValues(), (int) toWithdraw));
			storage.subtractBeam(output);
			if(beamer[dir.get3DDataValue()].emit(output, level)){
				refreshBeam(dir.get3DDataValue());
			}
		}else if(beamer[dir.get3DDataValue()].emit(BeamUnit.EMPTY, level)){
			refreshBeam(dir.get3DDataValue());
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] out = {true, true, true, true, true, true};
		out[getDir().get3DDataValue()] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[getDir().get3DDataValue()] = true;
		return out;
	}

	public int getRedstone(){
		return storage.getPower();
	}
} 
