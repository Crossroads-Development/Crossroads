package com.Da_Technomancer.crossroads;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keys{
	
	public static KeyBinding controlEnergy = new KeyBinding("key.control_energy", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
	public static KeyBinding controlPotential = new KeyBinding("key.control_potential", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
	public static KeyBinding controlStability = new KeyBinding("key.control_stability", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
	public static KeyBinding controlVoid = new KeyBinding("key.control_void", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
	public static KeyBinding boost = new KeyBinding("key.prop_pack_boost", 341, Crossroads.MODID);//341 is Control
	
	protected static void init(){
		ClientRegistry.registerKeyBinding(controlEnergy);
		ClientRegistry.registerKeyBinding(controlPotential);
		ClientRegistry.registerKeyBinding(controlStability);
		ClientRegistry.registerKeyBinding(controlVoid);
		ClientRegistry.registerKeyBinding(boost);
	}
}
