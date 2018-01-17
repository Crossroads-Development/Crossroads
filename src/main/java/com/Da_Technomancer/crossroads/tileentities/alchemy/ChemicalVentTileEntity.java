package com.Da_Technomancer.crossroads.tileentities.alchemy;

import java.awt.Color;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.particles.ModParticles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

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
		public double getContent(){
			return 0;
		}

		@Override
		public double getTransferCapacity(){
			return 10D;
		}

		@Override
		public double getHeat(){
			return 0;
		}

		@Override
		public void setHeat(double heatIn){

		}

		@Override
		public boolean insertReagents(ReagentStack[] reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
			double callerTemp = caller == null ? 293 : caller.getTemp() + 273D;
			boolean changed = false;
			for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
				ReagentStack r = reag[i];
				if(r != null){
					EnumMatterPhase phase = r.getPhase(callerTemp - 273D);
					if(ignorePhase || (phase.flows() && (side != EnumFacing.UP || phase.flowsDown()) && (side != EnumFacing.DOWN || phase.flowsUp()))){
						double moved = r.getAmount();
						if(moved <= 0D){
							continue;
						}
						changed = true;
						double heatTrans = moved * callerTemp;
						reag[i] = null;
						if(caller != null){
							caller.addHeat(-heatTrans);
						}

						Color col = r.getType().getColor(phase);
						WorldServer server = (WorldServer) world;


						switch(phase){
							case GAS:
								for(int j = 0; j <= (int) moved; j++){
									server.spawnParticle(ModParticles.COLOR_GAS, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (Math.random() * 2D - 1D) * 0.25D, (Math.random() * 2D - 1D) * 0.25D, (Math.random() * 2D - 1D) * 0.25D, 1F, new int[] {col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()});
								}
								r.getType().onRelease(world, pos, r.getAmount(), phase, null);
								break;
							case LIQUID:
							case SOLUTE:
								for(int j = 0; j <= (int) moved; j++){
									server.spawnParticle(ModParticles.COLOR_LIQUID, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (Math.random() * 2D - 1D) * 0.02D, -Math.random(), (Math.random() * 2D - 1D) * 0.02D, 1F, new int[] {col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()});
								}
								BlockPos searching = pos;
								for(int j = pos.getY() - 1; j > 0; j--){
									searching = new BlockPos(pos.getX(), j, pos.getZ());
									if(world.getBlockState(searching).isFullCube()){
										break;
									}
								}
								r.getType().onRelease(world, searching, r.getAmount(), phase, null);
								break;
							case FLAME:
								//TODO
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
		public double getContent(int type){
			return 0;
		}
	}
}
