package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBeamItemToServer;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BeamUsingItem extends Item{

	private static final String NBT_KEY = "setting";

	protected BeamUsingItem(Properties properties){
		super(properties);
	}

	protected abstract byte maxSetting();

	public static byte[] getSetting(ItemStack stack){
		CompoundNBT nbt = stack.getTag();
		if(nbt == null){
			return new byte[4];
		}
		return nbt.getByteArray(NBT_KEY);
	}

	public static void setSetting(ItemStack stack, byte[] settings){
		if(stack.getTag() == null){
			stack.setTag(new CompoundNBT());
		}
		stack.getTag().putByteArray(NBT_KEY, settings);
	}

	@OnlyIn(Dist.CLIENT)
	public void adjustSetting(ClientPlayerEntity player, ItemStack stack, int elemIndex, boolean increase){
		byte[] settings = getSetting(stack);
		if(increase){
			settings[elemIndex] = (byte) Math.min(settings[elemIndex] + 1, maxSetting());
		}else{
			settings[elemIndex] = (byte) Math.max(settings[elemIndex] - 1, 0);
		}
		player.world.playSound(player, player.getPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 5, (float) Math.random());
		CRPackets.sendPacketToServer(new SendBeamItemToServer(settings));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		byte[] settings = getSetting(stack);
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_item.energy", settings[0], maxSetting()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_item.potential", settings[1], maxSetting()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_item.stability", settings[2], maxSetting()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_item.void", settings[3], maxSetting()));
	}
}
