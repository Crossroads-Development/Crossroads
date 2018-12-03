package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import java.awt.*;

public class AlembicTileEntity extends AlchemyReactorTE{

	@Override
	protected boolean useCableHeat(){
		return true;
	}

	@Override
	protected int transferCapacity(){
		return 200;
	}

	@Nonnull
	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	@Override
	public void addVisualEffect(EnumParticleTypes particleType, double speedX, double speedY, double speedZ, int... particleArgs){

	}

	@Override
	protected void correctReag(){
		super.correctReag();

		if(contents.getTotalQty() == 0){
			return;
		}

		ReagentMap toInsert = new ReagentMap();
		EnumFacing dir = world.getBlockState(pos).getValue(Properties.HORIZ_FACING);

		double ambientTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
		for(IReagent type : contents.keySet()){
			//Movement of upwards-flowing phases from alembic to phial
			if(type.getPhase(cableTemp).flowsUp()){
				toInsert.transferReagent(type, contents.getQty(type), contents);
				markDirty();

				Color c = type.getColor(type.getPhase(ambientTemp));
				if(type.getPhase(ambientTemp).flowsDown()){
					((WorldServer) world).spawnParticle(ModParticles.COLOR_LIQUID, false, pos.getX() + 0.5D + dir.getXOffset(), pos.getY() + 1.1D, pos.getZ() + 0.5D + dir.getZOffset(), 0, 0, (Math.random() * 0.05D) - 0.1D, 0, 1F, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
				}else{
					((WorldServer) world).spawnParticle(ModParticles.COLOR_GAS, false, pos.getX() + 0.5D + dir.getXOffset(), pos.getY() + 1.1D, pos.getZ() + 0.5D + dir.getZOffset(), 0, 0, (Math.random() * -0.05D) + 0.1D, 0, 1F, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
				}
			}
		}

		toInsert.setTemp(ambientTemp);

		TileEntity te = world.getTileEntity(pos.offset(dir));
		if(te != null){
			IChemicalHandler handler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, EnumFacing.UP);
			if(handler != null){
				handler.insertReagents(toInsert, EnumFacing.UP, new PsuedoChemHandler(toInsert));
			}
		}
	}

	private static class PsuedoChemHandler implements IChemicalHandler{

		private final ReagentMap contents;

		private PsuedoChemHandler(ReagentMap map){
			contents = map;
		}

		@Override
		public int getContent(IReagent type){
			return contents.getQty(type);
		}

		@Override
		public int getTransferCapacity(){
			return 0;
		}

		@Override
		public double getTemp(){
			return contents.getTempC();
		}

		@Override
		public boolean insertReagents(ReagentMap reag, EnumFacing side, @Nonnull IChemicalHandler caller, boolean ignorePhase){
			return false;
		}

		@Nonnull
		@Override
		public EnumTransferMode getMode(EnumFacing side){
			return EnumTransferMode.OUTPUT;
		}

		@Nonnull
		@Override
		public EnumContainerType getChannel(EnumFacing side){
			return EnumContainerType.NONE;
		}
	}

	private final HeatHandler heatHandler = new HeatHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if((side == null || side == EnumFacing.DOWN) && cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			return (T) heatHandler;
		}
		return super.getCapability(cap, side);
	}

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				init = true;
				cableTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			}
		}

		@Override
		public double getTemp(){
			init();
			return cableTemp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			cableTemp = tempIn;
			//Shares heat between internal cable & contents
			dirtyReag = true;
			markDirty();
		}

		@Override
		public void addHeat(double tempChange){
			init();
			cableTemp += tempChange;
			//Shares heat between internal cable & contents
			dirtyReag = true;
			markDirty();
		}
	}
}
