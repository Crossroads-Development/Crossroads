package com.Da_Technomancer.crossroads.API.technomancy;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IPrototypeOwner{
	
	public boolean hasCap(Capability<?> cap, @Nonnull Direction side);

	/**
	 * This may crash if there is no tile entity adjacent. 
	 * This is fine, as all callers should first check hasCap(Capability<?> cap, EnumFacing side), which doesn't crash.
	 */
	public <T> T getCap(Capability<T> cap, @Nonnull Direction side) throws NullPointerException;
	
	/**
	 * Used to send block updates through the owner, for redstone.
	 * @param fromSide Side of the port calling this.
	 * @param blockIn The port block.
	 */
	public void neighborChanged(@Nonnull Direction fromSide, Block blockIn);
	
	/**
	 * Called once every 20 ticks on the virtual server for all IPrototypeOwners that would be loaded. 
	 * If this IPrototypeOwner needs to prematurely stop itself from loading, it can remove itself from the main map in this method. 
	 */
	public default void loadTick(){
		
	}
	
	/**
	 * Only call on the virtual server. 
	 * 
	 * @return True if this should be loaded/ticked. Returning false does not remove this from the prototype list. 
	 */
	public default boolean shouldRun(){
		return true;
	}
	
	/**
	 * Returns the prototype port types for each side. This cannot be used to fetch capabilities, it mainly exists for rendering purposes.
	 */
	@OnlyIn(Dist.CLIENT)
	public default PrototypePortTypes[] getTypes(){
		return new PrototypePortTypes[6];
	}
}
