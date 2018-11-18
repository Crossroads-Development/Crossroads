package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class RedstoneTransmitterTileEntity extends TileEntity implements IInfoTE{

	private ArrayList<BlockPos> linked = new ArrayList<>();
	private double output;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if(linked.isEmpty()){
			chat.add("No linked receivers");
		}else{
			for(BlockPos link : linked){
				chat.add("Linked Position: X=" + (pos.getX() + link.getX()) + " Y=" + (pos.getY() + link.getY()) + " Z=" + (pos.getZ() + link.getZ()));
			}
		}
	}

	public double getOutput(){
		return output;
	}

	public void clearLinks(){
		while(!linked.isEmpty()){
			BlockPos linkPos = pos.add(linked.remove(0));
			TileEntity te = world.getTileEntity(linkPos);
			if(te instanceof RedstoneReceiverTileEntity){
				((RedstoneReceiverTileEntity) te).setSrc(null);
			}
			markDirty();
		}
	}

	public void link(BlockPos link){
		BlockPos linkPos = link.subtract(pos);
		if(!linked.contains(linkPos)){
			linked.add(linkPos);
			TileEntity receiver = world.getTileEntity(link);
			if(receiver instanceof RedstoneReceiverTileEntity){
				((RedstoneReceiverTileEntity) receiver).setSrc(pos.subtract(link));
			}

			world.neighborChanged(linkPos, ModBlocks.redstoneTransmitter, linkPos);
		}
	}

	public void setOutput(double outputIn){
		if(output != outputIn){
			output = outputIn;
			for(BlockPos link : linked){
				BlockPos linkPos = pos.add(link);
				world.neighborChanged(linkPos, ModBlocks.redstoneTransmitter, linkPos);
			}
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		output = nbt.getDouble("out");
		int i = 0;
		while(nbt.hasKey("link_" + i)){
			linked.add(BlockPos.fromLong(nbt.getLong("link_" + i)));
			i++;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("out", output);
		for(int i = 0; i < linked.size(); i++){
			nbt.setLong("link_" + i, linked.get(i).toLong());
		}
		return nbt;
	}
}
