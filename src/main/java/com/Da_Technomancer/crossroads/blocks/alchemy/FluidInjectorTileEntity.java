package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentHolderTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class FluidInjectorTileEntity extends ReagentHolderTE implements IFluidCapable{

	public static final BlockEntityType<FluidInjectorTileEntity> TYPE = CRTileEntity.createType(FluidInjectorTileEntity::new, CRBlocks.fluidInjectorCrystal, CRBlocks.fluidInjectorGlass);

	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE = new Pair[] {Pair.of(new Vector3f(5F / 16F, 1F / 16F, 5F / 16F), new Vector3f(11F / 16F, 15F / 16F, 11F / 16F))};

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
	@Nullable
	public IChemicalHandler getChemicalHandler(Direction dir){
		if(dir == null || dir == Direction.DOWN){
			return chemHandler;
		}
		return null;
	}

	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction dir){
		//Allow pipes access to the internal fluid handler normally used for bucketing fluids in & out
		if(dir == null || dir == Direction.UP){
			return getInternalFluidHandler();
		}
		return null;
	}

	@Override
	public Pair<Vector3f, Vector3f>[] getRenderVolumes(){
		return RENDER_SHAPE;
	}
}
