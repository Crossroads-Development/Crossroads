package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class CrystallinePrismTileEntity extends BeamRenderTE{

	@ObjectHolder("crystal_prism")
	private static TileEntityType<CrystallinePrismTileEntity> type = null;

	private Direction dir = null;

	public CrystallinePrismTileEntity(){
		super(type);
	}

	private Direction getDir(){
		if(dir == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CrossroadsBlocks.crystallinePrism){
				return Direction.NORTH;
			}
			dir = state.get(CRProperties.HORIZ_FACING);
		}
		return dir;
	}

	@Override
	public void resetBeamer(){
		super.resetBeamer();
		dir = null;
	}

	@Override
	protected void doEmit(BeamUnit out){
		Direction dir = getDir();
		//Energy
		if(beamer[dir.getIndex()].emit(out.mult(1, 0, 0, 0, false), world)){
			refreshBeam(dir.getIndex());
		}
		dir = dir.rotateY();
		//Potential
		if(beamer[dir.getIndex()].emit(out.mult(0, 1, 0, 0, false), world)){
			refreshBeam(dir.getIndex());
		}
		dir = dir.rotateY();
		//Stability
		if(beamer[dir.getIndex()].emit(out.mult(0, 0, 1, 0, false), world)){
			refreshBeam(dir.getIndex());
		}
		dir = dir.rotateY();
		//Void
		if(beamer[dir.getIndex()].emit(out.mult(0, 0, 0, 1, false), world)){
			refreshBeam(dir.getIndex());
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
