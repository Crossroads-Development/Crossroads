package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.CrossroadsProperties;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;

public class HeatLimiterBasicTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE, IDoubleReceiver{

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, Direction side, BlockRayTraceResult hit){
		chat.add("In-Temp: " + MiscUtil.betterRound(heatHandlerIn.getTemp(), 3) + "°C");
		chat.add("Out-Temp: " + MiscUtil.betterRound(heatHandlerOut.getTemp(), 3) + "°C");
		chat.add("Biome Temp: " + HeatUtil.convertBiomeTemp(world, pos) + "°C");
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private double heatIn = 0;
	private double heatOut = 0;
	private boolean init = false;
	private double setting = 0;

	public void set(double newSetting){
		setting = newSetting;
		markDirty();
	}

	public double getSetting(){
		return setting;
	}

	@Override
	public void receiveDouble(String context, double message){
		if(context.equals("new_setting")){
			setting = message;
			markDirty();
		}
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}

		if(!init){
			heatHandlerIn.init();
		}

		double goalTemp = HeatUtil.toCelcius(getSetting());
		boolean blueMode = world.getBlockState(pos).get(CrossroadsProperties.ACTIVE);

		if(blueMode){
			heatIn = -heatIn;
			heatOut = -heatOut;
			goalTemp = -goalTemp;
		}

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

		if(blueMode){
			heatIn = -heatIn;
			heatOut = -heatOut;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("init_heat", init);
		nbt.putDouble("heat_in", heatIn);
		nbt.putDouble("heat_out", heatOut);
		nbt.putDouble("setting", setting);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		init = nbt.getBoolean("init_heat");
		heatIn = nbt.getDouble("heat_in");
		heatOut = nbt.getDouble("heat_out");
		setting = nbt.getDouble("setting");
	}

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		Direction facing = world.getBlockState(pos).get(EssentialsProperties.FACING);
		if(cap == Capabilities.HEAT_CAPABILITY && (side == null || side.getAxis() == facing.getAxis())){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		Direction facing = world.getBlockState(pos).get(EssentialsProperties.FACING);
		if(cap == Capabilities.HEAT_CAPABILITY){
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
				heatIn = HeatUtil.convertBiomeTemp(world, pos);
				heatOut = HeatUtil.convertBiomeTemp(world, pos);
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