package com.Da_Technomancer.crossroads.api.rotary;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import javax.annotation.Nullable;

public interface ICogCapable{
	IBlockCapabilityProvider<ICogHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof ICogCapable cogCapable){
			return cogCapable.getCogHandler(side);
		}
		return null;
	};

	/**
	 * TODO: this is an attempt at auto-registering everything that implements this interface; if you somehow
	 *  implement it on a class that doesn't also eventually implement Block, nothing happens, which seems fine,
	 *  but probably worth double-checking it works later.
	 * @param e
	 */
	@SuppressWarnings("unused")
	@SubscribeEvent
	default void registerCogCapability(RegisterCapabilitiesEvent e){
		if(this instanceof Block block){
			e.registerBlock(CRCapabilities.COG_CAPABILITY, CAPABLE_PROVIDER, block);
		}
	}

	@Nullable
	ICogHandler getCogHandler(Direction dir);
}
