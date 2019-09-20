package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * This can be used to store an NBTTagCompound on a render.
 * The most recent one received overwrites the previous. This is used for GUIs mainly. 
 */
public class StoreNBTToClient extends ClientPacket{

	/**
	 * Syncs the NBTTagCompound from MiscUtil.getPlayerTag(player), as well as the synced configs if config is true.
	 */
	public static void syncNBTToClient(ServerPlayerEntity player){
		CrossroadsPackets.channel.send(PacketDistributor.PLAYER.with(() -> player), new StoreNBTToClient(MiscUtil.getPlayerTag(player)));
	}

	public static CompoundNBT clientPlayerTag = new CompoundNBT();

	public CompoundNBT nbt;

	private static final Field[] FIELDS = fetchFields(StoreNBTToClient.class, "nbt");

	@SuppressWarnings("unused")
	public StoreNBTToClient(){

	}

	public StoreNBTToClient(CompoundNBT nbt){
		this.nbt = nbt;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		clientPlayerTag = nbt;
	}
}
