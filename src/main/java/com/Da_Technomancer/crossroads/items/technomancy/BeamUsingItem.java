package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendBeamItemToServer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public abstract class BeamUsingItem extends Item{

	public static final Codec<byte[]> CODEC = Codec.BYTE.listOf().comapFlatMap((unboundedList) -> {
		return Util.fixedSize(unboundedList, 4).map((list) -> {
			return new byte[] {list.get(0), list.get(1), list.get(2), list.get(3)};
		});
	}, byteArray -> List.of(byteArray[0], byteArray[1], byteArray[2], byteArray[3]));

	public static final StreamCodec<ByteBuf, byte[]> STREAM_CODEC = ByteBufCodecs.byteArray(4);

	private static long lastKeyTime = 0;//Used on the client side as a cooldown between setting changes

	protected BeamUsingItem(Properties properties){
		super(properties);
	}

	protected abstract byte maxSetting();

	public static byte[] getSetting(ItemStack stack){
		return stack.getOrDefault(CRItems.BEAM_SETTING_DATA, new byte[4]);
	}

	public static void setSetting(ItemStack stack, byte[] settings){
		stack.set(CRItems.BEAM_SETTING_DATA, settings);
	}

	@OnlyIn(Dist.CLIENT)
	public void adjustSetting(LocalPlayer player, ItemStack stack, int elemIndex, boolean increase){
		long currTime = System.currentTimeMillis();
		long timeSince = currTime - lastKeyTime;
		if(timeSince < 0){
			//Something weird is going on with system time.
			timeSince = 1_000_000;//Arbitrary large number
		}
		if(timeSince < 250){
			return;
		}
		lastKeyTime = currTime;
		byte[] settings = getSetting(stack);
		boolean acted = false;
		if(increase){
			if(settings[elemIndex] < maxSetting()){
				settings[elemIndex] += 1;
				acted = true;
			}
		}else if(settings[elemIndex] > 0){
			settings[elemIndex] -= 1;
			acted = true;
		}
		if(acted){
			CRSounds.playSoundClientLocal(player.level(), player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 4, (float) Math.random() / 4 + 0.5F);
			CRPackets.sendPacketToServer(new SendBeamItemToServer(settings));
		}else{
			//Play a sound at a slightly lower pitch
			CRSounds.playSoundClientLocal(player.level(), player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 4, (float) Math.random() / 4);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		byte[] settings = getSetting(stack);
		tooltip.add(Component.translatable("tt.crossroads.beam_item.energy", settings[0], maxSetting()));
		tooltip.add(Component.translatable("tt.crossroads.beam_item.potential", settings[1], maxSetting()));
		tooltip.add(Component.translatable("tt.crossroads.beam_item.stability", settings[2], maxSetting()));
		tooltip.add(Component.translatable("tt.crossroads.beam_item.void", settings[3], maxSetting()));
	}
}
