package com.Da_Technomancer.crossroads.api.rotary;

import net.minecraft.nbt.CompoundTag;

public interface IMechanismProperty{

	/**
	 * For networking only! Not for saving/loading to disk
	 * @return An int that identifies this material, and can be deserialized by the mechanism
	 */
	@Deprecated
	default int serialize(){
		return 0;
	}

	@Deprecated
	default String getSaveName(){
		return "";
	}

	/**
	 * The default implementation for backwards compatibility; will be removed in a later version. Override this method.
	 * @param nbt NBT tag to save this property to, for later reading by IMechanism.readProperty
	 */
	default void write(CompoundTag nbt){
		nbt.putString("prop_data", getSaveName());
	}

	public static class EmptyMechanismProperty implements IMechanismProperty{

		@Override
		public void write(CompoundTag nbt){
			//No-op
		}

		public static EmptyMechanismProperty read(CompoundTag nbt){
			return new EmptyMechanismProperty();
		}
	}
}
