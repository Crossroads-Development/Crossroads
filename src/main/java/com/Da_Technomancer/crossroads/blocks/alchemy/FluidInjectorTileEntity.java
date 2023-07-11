package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidInjectorTileEntity extends AlchemyCarrierTE{

	public static final BlockEntityType<FluidInjectorTileEntity> TYPE = CRTileEntity.createType(FluidInjectorTileEntity::new, CRBlocks.fluidInjectorCrystal, CRBlocks.fluidInjectorGlass);

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
		if(cap == ForgeCapabilities.FLUID_HANDLER){
			if(side == null || side == Direction.UP){
				return (LazyOptional<T>) fluidOpt;
			}
		}
		return super.getCapability(cap, side);
	}
}
