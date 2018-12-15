package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.ArrayList;

public class RedstoneTransmitterTileEntity extends TileEntity implements IInfoTE, ILinkTE{

	private ArrayList<BlockPos> linked = new ArrayList<>();
	private double output;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

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


	public void dye(EnumDyeColor color){
		if(world.getBlockState(pos).getValue(Properties.COLOR) != color){
			world.setBlockState(pos, world.getBlockState(pos).withProperty(Properties.COLOR, color));

			for(BlockPos link : linked){
				BlockPos worldLink = pos.add(link);
				IBlockState linkState = world.getBlockState(worldLink);
				if(linkState.getBlock() == ModBlocks.redstoneReceiver){
					world.setBlockState(worldLink, linkState.withProperty(Properties.COLOR, color));
				}
			}
		}
	}

	public double getOutput(){
		return output;
	}

	@Override
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

	public void setOutput(double outputIn){
		if(output != outputIn){
			output = outputIn;
			for(BlockPos link : linked){
				world.notifyNeighborsOfStateChange(pos.add(link), ModBlocks.redstoneTransmitter, true);
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

	@Override
	public TileEntity getTE(){
		return this;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return otherTE instanceof RedstoneReceiverTileEntity;
	}

	@Override
	public ArrayList<BlockPos> getLinks(){
		return linked;
	}

	@Override
	public int getRange(){
		return ModConfig.getConfigInt(ModConfig.redstoneTransmitterRange, false);
	}

	@Override
	public int getMaxLinks(){
		return 64;
	}

	@Override
	public boolean link(ILinkTE endpoint, EntityPlayer player){
		BlockPos linkPos = endpoint.getTE().getPos().subtract(pos);
		if(linked.contains(linkPos)){
			player.sendMessage(new TextComponentString("Device already linked; Canceling linking"));
		}else if(linked.size() < getMaxLinks()){
			linked.add(linkPos);
			getTE().markDirty();
			((RedstoneReceiverTileEntity) endpoint).setSrc(pos.subtract(((RedstoneReceiverTileEntity) endpoint).getPos()));
			((RedstoneReceiverTileEntity) endpoint).dye(world.getBlockState(pos).getValue(Properties.COLOR));

			world.neighborChanged(linkPos, ModBlocks.redstoneTransmitter, linkPos);
			player.sendMessage(new TextComponentString("Linked device at " + getTE().getPos() + " to send to " + endpoint.getTE().getPos()));
			return true;
		}else{
			player.sendMessage(new TextComponentString("All " + getMaxLinks() + " links already occupied; Canceling linking"));
		}
		return false;
	}
}
