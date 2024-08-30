package com.Da_Technomancer.crossroads;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.extensions.IKeyMappingExtension;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class Keys{

	//Stored as IKeyMappingExtension instead of KeyBinding as KeyBinding is a client-side-only class.
	public static IKeyMappingExtension controlEnergy;
	public static IKeyMappingExtension controlPotential;
	public static IKeyMappingExtension controlStability;
	public static IKeyMappingExtension controlVoid;
	public static IKeyMappingExtension controlZoom;
	public static IKeyMappingExtension boost;

	public static boolean keysInitialized = false;

	protected static final ArrayList<KeyMapping> toRegister = new ArrayList<>(6);

	protected static void init(){
		try{
			controlEnergy = new KeyMapping("key.control_energy", InputConstants.UNKNOWN.getValue(), Crossroads.MODID);
			controlPotential = new KeyMapping("key.control_potential", InputConstants.UNKNOWN.getValue(), Crossroads.MODID);
			controlStability = new KeyMapping("key.control_stability", InputConstants.UNKNOWN.getValue(), Crossroads.MODID);
			controlVoid = new KeyMapping("key.control_void", InputConstants.UNKNOWN.getValue(), Crossroads.MODID);
			controlZoom = new KeyMapping("key.crossroads.control_zoom", InputConstants.UNKNOWN.getValue(), Crossroads.MODID);
			boost = new KeyMapping("key.prop_pack_boost", 341, Crossroads.MODID);//341 is Control
			keysInitialized = true;
		}catch(RuntimeException e){
			Crossroads.logger.error("Keys loaded on server side; Report to mod author", e);
		}

		toRegister.add((KeyMapping) controlEnergy);
		toRegister.add((KeyMapping) controlPotential);
		toRegister.add((KeyMapping) controlStability);
		toRegister.add((KeyMapping) controlVoid);
		toRegister.add((KeyMapping) controlZoom);
		toRegister.add((KeyMapping) boost);
	}

	public static boolean isKeyActiveAndMatch(IKeyMappingExtension key, int activeKeyCode, int scanCode){
		InputConstants.Key input = InputConstants.getKey(activeKeyCode, scanCode);
		return key.isActiveAndMatches(input);
	}

	@Nullable
	public static KeyMapping asKeyMapping(IKeyMappingExtension key){
		if(key instanceof KeyMapping mapping){
			return mapping;
		}
		return null;
	}
}
