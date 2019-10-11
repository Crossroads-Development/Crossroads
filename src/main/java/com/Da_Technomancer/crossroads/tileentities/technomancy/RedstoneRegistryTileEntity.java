package com.Da_Technomancer.crossroads.tileentities.technomancy;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.packets.IDoubleArrayReceiver;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;

public class RedstoneRegistryTileEntity extends TileEntity implements IDoubleArrayReceiver, IIntReceiver, IInfoTE{

	private double[] output = {0};
	private int index = 0;

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		chat.add("Current output: " + output[index] + "; Index: " + (index + 1));
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}
	
	public double[] getOutput(){
		return output;
	}
	
	public void setOutput(double[] output){
		this.output = output == null ? new double[1] : output;
		index = Math.min(index, this.output.length - 1);
	}
	
	public int getIndex(){
		return index;
	}
	
	public void setIndex(int index){
		this.index = Math.min(index, output.length - 1);
		if(world != null && !world.isRemote){
			world.notifyNeighborsOfStateChange(pos, CrossroadsBlocks.redstoneRegistry, false);
		}
	}
	
	public void activate(double power){
		index++;
		index %= output.length;
//		int floor = (int) Math.floor(power);
//		if(floor >= output.length){
//			index = 0;
//		}else{
//			index += floor;
//			index %= output.length;
//		}
		world.notifyNeighborsOfStateChange(pos, CrossroadsBlocks.redstoneRegistry, false);
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		output = new double[nbt.getInt("length")];
		for(int i = 0; i < output.length; i++){
			output[i] = nbt.getDouble("output_" + i);
		}
		index = Math.min(nbt.getInt("index"), output.length - 1);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("index", index);
		nbt.putInt("length", output.length);
		for(int i = 0; i < output.length; i++){
			nbt.putDouble("output_" + i, output[i]);
		}
		return nbt;
	}

	@Override
	public void receiveDoubles(String context, double[] message, @Nullable ServerPlayerEntity sendingPlayer){
		if(context.equals("output") || context.equals("newOutput")){
			output = message == null ? new double[1] : message;
			if(!world.isRemote){
				world.notifyNeighborsOfStateChange(pos, CrossroadsBlocks.redstoneRegistry, false);
			}
		}
	}
	
	@Override
	public void receiveInt(byte identifier, int message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == 0){
			setIndex(message);
		}
	}

	private final RedstoneHandler redstoneHandler = new RedstoneHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		return cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY || super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redstoneHandler;
		}
		return super.getCapability(cap, side);
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean measure){
			return measure ? output[index] : 0;
		}
	}
}
