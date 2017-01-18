package com.Da_Technomancer.crossroads;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.fields.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendElementNBTToClient;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.BrazierTileEntity;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public final class EventHandlerCommon{

	@SubscribeEvent
	public void cancelWitchSpawns(LivingSpawnEvent e){
		if(e.getEntity() instanceof EntityWitch && BrazierTileEntity.blockSpawning(e.getWorld(), e.getX(), e.getY(), e.getZ())){
			e.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void addItemsAndUpdateData(EntityJoinWorldEvent event){
		if(event.getEntity() instanceof EntityPlayer && !event.getEntity().worldObj.isRemote){
			EntityPlayer player = (EntityPlayer) event.getEntity();

			NBTTagCompound tag = MiscOp.getPlayerTag(player);
			ModPackets.network.sendTo(new SendElementNBTToClient(tag.getCompoundTag("elements")), (EntityPlayerMP) event.getEntity());
			
			//A convenience feature to start with a debug tool.
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
	public void runRetrogen(WorldTickEvent e){
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
			e.getData().setTag(Main.MODID, tag);

			if (!tag.hasKey(ModConfig.retrogen.getString())) {
				tag.setBoolean(ModConfig.retrogen.getString(), true);
				TO_RETROGEN.add(e.getChunk());
			}
		}
	}
	
	//These values were created from the formula
	//n = maximum distance in nodes where forces have effect.
	//t = n - (distance from the node with force (At node with force, this equals 1))
	//x = final multiplier on force
	//if(t < n/2), x = 2(t*t)/(n*n)
	//else t -= n/2
	//x = -2t*t/(n*n) + 2t/n + .5
	private final float longRange = 2F/9F;
	private final float shortRange = 7F/9F;
	
	@SubscribeEvent
	public void calcFields(WorldTickEvent e){
		e.world.theProfiler.startSection("CrossroadsFieldCalculations");
		if(!e.world.isRemote && e.world.getTotalWorldTime() % 5 == 0){
			FieldWorldSavedData data = FieldWorldSavedData.get(e.world);
			if(e.phase == TickEvent.Phase.START){
				data.nodeForces.clear();
				
			}else{
				HashSet<Long> toRemove = new HashSet<Long>();
				for(Entry<Long, byte[][][]> datum : data.fieldNodes.entrySet()){
					//The fields must be processed in the order 2->0->1
					for(int i = 2; i < 3; ){
						for(int j = 0; j < 8; j++){
							for(int k = 0; k < 8; k++){
								if(i == 0){
									if(data.nodeForces.containsKey(datum.getKey())){
										data.nodeForces.get(datum.getKey())[0][j][k] = (short) (datum.getValue()[2][j][k] == -1 ? 128 : Math.min(128, ((float) data.nodeForces.get(datum.getKey())[0][j][k]) * (1F + (((float) data.nodeForces.get(datum.getKey())[0][j][k]) * (RAND.nextFloat() - .1F) * 8F / ((float) data.fieldNodes.get(datum.getKey())[2][j][k] + 1F)))));
									}
								}

								float netForce = Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[0][j][k], -10));
								netForce += j == 0 ? 0 : shortRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j - 1][k], -10));
								netForce += k == 0 ? 0 : shortRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j][k - 1], -10));
								netForce += j == 7 ? 0 : shortRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j + 1][k], -10));
								netForce += k == 7 ? 0 : shortRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j][k + 1], -10));
								netForce += j == 0 || k == 0 ? 0 : longRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j - 1][k - 1], -10));
								netForce += j == 7 || k == 0 ? 0 : longRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j + 1][k - 1], -10));
								netForce += j == 7 || k == 7 ? 0 : longRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j + 1][k + 1], -10));
								netForce += j == 0 || k == 7 ? 0 : longRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j - 1][k + 1], -10));
								netForce += j <= 1 ? 0 : longRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j - 2][k], -10));
								netForce += k <= 1 ? 0 : longRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j][k - 2], -10));
								netForce += j >= 6 ? 0 : longRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j + 2][k], -10));
								netForce += k >= 6 ? 0 : longRange * (float) Math.min(128, Math.max(data.nodeForces.get(datum.getKey())[i][j][k + 2], -10));

								if(i != 0 || datum.getValue()[0][j][k] >= datum.getValue()[2][j][k]){
									datum.getValue()[i][j][k] = (byte) (i == 1 ? datum.getValue()[2][j][k] > datum.getValue()[0][j][k] ? -1 : Math.max(Math.min((byte) Math.min(7 + (int) netForce, 127), datum.getValue()[0][j][k]), datum.getValue()[2][j][k]) : (byte) Math.max(0, Math.min(7 + (int) netForce, 127)));
								}else{
									datum.getValue()[0][j][k] = (byte) Math.min(127, !data.nodeForces.containsKey(datum.getKey()) || data.nodeForces.get(datum.getKey())[0][j][k] >= 0 ? 32 : Math.max(1, 32 / -data.nodeForces.get(datum.getKey())[0][j][k]));
									if(datum.getValue()[0][j][k] > datum.getValue()[2][j][k]){
										toRemove.add(datum.getKey());
									}
								}
								if(i == 0 && datum.getValue()[0][j][k] == 127){
									MagicElements.TIME.getVoidEffect().doEffect(e.world, FieldWorldSavedData.getChunkFromLong(e.world, datum.getKey()).getChunkCoordIntPair().getBlock(1 + (2 * j), RAND.nextInt(250) + 1, 1 + (2 * k)), 127 - datum.getValue()[2][j][k]);
									//To (in most cases) prevent a chunk from triggering multiple disasters at once.
									data.nodeForces.get(datum.getKey())[0][j][k] = 0;
									toRemove.add(datum.getKey());
								}
							}
						}
						switch(i){
							case 2: i = 0;
							break;
							case 0: i = 1;
							break;
							case 1: i = 3;
							break;
						}
					}
				}
				
				for(long remove : toRemove){
					data.fieldNodes.remove(remove);
				}
			}
		}
		e.world.theProfiler.endSection();
	}
	
	private boolean dilatingTime = false;
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void dilateEntityTime(LivingUpdateEvent e){
		if(e.getEntity().worldObj.isRemote || dilatingTime || !FieldWorldSavedData.get(e.getEntity().getEntityWorld()).fieldNodes.containsKey(FieldWorldSavedData.getLongFromChunk(e.getEntity().getEntityWorld().getChunkFromBlockCoords(e.getEntity().getPosition())))){
			return;
		}
		
		int potential = 1 + FieldWorldSavedData.get(e.getEntity().getEntityWorld()).fieldNodes.get(FieldWorldSavedData.getLongFromChunk(e.getEntity().getEntityWorld().getChunkFromBlockCoords(e.getEntity().getPosition())))[1][(e.getEntity().getPosition().getX() % 16) / 2][(e.getEntity().getPosition().getZ() % 16) / 2];
		dilatingTime = true;
		
		for(int i = 1; i < potential / 8; i++){
			e.getEntity().onUpdate();
		}
		if(RAND.nextInt(8) < potential % 8){
			if(potential > 8){
				e.getEntity().onUpdate();
			}
		}else if(potential < 8){
			e.setCanceled(true);
		}
		
		dilatingTime = false;
	}
}
