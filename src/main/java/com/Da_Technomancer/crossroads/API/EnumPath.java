package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;
import java.util.Locale;

public enum EnumPath{

	TECHNOMANCY((byte) 0),
	ALCHEMY((byte) 1),
	WITCHCRAFT((byte) 2);
	//Witchcraft is NYI by Crossroads
	//However, all the code is in place for recipes to be added to the witchcraft category
	//If a datapack modifies the witchcraft_unlock_key tag, it can be unlocked
	//Food for thought, modpack makers

	private final byte index;

	EnumPath(byte ind){
		index = ind;
	}

	public byte getIndex(){
		return index;
	}

	public static EnumPath fromIndex(byte ind){
		return values()[ind];
	}

	@Override
	public String toString(){
		return name().toLowerCase(Locale.US);
	}

	@Nullable
	public static EnumPath fromName(String name){
		try{
			return valueOf(name.toUpperCase(Locale.US));
		}catch(Exception e){
			return null;
		}
	}

	/**
	 * Gets whether a player has unlocked this path.
	 * If this is the client side, make sure the nbt cache is up to date (via StoreNBTToClient)
	 * @param player The player to check
	 * @return Whether the given player has unlocked this path
	 */
	public boolean isUnlocked(PlayerEntity player){
		return StoreNBTToClient.getPlayerTag(player).getBoolean(toString());
	}

	/**
	 * Sets whether a player has unlocked this path.
	 * Only meaningful on the server side
	 * Also updates the nbt cache (via StoreNBTToClient)
	 * @param player The player to (un)lock this path for
	 * @param unlocked Whether this player should have this path unlocked. If false, relocks this path
	 */
	public void setUnlocked(PlayerEntity player, boolean unlocked){
		if(isUnlocked(player) ^ unlocked){
			StoreNBTToClient.getPlayerTag(player).putBoolean(toString(), unlocked);
			if(player instanceof ServerPlayerEntity){
				StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) player);
			}
		}
	}

	/**
	 * Tests whether a player has passed the gate to be allowed to unlock paths
	 * @param player The player to check
	 * @return Whether this player should be allowed to unlock paths
	 */
	public boolean pathGatePassed(PlayerEntity player){
		boolean multiplayer;//We use a different config option depending on if this is multiplayer or singleplayer
		if(player.world.isRemote){
			multiplayer = !Minecraft.getInstance().isSingleplayer();
		}else{
			multiplayer = player.world.getServer().isDedicatedServer();
		}
		if(multiplayer ? !CRConfig.allowAllServer.get() : !CRConfig.allowAllSingle.get()){
			//If only 1 path is allowed, deny if any other path is found
			for(EnumPath path : values()){
				if(path.isUnlocked(player)){
					return false;
				}
			}
		}
		for(EnumBeamAlignments align : EnumBeamAlignments.values()){
			if(align != EnumBeamAlignments.VOID && align != EnumBeamAlignments.NO_MATCH && !align.isDiscovered(player)){
				return false;
			}
		}
		return true;
	}
}
