package com.Da_Technomancer.crossroads;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keys{
	
	public static KeyBinding controlEnergy = new KeyBinding("control_energy.name", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
	public static KeyBinding controlPotential = new KeyBinding("control_potential.name", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
	public static KeyBinding controlStability = new KeyBinding("control_stability.name", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
	public static KeyBinding controlVoid = new KeyBinding("control_void.name", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
	
	protected static void init(){
		ClientRegistry.registerKeyBinding(controlEnergy);
		ClientRegistry.registerKeyBinding(controlPotential);
		ClientRegistry.registerKeyBinding(controlStability);
		ClientRegistry.registerKeyBinding(controlVoid);
	}
}
