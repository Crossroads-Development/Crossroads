package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.effects.alchemy.AetherEffect;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

@SuppressWarnings("serial")
public class SendBiomeUpdateToClient extends ClientPacket{

	public BlockPos pos;
	public String newBiome;

	private static final Field[] FIELDS = fetchFields(SendBiomeUpdateToClient.class, "pos", "newBiome");

	@SuppressWarnings("unused")
	public SendBiomeUpdateToClient(){

	}

	/**
	 * When a biome is changed on the server side, the change isn't sent to clients (visible in f3 menu) until the render dimension switches/rejoins. This packet forces the render to recognize a new biome.
	 * @param pos The position that changed
	 * @param newBiome The registry name of the new biome
	 */
	public SendBiomeUpdateToClient(BlockPos pos, ResourceLocation newBiome){
		this.pos = pos;
		this.newBiome = newBiome.toString();
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		//The .getClientWorld() call is needed to defer class loading and prevent this crashing on dedicated servers
		World world = SafeCallable.getClientWorld();
		AetherEffect.setBiomeAtPos(world, pos, AetherEffect.lookupBiome(RegistryKey.func_240903_a_(Registry.BIOME_KEY, new ResourceLocation(newBiome)), world));
	}
}
