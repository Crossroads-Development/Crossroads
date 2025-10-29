package com.Da_Technomancer.crossroads.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;

public enum EnumPath implements StringRepresentable{

	TECHNOMANCY((byte) 0, "progress/path/technomancy", "progress/path/technomancy_complete", "progress/path/can_unlock_path"),
	ALCHEMY((byte) 1, "progress/path/alchemy", "progress/path/alchemy_complete", "progress/path/can_unlock_path"),
	WITCHCRAFT((byte) 2, "progress/path/witchcraft", "progress/path/witchcraft_complete", "progress/path/can_unlock_path");

	private final byte index;
	private final String pathAdvancement;//Advancement used to track whether this path is currently unlocked
	private final String finishingAdvancement;//Finishing this advancement will unlock the ability to take another path with certain config options
	private final String unlockAdvancement;//Advancement required to unlock this path (pre-requisite)

	EnumPath(byte ind, String pathAdvancement, String finishingAdvancement, String unlockAdvancement){
		index = ind;
		this.pathAdvancement = pathAdvancement;
		this.finishingAdvancement = finishingAdvancement;
		this.unlockAdvancement = unlockAdvancement;
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

	public String getLocalName(){
		return MiscUtil.localize("path." + toString());
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
