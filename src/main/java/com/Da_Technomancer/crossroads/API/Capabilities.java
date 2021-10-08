package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.alchemy.DefaultChemicalHandler;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.beams.DefaultBeamHandler;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.heat.DefaultHeatHandler;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.rotary.*;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

public class Capabilities{

	@CapabilityInject(IHeatHandler.class)
	public static Capability<IHeatHandler> HEAT_CAPABILITY = null;

	@CapabilityInject(IAxleHandler.class)
	public static Capability<IAxleHandler> AXLE_CAPABILITY = null;
	
	@CapabilityInject(ICogHandler.class)
	public static Capability<ICogHandler> COG_CAPABILITY = null;
	
	@CapabilityInject(IBeamHandler.class)
	public static Capability<IBeamHandler> BEAM_CAPABILITY = null;
	
	@CapabilityInject(IAxisHandler.class)
	public static Capability<IAxisHandler> AXIS_CAPABILITY = null;
	
	@CapabilityInject(IChemicalHandler.class)
	public static Capability<IChemicalHandler> CHEMICAL_CAPABILITY = null;

	public static void register(){
		CapabilityManager.INSTANCE.register(IHeatHandler.class, new DefaultStorage<>(), DefaultHeatHandler::new);
		CapabilityManager.INSTANCE.register(IAxleHandler.class, new DefaultStorage<>(), DefaultAxleHandler::new);
		CapabilityManager.INSTANCE.register(ICogHandler.class, new DefaultStorage<>(), DefaultCogHandler::new);
		CapabilityManager.INSTANCE.register(IBeamHandler.class, new DefaultStorage<>(), DefaultBeamHandler::new);
		CapabilityManager.INSTANCE.register(IAxisHandler.class, new DefaultStorage<>(), DefaultAxisHandler::new);
//		CapabilityManager.INSTANCE.register(ISlaveAxisHandler.class, new DefaultStorage<>(), DefaultSlaveAxisHandler::new);
		CapabilityManager.INSTANCE.register(IChemicalHandler.class, new DefaultStorage<>(), DefaultChemicalHandler::new);
	}

	/**
	 * All credit for this class goes to aidancbrady.
	 * This code originally came from his mod Mekanism, though has been modified over time.
	 * Copying it is allowed under Mekanism's license at the time of writing this.
	 *
	 * It checks if the instance is an instance of INBTSerializable and uses that to (de-)serialize. Otherwise, it does nothing.
	 */
	private static class DefaultStorage<T> implements Capability.IStorage<T>{

		@Override
		public Tag writeNBT(Capability<T> capability, T instance, Direction side){
			if(instance instanceof INBTSerializable){
				return ((INBTSerializable<? extends Tag>) instance).serializeNBT();
			}
			return null;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, Tag nbt){
			if(nbt != null && instance instanceof INBTSerializable){
				Class<? extends Tag> nbtClass = ((INBTSerializable<? extends Tag>) instance).serializeNBT().getClass();

				if(nbtClass.isInstance(nbt)){
					((INBTSerializable) instance).deserializeNBT(nbtClass.cast(nbt));
				}
			}
		}
	}
}
