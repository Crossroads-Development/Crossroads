package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class RedstoneReceiverTileEntity extends TileEntity implements IInfoTE{

	private BlockPos src = null;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if(src == null){
			chat.add("No linked transmitter");
		}else{
			chat.add("Linked Position: X=" + (pos.getX() + src.getX()) + " Y=" + (pos.getY() + src.getY()) + " Z=" + (pos.getZ() + src.getZ()));
		}
	}

	public void setSrc(BlockPos srcIn){
		src = srcIn;
		markDirty();
		world.notifyNeighborsOfStateChange(pos, ModBlocks.redstoneReceiver, true);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if(nbt.hasKey("src")){
			src = BlockPos.fromLong(nbt.getLong("src"));
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(src != null){
			nbt.setLong("src", src.toLong());
		}
		return nbt;
	}

	public double getStrength(){
		if(src != null){
			TileEntity te = world.getTileEntity(pos.add(src));
			if(te instanceof RedstoneTransmitterTileEntity){
				return ((RedstoneTransmitterTileEntity) te).getOutput();
			}
		}
		return 0;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		return capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	private final RedstoneHandler redsHandler = new RedstoneHandler();

	@Nullable
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return (T) redsHandler;
		}
		return super.getCapability(capability, facing);
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean measure){
			return getStrength();
		}
	}
}
