package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.items.MagicUsingItem;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendMagicItemToServer extends Message<SendMagicItemToServer>{

	public SendMagicItemToServer(){
		
	}
	
	public String element;
	public boolean decrease;

	public SendMagicItemToServer(String element, boolean decrease){
		this.element = element;
		this.decrease = decrease;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.SERVER){
			System.err.println("MessageToServer received on wrong side:" + context.side);
			return null;
		}
		EntityPlayerMP player = context.getServerHandler().player;
		if(player == null){
			System.err.println("Player was null on packet arrival");
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
	
	public void processMessage(EntityPlayer player, String element, boolean decrease){
		EnumHand hand = null;
		if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof MagicUsingItem){
			hand = EnumHand.MAIN_HAND;
		}else if(player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() instanceof MagicUsingItem){
			hand = EnumHand.OFF_HAND;
		}
		if(hand == null){
			return;
		}
		ItemStack stack = player.getHeldItem(hand);
		((MagicUsingItem) stack.getItem()).preChanged(stack, player);
		if(stack.getTagCompound() == null){
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbt = stack.getTagCompound();
		int i = nbt.getInteger(element);
		i += decrease ? -1 : 1;
		i = Math.min(8, Math.max(i, 0));
		nbt.setInteger(element, i);
		player.resetActiveHand();
	}
}
