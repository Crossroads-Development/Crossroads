package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class FluidInjectorTileEntity extends AlchemyCarrierTE{

	@ObjectHolder("fluid_injector")
	public static BlockEntityType<FluidInjectorTileEntity> TYPE = null;

	public FluidInjectorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public FluidInjectorTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.BOTH, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		fluidOpt.invalidate();
	}

	private final LazyOptional<IFluidHandler> fluidOpt = LazyOptional.of(() -> falseFluidHandler);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side == Direction.DOWN)){
			return (LazyOptional<T>) chemOpt;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(side == null || side == Direction.UP){
				return (LazyOptional<T>) fluidOpt;
			}
		}
		return super.getCapability(cap, side);
	}
}
