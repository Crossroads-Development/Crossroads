package com.Da_Technomancer.crossroads.particles.sounds;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

public class CRSounds{

	private static final ArrayList<SoundEvent> soundsToRegister = new ArrayList<>(2);

	public static final SoundEvent BEAM_PASSIVE = createEvent("beam_passive");
	public static final SoundEvent BEAM_TRANSMUTE = createEvent("beam_transmute");

	public static void register(IForgeRegistry<SoundEvent> reg){
		for(SoundEvent e : soundsToRegister){
			reg.register(e);
		}
	}

	private static SoundEvent createEvent(String name){
		ResourceLocation id = new ResourceLocation(Crossroads.MODID, name);
		SoundEvent created = new SoundEvent(id).setRegistryName(id);
		soundsToRegister.add(created);
		return created;
	}

	/**
	 * Plays a sounds to all nearby players when called on the virtual server
	 * Does nothing when called on the virtual client
	 * @param world The world to play the sound in
	 * @param pos The position to play the sound at. Plays from the center of the blockspace
	 * @param sound Sound to play
	 * @param category Sound category, for volume settings
	 * @param volume Volume multiplier, multiplied with event volume and clamped within [0, 1] after multiplying
	 * @param pitch Pitch multiplier, multiplied with event pitch and clamped within [0.5, 2] after multiplying
	 */
	public static void playSoundServer(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch){
		world.playSound(null, pos, sound, category, volume, pitch);
	}
}
