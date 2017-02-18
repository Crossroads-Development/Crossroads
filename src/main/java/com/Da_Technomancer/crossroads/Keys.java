package com.Da_Technomancer.crossroads;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keys{
	
	public static KeyBinding staffEnergy;
	public static KeyBinding staffPotential;
	public static KeyBinding staffStability;
	public static KeyBinding staffVoid;
	
	protected static void init(){
		ClientRegistry.registerKeyBinding(staffEnergy = new KeyBinding("staffEnergy.name", Keyboard.KEY_NONE, Main.MODID));
		ClientRegistry.registerKeyBinding(staffPotential = new KeyBinding("staffPotential.name", Keyboard.KEY_NONE, Main.MODID));
		ClientRegistry.registerKeyBinding(staffStability = new KeyBinding("staffStability.name", Keyboard.KEY_NONE, Main.MODID));
		ClientRegistry.registerKeyBinding(staffVoid = new KeyBinding("staffVoid.name", Keyboard.KEY_NONE, Main.MODID));
	}
}
