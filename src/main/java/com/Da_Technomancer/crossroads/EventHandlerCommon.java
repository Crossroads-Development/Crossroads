package com.Da_Technomancer.crossroads;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendElementNBTToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.BrazierTileEntity;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AnvilUpdateEvent;
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
		if(event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote){
			EntityPlayer player = (EntityPlayer) event.getEntity();

			NBTTagCompound tag = MiscOp.getPlayerTag(player);
			ModPackets.network.sendTo(new SendElementNBTToClient(tag.getCompoundTag("elements")), (EntityPlayerMP) event.getEntity());

			//A convenience feature to start with a debug tool.
			if(!tag.hasKey("starter")){
				if(player.getGameProfile().getName().equals("Da_Technomancer")){
					player.inventory.addItemStackToInventory(new ItemStack(ModItems.moduleGoggles, 1));
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

	private final float longRange = 1F/3F;
	private final float shortRange = 2F/3F;

	@SubscribeEvent
	public void calcFields(WorldTickEvent e){
		if(!e.world.isRemote && e.world.getTotalWorldTime() % 5 == 0){
			e.world.theProfiler.startSection("CrossroadsFieldCalculations");
			FieldWorldSavedData data = FieldWorldSavedData.get(e.world);
			if(e.phase == TickEvent.Phase.START){
				data.nodeForces.clear();
				for(long key : data.fieldNodes.keySet()){
					data.nodeForces.put(key, FieldWorldSavedData.getDefaultChunkForce());
				}
			}else{
				HashSet<Long> toRemove = new HashSet<Long>();
				//This method has some labeled continues. Please don't hurt me...
				fluxEvent:
					for(Entry<Long, byte[][][]> datum : data.fieldNodes.entrySet()){
						for(int i = 1; i >= 0; i--){
							for(int j = 0; j < 8; j++){
								for(int k = 0; k < 8; k++){
									float netForce = data.nodeForces.get(datum.getKey())[i][j][k];
									if(i == 1){
										netForce += j == 0 ? 0 : shortRange * (float) data.nodeForces.get(datum.getKey())[i][j - 1][k];
										netForce += k == 0 ? 0 : shortRange * (float) data.nodeForces.get(datum.getKey())[i][j][k - 1];
										netForce += j == 7 ? 0 : shortRange * (float) data.nodeForces.get(datum.getKey())[i][j + 1][k];
										netForce += k == 7 ? 0 : shortRange * (float) data.nodeForces.get(datum.getKey())[i][j][k + 1];
										netForce += j == 0 || k == 0 ? 0 : longRange * (float) data.nodeForces.get(datum.getKey())[i][j - 1][k - 1];
										netForce += j == 7 || k == 0 ? 0 : longRange * (float) data.nodeForces.get(datum.getKey())[i][j + 1][k - 1];
										netForce += j == 7 || k == 7 ? 0 : longRange * (float) data.nodeForces.get(datum.getKey())[i][j + 1][k + 1];
										netForce += j == 0 || k == 7 ? 0 : longRange * (float) data.nodeForces.get(datum.getKey())[i][j - 1][k + 1];
										netForce += j <= 1 ? 0 : longRange * (float) data.nodeForces.get(datum.getKey())[i][j - 2][k];
										netForce += k <= 1 ? 0 : longRange * (float) data.nodeForces.get(datum.getKey())[i][j][k - 2];
										netForce += j >= 6 ? 0 : longRange * (float) data.nodeForces.get(datum.getKey())[i][j + 2][k];
										netForce += k >= 6 ? 0 : longRange * (float) data.nodeForces.get(datum.getKey())[i][j][k + 2];
									}
									if(i == 0){
										if(datum.getValue()[0][j][k] < datum.getValue()[1][j][k]){
											datum.getValue()[0][j][k] = (byte) Math.max(0, Math.min(127, (int) (Math.max(1, 32 * Math.pow(2, (int) netForce)) + (int) datum.getValue()[0][j][k])));
											if(datum.getValue()[0][j][k] == 127){
												MagicElements.TIME.getVoidEffect().doEffect(e.world, FieldWorldSavedData.getChunkFromLong(e.world, datum.getKey()).getPos().getBlock(1 + (2 * j), RAND.nextInt(250) + 1, 1 + (2 * k)), 128);
												toRemove.add(datum.getKey());
												continue fluxEvent;
											}else if(datum.getValue()[0][j][k] >= datum.getValue()[1][j][k]){
												toRemove.add(datum.getKey());
												continue fluxEvent;
											}
										}else{
											netForce += datum.getValue()[0][j][k] == 7 ? 0 : ((float) (RAND.nextInt(8) - 1));
											datum.getValue()[0][j][k] = (byte) Math.max(0, Math.min(127, (int) netForce + (int) datum.getValue()[0][j][k]));
										}
									}else{
										byte newValue = (byte) Math.max(0, Math.min((int) netForce + 7, 127));
										data.nodeForces.get(datum.getKey())[0][j][k] += Math.abs(newValue - datum.getValue()[1][j][k]) / 2;
										datum.getValue()[1][j][k] = newValue;
									}

									if(i == 0 && datum.getValue()[0][j][k] == 127){
										MagicElements.TIME.getVoidEffect().doEffect(e.world, FieldWorldSavedData.getChunkFromLong(e.world, datum.getKey()).getPos().getBlock(1 + (2 * j), RAND.nextInt(250) + 1, 1 + (2 * k)), 128);
										toRemove.add(datum.getKey());
										continue fluxEvent;
									}
								}
							}
						}
					}
				for(long remove : toRemove){
					data.fieldNodes.remove(remove);
				}
			}
			e.world.theProfiler.endSection();
		}
	}

	private boolean dilatingTime = false;

	/**
	 * TODO make this work on A) Players, and B) entities other than EntityLiving (Ex. Arrows)
	 */
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void dilateEntityTime(LivingUpdateEvent e){
		if(e.getEntity().world.isRemote || dilatingTime || !FieldWorldSavedData.get(e.getEntity().getEntityWorld()).fieldNodes.containsKey(FieldWorldSavedData.getLongFromPos(e.getEntity().getPosition()))){
			return;
		}

		int potential = 1 + FieldWorldSavedData.get(e.getEntity().getEntityWorld()).fieldNodes.get(FieldWorldSavedData.getLongFromPos(e.getEntity().getPosition()))[1][FieldWorldSavedData.getChunkRelativeCoord(e.getEntity().getPosition().getX()) / 2][FieldWorldSavedData.getChunkRelativeCoord(e.getEntity().getPosition().getZ()) / 2];
		dilatingTime = true;
		if(FieldWorldSavedData.get(e.getEntity().getEntityWorld()).fieldNodes.get(FieldWorldSavedData.getLongFromPos(e.getEntity().getPosition()))[1][FieldWorldSavedData.getChunkRelativeCoord(e.getEntity().getPosition().getX()) / 2][FieldWorldSavedData.getChunkRelativeCoord(e.getEntity().getPosition().getZ()) / 2] > FieldWorldSavedData.get(e.getEntity().getEntityWorld()).fieldNodes.get(FieldWorldSavedData.getLongFromPos(e.getEntity().getPosition()))[0][FieldWorldSavedData.getChunkRelativeCoord(e.getEntity().getPosition().getX()) / 2][FieldWorldSavedData.getChunkRelativeCoord(e.getEntity().getPosition().getZ()) / 2]){
			potential = 0;
		}
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

	@SubscribeEvent
	public void craftGoggles(AnvilUpdateEvent e){
		if(e.getLeft().getItem() == ModItems.moduleGoggles){
			for(GoggleLenses lens : GoggleLenses.values()){
				if(lens.matchesRecipe(e.getRight()) && (!e.getLeft().hasTagCompound() || !e.getLeft().getTagCompound().hasKey(lens.name()))){
					ItemStack out = e.getLeft().copy();
					if(!out.hasTagCompound()){
						out.setTagCompound(new NBTTagCompound());
					}
					out.getTagCompound().setBoolean(lens.name(), true);
					e.setOutput(out);
					e.setCost(2);
					e.setMaterialCost(1);
					break;
				}
			}
		}
	}
}
