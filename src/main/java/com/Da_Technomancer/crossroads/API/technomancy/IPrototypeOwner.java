package com.Da_Technomancer.crossroads.API.technomancy;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public interface IPrototypeOwner{
	
	public boolean hasCap(Capability<?> cap, EnumFacing side);

	public <T> T getCap(Capability<T> cap, EnumFacing side);
}
