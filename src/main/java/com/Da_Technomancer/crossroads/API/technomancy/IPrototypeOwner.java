package com.Da_Technomancer.crossroads.API.technomancy;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public interface IPrototypeOwner{
	
	public boolean hasCap(Capability<?> cap, EnumFacing side);

	/**
	 * This may crash if there is no tile entity adjacent. 
	 * This is fine, as all callers should first check hasCap(Capability<?> cap, EnumFacing side), which doesn't crash.
	 */
	public <T> T getCap(Capability<T> cap, EnumFacing side);
	
	/**
	 * Used to send block updates through the owner, for redstone.
	 * @param fromSide Side of the port calling this.
	 * @param blockIn The port block.
	 */
	public void neighborChanged(EnumFacing fromSide, Block blockIn);
}
