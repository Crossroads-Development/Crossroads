package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.*;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.blocks.Ratiator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;

public class HeatLimiterTileEntity extends TileEntity implements ITickable, IInfoTE{

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side){
		chat.add("In-Temp: " + MiscOp.betterRound(heatHandlerIn.getTemp(), 3) + "°C");
		chat.add("Out-Temp: " + MiscOp.betterRound(heatHandlerOut.getTemp(), 3) + "°C");
		chat.add("Biome Temp: " + EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private double heatIn = 0;
	private double heatOut = 0;
	private boolean init = false;

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!init){
			heatHandlerIn.init();
		}

		double goalTemp = 0;
		for(EnumFacing side : EnumFacing.VALUES){
			goalTemp = Math.max(goalTemp, Ratiator.getPowerOnSide(world, pos, side, false));
		}
		goalTemp -= 273D;
		if(heatOut > goalTemp){
			if(heatIn < goalTemp){
				double toTrans = goalTemp - heatOut;
				toTrans = Math.max(toTrans, heatIn - goalTemp);
				heatOut += toTrans;
				heatIn -= toTrans;
				markDirty();
			}else{
				double toTrans = heatIn - heatOut;
				toTrans /= 2D;
				toTrans = Math.min(0, toTrans);
				heatOut += toTrans;
				heatIn -= toTrans;
				markDirty();
			}
		}else if(heatIn > goalTemp){
			double toTrans = goalTemp - heatOut;
			toTrans = Math.min(toTrans, heatIn - goalTemp);
			heatOut += toTrans;
			heatIn -= toTrans;
			markDirty();
		}else{
			double toTrans = heatIn - heatOut;
			toTrans /= 2D;
			toTrans = Math.max(0, toTrans);
			heatOut += toTrans;
			heatIn -= toTrans;
			markDirty();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("init", init);
		nbt.setDouble("heatIn", heatIn);
		nbt.setDouble("heatOut", heatOut);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		init = nbt.getBoolean("init");
		heatIn = nbt.getDouble("heatIn");
		heatOut = nbt.getDouble("heatOut");
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		EnumFacing facing = world.getBlockState(pos).getValue(Properties.FACING);
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && (side == null || side.getAxis() == facing.getAxis())){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		EnumFacing facing = world.getBlockState(pos).getValue(Properties.FACING);
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			if(side == null || side == facing.getOpposite()){
				return (T) heatHandlerIn;
			}else if(side == facing){
				return (T) heatHandlerOut;
			}
		}
		return super.getCapability(cap, side);
	}

	private final HeatHandler heatHandlerIn = new HeatHandler(true);
	private final HeatHandler heatHandlerOut = new HeatHandler(false);

	private class HeatHandler implements IHeatHandler{

		private final boolean in;

		private HeatHandler(boolean in){
			this.in = in;
		}

		private void init(){
			if(!init){
				init = true;
				heatIn = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				heatOut = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				markDirty();
			}
		}

		@Override
		public double getTemp(){
			init();
			return in ? heatIn : heatOut;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			if(in){
				heatIn = tempIn;
			}else{
				heatOut = tempIn;
			}
			markDirty();
		}

		@Override
		public void addHeat(double heatChange){
			init();
			if(in){
				heatIn += heatChange;
			}else{
				heatOut += heatChange;
			}
			markDirty();
		}
	}
}