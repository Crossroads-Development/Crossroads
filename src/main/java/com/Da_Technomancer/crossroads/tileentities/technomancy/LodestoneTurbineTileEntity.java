package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class LodestoneTurbineTileEntity extends ModuleTE{

	@ObjectHolder("lodestone_turbine")
	private static TileEntityType<LodestoneTurbineTileEntity> type = null;

	public static final double INERTIA = 300;
	public static final double MAX_SPEED = 20;

	public LodestoneTurbineTileEntity(){
		super(type);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void tick(){
		super.tick();
		if(!world.isRemote && axleHandler.axis != null && validDimension() && motData[0] < MAX_SPEED){
			motData[1] += CRConfig.lodestoneTurbinePower.get();
			markDirty();
		}
	}

	private boolean validDimension(){
		return !world.func_230315_m_().func_236043_f_();//MCP note: get dimension type from world, get clocks work property from dimensiontype. See clock item property for reference
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap == Capabilities.AXLE_CAPABILITY && side == Direction.UP){
			return (LazyOptional<T>) axleOpt;
		}
		return super.getCapability(cap, side);
	}
}
