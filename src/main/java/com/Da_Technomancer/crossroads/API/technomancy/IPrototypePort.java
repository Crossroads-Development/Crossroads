package com.Da_Technomancer.crossroads.API.technomancy;

import net.minecraftforge.common.capabilities.Capability;

public interface IPrototypePort{
	
	public boolean hasCapPrototype(Capability<?> cap);

	/**
	 * This may crash if there is no tile entity adjacent. 
	 * This is fine, as all callers should first check hasCapPrototype(Capability<?> cap), which doesn't crash.
	 */
	public <T> T getCapPrototype(Capability<T> cap);
}
