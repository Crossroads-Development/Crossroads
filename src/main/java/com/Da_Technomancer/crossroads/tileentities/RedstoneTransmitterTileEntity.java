package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class RedstoneTransmitterTileEntity extends TileEntity implements IInfoTE, ILinkTE{

	private ArrayList<BlockPos> linked = new ArrayList<>();
	private double output;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, Direction side, BlockRayTraceResult hit){
		if(linked.isEmpty()){
			chat.add("No linked receivers");
		}else{
			for(BlockPos link : linked){
				chat.add("Linked Position: X=" + (pos.getX() + link.getX()) + " Y=" + (pos.getY() + link.getY()) + " Z=" + (pos.getZ() + link.getZ()));
			}
		}
	}

	@Override
	public boolean canBeginLinking(){
		return true;
	}

	public void dye(DyeColor color){
		if(world.getBlockState(pos).get(Properties.COLOR) != color){
			world.setBlockState(pos, world.getBlockState(pos).with(Properties.COLOR, color));

			for(BlockPos link : linked){
				BlockPos worldLink = pos.add(link);
				BlockState linkState = world.getBlockState(worldLink);
				if(linkState.getBlock() == CrossroadsBlocks.redstoneReceiver){
					world.setBlockState(worldLink, linkState.with(Properties.COLOR, color));
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
		ModPackets.network.sendToAllAround(new SendLongToClient(CLEAR_PACKET_ID, 0, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	public void setOutput(double outputIn){
		if(output != outputIn){
			output = outputIn;
			for(BlockPos link : linked){
				world.notifyNeighborsOfStateChange(pos.add(link), CrossroadsBlocks.redstoneTransmitter, true);
			}
			markDirty();
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		for(int i = 0; i < linked.size(); i++){
			nbt.setLong("link" + i, linked.get(i).toLong());
		}
		return nbt;
	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);
		output = nbt.getDouble("out");
		int i = 0;
		while(nbt.hasKey("link_" + i)){
			linked.add(BlockPos.fromLong(nbt.getLong("link_" + i)));
			i++;
		}
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
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
		return CrossroadsConfig.redstoneTransmitterRange.get();
	}

	@Override
	public int getMaxLinks(){
		return 64;
	}

	@Override
	public boolean link(ILinkTE endpoint, PlayerEntity player){
		BlockPos linkPos = endpoint.getTE().getPos().subtract(pos);
		if(linked.contains(linkPos)){
			player.sendMessage(new StringTextComponent("Device already linked; Canceling linking"));
		}else if(linked.size() < getMaxLinks()){
			linked.add(linkPos);
			ModPackets.network.sendToAllAround(new SendLongToClient(LINK_PACKET_ID, linkPos.toLong(), pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			getTE().markDirty();
			((RedstoneReceiverTileEntity) endpoint).setSrc(pos.subtract(((RedstoneReceiverTileEntity) endpoint).getPos()));
			((RedstoneReceiverTileEntity) endpoint).dye(world.getBlockState(pos).get(Properties.COLOR));

			world.neighborChanged(linkPos, CrossroadsBlocks.redstoneTransmitter, linkPos);
			player.sendMessage(new StringTextComponent("Linked device at " + getTE().getPos() + " to send to " + endpoint.getTE().getPos()));
			return true;
		}else{
			player.sendMessage(new StringTextComponent("All " + getMaxLinks() + " links already occupied; Canceling linking"));
		}
		return false;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == LINK_PACKET_ID){
			linked.add(BlockPos.fromLong(message));
		}else if(identifier == CLEAR_PACKET_ID){
			linked.clear();
		}
	}
}
