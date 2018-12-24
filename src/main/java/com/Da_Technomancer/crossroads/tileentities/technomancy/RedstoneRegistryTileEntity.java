package com.Da_Technomancer.crossroads.tileentities.technomancy;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.packets.IDoubleArrayReceiver;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class RedstoneRegistryTileEntity extends TileEntity implements IDoubleArrayReceiver, IIntReceiver{

	private double[] output = {0};
	private int index = 0;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
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
		world.notifyNeighborsOfStateChange(pos, ModBlocks.redstoneRegistry, false);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		output = new double[nbt.getInteger("length")];
		for(int i = 0; i < output.length; i++){
			output[i] = nbt.getDouble("output_" + i);
		}
		setIndex(nbt.getInteger("index"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("index", index);
		nbt.setInteger("length", output.length);
		for(int i = 0; i < output.length; i++){
			nbt.setDouble("output_" + i, output[i]);
		}
		return nbt;
	}

	@Override
	public void receiveDoubles(String context, double[] message, @Nullable EntityPlayerMP sendingPlayer){
		if(context.equals("output") || context.equals("newOutput")){
			output = message == null ? new double[1] : message;
			if(!world.isRemote){
				world.notifyNeighborsOfStateChange(pos, ModBlocks.redstoneRegistry, false);
			}
		}
	}
	
	@Override
	public void receiveInt(int identifier, int message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == 0){
			setIndex(message);
		}
	}

	private final RedstoneHandler redstoneHandler = new RedstoneHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY || super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
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
