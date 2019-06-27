package com.Da_Technomancer.crossroads.integration.patchouli;

import com.google.gson.JsonElement;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class PathPredicate {
	public static PathPredicate ANY = new PathPredicate("ANY");
	String path;

	public PathPredicate(String path) {
		this.path = path;
	}

	public boolean test(NBTTagCompound tag) {
		return tag.getCompoundTag("path").getBoolean(path);

	}
	public static PathPredicate deserialize(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull() && element.isJsonObject()) {
			String unConcPath = element.getAsJsonObject().get("path").toString();
			return new PathPredicate(unConcPath.substring(1, unConcPath.length()-1));
		}
		else {
			return ANY;
		}
	}
}
