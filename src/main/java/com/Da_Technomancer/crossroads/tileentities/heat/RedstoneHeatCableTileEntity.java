package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class RedstoneHeatCableTileEntity extends HeatCableTileEntity{

	public RedstoneHeatCableTileEntity(){
		this(HeatInsulators.WOOL);
	}

	public RedstoneHeatCableTileEntity(HeatInsulators insulator){
		super(insulator);
	}

	@Override
	public void update(){
		if(world.getBlockState(pos).getValue(EssentialsProperties.REDSTONE_BOOL)){
			super.update();
		}
	}

	private final RedstoneHandler redstoneHandler = new RedstoneHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY){
			if(world.getBlockState(pos).getValue(EssentialsProperties.REDSTONE_BOOL)){
				return (T) heatHandler;
			}else{
				return null;
			}
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return (T) redstoneHandler;
		}
		return super.getCapability(capability, facing);
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean read){
			if(!read || !world.getBlockState(pos).getValue(EssentialsProperties.REDSTONE_BOOL) || insulator == null){
				return 0;
			}
			return 16D * HeatUtil.toKelvin(temp) / HeatUtil.toKelvin(insulator.getLimit());
		}
	}
}
