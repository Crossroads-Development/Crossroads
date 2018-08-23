package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.magic.MagicUnitStorage;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClockworkStabilizerTileEntity extends BeamRenderTE{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private static final double RATE = 0.25D;
	private static final int LIMIT = 0x0FFFFFFF;//While the documentation claims there is no storage limit to this device, there is of course the practical limit of 32 bits. In order to prevent an integer overflow error, a limit smaller than Integer.MAX_VALUE (but still extremely large) is imposed.
	private MagicUnitStorage storage = new MagicUnitStorage();

	public ClockworkStabilizerTileEntity(){
		super();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		storage.writeToNBT("stabMag", nbt);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		storage = MagicUnitStorage.readFromNBT("stabMag", nbt);
	}

	@Override
	protected void doEmit(MagicUnit toEmit){
		storage.addMagic(toEmit);

		//Enforce LIMIT
		if(storage.getOutput() != null && storage.getOutput().getPower() > LIMIT){
			MagicUnit stored = storage.getOutput();
			storage.clear();
			storage.addMagic(stored.mult((double) LIMIT / (double) stored.getPower(), false));
		}

		EnumFacing dir = world.getBlockState(pos).getValue(EssentialsProperties.FACING);

		if(!storage.isEmpty()){
			MagicUnit mag = storage.getOutput().mult(RATE, true);
			storage.subtractMagic(mag);
			beamer[dir.getIndex()].emit(mag.getPower() == 0 ? null : mag, world);

		}else{
			beamer[dir.getIndex()].emit(null, world);

		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] out = {true, true, true, true, true, true};
		out[world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex()] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = new boolean[6];
		out[world.getBlockState(pos).getValue(EssentialsProperties.FACING).getIndex()] = true;
		return out;
	}
} 
