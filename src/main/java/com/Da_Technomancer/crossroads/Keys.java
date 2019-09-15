package com.Da_Technomancer.crossroads;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keys{
	
	public static KeyBinding controlEnergy;
	public static KeyBinding controlPotential;
	public static KeyBinding controlStability;
	public static KeyBinding controlVoid;
	
	protected static void init(){
		ClientRegistry.registerKeyBinding(controlEnergy = new KeyBinding("control_energy.name", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID));
		ClientRegistry.registerKeyBinding(controlPotential = new KeyBinding("control_potential.name", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID));
		ClientRegistry.registerKeyBinding(controlStability = new KeyBinding("control_stability.name", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID));
		ClientRegistry.registerKeyBinding(controlVoid = new KeyBinding("control_void.name", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID));
	}
}
