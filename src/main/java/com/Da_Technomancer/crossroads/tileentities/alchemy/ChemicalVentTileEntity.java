package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

import java.awt.*;

public class ChemicalVentTileEntity extends TileEntity{

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
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
		public int getContent(){
			return 0;
		}

		@Override
		public int getTransferCapacity(){
			return 10;
		}

		@Override
		public double getHeat(){
			return 0;
		}

		@Override
		public void setHeat(double heatIn){

		}

		@Override
		public boolean insertReagents(ReagentMap reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
			double callerTemp = caller == null ? HeatUtil.toKelvin(20) : HeatUtil.toKelvin(caller.getTemp());
			boolean changed = false;
			for(IReagent type : reag.keySet()){
				int qty = reag.getQty(type);
				if(qty > 0){
					EnumMatterPhase phase = type.getPhase(HeatUtil.toCelcius(callerTemp));
					if(ignorePhase || (phase.flows() && (side != EnumFacing.UP || phase.flowsDown()) && (side != EnumFacing.DOWN || phase.flowsUp()))){
						changed = true;
						double heatTrans = qty * callerTemp;
						reag.put(type, 0);
						if(caller != null){
							caller.addHeat(-heatTrans);
						}

						Color col = type.getColor(phase);
						WorldServer server = (WorldServer) world;

						switch(phase){
							case GAS:
								for(int j = 0; j <= qty; j++){
									server.spawnParticle(ModParticles.COLOR_GAS, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (Math.random() * 2D - 1D) * 0.25D, (Math.random() * 2D - 1D) * 0.25D, (Math.random() * 2D - 1D) * 0.25D, 1F, col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
								}
								type.onRelease(world, pos, qty, callerTemp, phase, null);
								break;
							case LIQUID:
							case SOLID:
								for(int j = 0; j <= qty; j++){
									server.spawnParticle(ModParticles.COLOR_LIQUID, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (Math.random() * 2D - 1D) * 0.02D, -Math.random(), (Math.random() * 2D - 1D) * 0.02D, 1F, col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha());
								}
								BlockPos searching = pos;
								for(int j = pos.getY() - 1; j > 0; j--){
									searching = new BlockPos(pos.getX(), j, pos.getZ());
									if(world.getBlockState(searching).isFullCube()){
										break;
									}
								}
								type.onRelease(world, searching, qty, heatTrans, phase, null);
								break;
							case FLAME:
								type.onRelease(world, pos, qty, heatTrans, phase, null);
								break;
							default:
								break;	
						}
					}
				}
			}
			return changed;
		}

		@Override
		public int getContent(String type){
			return 0;
		}
	}
}
