package com.Da_Technomancer.crossroads.api.packets;


import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Predicate;

public record SendOptionalSoundToClient(BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch, SoundCondition condition) implements CustomPacketPayload{

	public static Type<SendOptionalSoundToClient> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_optional_sound_to_client"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SendOptionalSoundToClient> STREAM_CODEC = StreamCodec.ofMember(SendOptionalSoundToClient::encode, SendOptionalSoundToClient::new);

	private SendOptionalSoundToClient(RegistryFriendlyByteBuf buffer){
		this(BlockPos.STREAM_CODEC.decode(buffer), SoundEvent.DIRECT_STREAM_CODEC.decode(buffer), buffer.readEnum(SoundSource.class), buffer.readFloat(), buffer.readFloat(), SoundCondition.STREAM_CODEC.decode(buffer));
	}

	private void encode(RegistryFriendlyByteBuf buffer){
		BlockPos.STREAM_CODEC.encode(buffer, pos);
		SoundEvent.DIRECT_STREAM_CODEC.encode(buffer, sound);
		buffer.writeEnum(source);
		buffer.writeFloat(volume);
		buffer.writeFloat(pitch);
		SoundCondition.STREAM_CODEC.encode(buffer, condition);
	}


	static void handlePacketClient(final SendOptionalSoundToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(packet.condition.evaluate(packet.pos)){
				Level level = SafeCallable.getClientWorld();
				CRSounds.playSoundClientLocal(level, packet.pos, packet.sound, packet.source, packet.volume, packet.pitch);
			}
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}

	public enum SoundCondition implements StringRepresentable{

		ALWAYS(Predicates.alwaysTrue()),
		NEVER(Predicates.alwaysFalse()),
		BEAM_SOUND_CONFIG(pos -> CRConfig.beamSounds.getAsBoolean()),
		ELECTRIC_SOUND_CONFIG(pos -> CRConfig.electricSounds.getAsBoolean()),
		FLUX_SOUND_CONFIG(pos -> CRConfig.fluxSounds.getAsBoolean());

		public static final StreamCodec<RegistryFriendlyByteBuf, SoundCondition> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(StringRepresentable.fromEnum(SoundCondition::values));

		private final Predicate<BlockPos> soundCondition;

		SoundCondition(Predicate<BlockPos> soundCondition){
			this.soundCondition = soundCondition;
		}

		boolean evaluate(BlockPos pos){
			return soundCondition.test(pos);
		}

		@Override
		public String getSerializedName(){
			return name();
		}
	}
}
