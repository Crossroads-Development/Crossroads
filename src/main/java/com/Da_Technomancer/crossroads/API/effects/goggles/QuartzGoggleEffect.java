package com.Da_Technomancer.crossroads.API.effects.goggles;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendFieldsToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.items.OmniMeter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class QuartzGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat, RayTraceResult ray){
		if(ray == null){
			return;
		}

		OmniMeter.measure(chat, player, player.world, ray.getBlockPos(), ray.sideHit, (float) ray.hitVec.x, (float) ray.hitVec.y, (float) ray.hitVec.z);

		if(world.getTotalWorldTime() % 5 == 1){
			long key = MiscUtil.getLongFromChunkPos(new ChunkPos(player.getPosition()));
			if(FieldWorldSavedData.get(world).fieldNodes.containsKey(key) && FieldWorldSavedData.get(world).fieldNodes.get(key).isActive){
				ModPackets.network.sendTo(new SendFieldsToClient(FieldWorldSavedData.get(world).fieldNodes.get(key), key), (EntityPlayerMP) player);
			}else{
				ModPackets.network.sendTo(new SendFieldsToClient(null, key), (EntityPlayerMP) player);
			}
		}
	}
}