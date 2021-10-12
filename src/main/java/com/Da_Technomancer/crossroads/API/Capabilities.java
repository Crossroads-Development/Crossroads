package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

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

	public static void register(RegisterCapabilitiesEvent e){
		e.register(IHeatHandler.class);
		e.register(IAxleHandler.class);
		e.register(ICogHandler.class);
		e.register(IBeamHandler.class);
		e.register(IAxisHandler.class);
		e.register(IChemicalHandler.class);
	}
}
