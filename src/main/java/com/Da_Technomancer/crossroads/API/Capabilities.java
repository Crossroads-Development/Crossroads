package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class Capabilities{

	public static Capability<IHeatHandler> HEAT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	public static Capability<IAxleHandler> AXLE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	
	public static Capability<ICogHandler> COG_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	
	public static Capability<IBeamHandler> BEAM_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	
	public static Capability<IAxisHandler> AXIS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	public static Capability<IChemicalHandler> CHEMICAL_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	public static void register(RegisterCapabilitiesEvent e){
		e.register(IHeatHandler.class);
		e.register(IAxleHandler.class);
		e.register(ICogHandler.class);
		e.register(IBeamHandler.class);
		e.register(IAxisHandler.class);
		e.register(IChemicalHandler.class);
	}
}
