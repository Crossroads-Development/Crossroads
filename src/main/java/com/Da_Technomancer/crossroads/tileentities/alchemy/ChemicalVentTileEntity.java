package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;

@ObjectHolder(Crossroads.MODID)
public class ChemicalVentTileEntity extends BlockEntity implements TickableBlockEntity{

	@ObjectHolder("chemical_vent")
	private static BlockEntityType<ChemicalVentTileEntity> type = null;

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

	public ChemicalVentTileEntity(){
		super(type);
	}

	@Override
	public void tick(){
		if(!reags.isEmpty() && (level.getGameTime() - lastInputTime) >= (CYCLES - 1) * AlchemyUtil.ALCHEMY_TIME){
			AlchemyUtil.releaseChemical(level, worldPosition, reags);
			reags = new ReagentMap();
		}
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		lastInputTime = nbt.getLong("last_input");
		reags = ReagentMap.readFromNBT(nbt);
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putLong("last_input", lastInputTime);
		reags.write(nbt);
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		alcOpt.invalidate();
	}

	private final LazyOptional<IChemicalHandler> alcOpt = LazyOptional.of(AlchHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (LazyOptional<T>) alcOpt;
		}
		return super.getCapability(cap, side);
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
		public boolean insertReagents(ReagentMap reag, Direction side, IChemicalHandler caller, boolean ignorePhase){
			double callerTemp = reag.getTempK();

			HashSet<String> validIds = new HashSet<>(4);

			for(IReagent type : reag.keySetReag()){
				ReagentStack r = reag.getStack(type);
				if(!r.isEmpty()){
					EnumMatterPhase phase = type.getPhase(HeatUtil.toCelcius(callerTemp));
					if(ignorePhase || (phase.flows() && (side != Direction.UP || phase.flowsDown()) && (side != Direction.DOWN || phase.flowsUp()))){
						validIds.add(type.getID());
					}
				}
			}

			boolean acted = false;
			for(String id : validIds){
				int moved = reag.getQty(id);
				if(moved != 0){
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
