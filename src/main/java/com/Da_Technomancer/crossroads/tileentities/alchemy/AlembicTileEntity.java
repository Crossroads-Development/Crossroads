package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.particles.CRParticles;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.awt.*;

public class AlembicTileEntity extends AlchemyReactorTE{

	@Override
	protected boolean useCableHeat(){
		return true;
	}

	@Override
	protected void initHeat(){
		if(!init){
			init = true;
			cableTemp = HeatUtil.convertBiomeTemp(world, pos);
		}
	}

	@Override
	protected int transferCapacity(){
		return 200;
	}

	@Override
	public <T extends IParticleData> void addVisualEffect(T particleType, double speedX, double speedY, double speedZ){
		//No-op; opaque
	}

	@Nonnull
	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	@Override
	protected void correctReag(){
		super.correctReag();

		if(contents.getTotalQty() == 0){
			return;
		}

		ReagentMap toInsert = new ReagentMap();
		Direction dir = world.getBlockState(pos).get(CRProperties.HORIZ_FACING);

		double ambientTemp = HeatUtil.convertBiomeTemp(world, pos);
		for(IReagent type : contents.keySet()){
			//Movement of upwards-flowing phases from alembic to phial
			if(type.getPhase(cableTemp).flowsUp()){
				toInsert.transferReagent(type, contents.getQty(type), contents);
				markDirty();

				Color c = type.getColor(type.getPhase(ambientTemp));
				if(type.getPhase(ambientTemp).flowsDown()){
					((ServerWorld) world).spawnParticle(CRParticles.COLOR_LIQUID, false, pos.getX() + 0.5D + dir.getXOffset(), pos.getY() + 1.1D, pos.getZ() + 0.5D + dir.getZOffset(), 0, 0, (Math.random() * 0.05D) - 0.1D, 0, 1F, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
				}else{
					((ServerWorld) world).spawnParticle(CRParticles.COLOR_GAS, false, pos.getX() + 0.5D + dir.getXOffset(), pos.getY() + 1.1D, pos.getZ() + 0.5D + dir.getZOffset(), 0, 0, (Math.random() * -0.05D) + 0.1D, 0, 1F, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
				}
			}
		}

		toInsert.setTemp(ambientTemp);

		TileEntity te = world.getTileEntity(pos.offset(dir));
		if(te != null){
			IChemicalHandler handler = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, Direction.UP);
			if(handler != null){
				handler.insertReagents(toInsert, Direction.UP, new PsuedoChemHandler(toInsert));
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
		public boolean insertReagents(ReagentMap reag, Direction side, @Nonnull IChemicalHandler caller, boolean ignorePhase){
			return false;
		}

		@Nonnull
		@Override
		public EnumTransferMode getMode(Direction side){
			return EnumTransferMode.OUTPUT;
		}

		@Nonnull
		@Override
		public EnumContainerType getChannel(Direction side){
			return EnumContainerType.NONE;
		}
	}

	private final HeatHandler heatHandler = new HeatHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if((side == null || side == Direction.DOWN) && cap == Capabilities.HEAT_CAPABILITY){
			return (T) heatHandler;
		}
		return super.getCapability(cap, side);
	}

	private class HeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			initHeat();
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
			initHeat();
			cableTemp += tempChange;
			//Shares heat between internal cable & contents
			dirtyReag = true;
			markDirty();
		}
	}
}
