package com.Da_Technomancer.crossroads.particles.sounds;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CRSounds{

	public static final SoundEvent BEAM_PASSIVE = createEvent("beam_passive");

	public static void register(IForgeRegistry<SoundEvent> reg){
		reg.register(BEAM_PASSIVE);
	}

	private static SoundEvent createEvent(String name){
		ResourceLocation id = new ResourceLocation(Crossroads.MODID, name);
		return new SoundEvent(id).setRegistryName(id);
	}
}
