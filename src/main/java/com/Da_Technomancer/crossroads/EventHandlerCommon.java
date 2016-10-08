package com.Da_Technomancer.crossroads;

import java.util.ArrayList;
import java.util.Random;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.BrazierTileEntity;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public final class EventHandlerCommon{

	@SubscribeEvent
	public void cancelWitchSpawns(LivingSpawnEvent e){
		if(e.getEntity() instanceof EntityWitch && BrazierTileEntity.blockSpawning(e.getWorld(), e.getX(), e.getY(), e.getZ())){
			e.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void addSpecialItems(EntityJoinWorldEvent event){
		if(event.getEntity() instanceof EntityPlayer && !event.getEntity().worldObj.isRemote){
			EntityPlayer player = (EntityPlayer) event.getEntity();

			NBTTagCompound tag = MiscOp.getPlayerTag(player);
			if(!tag.hasKey("starter")){
				switch(player.getGameProfile().getName()){
					case "Da_Technomancer":
						player.inventory.addItemStackToInventory(new ItemStack(ModItems.debugReader, 1));
						break;
					default:
						break;
				}

				tag.setBoolean("starter", true);
			}
		}
	}

	private static final Random RAND = new Random();
	private static final ArrayList<Chunk> TO_RETROGEN = new ArrayList<Chunk>();
	
	@SubscribeEvent
	public void worldTick(WorldTickEvent e){
		if(TO_RETROGEN.size() != 0){
			Chunk chunk = TO_RETROGEN.get(0);
			CommonProxy.WORLD_GEN.generate(RAND, chunk.xPosition, chunk.zPosition, chunk.getWorld(), null, null);
			TO_RETROGEN.remove(0);
		}
	}
	
	@SubscribeEvent
	public void buildRetrogenList(ChunkDataEvent.Load e) {
		if (!ModConfig.retrogen.getString().isEmpty()) {
			NBTTagCompound tag = e.getData().getCompoundTag(Main.MODID);

			if(tag == null){
				tag = new NBTTagCompound();
				e.getData().setTag(Main.MODID, tag);
			}

			if (!tag.hasKey(ModConfig.retrogen.getString())) {
				tag.setBoolean(ModConfig.retrogen.getString(), true);
				TO_RETROGEN.add(e.getChunk());
			}
		}
	}
}
