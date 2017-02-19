package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendStaffToServer extends Message<SendStaffToServer>{

	public SendStaffToServer(){
		
	}
	
	public String element;
	public boolean decrease;

	public SendStaffToServer(String element, boolean decrease){
		this.element = element;
		this.decrease = decrease;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.SERVER){
			System.err.println("MessageToServer received on wrong side:" + context.side);
			return null;
		}
		EntityPlayerMP player = context.getServerHandler().playerEntity;
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
		if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == ModItems.staffTechnomancy){
			hand = EnumHand.MAIN_HAND;
		}else if(player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == ModItems.staffTechnomancy){
			hand = EnumHand.OFF_HAND;
		}
		if(hand == null){
			return;
		}
		if(player.getHeldItem(hand).getTagCompound() == null){
			player.getHeldItem(hand).setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbt = player.getHeldItem(hand).getTagCompound();
		int i = nbt.getInteger(element);
		i += decrease ? -1 : 1;
		i = Math.min(8, Math.max(i, 0));
		nbt.setInteger(element, i);
		player.resetActiveHand();
	}
}
