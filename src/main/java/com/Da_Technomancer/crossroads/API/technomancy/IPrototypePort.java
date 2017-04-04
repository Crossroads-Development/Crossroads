package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Should be placed on the TileEntity.
 */
public interface IPrototypePort{
	
	public boolean hasCapPrototype(Capability<?> cap);

	/**
	 * This may crash if there is no tile entity adjacent. 
	 * This is fine, as all callers should first check hasCapPrototype(Capability<?> cap), which doesn't crash.
	 */
	public <T> T getCapPrototype(Capability<T> cap);
	
	public PrototypePortTypes getType();
	
	public EnumFacing getSide();
	
	public void makeActive();
	
	public boolean isActive();
}
