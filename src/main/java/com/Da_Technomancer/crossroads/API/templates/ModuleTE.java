package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class ModuleTE extends TileEntity implements ITickable, IInfoTE{

	//Rotary
	protected final double[] motData = new double[4];
	//Heat
	protected boolean initHeat = false;
	protected double temp;

	protected abstract boolean useHeat();
	protected abstract boolean useRotary();

	public ModuleTE(){
		super();
		if(useHeat()){
			heatHandler = new HeatHandler();
		}else{
			heatHandler = null;
		}
		if(useRotary()){
			axleHandler = new AxleHandler();
		}else{
			axleHandler = null;
		}
	}

	@Override
	public void update(){
		if(!world.isRemote && useHeat() && !initHeat){
			heatHandler.init();
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		if(useHeat()){
			chat.add("Temp: " + MiscUtil.betterRound(temp, 3) + "°C");
			chat.add("Biome Temp: " + HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
		}
		if(useRotary()){
			chat.add("Speed: " + MiscUtil.betterRound(motData[0], 3));
			chat.add("Energy: " + MiscUtil.betterRound(motData[1], 3));
			chat.add("Power: " + MiscUtil.betterRound(motData[2], 3));
			chat.add("I: " + getMoInertia() + ", Rotation Ratio: " + axleHandler.getRotationRatio());
		}
	}

	protected double getMoInertia(){
		return 0;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		for(int i = 0; i < 4; i++){
			nbt.setDouble("mot_" + i, motData[i]);
		}
		nbt.setBoolean("init_heat", initHeat);
		nbt.setDouble("temp", temp);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		for(int i = 0; i < 4; i++){
			motData[i] = nbt.getDouble("mot_" + i);
		}
		initHeat = nbt.getBoolean("init_heat");
		temp = nbt.getDouble("temp");
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return getCapability(cap, side) != null || super.hasCapability(cap, side);
	}

	protected final HeatHandler heatHandler;
	protected final AxleHandler axleHandler;

	protected class HeatHandler implements IHeatHandler{

		protected void init(){
			if(!initHeat){
				temp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				initHeat = true;
				markDirty();
			}
		}

		@Override
		public double getTemp(){
			init();
			return temp;
		}

		@Override
		public void setTemp(double tempIn){
			initHeat = true;
			temp = tempIn;
			markDirty();
		}

		@Override
		public void addHeat(double heat){
			init();
			temp += heat;
			markDirty();
		}
	}

	protected class AxleHandler implements IAxleHandler{

		protected boolean connected;
		protected double rotRatio;
		protected byte updateKey;

		@Override
		public double[] getMotionData(){
			return motData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
			connected = true;
		}

		@Override
		public double getMoInertia(){
			return ModuleTE.this.getMoInertia();
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

		@Override
		public void disconnect(){
			connected = false;
		}
	}
}
