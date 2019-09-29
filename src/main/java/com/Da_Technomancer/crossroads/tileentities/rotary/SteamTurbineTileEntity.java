package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class SteamTurbineTileEntity extends ModuleTE{

	public SteamTurbineTileEntity(){
		super();
		fluidProps[0] = new TankProperty(0, CAPACITY, false, true);
		fluidProps[1] = new TankProperty(1, CAPACITY, true, false, (Fluid f) -> f == BlockSteam.getSteam());
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
		return 80;
	}

	private static final int CAPACITY = 10_000;
	public static final int LIMIT = 5;

	@Override
	public void tick(){
		super.tick();
		
		if(world.isRemote){
			IAxleHandler gear = null;
			TileEntity te = world.getTileEntity(pos.offset(Direction.UP));
			if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, Direction.DOWN)){
				gear = te.getCapability(Capabilities.AXLE_CAPABILITY, Direction.DOWN);
			}
			completion = (float) (gear == null ? 0 : gear.getAngle(0));
			return;
		}

		if(fluids[1] != null){
			int limit = fluids[1].amount / 100;
			limit = Math.min(limit, (CAPACITY - (fluids[0] == null ? 0 : fluids[0].amount)) / 100);
			limit = Math.min(limit, LIMIT);
			if(limit != 0){
				fluids[1].amount -= limit * 100;
				if(fluids[1].amount <= 0){
					fluids[1] = null;
				}
				fluids[0] = new FluidStack(BlockDistilledWater.getDistilledWater(), (fluids[0] == null ? 0 : fluids[0].amount) + (100 * limit));
				if(axleHandler.axis != null){
					axleHandler.addEnergy(((double) limit) * .1D * EnergyConverters.degPerSteamBucket(false) / EnergyConverters.degPerJoule(false), true, true);
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

	private final FluidHandler waterHandler = new FluidHandler(0);
	private final FluidHandler steamHandler = new FluidHandler(1);
	private final FluidHandler innerHandler = new FluidHandler(-1);
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(facing == null){
				return (T) innerHandler;
			}

			if(facing == Direction.DOWN){
				return (T) steamHandler;
			}else if(facing != Direction.UP){
				return (T) waterHandler;
			}
		}
		if(capability == Capabilities.AXLE_CAPABILITY && facing == Direction.UP){
			return (T) axleHandler;
		}

		return super.getCapability(capability, facing);
	}
}
