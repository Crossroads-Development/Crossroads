package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;

public class RotaryPumpTileEntity extends ModuleTE{

	public RotaryPumpTileEntity(){
		super();
		fluidProps[0] = new TankProperty(0, CAPACITY, false, true);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected double getMoInertia(){
		return 80;
	}

	private static final double REQUIRED = 100;
	public static final double MAX_POWER = 5;
	private double progress = 0;
	private int lastProgress = 0;


	@Override
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}

		BlockState fluidBlockstate = world.getBlockState(pos.offset(Direction.DOWN));
		Block fluidBlock = fluidBlockstate.getBlock();
		Fluid fl = FluidRegistry.lookupFluidForBlock(fluidBlock);
		//If anyone knows a builtin way to simplify this if statement, be my guest. It's so long it scares me...
		//2019-29-1: Looking back on this if statement 2 years later, this is a perfectly reasonable length and not at all scary. If anything, it's too short. If anyone can find a way to make it longer, that would be appreciated
		if(fl != null && (fluidBlock instanceof BlockFluidClassic && ((BlockFluidClassic) fluidBlock).isSourceBlock(world, pos.offset(Direction.DOWN)) || fluidBlockstate.get(BlockLiquid.LEVEL) == 0) && (fluids[0] == null || (CAPACITY - fluids[0].amount >= 1000 && fluids[0].getFluid() == fl))){
			double holder = motData[1] < 0 ? 0 : Math.min(Math.min(MAX_POWER, motData[1]), REQUIRED - progress);
			motData[1] -= holder;
			progress += holder;
		}else{
			progress = 0;
		}

		if(progress >= REQUIRED){
			progress = 0;
			fluids[0] = new FluidStack(fl, 1000 + (fluids[0] == null ? 0 : fluids[0].amount));
			world.setBlockToAir(pos.offset(Direction.DOWN));
		}

		if(lastProgress != (int) progress){
			CrossroadsPackets.network.sendToAllAround(new SendLongToClient((byte) 1, (int) progress, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			lastProgress = (int) progress;
		}
	}

	private static final int CAPACITY = 4_000;

	public float getCompletion(){
		return ((float) progress) / ((float) REQUIRED);
	}

	@Override
	public void receiveLong(byte identifier, long message, ServerPlayerEntity player){
		super.receiveLong(identifier, message, player);
		if(identifier == 1){
			progress = message;
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		progress = nbt.getDouble("prog");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putDouble("prog", progress);
		return nbt;
	}

	private final FluidHandler fluidHandler = new FluidHandler(0);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) fluidHandler;
		}
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == Direction.UP || facing == null)){
			return (T) axleHandler;
		}

		return super.getCapability(capability, facing);
	}
}
