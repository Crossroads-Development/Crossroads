package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.technomancy.BeamUsingItem;

import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendBeamItemToServer extends Message<SendBeamItemToServer>{

	public SendBeamItemToServer(){
		
	}
	
	public String element;
	public boolean decrease;

	public SendBeamItemToServer(String element, boolean decrease){
		this.element = element;
		this.decrease = decrease;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.SERVER){
			Crossroads.logger.error("MessageToServer received on wrong side:" + context.side);
			return null;
		}
		ServerPlayerEntity player = context.getServerHandler().player;
		if(player == null){
			Crossroads.logger.error("Player was null on packet arrival");
			return null;
		}
		player.getServerWorld().addScheduledTask(new Runnable(){
			@Override
			public void run(){
				processMessage(player, element, decrease);
			}
		});

		return null;
	}
	
	public void processMessage(PlayerEntity player, String element, boolean decrease){
		Hand hand = null;
		if(player.getHeldItemMainhand().getItem() instanceof BeamUsingItem){
			hand = Hand.MAIN_HAND;
		}else if(player.getHeldItemOffhand().getItem() instanceof BeamUsingItem){
			hand = Hand.OFF_HAND;
		}
		if(hand == null){
			return;
		}
		ItemStack stack = player.getHeldItem(hand);
		((BeamUsingItem) stack.getItem()).preChanged(stack, player);
		if(stack.getTagCompound() == null){
			stack.setTagCompound(new CompoundNBT());
		}
		CompoundNBT nbt = stack.getTagCompound();
		int i = nbt.getInteger(element);
		i += decrease ? -1 : 1;
		i = Math.min(8, Math.max(i, 0));
		nbt.setInteger(element, i);
		player.resetActiveHand();
	}
}
