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
		ClientRegistry.registerKeyBinding(controlEnergy = new KeyBinding("controlEnergy.name", Keyboard.KEY_NONE, Main.MODID));
		ClientRegistry.registerKeyBinding(controlPotential = new KeyBinding("controlPotential.name", Keyboard.KEY_NONE, Main.MODID));
		ClientRegistry.registerKeyBinding(controlStability = new KeyBinding("controlStability.name", Keyboard.KEY_NONE, Main.MODID));
		ClientRegistry.registerKeyBinding(controlVoid = new KeyBinding("controlVoid.name", Keyboard.KEY_NONE, Main.MODID));
	}
}
