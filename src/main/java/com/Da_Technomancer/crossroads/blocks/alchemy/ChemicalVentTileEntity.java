package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.function.Function;

public class ChemicalVentTileEntity extends BlockEntity implements ITickableTileEntity, IChemicalCapable{

	public static final BlockEntityType<ChemicalVentTileEntity> TYPE = CRTileEntity.createType(ChemicalVentTileEntity::new, CRBlocks.chemicalVent);

	/*
	 * In order to make behaviour more consistent when venting large quantities or mixes (expecially phelostogen + anything else),
	 * Instead of venting immediately, wait three cycles after receiving to see if more is input. If so, vent all inputs together. Otherwise, vent the input from first cycle
	 *
	 * Combines up to 10 cycles worth of input- equal to 1 second, or 5 full phials or one full florence flask being dumped
	 */
	//Timestamp from the last received input
	private long lastInputTime = 0;
	//Stored reagents to vent
	private ReagentMap reags = new ReagentMap();
	private static final int CYCLES = 10;//The number of cycles of input to combine

	private final IChemicalHandler chemicalHandler = new AlchHandler();

	public ChemicalVentTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void serverTick(){
		if(!reags.isEmpty() && (level.getGameTime() - lastInputTime) >= (CYCLES - 1) * AlchemyUtil.ALCHEMY_TIME){
			AlchemyUtil.releaseChemical(level, worldPosition, reags);
			reags = new ReagentMap();
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		lastInputTime = nbt.getLong("last_input");
		reags = ReagentMap.readFromNBT(nbt);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putLong("last_input", lastInputTime);
		reags.write(nbt);
	}

	@Override
	public void setBlockState(BlockState pBlockState){
		super.setBlockState(pBlockState);
		//This is not, strictly speaking, optimized
		//Pre MC1.21, default behavior for all TEs was that changing blockstate invalidated capability caches
		//Post MC1.21, this is no longer the case, which opens up some opportunities for optimization
		//But everything was written with the assumption of invalidation on state change,
		//So anything other than re-implementing the old default is going to introduce a lot of new bugs
		level.invalidateCapabilities(worldPosition);
	}

	@Override
	@Nullable
	public IChemicalHandler getChemicalHandler(Direction dir){
		return chemicalHandler;
	}

	private class AlchHandler implements IChemicalHandler{

		@Override
		public EnumTransferMode getMode(Direction side){
			return EnumTransferMode.INPUT;
		}

		@Override
		public EnumContainerType getChannel(Direction side){
			return EnumContainerType.NONE;
		}

		@Override
		public int getTransferCapacity(){
			return 10;
		}

		@Override
		public double getTemp(){
			return HeatUtil.ABSOLUTE_ZERO;
		}

		@Override
		public boolean insertReagents(ReagentMap reag, Direction side, IChemicalHandler caller, Function<IReagent, Integer> maximumTransferQuantities){
			boolean acted = false;
			HashSet<IReagent> reagentTypes = new HashSet<>(reag.keySetReag());
			for(IReagent id : reagentTypes){
				int moved = Math.min(reag.getQty(id), maximumTransferQuantities.apply(id));
				if(moved > 0){
					reags.transferReagent(id, moved, reag);
					acted = true;
				}
			}
			if(acted && (level.getGameTime() - lastInputTime) > (CYCLES - 1) * AlchemyUtil.ALCHEMY_TIME){
				lastInputTime = level.getGameTime();
			}

			return acted;
		}

		@Override
		public int getContent(IReagent type){
			return 0;
		}
	}
}
