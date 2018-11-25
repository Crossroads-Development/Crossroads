package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;

public class MaxwellDemonTileEntity extends TileEntity implements ITickable, IInfoTE{

	private double tempUp = 0;
	private double tempDown = 0;
	private boolean init = false;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Upper Temp: " + MiscUtil.betterRound(tempUp, 3) + "°C");
		chat.add("Lower Temp: " + MiscUtil.betterRound(tempDown, 3) + "°C");
		chat.add("Biome Temp: " + HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!init){
			heatHandlerUp.init();
		}

		if(tempUp < 2500D){
			tempUp = Math.min(2500D, tempUp + 5D);
			markDirty();
		}
		if(tempDown > -250D){
			tempDown = Math.max(-250D, tempDown - 5D);
			markDirty();
		}

		for(int i = 0; i < 2; i++){
			EnumFacing dir = EnumFacing.byIndex(i);

			TileEntity te = world.getTileEntity(pos.offset(dir));
			if(te != null && te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, EnumFacing.DOWN)){
				double reservePool = i == 0 ? tempDown : tempUp;
				if(i == 0){
					tempDown -= reservePool;
				}else{
					tempUp -= reservePool;
				}


				IHeatHandler handler = te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, EnumFacing.DOWN);
				reservePool += handler.getTemp();
				handler.addHeat(-handler.getTemp());
				reservePool /= 2;
				if(i == 0){
					tempDown += reservePool;
				}else{
					tempUp += reservePool;
				}
				handler.addHeat(reservePool);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("init", init);
		nbt.setDouble("temp_u", tempUp);
		nbt.setDouble("temp_d", tempDown);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		init = nbt.getBoolean("init");
		tempUp = nbt.getDouble("temp_u");
		tempDown = nbt.getDouble("temp_d");
	}

	private final HeatHandler heatHandlerUp = new HeatHandler(true);
	private final HeatHandler heatHandlerDown = new HeatHandler(false);

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && (side == null || side.getAxis() == Axis.Y)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			if(side == null || side == EnumFacing.UP){
				return (T) heatHandlerUp;
			}else if(side == EnumFacing.DOWN){
				return (T) heatHandlerDown;
			}
		}
		return super.getCapability(cap, side);
	}

	private class HeatHandler implements IHeatHandler{

		private final boolean up;

		private HeatHandler(boolean up){
			this.up = up;
		}

		private void init(){
			if(!init){
				tempUp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				tempDown = tempUp;
				init = true;
			}
		}

		@Override
		public double getTemp(){
			init();
			return up ? tempUp : tempDown;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			if(up){
				tempUp = tempIn;
			}else{
				tempDown = tempIn;
			}
			markDirty();
		}

		@Override
		public void addHeat(double heat){
			init();
			if(up){
				tempUp += heat;
			}else{
				tempDown += heat;
			}
			markDirty();
		}
	}
}
