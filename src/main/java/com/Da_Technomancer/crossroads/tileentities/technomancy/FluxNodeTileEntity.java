package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickableTileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class FluxNodeTileEntity extends TileEntity implements IIntReceiver, ITickableTileEntity, IInfoTE{

	private float clientEntropy;
	private float angle;

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		chat.add("Temporal Entropy: " + EntropySavedData.getEntropy(world) + "%");
	}

	private void syncFlux(){
		float entropy = (float) EntropySavedData.getEntropy(world);
		if(clientEntropy == 0 ^ entropy == 0 || Math.abs(clientEntropy - entropy) >= .005F){
			clientEntropy = entropy;
			CrossroadsPackets.network.sendToAllAround(new SendIntToClient((byte) 0, Float.floatToIntBits(clientEntropy), pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			world.updateComparatorOutputLevel(pos, CrossroadsBlocks.fluxNode);
		}
	}

	public float getRenderAngle(float partialTicks){
		return angle + partialTicks * clientEntropy * 3.6F / 20F;
	}

	@Override
	public void tick(){
		if(world.isRemote){
			angle += clientEntropy * 3.6D / 20F;
		}else if(world.getGameTime() % FluxUtil.FLUX_TIME == 0){
			syncFlux();
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putFloat("angle", angle);
		nbt.putFloat("entropy", clientEntropy);
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putFloat("entropy", clientEntropy);
		nbt.putFloat("angle", angle);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		angle = nbt.getFloat("angle");
		clientEntropy = nbt.getFloat("entropy");
	}

	@Override
	public void receiveInt(byte identifier, int message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == 0){
			clientEntropy = Float.intBitsToFloat(message);
		}
	}

	private final RedsHandler redsHandler = new RedsHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
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
