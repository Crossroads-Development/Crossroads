package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class FluxNodeTileEntity extends TileEntity implements IIntReceiver, ITickable, IInfoTE{

	private float clientEntropy;
	private float angle;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Temporal Entropy: " + EntropySavedData.getEntropy(world) + "%");
	}

	private void syncFlux(){
		float entropy = (float) EntropySavedData.getEntropy(world);
		if(clientEntropy == 0 ^ entropy == 0 || Math.abs(clientEntropy - entropy) >= .005F){
			clientEntropy = entropy;
			ModPackets.network.sendToAllAround(new SendIntToClient((byte) 0, Float.floatToIntBits(clientEntropy), pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			world.updateComparatorOutputLevel(pos, ModBlocks.fluxNode);
		}
	}

	public float getRenderAngle(float partialTicks){
		return angle + partialTicks * clientEntropy * 3.6F / 20F;
	}

	@Override
	public void update(){
		if(world.isRemote){
			angle += clientEntropy * 3.6D / 20F;
		}else if(world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
			syncFlux();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setFloat("angle", angle);
		nbt.setFloat("entropy", clientEntropy);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setFloat("entropy", clientEntropy);
		nbt.setFloat("angle", angle);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		angle = nbt.getFloat("angle");
		clientEntropy = nbt.getFloat("entropy");
	}

	@Override
	public void receiveInt(byte identifier, int message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == 0){
			clientEntropy = Float.intBitsToFloat(message);
		}
	}

	private final RedsHandler redsHandler = new RedsHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redsHandler;
		}
		return super.getCapability(cap, side);
	}

	private class RedsHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean measure){
			return measure ? EntropySavedData.getEntropy(world) : 0;
		}
	}
}
