package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerProfileCache;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 *
 * A version of GameProfile with a looser {@link #equals(Object)} implementation.
 * This version will not immediately return false if only 1 of the two GameProfiles has a null ID or Name.
 * However, in order that equals be symmetric, this will return false if the other object is a normal GameProfile instance.
 */
public class FlexibleGameProfile extends GameProfile{

	private boolean newlyCompleted = false;

	public FlexibleGameProfile(UUID id, String name){
		super(id, name);
	}

	public FlexibleGameProfile(GameProfile prof){
		super(prof.getId(), prof.getName());
	}

	public boolean isNewlyCompleted(){
		return newlyCompleted;
	}

	@Override
	public boolean equals(Object other){
		if(this == other){
			return true;
		}
		if(!(other instanceof FlexibleGameProfile)){
			return false;
		}

		final FlexibleGameProfile that = (FlexibleGameProfile) other;

		if(getId() != null && that.getId() != null){
			return getId().equals(that.getId());
		}
		return getName() == null || that.getName() == null || getName().equals(that.getName());
	}

	@Override
	public int hashCode(){
		String name = getName();
		UUID id = getId();
		return ((name == null ? 0 : name.hashCode()) << 8) + ((id == null ? 0 : id.hashCode()) & 0xEE);
	}

	public void writeToNBT(CompoundNBT nbt, String name){
		String profName = getName();
		UUID id = getId();
		if(profName != null){
			nbt.putString(name + "_name", profName);
		}
		if(id != null){
			nbt.putUniqueId(name + "_id", id);
		}
	}

	/**
	 * If cache is not null and the loaded profile is missing a UUID, the cache will be used to attempt to complete the profile.
	 *
	 * Returns null if no profile was stored to nbt. 
	 */
	@Nullable
	public static FlexibleGameProfile readFromNBT(CompoundNBT nbt, String name, @Nullable PlayerProfileCache cache){
		String profName = nbt.getString(name + "_name");
		if(profName.isEmpty()){
			return null;
		}
		UUID id = nbt.contains(name + "_idMost") ? nbt.getUniqueId(name + "_id") : null;
		boolean loadedID = false;

		if(id == null && cache != null){
			GameProfile search = cache.getGameProfileForUsername(profName);
			if(search != null){
				id = search.getId();
				loadedID = true;
			}
			Crossroads.logger.info("Attempting to complete player profile for " + profName + (loadedID ? ". Failed (not severe). " : ". Succeeded. UUID is " + id.toString()));
		}

		FlexibleGameProfile out = new FlexibleGameProfile(id, profName);
		out.newlyCompleted = loadedID;
		return out;
	}
}
