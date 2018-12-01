package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;

public class AlembicTileEntity extends AlchemyReactorTE{

	private double cableTemp = 0;
	private boolean init = false;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(!init){
			cableTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			init = true;
		}

		if(dirtyReag){
			correctReag();
		}

		if(world.getTotalWorldTime() % AlchemyCore.ALCHEMY_TIME == 0){
			performReaction();
		}
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
	protected double correctTemp(){
		//Shares heat between internal cable & contents
		cableTemp = amount <= 0 ? cableTemp : HeatUtil.toCelcius((HeatUtil.toKelvin(cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * heat) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D)));
		heat = HeatUtil.toKelvin(cableTemp) * amount;
		return cableTemp;
	}

	@Override
	protected void correctReag(){
		super.correctReag();

		ReagentMap toInsert = new ReagentMap();
		ArrayList<IReagent> moved = new ArrayList<>();
		for(IReagent type : contents.keySet()){
			int qty = contents.getQty(type);
			if(qty == 0){
				continue;
			}

			//Movement of upwards-flowing phases from alembic to phial
			if(type.getPhase(cableTemp).flowsUp()){
				heat -= HeatUtil.toKelvin(cableTemp) * qty;
				amount -= qty;
				toInsert.addReagent(type, qty);
				moved.add(type);
				markDirty();
			}
		}

		EnumFacing dir = world.getBlockState(pos).getValue(Properties.HORIZ_FACING);
		TileEntity te = world.getTileEntity(pos.offset(dir));
		if(te != null){
			IChemicalHandler handler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, EnumFacing.UP);
			if(handler != null){
				handler.insertReagents(toInsert, EnumFacing.UP, null);
			}
		}

		double ambientTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
		for(IReagent type : moved){
			Color c = type.getColor(type.getPhase(ambientTemp));
			if(type.getPhase(ambientTemp).flowsDown()){
				((WorldServer) world).spawnParticle(ModParticles.COLOR_LIQUID, false, pos.getX() + 0.5D + dir.getXOffset(), pos.getY() + 1.1D, pos.getZ() + 0.5D + dir.getZOffset(), 0, 0, (Math.random() * 0.05D) - 0.1D, 0, 1F, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
			}else{
				((WorldServer) world).spawnParticle(ModParticles.COLOR_GAS, false, pos.getX() + 0.5D + dir.getXOffset(), pos.getY() + 1.1D, pos.getZ() + 0.5D + dir.getZOffset(), 0, 0, (Math.random() * -0.05D) + 0.1D, 0, 1F, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
			}
			contents.remove(type);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		cableTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("init");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("temp", cableTemp);
		nbt.setBoolean("init", init);

		return nbt;
	}

	private final HeatHandler heatHandler = new HeatHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if((side == null || side == EnumFacing.DOWN) && cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

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
			if(amount != 0){
				cableTemp = (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);
				heat = (cableTemp + 273D) * amount;
				dirtyReag = true;
			}
			markDirty();
		}

		@Override
		public void addHeat(double tempChange){
			init();
			cableTemp += tempChange;
			//Shares heat between internal cable & contents
			if(amount != 0){
				cableTemp = (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);
				heat = (cableTemp + 273D) * amount;
				dirtyReag = true;
			}
			markDirty();
		}
	}
}
