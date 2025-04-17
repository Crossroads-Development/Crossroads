package com.Da_Technomancer.crossroads.api.electric;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public interface IEnergyCapable{
	IBlockCapabilityProvider<IEnergyStorage, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IEnergyCapable energyCapable){
			return energyCapable.getEnergyHandler(side);
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
	default void registerEnergyCapability(RegisterCapabilitiesEvent e){
		if(this instanceof Block block){
			e.registerBlock(Capabilities.EnergyStorage.BLOCK, CAPABLE_PROVIDER, block);
		}
	}

	@Nullable
	IEnergyStorage getEnergyHandler(Direction dir);
}
