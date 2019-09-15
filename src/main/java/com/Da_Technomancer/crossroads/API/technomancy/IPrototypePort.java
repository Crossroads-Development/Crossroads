package com.Da_Technomancer.crossroads.API.technomancy;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

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
	
	public Direction getSide();
	
	public void makeActive();
	
	public boolean isActive();
	
	public int getIndex();
	
	public void setIndex(int index);

	@Nonnull
	public String getDesc();
}
