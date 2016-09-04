package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergizeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		if(worldIn.isRemote){
			return;
		}
		if(worldIn.getTileEntity(pos) != null){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
				te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).addHeat(mult);
			}
			if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)){
				te.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN).addEnergy(mult, true, false);
			}
		}
	}

	public static class VoidEnergizeEffect implements IEffect{
		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			if(worldIn.isRemote){
				return;
			}
			if(worldIn.getTileEntity(pos) != null){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null) && te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp() >= -273D + mult){
					te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).addHeat(-mult);
				}
				if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)){
					te.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN).addEnergy(-mult, false, false);
				}
			}
		}
	}
}
