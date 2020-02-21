package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class RedstoneHeatCableTileEntity extends HeatCableTileEntity{

	@ObjectHolder("redstone_heat_cable")
	private static TileEntityType<RedstoneHeatCableTileEntity> type = null;

	public RedstoneHeatCableTileEntity(){
		this(HeatInsulators.WOOL);
	}

	public RedstoneHeatCableTileEntity(HeatInsulators insulator){
		super(type);
		this.insulator = insulator;
	}

	private boolean isUnlocked(){
		return world.getBlockState(pos).get(ESProperties.REDSTONE_BOOL);
	}

	@Override
	public void tick(){
		if(isUnlocked()){
			super.tick();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			if((facing == null || !locked(facing.getIndex())) && isUnlocked()){
				return (LazyOptional<T>) heatOpt;
			}else{
				return LazyOptional.empty();
			}
		}
		return super.getCapability(capability, facing);
	}

	public float getTemp(){
		if(isUnlocked()){
			return (float) HeatUtil.toKelvin(temp);
		}
		return 0;
	}
}
