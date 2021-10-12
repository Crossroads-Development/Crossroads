package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class CrystallinePrismTileEntity extends BeamRenderTE{

	@ObjectHolder("crystal_prism")
	public static BlockEntityType<CrystallinePrismTileEntity> TYPE = null;

	private Direction dir = null;

	public CrystallinePrismTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction getDir(){
		if(dir == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.crystallinePrism){
				return Direction.NORTH;
			}
			dir = state.getValue(CRProperties.HORIZ_FACING);
		}
		return dir;
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		dir = null;
	}

	@Override
	protected void doEmit(BeamUnit out){
		Direction dir = getDir();
		//Energy
		if(beamer[dir.get3DDataValue()].emit(out.mult(1, 0, 0, 0, false), level)){
			refreshBeam(dir.get3DDataValue());
		}
		dir = dir.getClockWise();
		//Potential
		if(beamer[dir.get3DDataValue()].emit(out.mult(0, 1, 0, 0, false), level)){
			refreshBeam(dir.get3DDataValue());
		}
		dir = dir.getClockWise();
		//Stability
		if(beamer[dir.get3DDataValue()].emit(out.mult(0, 0, 1, 0, false), level)){
			refreshBeam(dir.get3DDataValue());
		}
		dir = dir.getClockWise();
		//Void
		if(beamer[dir.get3DDataValue()].emit(out.mult(0, 0, 0, 1, false), level)){
			refreshBeam(dir.get3DDataValue());
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[] {true, true, false, false, false, false};
	}

	@Override
	protected boolean[] outputSides(){
		return new boolean[] {false, false, true, true, true, true};
	}
} 
