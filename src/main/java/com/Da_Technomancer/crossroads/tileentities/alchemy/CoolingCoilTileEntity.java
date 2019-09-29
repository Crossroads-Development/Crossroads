package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CrossroadsProperties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class CoolingCoilTileEntity extends AlchemyCarrierTE{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private double ambientTemp = 0;
	private boolean init = false;

	public CoolingCoilTileEntity(){
		super();
	}

	public CoolingCoilTileEntity(boolean glass){
		super(glass);
	}

	@Override
	public double correctTemp(){
		//Shares heat between internal cable & contents
		contents.setTemp(ambientTemp);
		return ambientTemp;
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}
		if(!init){
			ambientTemp = HeatUtil.convertBiomeTemp(world, pos);
			init = true;
		}
		super.tick();
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		ambientTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("initHeat");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putDouble("temp", ambientTemp);
		nbt.putBoolean("initHeat", init);
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).get(CrossroadsProperties.HORIZ_FACING).getAxis())){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).get(CrossroadsProperties.HORIZ_FACING).getAxis())){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		Direction outSide = world.getBlockState(pos).get(CrossroadsProperties.HORIZ_FACING);
		output[outSide.getIndex()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().getIndex()] = EnumTransferMode.INPUT;
		return output;
	}
	
	@Override
	protected Vec3d getParticlePos(){
		return new Vec3d(pos).add(0.5D, 0.3D, 0.5D);
		
	}
}
