package com.Da_Technomancer.crossroads.ambient.sounds;

import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

public class CRSounds{

	private static final ArrayList<SoundEvent> soundsToRegister = new ArrayList<>(6);

	public static final SoundEvent BEAM_PASSIVE = createEvent("beam_passive");
	public static final SoundEvent BEAM_TRANSMUTE = createEvent("beam_transmute");
	public static final SoundEvent ELECTRIC_SPARK = createEvent("electric_spark");
	public static final SoundEvent ELECTRIC_ARC = createEvent("electric_arc");
	public static final SoundEvent FLUX_TRANSFER = createEvent("entropy_transfer");
	public static final SoundEvent STEAM_RELEASE = createEvent("steam_release");

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

	/**
	 * Plays a sounds to the player when called on the virtual client
	 * Does nothing when called on the virtual server
	 * Only plays sounds for players within a certain distance- same distance used for sound packets from the server
	 * @param world The world to play the sound in
	 * @param pos The position to play the sound at. Plays from the center of the blockspace
	 * @param sound Sound to play
	 * @param category Sound category, for volume settings
	 * @param volume Volume multiplier, multiplied with event volume and clamped within [0, 1] after multiplying
	 * @param pitch Pitch multiplier, multiplied with event pitch and clamped within [0.5, 2] after multiplying
	 */
	public static void playSoundClientLocal(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch){
		if(world.isClientSide){
			float distance = Math.max(1, volume) * 16F;
			PlayerEntity player = SafeCallable.getClientPlayer();
			if(player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= distance * distance){
				world.playSound(player, pos, sound, category, volume, pitch);
			}
		}
	}
}
