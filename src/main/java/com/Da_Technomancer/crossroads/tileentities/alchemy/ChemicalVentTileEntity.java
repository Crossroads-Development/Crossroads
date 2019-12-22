package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;

@ObjectHolder(Crossroads.MODID)
public class ChemicalVentTileEntity extends TileEntity{

	@ObjectHolder("chemical_vent")
	private static TileEntityType<ChemicalVentTileEntity> type = null;

	public ChemicalVentTileEntity(){
		super(type);
	}

	@Override
	public void remove(){
		super.remove();
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

			ReagentMap toRelease = new ReagentMap();

			HashSet<String> validIds = new HashSet<>(4);

			for(IReagent type : reag.keySet()){
				ReagentStack r = reag.getStack(type);
				if(!r.isEmpty()){
					EnumMatterPhase phase = type.getPhase(HeatUtil.toCelcius(callerTemp));
					if(ignorePhase || (phase.flows() && (side != Direction.UP || phase.flowsDown()) && (side != Direction.DOWN || phase.flowsUp()))){
						validIds.add(type.getId());
					}
				}
			}

			for(String id : validIds){
				int moved = reag.getQty(id);
				if(moved != 0){
					toRelease.transferReagent(id, moved, reag);
				}
			}

			AlchemyUtil.releaseChemical(world, pos, toRelease);

			return toRelease.getTotalQty() != 0;
		}

		@Override
		public int getContent(IReagent type){
			return 0;
		}
	}
}
