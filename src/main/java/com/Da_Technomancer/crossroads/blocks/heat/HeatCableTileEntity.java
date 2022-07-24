package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.api.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.items.item_sets.HeatCableFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class HeatCableTileEntity extends ModuleTE implements ConduitBlock.IConduitTE<EnumTransferMode>{

	public static final BlockEntityType<HeatCableTileEntity> TYPE = CRTileEntity.createType(HeatCableTileEntity::new, HeatCableFactory.HEAT_CABLES.values().toArray(new HeatCable[0]));

	@SuppressWarnings("unchecked")//Darn Java, not being able to verify arrays of parameterized types. Bah Humbug!
	protected final LazyOptional<IHeatHandler>[] neighCache = new LazyOptional[] {LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty()};
	protected HeatInsulators insulator;
	protected boolean[] matches = new boolean[6];
	protected EnumTransferMode[] modes = ConduitBlock.IConduitTE.genModeArray(EnumTransferMode.BOTH);

	public HeatCableTileEntity(BlockPos pos, BlockState state){
		this(pos, state, state.getBlock() instanceof HeatCable hc ? hc.insulator : HeatInsulators.WOOL);
	}

	public HeatCableTileEntity(BlockPos pos, BlockState state, HeatInsulators insulator){
		super(TYPE, pos, state);
		this.insulator = insulator;
	}

	protected HeatCableTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, HeatInsulators insulator){
		super(type, pos, state);
		this.insulator = insulator;
	}

	private Double biomeTempCache = null;

	protected double getBiomeTemp(){
		if(biomeTempCache == null){
			biomeTempCache = HeatUtil.convertBiomeTemp(level, worldPosition);
		}
		return biomeTempCache;
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		//When adjusting a side to lock, we need to invalidate the optional in case a side was disconnected
		heatOpt.invalidate();
		heatOpt = LazyOptional.of(this::createHeatHandler);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	protected HeatHandler createHeatHandler(){
		return new CableHeatHandler();
	}

	protected boolean locked(int side){
		return !modes[side].isConnection();
	}

	@Override
	public void serverTick(){
		super.serverTick();

		double prevTemp = temp;

		//Heat transfer
		ArrayList<IHeatHandler> heatHandlers = new ArrayList<>(6);
		for(Direction side : Direction.values()){
			if(locked(side.get3DDataValue())){
				continue;
			}
			LazyOptional<IHeatHandler> otherOpt = neighCache[side.get3DDataValue()];
			if(!neighCache[side.get3DDataValue()].isPresent()){
				BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
				if(te != null){
					otherOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, side.getOpposite());
					neighCache[side.get3DDataValue()] = otherOpt;
				}
			}

			if(otherOpt.isPresent()){
				IHeatHandler handler = otherOpt.orElseThrow(NullPointerException::new);
				temp += handler.getTemp();
//				handler.addHeat(-handler.getTemp());
				heatHandlers.add(handler);
				setData(side.get3DDataValue(), true, modes[side.get3DDataValue()]);
			}else{
				setData(side.get3DDataValue(), false, modes[side.get3DDataValue()]);
			}
		}

		temp /= heatHandlers.size() + 1;

		for(IHeatHandler handler : heatHandlers){
			handler.addHeat(temp - handler.getTemp());
		}

		temp = runLoss();

		if(temp != prevTemp){
			setChanged();
		}

		if(temp > insulator.getLimit()){
			if(CRConfig.heatEffects.get()){
				insulator.getEffect().doEffect(level, worldPosition);
			}else{
				level.setBlock(worldPosition, Blocks.FIRE.defaultBlockState(), 3);
			}
		}
	}

	protected double runLoss(){
		//Does not change the temperature- only does the calculation
		//Energy loss
		double biomeTemp = getBiomeTemp();
		return temp + Math.min(insulator.getRate(), Math.abs(temp - biomeTemp)) * Math.signum(biomeTemp - temp);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		ConduitBlock.IConduitTE.readConduitNBT(nbt, this);
		insulator = nbt.contains("insul") ? HeatInsulators.valueOf(nbt.getString("insul")) : HeatInsulators.WOOL;
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		ConduitBlock.IConduitTE.writeConduitNBT(nbt, this);
		nbt.putString("insul", insulator.name());
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		saveAdditional(nbt);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY && (facing == null || !locked(facing.get3DDataValue()))){
			return (LazyOptional<T>) heatOpt;
		}
		return super.getCapability(capability, facing);
	}

	@Nonnull
	@Override
	public boolean[] getHasMatch(){
		return matches;
	}

	@Nonnull
	@Override
	public EnumTransferMode[] getModes(){
		return modes;
	}

	@Nonnull
	@Override
	public EnumTransferMode deserialize(String name){
		return ConduitBlock.IConduitTE.deserializeEnumMode(name);
	}

	@Override
	public boolean hasMatch(int side, EnumTransferMode mode){
		Direction face = Direction.from3DDataValue(side);
		BlockEntity neighTE = level.getBlockEntity(worldPosition.relative(face));
		return neighTE != null && neighTE.getCapability(Capabilities.HEAT_CAPABILITY, face.getOpposite()).isPresent();
	}

	private class CableHeatHandler extends HeatHandler{

		@Override
		public void init(){
			if(!initHeat){
				if(insulator == HeatInsulators.ICE){
					temp = -10;
				}else{
					temp = getBiomeTemp();
				}
				initHeat = true;
				setChanged();
			}
		}
	}
}
