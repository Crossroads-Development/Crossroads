package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * This can be used to store an NBTTagCompound on a render.
 * The most recent one received overwrites the previous. This is used for GUIs mainly. 
 */
public class StoreNBTToClient extends ClientPacket{

	/**
	 * The CR persistant player data for this player. Client side only
	 */
	@OnlyIn(Dist.CLIENT)
	private static CompoundNBT clientPlayerTag = new CompoundNBT();

	/**
	 * @param playerIn The player whose tag is being retrieved.
	 * @return The player's persistent NBT tag
	 */
	@Nonnull
	public static CompoundNBT getPlayerTag(PlayerEntity playerIn){
		if(playerIn.world.isRemote){
			//If this is client side, use the synced nbt compound
			return clientPlayerTag;
		}

		CompoundNBT tag = playerIn.getPersistentData();
		//Use the Forge persistant data tag that is kept between dimensions/respawns
		if(!tag.contains(PlayerEntity.PERSISTED_NBT_TAG)){
			tag.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
		}
		tag = tag.getCompound(PlayerEntity.PERSISTED_NBT_TAG);

		if(!tag.contains(Crossroads.MODID)){
			tag.put(Crossroads.MODID, new CompoundNBT());
		}
		return tag.getCompound(Crossroads.MODID);
	}

	/**
	 * Syncs the NBTTagCompound from MiscUtil.getPlayerTag(player)
	 */
	public static void syncNBTToClient(ServerPlayerEntity player){
		CRPackets.channel.send(PacketDistributor.PLAYER.with(() -> player), new StoreNBTToClient(getPlayerTag(player)));
	}

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
