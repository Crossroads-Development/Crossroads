package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;

public class RatiatorTileEntity extends TileEntity implements IInfoTE{
	
	private double output;
	private EnumFacing dir = null;

	private EnumFacing getDir(){
		if(dir == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.ratiator){
				return EnumFacing.NORTH;
			}
			dir = state.getValue(Properties.HORIZ_FACING);
		}
		return dir;
	}

	public void onRotate(){
		dir = null;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return newState.getBlock() != ModBlocks.ratiator;
	}
	
	public double getOutput(){
		return output;
	}
	
	public void setOutput(double outputIn){
		if(output != outputIn){
			output = outputIn;
			markDirty();
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		output = nbt.getDouble("out");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("out", output);
		return nbt;
	}
	
	private final IAdvancedRedstoneHandler redstoneHandler = new RedstoneHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && getDir() == side){
			return true;
		}
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY && getDir() == side){
			return (T) redstoneHandler;
		}
		return super.getCapability(cap, side);
	}
	
	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean read){
			return output;
		}
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Out: " + output);
	}
}
