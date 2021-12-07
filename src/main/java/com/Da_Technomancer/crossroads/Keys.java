package com.Da_Technomancer.crossroads;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.extensions.IForgeKeyMapping;

import javax.annotation.Nullable;

public class Keys{

	//Stored as IForgeKeyMapping instead of KeyBinding as KeyBinding is a client-side-only class.
	public static IForgeKeyMapping controlEnergy;
	public static IForgeKeyMapping controlPotential;
	public static IForgeKeyMapping controlStability;
	public static IForgeKeyMapping controlVoid;
	public static IForgeKeyMapping boost;

	public static boolean keysInitialized = false;

	protected static void init(){
		try{
			controlEnergy = new KeyMapping("key.control_energy", InputConstants.UNKNOWN.getValue(), Crossroads.MODID);
			controlPotential = new KeyMapping("key.control_potential", InputConstants.UNKNOWN.getValue(), Crossroads.MODID);
			controlStability = new KeyMapping("key.control_stability", InputConstants.UNKNOWN.getValue(), Crossroads.MODID);
			controlVoid = new KeyMapping("key.control_void", InputConstants.UNKNOWN.getValue(), Crossroads.MODID);
			boost = new KeyMapping("key.prop_pack_boost", 341, Crossroads.MODID);//341 is Control
			keysInitialized = true;
		}catch(RuntimeException e){
			Crossroads.logger.error("Keys loaded on server side; Report to mod author", e);
		}
		ClientRegistry.registerKeyBinding((KeyMapping) controlEnergy);
		ClientRegistry.registerKeyBinding((KeyMapping) controlPotential);
		ClientRegistry.registerKeyBinding((KeyMapping) controlStability);
		ClientRegistry.registerKeyBinding((KeyMapping) controlVoid);
		ClientRegistry.registerKeyBinding((KeyMapping) boost);
	}

	public static boolean isKeyActiveAndMatch(IForgeKeyMapping key, int activeKeyCode, int scanCode){
		InputConstants.Key input = InputConstants.getKey(activeKeyCode, scanCode);
		return key.isActiveAndMatches(input);
	}

	@Nullable
	public static KeyMapping asKeyMapping(IForgeKeyMapping key){
		if(key instanceof KeyMapping mapping){
			return mapping;
		}
		return null;
	}
}
