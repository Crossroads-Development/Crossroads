package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidVoidTileEntity extends BlockEntity implements IFluidCapable{

	public static final BlockEntityType<FluidVoidTileEntity> TYPE = CRTileEntity.createType(FluidVoidTileEntity::new, CRBlocks.fluidVoid);

	public FluidVoidTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private final IFluidHandler mainOpt = new VoidHandler();


	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction direction){
		return mainOpt;
	}


	private static class VoidHandler implements IFluidHandler{

		@Override
		public int getTanks(){
			return 1;
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tank){
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank){
			return 10_000;
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
			return true;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action){
			return resource.getAmount();
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action){
			return FluidStack.EMPTY;
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			return FluidStack.EMPTY;
		}
	}
}
