package com.Da_Technomancer.crossroads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendPlayerTickCountToClient;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.BrazierTileEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public final class EventHandlerCommon{

	@SubscribeEvent
	public void cancelWitchSpawns(LivingSpawnEvent e){
		if(e.getEntity() instanceof EntityWitch){
			// 64 squared
			int RANGE_SQUARED = 4096;

			for(TileEntity te : e.getWorld().tickableTileEntities){
				if(te instanceof BrazierTileEntity && ((BrazierTileEntity) te).getState() == 2 && te.getDistanceSq(e.getX(), e.getY(), e.getZ()) <= RANGE_SQUARED){
					e.setResult(Result.DENY);
				}
			}
		}
	}

	private static final Random RAND = new Random();
	private static final ArrayList<Chunk> TO_RETROGEN = new ArrayList<Chunk>();
	protected static Ticket loadingTicket;

	/**
	 * Only should be called on the virtual server side. 
	 */
	public static void updateLoadedPrototypeChunks(){
		PrototypeWorldSavedData data = PrototypeWorldSavedData.get(false);

		ArrayList<ChunkPos> toLoad = new ArrayList<ChunkPos>();
		for(PrototypeInfo info : data.prototypes){
			if(info != null && info.owner != null && info.owner.get() != null){
				info.owner.get().loadTick();
				if(info.owner.get().shouldRun()){
					toLoad.add(info.chunk);
				}
			}
		}

		boolean emptyTicket = loadingTicket == null || loadingTicket.getChunkList().isEmpty();

		if(!emptyTicket || !toLoad.isEmpty()){
			if(emptyTicket){
				DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
			}
			ForgeChunkManager.releaseTicket(loadingTicket);
			WorldServer world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			loadingTicket = ForgeChunkManager.requestTicket(Main.instance, world, ForgeChunkManager.Type.NORMAL);
		}

		for(ChunkPos chunk : toLoad){
			ForgeChunkManager.forceChunk(loadingTicket, chunk);
		}
	}

	@SubscribeEvent
	public void runRetrogenAndLoadChunks(WorldTickEvent e){
		//Retrogen
		if(TO_RETROGEN.size() != 0){
			Chunk chunk = TO_RETROGEN.get(0);
			CommonProxy.WORLD_GEN.generate(RAND, chunk.xPosition, chunk.zPosition, chunk.getWorld(), null, null);
			TO_RETROGEN.remove(0);
		}

		//Prototype chunk loading
		//Only should be called on the server side.
		if(!e.world.isRemote && e.phase == Phase.START && e.world.provider.getDimension() == 0){
			e.world.profiler.startSection(Main.MODNAME + ": Prototype Loading Control");
			if(e.world.getTotalWorldTime() % 20 == 0){
				updateLoadedPrototypeChunks();
			}
			e.world.profiler.endSection();

			BeamManager.beamStage = (int) (e.world.getTotalWorldTime() % BeamManager.BEAM_TIME);
			BeamManager.resetVisual = e.world.getTotalWorldTime() % (20 * BeamManager.BEAM_TIME) < BeamManager.BEAM_TIME;
			BeamManager.cycleNumber = e.world.getTotalWorldTime() / BeamManager.BEAM_TIME;
		}

		//Field calculations
		if(!e.world.isRemote && e.world.getTotalWorldTime() % 5 == 0){
			e.world.profiler.startSection(Main.MODNAME + ": Field Calculations");
			FieldWorldSavedData data = FieldWorldSavedData.get(e.world);
			if(e.phase == TickEvent.Phase.START){
				data.nodeForces.clear();
				for(long key : data.fieldNodes.keySet()){
					data.nodeForces.put(key, FieldWorldSavedData.getDefaultChunkForce());
				}
			}else{
				HashSet<Long> toRemove = new HashSet<Long>();

				for(Entry<Long, byte[][][]> datum : data.fieldNodes.entrySet()){
					Long key = datum.getKey();
					try{
						BlockPos pos = calcFields(datum.getValue()[0], datum.getValue()[1], data.nodeForces.get(key)[0], data.nodeForces.get(key)[1]);
						if(pos != null){
							toRemove.add(key);
							MagicElements.TIME.getVoidEffect().doEffect(e.world, MiscOp.getChunkPosFromLong(key).getBlock(pos.getX(), pos.getY(), pos.getZ()), 128);
						}
					}catch(Exception ex){
						toRemove.add(key);
						Main.logger.log(Level.ERROR, "Caught an exception while calculating fields in dim: " + e.world.provider.getDimension() + ", ChunkPos: " + MiscOp.getChunkPosFromLong(key).toString(), ex);
					}
				}

				for(long remove : toRemove){
					data.fieldNodes.remove(remove);
				}
			}
			e.world.profiler.endSection();
		}

		//Time Dilation
		if(!e.world.isRemote && e.phase == Phase.START){
			e.world.profiler.startSection(Main.MODNAME + ": Entity Time Dilation");
			HashMap<Long, byte[][][]> fields = FieldWorldSavedData.get(e.world).fieldNodes;
			ArrayList<PrototypeInfo> prototypes = PrototypeWorldSavedData.get(false).prototypes;
			WorldServer prototypeWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			HashMap<Long, byte[][][]> fieldsProt = prototypeWorld == null ? null : FieldWorldSavedData.get(prototypeWorld).fieldNodes;
			ArrayList<Entity> entities = new ArrayList<Entity>();
			entities.addAll(e.world.loadedEntityList); //A copy of the original list is used to avoid ConcurrentModificationExceptions that arise from entities removing themselves when ticked. 
			for(Entity ent : entities){
				NBTTagCompound entNBT = ent.getEntityData();
				if(!entNBT.hasKey("fStop")){
					ent.updateBlocked = false;
				}else{
					entNBT.removeTag("fStop");
				}

				int potential = 8;

				BlockPos entityPos = ent.getPosition();
				long entityChunk = MiscOp.getLongFromChunkPos(new ChunkPos(entityPos));
				if(fields.containsKey(entityChunk)){
					byte[][][] entFields = fields.get(entityChunk);
					int chunkRelX = MiscOp.getChunkRelativeCoord(entityPos.getX()) / 2;
					int chunkRelZ = MiscOp.getChunkRelativeCoord(entityPos.getZ()) / 2;
					potential = 1 + entFields[1][chunkRelX][chunkRelZ];
					if(entFields[1][chunkRelX][chunkRelZ] > entFields[0][chunkRelX][chunkRelZ]){
						potential = 0;
					}
				}

				for(EntityPlayer play : e.world.playerEntities){
					ItemStack heldStack = play.getHeldItem(EnumHand.MAIN_HAND);
					int offsetX = 7 + entityPos.getX() - play.getPosition().getX();
					int offsetZ = 7 + entityPos.getZ() - play.getPosition().getZ();
					if(heldStack.getItem() == ModItems.watch && heldStack.hasTagCompound() && offsetX < 16 && offsetZ < 16 && offsetX >= 0 && offsetZ >= 0){
						NBTTagCompound watchNBT = heldStack.getTagCompound().getCompoundTag("prot");
						if(!watchNBT.hasKey("index")){
							continue;
						}
						int index = watchNBT.getInteger("index");
						if(prototypes.size() <= index || prototypes.get(index) == null){
							heldStack.getTagCompound().removeTag("prot");
							continue;
						}

						byte[][][] watchFields = fieldsProt == null ? FieldWorldSavedData.getDefaultChunkFlux() : fieldsProt.get(MiscOp.getLongFromChunkPos(prototypes.get(index).chunk));
						if(watchFields != null){
							offsetX /= 2;
							offsetZ /= 2;
							potential *= 1 + watchFields[1][offsetX][offsetZ];
							potential /= 8;
							if(watchFields[1][offsetX][offsetZ] > watchFields[0][offsetX][offsetZ]){
								potential = 0;
							}
						}
					}
				}

				int totalRuns = (potential / 8) + (RAND.nextInt(8) < potential % 8 ? 1 : 0);

				if(totalRuns == 1){
					continue;
				}
				if(ent instanceof EntityPlayerMP){
					ModPackets.network.sendTo(new SendPlayerTickCountToClient(totalRuns), (EntityPlayerMP) ent);
				}
				for(int i = 1; i < totalRuns; i++){
					ent.onUpdate();
				}
				if(totalRuns == 0){
					if(ent.updateBlocked){
						entNBT.setBoolean("fStop", true);
					}else{
						ent.updateBlocked = true;
					}
				}
			}
			e.world.profiler.endSection();
		}
	}

	//private static final float ADJACENT_RATE_COEFFICIENT = .5F;

	/**
	 * Performs the field calculations for one chunk. 
	 * @return A non-null value if the chunk fields should be wiped and there should be a flux event at the returned position (which is relative to 0, 0, 0 in the chunk, not the world). 
	 */
	@Nullable
	private static BlockPos calcFields(byte[][] fluxIn, byte[][] rateIn, short[][] fluxForceIn, short[][] rateForceIn){
		for(int x = 0; x < 8; x++){
			for(int z = 0; z < 8; z++){
				int fluxForce = fluxForceIn[x][z];
				int flux = ((int) fluxIn[x][z]) + 1;
				int rate = ((int) rateIn[x][z]) + 1;

				if(rate != 0){
					float change = rateForceIn[x][z];
					//change += x == 0 ? 0 : ADJACENT_RATE_COEFFICIENT * (float) rateForceIn[x - 1][z];
					//change += z == 0 ? 0 : ADJACENT_RATE_COEFFICIENT * (float) rateForceIn[x][z - 1];
					//change += x == 7 ? 0 : ADJACENT_RATE_COEFFICIENT * (float) rateForceIn[x + 1][z];
					//change += z == 7 ? 0 : ADJACENT_RATE_COEFFICIENT * (float) rateForceIn[x][z + 1];

					rate = (int) Math.max(1, Math.min(8 + change, 128));
					flux += Math.abs(rate - rateIn[x][z]) / 2;
				}
				float holder = ((float) (flux - 8)) / 8F;
				holder = holder == 0 ? 0 : RAND.nextInt((int) Math.copySign(Math.ceil(Math.abs(holder)), holder) + (int) (Math.abs(holder) / holder));

				if(rate == 0 || flux + fluxForce + holder < rate){
					flux += Math.max(1, 32 * Math.pow(2, fluxForce));
					rate = 0;
				}else{
					flux += holder;
					flux += fluxForce;
				}

				if(flux >= 128){
					return new BlockPos(1 + (2 * x), RAND.nextInt(256), 1 + (2 * z));
				}

				fluxIn[x][z] = (byte) Math.min(127, Math.max(0, flux - 1));
				rateIn[x][z] = (byte) Math.min(127, Math.max(-1, rate - 1));
			}
		}

		return null;
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

	@SubscribeEvent
	public void syncPlayerTagToClient(EntityJoinWorldEvent e){
		//The down-side of using this event is that every time the player switches dimension, the update data has to be resent. 

		if(e.getEntity() instanceof EntityPlayerMP){
			StoreNBTToClient.syncNBTToClient((EntityPlayerMP) e.getEntity(), false);
		}
	}
}
