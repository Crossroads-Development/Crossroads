package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class SteamTurbineTileEntity extends ModuleTE{

	@ObjectHolder("steam_turbine")
	private static TileEntityType<SteamTurbineTileEntity> type = null;

	public static final double INERTIA = 80D;
	private static final int CAPACITY = 10_000;
	public static final int LIMIT = 5;

	public SteamTurbineTileEntity(){
		super(type);
		fluidProps[0] = new TankProperty(CAPACITY, false, true);
		fluidProps[1] = new TankProperty(CAPACITY, true, false, (Fluid f) -> f == CRFluids.steam.still);
	}

	@Override
	protected int fluidTanks(){
		return 2;
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
		
		if(world.isRemote){
			IAxleHandler gear = null;
			TileEntity te = world.getTileEntity(pos.offset(Direction.UP));
			LazyOptional<IAxleHandler> axleOpt;
			if(te != null && (axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, Direction.DOWN)).isPresent()){
				gear = axleOpt.orElseThrow(NullPointerException::new);
			}
			completion = (gear == null ? 0 : gear.getAngle(0));
			return;
		}

		if(!fluids[1].isEmpty()){
			int limit = fluids[1].getAmount() / 100;
			limit = Math.min(limit, (CAPACITY - fluids[0].getAmount()) / 100);
			limit = Math.min(limit, LIMIT);
			if(limit != 0){
				fluids[1].shrink(limit * 100);
				if(fluids[0].isEmpty()){
					fluids[0] = new FluidStack(CRFluids.distilledWater.still, 100 * limit);
				}else{
					fluids[0].grow(100 * limit);
				}
				if(axleHandler.axis != null){
					axleHandler.addEnergy(((double) limit) * .1D * EnergyConverters.degPerSteamBucket() / EnergyConverters.degPerJoule(), true, true);
				}
			}
		}
	}
	
	private float completion;
	
	/**
	 * This uses the angle of the attached gear instead of calculating its own for a few reasons. It will always be attached when it should spin, and should always have the same angle as the attached gear (no point calculating).
	 */
	@OnlyIn(Dist.CLIENT)
	public float getCompletion(){
		return completion;
	}

	@Override
	public void remove(){
		super.remove();
		waterOpt.invalidate();
		steamOpt.invalidate();
	}

	private final LazyOptional<IFluidHandler> waterOpt = LazyOptional.of(() -> new FluidHandler(0));
	private final LazyOptional<IFluidHandler> steamOpt = LazyOptional.of(() -> new FluidHandler(1));
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(facing == null){
				return (LazyOptional<T>) globalFluidOpt;
			}

			if(facing == Direction.DOWN){
				return (LazyOptional<T>) steamOpt;
			}else if(facing != Direction.UP){
				return (LazyOptional<T>) waterOpt;
			}
		}
		if(capability == Capabilities.AXLE_CAPABILITY && facing == Direction.UP){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(capability, facing);
	}
}
