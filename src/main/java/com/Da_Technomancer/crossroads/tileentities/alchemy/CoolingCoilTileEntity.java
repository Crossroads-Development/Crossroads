package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class CoolingCoilTileEntity extends AlchemyCarrierTE{

	@ObjectHolder("cooling_coil")
	private static TileEntityType<CoolingCoilTileEntity> type = null;

	//Cached biome temperature
	private boolean init = false;
	private double ambientTemp = 0;

	public CoolingCoilTileEntity(){
		super(type);
	}

	public CoolingCoilTileEntity(boolean glass){
		super(type, glass);
	}

	private double calcAmbTemp(){
		if(!init){
			init = true;
			ambientTemp = HeatUtil.convertBiomeTemp(world, pos);
		}
		return ambientTemp;
	}

	@Override
	public double correctTemp(){
		//Shares heat between internal cable & contents
		double temp = calcAmbTemp();
		contents.setTemp(temp);
		return temp;
	}

	public void rotate(){
		chemOpt.invalidate();
		chemOpt = LazyOptional.of(() -> handler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).get(CRProperties.HORIZ_FACING).getAxis())){
			return (LazyOptional<T>) chemOpt;
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		Direction outSide = world.getBlockState(pos).get(CRProperties.HORIZ_FACING);
		output[outSide.getIndex()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().getIndex()] = EnumTransferMode.INPUT;
		return output;
	}
	
	@Override
	protected Vector3d getParticlePos(){
		return Vector3d.copy(pos).add(0.5D, 0.3D, 0.5D);//We add the offset ourselves for finer precision
		
	}
}
