package com.Da_Technomancer.crossroads;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keys{
	
	public static KeyBinding controlEnergy;
	public static KeyBinding controlPotential;
	public static KeyBinding controlStability;
	public static KeyBinding controlVoid;
	
	protected static void init(){
		ClientRegistry.registerKeyBinding(controlEnergy = new KeyBinding("control_energy.name", Keyboard.KEY_NONE, Main.MODID));
		ClientRegistry.registerKeyBinding(controlPotential = new KeyBinding("control_potential.name", Keyboard.KEY_NONE, Main.MODID));
		ClientRegistry.registerKeyBinding(controlStability = new KeyBinding("control_stability.name", Keyboard.KEY_NONE, Main.MODID));
		ClientRegistry.registerKeyBinding(controlVoid = new KeyBinding("control_void.name", Keyboard.KEY_NONE, Main.MODID));
	}
}
