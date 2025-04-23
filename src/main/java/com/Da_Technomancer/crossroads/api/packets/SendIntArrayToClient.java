package com.Da_Technomancer.crossroads.api.packets;


import com.Da_Technomancer.crossroads.Crossroads;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendIntArrayToClient(byte id, int[] message, BlockPos pos) implements CustomPacketPayload{
	public static CustomPacketPayload.Type<SendIntArrayToClient> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "send_int_array_client"));

	// TODO: needed to use ofMember because I couldn't find a way to encode an int array. If there's one I just missed, this can be returned to a composite
	public static final StreamCodec<ByteBuf, SendIntArrayToClient> STREAM_CODEC = StreamCodec.ofMember(SendIntArrayToClient::encode, SendIntArrayToClient::new);

	private SendIntArrayToClient(ByteBuf buffer){
		this(buffer.readByte(), bufferToIntArray(buffer), BlockPos.STREAM_CODEC.decode(buffer));
	}

	private void encode(ByteBuf buffer){
		buffer.writeByte(id);
		intArrayToBuffer(buffer);
		BlockPos.STREAM_CODEC.encode(buffer, pos);
	}

	private static int[] bufferToIntArray(ByteBuf buf){
		int count = buf.readInt();
		int[] message = new int[count];
		for(int i = 0; i < count; i++){
			message[i] = buf.readInt();
		}
		return message;
	}

	private void intArrayToBuffer(ByteBuf buffer){
		buffer.writeInt(message.length);
		for(int i : message){
			buffer.writeInt(i);
		}
	}


	static void handlePacketClient(final SendIntArrayToClient packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			BlockEntity te = SafeCallable.getClientWorld().getBlockEntity(packet.pos);

			if(te instanceof IIntArrayReceiver receiver){
				receiver.receiveInts(packet.id, packet.message, null);
			}
		});
	}

	// TODO: This should be client side only. Remove this message before final commit.


	@Override
	public Type<? extends CustomPacketPayload> type(){
		return null;
	}
}
