package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;
import com.Da_Technomancer.essentials.packets.ServerPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SendBeamItemToServer extends ServerPacket{

	public String element;
	public boolean decrease;

	private static final Field[] FIELDS = fetchFields(SendBeamItemToServer.class, "element", "decrease");

	@SuppressWarnings("unused")
	public SendBeamItemToServer(){

	}

	public SendBeamItemToServer(String element, boolean decrease){
		this.element = element;
		this.decrease = decrease;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(@Nullable ServerPlayerEntity player){
		Hand hand = null;
		if(player != null && player.getHeldItemMainhand().getItem() instanceof BeamUsingItem){
			hand = Hand.MAIN_HAND;
		}else if(player != null && player.getHeldItemOffhand().getItem() instanceof BeamUsingItem){
			hand = Hand.OFF_HAND;
		}
		if(hand == null){
			return;
		}
		ItemStack stack = player.getHeldItem(hand);
		((BeamUsingItem) stack.getItem()).preChanged(stack, player);
		if(stack.getTag() == null){
			stack.put(new CompoundNBT());
		}
		CompoundNBT nbt = stack.getTag();
		int i = nbt.getInt(element);
		i += decrease ? -1 : 1;
		i = Math.min(8, Math.max(i, 0));
		nbt.putInt(element, i);
		player.resetActiveHand();
	}
}
