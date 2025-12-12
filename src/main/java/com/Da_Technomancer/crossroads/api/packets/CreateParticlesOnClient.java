package com.Da_Technomancer.crossroads.api.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CreateParticlesOnClient(ParticleOptions particle, double x, double y, double z, float xDeviation,
									  float yDeviation, float zDeviation, float xVel, float yVel, float zVel,
									  float xVelDeviation, float yVelDeviation, float zVelDeviation, int count,
									  boolean gaussian) implements CustomPacketPayload{

	public static final CustomPacketPayload.Type<CreateParticlesOnClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "create_particles_te"));


	public static final StreamCodec<RegistryFriendlyByteBuf, CreateParticlesOnClient> STREAM_CODEC = StreamCodec.ofMember(CreateParticlesOnClient::encode, CreateParticlesOnClient::new);

	public CreateParticlesOnClient(RegistryFriendlyByteBuf buffer){
		this(
				ParticleTypes.STREAM_CODEC.decode(buffer),
				// position
				buffer.readDouble(),
				buffer.readDouble(),
				buffer.readDouble(),
				// deviation
				buffer.readFloat(),
				buffer.readFloat(),
				buffer.readFloat(),
				// velocity
				buffer.readFloat(),
				buffer.readFloat(),
				buffer.readFloat(),
				// velocity deviation
				buffer.readFloat(),
				buffer.readFloat(),
				buffer.readFloat(),
				// remainder
				buffer.readInt(),
				buffer.readBoolean()
		);
	}

	private void encode(RegistryFriendlyByteBuf buffer){
		// particle
		ParticleTypes.STREAM_CODEC.encode(buffer, particle);
		// position
		buffer.writeDouble(x);
		buffer.writeDouble(y);
		buffer.writeDouble(z);
		//deviation
		buffer.writeFloat(xDeviation);
		buffer.writeFloat(yDeviation);
		buffer.writeFloat(zDeviation);
		//velocity
		buffer.writeFloat(xVel);
		buffer.writeFloat(yVel);
		buffer.writeFloat(zVel);
		//velocity deviation
		buffer.writeFloat(xVelDeviation);
		buffer.writeFloat(yVelDeviation);
		buffer.writeFloat(zVelDeviation);
		//remaining params
		buffer.writeInt(count);
		buffer.writeBoolean(gaussian);
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}

	static void handlePacketClient(final CreateParticlesOnClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			Level world;
			if((world = SafeCallable.getClientWorld()) != null){
				CRParticles.summonParticlesFromClient(world,
						packet.particle,
						packet.count,
						packet.x,
						packet.y,
						packet.z,
						packet.xDeviation,
						packet.yDeviation,
						packet.zDeviation,
						packet.xVel,
						packet.yVel,
						packet.zVel,
						packet.xVelDeviation,
						packet.yVelDeviation,
						packet.zVelDeviation,
						packet.gaussian);
			}
		});
	}
}
