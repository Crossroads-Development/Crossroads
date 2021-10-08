package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.effects.alchemy.AetherEffect;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;

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
		Level world = SafeCallable.getClientWorld();
		AetherEffect.setBiomeAtPos(world, pos, AetherEffect.lookupBiome(ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(newBiome)), world));
	}
}
