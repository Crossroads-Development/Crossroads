package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class StirlingEngineTileEntity extends TileEntity implements ITickable, IInfoTE{

	private final double[] motionData = new double[4];
	private double temp1;
	private double temp2;
	private boolean init = false;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side){
		chat.add("Speed: " + MiscOp.betterRound(motionData[0], 3));
		chat.add("Energy: " + MiscOp.betterRound(motionData[1], 3));
		chat.add("Power: " + MiscOp.betterRound(motionData[2], 3));
		chat.add("I: " + axleHandler.getMoInertia() + ", Rotation Ratio: " + axleHandler.getRotationRatio());

		chat.add("Side Temp: " + MiscOp.betterRound(sideHeatHandler.getTemp(), 3) + "°C");
		chat.add("Bottom Temp: " + MiscOp.betterRound(bottomHeatHandler.getTemp(), 3) + "°C");
		chat.add("Biome Temp: " + HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		init();

		int level = (int) ((temp1 - temp2) / 100D);
		temp1 -= 5D * level;
		temp2 += 5D * level;

		if(axleHandler.hasMaster && Math.signum(level) * motionData[0] < ModConfig.getConfigDouble(ModConfig.stirlingSpeedLimit, false)){
			motionData[1] += ModConfig.getConfigDouble(ModConfig.stirlingMultiplier, false) * 5D * level * Math.abs(level);//5*stirlingMult*level^2 with sign of level
		}

		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		nbt.setBoolean("init", init);
		nbt.setDouble("temp1", temp1);
		nbt.setDouble("temp2", temp2);

		for(int i = 0; i < 4; i++){
			motionData[i] = nbt.getDouble("mot" + i);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		init = nbt.getBoolean("init");
		temp1 = nbt.getDouble("temp1");
		temp2 = nbt.getDouble("temp2");

		for(int i = 0; i < 4; i++){
			nbt.setDouble("mot" + i, motionData[i]);
		}
		
		return nbt;
	}

	private final AxleHandler axleHandler = new AxleHandler();
	private final SideHeatHandler sideHeatHandler = new SideHeatHandler();
	private final BottomHeatHandler bottomHeatHandler = new BottomHeatHandler();
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.UP){
			return true;
		}
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing != EnumFacing.UP){
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == EnumFacing.UP){
			return (T) axleHandler;
		}
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing != EnumFacing.UP){
			return facing == EnumFacing.DOWN ? (T) bottomHeatHandler : (T) sideHeatHandler;
		}

		return super.getCapability(capability, facing);
	}

	private class AxleHandler implements IAxleHandler{

		private boolean hasMaster;

		@Override
		public void disconnect(){
			hasMaster = false;
		}

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		private double rotRatio;
		private byte updateKey;

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
			hasMaster = true;
		}

		@Override
		public double getMoInertia(){
			return 200;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void markChanged(){
			markDirty();
		}

		@Override
		public boolean shouldManageAngle(){
			return false;
		}
	}


	private void init(){
		if(!init){
			temp1 = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			temp2 = temp1;
			init = true;
		}
	}

	private class SideHeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			init();
			return temp1;
		}

		@Override
		public void setTemp(double tempIn){
			init();
			temp1 = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			temp1 += heat;
		}
	}

	private class BottomHeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			init();
			return temp2;
		}

		@Override
		public void setTemp(double tempIn){
			init();
			temp2 = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			temp2 += heat;
		}
	}
}
