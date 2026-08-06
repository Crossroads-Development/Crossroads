package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.api.electric.IEnergyCapable;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Function;

public class VoltusGeneratorTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE, IChemicalCapable, IEnergyCapable{

	public static final BlockEntityType<VoltusGeneratorTileEntity> TYPE = CRTileEntity.createType(VoltusGeneratorTileEntity::new, CRBlocks.voltusGenerator);

	private static final int VOLTUS_CAPACITY = 100;
	private static final int FE_CAPACITY = 1_000_000;
	private int voltusAmount = 0;
	private int fuelTime = 0;
	public static final int VOLTUS_FUEL_TIME = AlchemyUtil.ALCHEMY_TIME;
	private int fe = 0;

	private IChemicalHandler chemicalHandler = new AlchHandler();
	private ElecHandler energyHandler = new ElecHandler();

	public VoltusGeneratorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.voltus_generator.read", voltusAmount, VOLTUS_CAPACITY));
	}

	public int getRedstone(){
		return voltusAmount;
	}

	@Override
	public void serverTick(){
		if(voltusAmount + fuelTime != 0 && FE_CAPACITY - fe >= CRConfig.voltusValue.get()){
			if(fuelTime == 0){
				fuelTime = VOLTUS_FUEL_TIME;
				voltusAmount -= 1;
			}
			fuelTime -= 1;
			fe += CRConfig.voltusValue.get();
			setChanged();
		}

		for(Direction dir : Direction.values()){
			BlockPos relPos = worldPosition.relative(dir);
			IEnergyStorage energyHandler;
			if((energyHandler = level.getCapability(Capabilities.EnergyStorage.BLOCK, relPos, dir.getOpposite())) != null){
				int moved = energyHandler.receiveEnergy(fe, false);
				if(moved > 0){
					fe -= moved;
					setChanged();
				}
			}
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		voltusAmount = nbt.getInt("voltus");
		fuelTime = nbt.getInt("fuel_time");
		fe = nbt.getInt("fe");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("voltus", voltusAmount);
		nbt.putInt("fe", fe);
		nbt.putInt("fuel_time", fuelTime);
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

	@Nullable
	@Override
	public IEnergyStorage getEnergyHandler(Direction dir){
		return energyHandler;
	}

	private class ElecHandler implements IEnergyStorage{


		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			int toMove = Math.min(maxExtract, fe);
			if(!simulate){
				fe -= toMove;
				setChanged();
			}
			return toMove;
		}

		@Override
		public int getEnergyStored(){
			return fe;
		}

		@Override
		public int getMaxEnergyStored(){
			return FE_CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return true;
		}

		@Override
		public boolean canReceive(){
			return false;
		}
	}

	private class AlchHandler implements IChemicalHandler{

		@Override
		public EnumTransferMode getMode(Direction side){
			return side == Direction.DOWN ? EnumTransferMode.INPUT : EnumTransferMode.NONE;
		}

		@Override
		public EnumContainerType getChannel(Direction side){
			return EnumContainerType.CRYSTAL;
		}

		@Override
		public int getTransferCapacity(){
			return VOLTUS_CAPACITY;
		}

		@Override
		public double getTemp(){
			return HeatUtil.ABSOLUTE_ZERO;
		}

		@Override
		public boolean insertReagents(ReagentMap reag, Direction side, IChemicalHandler caller, Function<IReagent, Integer> maximumTransferQuantities){
			//Only allows insertion of voltus
			int moved = Math.min(Math.min(reag.getQty(EnumReagents.ELEM_CHARGE.id()), VOLTUS_CAPACITY - voltusAmount), maximumTransferQuantities.apply(ReagentManager.getReagent(EnumReagents.ELEM_CHARGE.id())));
			if(moved > 0){
				voltusAmount += moved;
				reag.removeReagent(EnumReagents.ELEM_CHARGE.id(), moved);
				setChanged();
				return true;
			}
			return false;
		}

		@Override
		public int getContent(IReagent type){
			return type.getID().equals(EnumReagents.ELEM_CHARGE.id()) ? voltusAmount : 0;
		}
	}
}
