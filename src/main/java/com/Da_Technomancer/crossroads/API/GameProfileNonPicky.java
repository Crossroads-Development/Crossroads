package com.Da_Technomancer.crossroads.API;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

/**
 * 
 * A version of GameProfile with a looser {@link #equals(Object)} implementation.
 * This version will not immediately return false if only 1 of the two GameProfiles has a null ID or Name.
 * However, in order that equals be symmetric, this will return false if the other object is a normal GameProfile instance.
 */
public class GameProfileNonPicky extends GameProfile{

	public GameProfileNonPicky(UUID id, String name){
		super(id, name);
	}

	public GameProfileNonPicky(GameProfile prof){
		super(prof.getId(), prof.getName());
	}

	@Override
	public boolean equals(Object other){
		if (this == other) {
			return true;
		}
		if (!(other instanceof GameProfileNonPicky)) {
			return false;
		}

		final GameProfileNonPicky that = (GameProfileNonPicky) other;

		if (getId() != null && that.getId() != null) {
			return getId().equals(that.getId());
		}
		if (getName() != null && that.getName() != null && !getName().equals(that.getName())) {
			return false;
		}

		return true;
	}
}
