package com.Da_Technomancer.crossroads.effects.goggles_effects;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.api.technomancy.IGoggleEffect;
import com.Da_Technomancer.crossroads.items.OmniMeter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class QuartzGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(Level world, Player player){
		BlockHitResult ray = MiscUtil.rayTrace(player, 8);
		if(ray == null){
			return;
		}
		ArrayList<Component> chat = new ArrayList<>();
		OmniMeter.measure(chat, player, player.level, ray.getBlockPos(), ray.getDirection(), ray);
		if(!chat.isEmpty()){
			CRPackets.sendPacketToPlayer((ServerPlayer) player, new SendChatToClient(chat, OmniMeter.CHAT_ID, ray.getBlockPos()));
		}
	}
}