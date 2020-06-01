package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.API.effects.alchemy.AetherEffect;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

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
		AetherEffect.setBiomeAtPos(Minecraft.getInstance().world, pos, ForgeRegistries.BIOMES.getValue(new ResourceLocation(newBiome)));
	}
}
