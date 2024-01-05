package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendBeamItemToServer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BeamUsingItem extends Item{

	private static final String NBT_KEY = "setting";

	private static long lastKeyTime = 0;//Used on the client side as a cooldown between setting changes

	protected BeamUsingItem(Properties properties){
		super(properties);
	}

	protected abstract byte maxSetting();

	public static byte[] getSetting(ItemStack stack){
		CompoundTag nbt = stack.getTag();
		if(nbt == null || !nbt.contains(NBT_KEY)){
			return new byte[4];
		}
		return nbt.getByteArray(NBT_KEY);
	}

	public static void setSetting(ItemStack stack, byte[] settings){
		if(stack.getTag() == null){
			stack.setTag(new CompoundTag());
		}
		stack.getTag().putByteArray(NBT_KEY, settings);
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
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced){
		byte[] settings = getSetting(stack);
		tooltip.add(Component.translatable("tt.crossroads.beam_item.energy", settings[0], maxSetting()));
		tooltip.add(Component.translatable("tt.crossroads.beam_item.potential", settings[1], maxSetting()));
		tooltip.add(Component.translatable("tt.crossroads.beam_item.stability", settings[2], maxSetting()));
		tooltip.add(Component.translatable("tt.crossroads.beam_item.void", settings[3], maxSetting()));
	}
}
