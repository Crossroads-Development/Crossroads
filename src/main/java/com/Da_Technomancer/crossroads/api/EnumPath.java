package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Locale;

public enum EnumPath implements StringRepresentable{

	TECHNOMANCY((byte) 0, "progress/path/technomancy"),
	ALCHEMY((byte) 1, "progress/path/alchemy"),
	WITCHCRAFT((byte) 2, "progress/path/witchcraft");

	public static final Codec<EnumPath> CODEC = StringRepresentable.fromEnum(EnumPath::values);
	public static final StreamCodec<RegistryFriendlyByteBuf, EnumPath> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

	private static final String PREREQUISITE_ADVANCEMENT = "progress/path/can_unlock_path";
	/**
	 * Any advancement (must be in Crossroads namespace) added to this list will provide +1 path point when completed
	 */
	private static final ArrayList<String> PATH_POINT_ADVANCEMENTS = new ArrayList<>(4);

	static{
		PATH_POINT_ADVANCEMENTS.add("progress/path/can_unlock_path");
		PATH_POINT_ADVANCEMENTS.add("progress/path/technomancy_complete");
		PATH_POINT_ADVANCEMENTS.add("progress/path/alchemy_complete");
		PATH_POINT_ADVANCEMENTS.add("progress/path/witchcraft_complete");
	}

	private final byte index;
	private final String pathAdvancement;//Advancement used to track whether this path is currently unlocked

	EnumPath(byte ind, String pathAdvancement){
		index = ind;
		this.pathAdvancement = pathAdvancement;
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

	/**
	 * Gets whether a player has unlocked this path.
	 * If this is the client side, requires AdvancementTracker.listen() having been called first
	 * @param player The player to check
	 * @return Whether the given player has unlocked this path
	 */
	public boolean isUnlocked(Player player){
		return AdvancementTracker.hasAdvancement(player, pathAdvancement);
	}

	/**
	 * Sets whether a player has unlocked this path.
	 * Only works on the server side
	 * @param player The player to (un)lock this path for
	 * @param unlocked Whether this player should have this path unlocked. If false, relocks this path
	 */
	public void setUnlocked(Player player, boolean unlocked){
		if(player.level().isClientSide){
			return;//We can't do this on the client side
		}
		AdvancementTracker.unlockAdvancement((ServerPlayer) player, pathAdvancement, unlocked);
	}

	public static int totalUnlockedPathsCount(Player player){
		int currentlyUnlocked = 0;
		for(EnumPath path : EnumPath.values()){
			if(path.isUnlocked(player)){
				currentlyUnlocked += 1;
			}
		}
		return currentlyUnlocked;
	}

	public static boolean canUnlock(Player player){
		if(!AdvancementTracker.hasAdvancement(player, PREREQUISITE_ADVANCEMENT)){
			return false;
		}
		MultiPathMode mode = CRConfig.multiPathMode.get();
		switch(mode){
			case SINGLE -> {
				for(EnumPath path : EnumPath.values()){
					if(path.isUnlocked(player)){
						return false;
					}
				}
				return true;
			}
			case UNLIMITED -> {
				return true;
			}
			case SEQUENTIAL -> {
				int allowed = 0;
				for(String pointAdvancement : PATH_POINT_ADVANCEMENTS){
					if(AdvancementTracker.hasAdvancement(player, pointAdvancement)){
						allowed += 1;
					}
				}
				return allowed > totalUnlockedPathsCount(player);
			}
		}

		return false;
	}

	@Override
	public String getSerializedName(){
		return toString();
	}

	public enum MultiPathMode{

		SINGLE,
		UNLIMITED,
		SEQUENTIAL

	}
}
