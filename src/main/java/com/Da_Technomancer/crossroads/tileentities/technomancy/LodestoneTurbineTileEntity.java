package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class LodestoneTurbineTileEntity extends ModuleTE{

	@ObjectHolder("lodestone_turbine")
	private static BlockEntityType<LodestoneTurbineTileEntity> type = null;

	public static final double INERTIA = 300;
	public static final double MAX_SPEED = 10;

	public LodestoneTurbineTileEntity(BlockPos pos, BlockState state){
		super(type, pos, state);
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
		if(!level.isClientSide && axleHandler.axis != null && validDimension() && axleHandler.getSpeed() < MAX_SPEED){
			axleHandler.addEnergy(CRConfig.lodestoneTurbinePower.get(), true);
			setChanged();
		}
	}

	private boolean validDimension(){
		return !level.dimensionType().natural();//See clock item property for reference
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
