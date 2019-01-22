package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashSet;

public class ChemicalVentTileEntity extends TileEntity{

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	private final AlchHandler handler = new AlchHandler();

	private class AlchHandler implements IChemicalHandler{

		@Override
		public EnumTransferMode getMode(EnumFacing side){
			return EnumTransferMode.INPUT;
		}

		@Override
		public EnumContainerType getChannel(EnumFacing side){
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
		public boolean insertReagents(ReagentMap reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
			double callerTemp = reag.getTempK();

			ReagentMap toRelease = new ReagentMap();

			HashSet<String> validIds = new HashSet<>(4);

			for(IReagent type : reag.keySet()){
				ReagentStack r = reag.getStack(type);
				if(!r.isEmpty()){
					EnumMatterPhase phase = type.getPhase(HeatUtil.toCelcius(callerTemp));
					if(ignorePhase || (phase.flows() && (side != EnumFacing.UP || phase.flowsDown()) && (side != EnumFacing.DOWN || phase.flowsUp()))){
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
