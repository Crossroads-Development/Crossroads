package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class VoltusGeneratorTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE{

	@ObjectHolder("voltus_generator")
	public static BlockEntityType<VoltusGeneratorTileEntity> TYPE = null;

	private static final int VOLTUS_CAPACITY = 100;
	private static final int FE_CAPACITY = 100_000;
	private int voltusAmount = 0;
	private int fe = 0;

	public VoltusGeneratorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(new TranslatableComponent("tt.crossroads.voltus_generator.read", voltusAmount, VOLTUS_CAPACITY));
	}

	public int getRedstone(){
		return voltusAmount;
	}

	@Override
	public void serverTick(){
		if(voltusAmount != 0 && FE_CAPACITY - fe >= CRConfig.voltusValue.get()){
			voltusAmount -= 1;
			fe += CRConfig.voltusValue.get();
			setChanged();
		}

		for(Direction dir : Direction.values()){
			BlockEntity te = level.getBlockEntity(worldPosition.relative(dir));
			LazyOptional<IEnergyStorage> energyOpt;
			if(te != null && (energyOpt = te.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite())).isPresent()){
				IEnergyStorage storage = energyOpt.orElseThrow(NullPointerException::new);
				int moved = storage.receiveEnergy(fe, false);
				if(moved > 0){
					fe -= moved;
					setChanged();
				}
			}
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		voltusAmount = nbt.getInt("voltus");
		fe = nbt.getInt("fe");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putInt("voltus", voltusAmount);
		nbt.putInt("fe", fe);
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		chemOpt.invalidate();
		feOpt.invalidate();
	}

	private LazyOptional<IChemicalHandler> chemOpt = LazyOptional.of(AlchHandler::new);
	private LazyOptional<ElecHandler> feOpt = LazyOptional.of(ElecHandler::new);

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (LazyOptional<T>) chemOpt;
		}
		if(cap == CapabilityEnergy.ENERGY){
			return (LazyOptional<T>) feOpt;
		}
		return super.getCapability(cap, side);
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
		public boolean insertReagents(ReagentMap reag, Direction side, IChemicalHandler caller, boolean ignorePhase){
			//Only allows insertion of voltus
			if(voltusAmount >= VOLTUS_CAPACITY || reag.getQty(EnumReagents.ELEM_CHARGE.id()) == 0){
				return false;
			}

			int moved = Math.min(reag.getQty(EnumReagents.ELEM_CHARGE.id()), VOLTUS_CAPACITY - voltusAmount);
			voltusAmount += moved;
			reag.removeReagent(EnumReagents.ELEM_CHARGE.id(), moved);
			setChanged();
			return true;
		}

		@Override
		public int getContent(IReagent type){
			return type.getID().equals(EnumReagents.ELEM_CHARGE.id()) ? voltusAmount : 0;
		}
	}
}
