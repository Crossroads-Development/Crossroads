package com.Da_Technomancer.crossroads;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.extensions.IForgeKeybinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keys{

	//Stored as IForgeKeybinding instead of KeyBinding as KeyBinding is a client-side-only class.
	public static IForgeKeybinding controlEnergy;
	public static IForgeKeybinding controlPotential;
	public static IForgeKeybinding controlStability;
	public static IForgeKeybinding controlVoid;
	public static IForgeKeybinding boost;

	public static boolean keysInitialized = false;

	protected static void init(){
		try{
			controlEnergy = new KeyBinding("key.control_energy", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
			controlPotential = new KeyBinding("key.control_potential", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
			controlStability = new KeyBinding("key.control_stability", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
			controlVoid = new KeyBinding("key.control_void", InputMappings.INPUT_INVALID.getKeyCode(), Crossroads.MODID);
			boost = new KeyBinding("key.prop_pack_boost", 341, Crossroads.MODID);//341 is Control
			keysInitialized = true;
		}catch(RuntimeException e){
			Crossroads.logger.error("Keys loaded on server side; Report to mod author", e);
		}
		ClientRegistry.registerKeyBinding((KeyBinding) controlEnergy);
		ClientRegistry.registerKeyBinding((KeyBinding) controlPotential);
		ClientRegistry.registerKeyBinding((KeyBinding) controlStability);
		ClientRegistry.registerKeyBinding((KeyBinding) controlVoid);
		ClientRegistry.registerKeyBinding((KeyBinding) boost);
	}
}
