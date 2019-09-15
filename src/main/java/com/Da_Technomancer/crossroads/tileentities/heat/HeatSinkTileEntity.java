package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;

public class HeatSinkTileEntity extends ModuleTE{

	public static final int[] MODES = {5, 10, 15, 20, 25};
	private int mode = 0;

	public int cycleMode(){
		mode = (mode + 1) % MODES.length;
		markDirty();
		return mode;
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, Direction side, BlockRayTraceResult hit){
		chat.add("Current Loss: -" + MODES[mode] + "°C/t");
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote){
			return;
		}

		double prevTemp = temp;
		double biomeTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
		temp += Math.min(MODES[mode], Math.abs(temp - biomeTemp)) * Math.signum(biomeTemp - temp);
		if(temp != prevTemp){
			markDirty();
		}
	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);
		mode = nbt.getInteger("mode");
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("mode", mode);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (T) heatHandler;
		}

		return super.getCapability(capability, facing);
	}
}
