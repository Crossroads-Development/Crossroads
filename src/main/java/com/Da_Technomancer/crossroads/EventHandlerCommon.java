package com.Da_Technomancer.crossroads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.BrazierTileEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
	private final float adjacentRateCoefficient= .5F;

	@SubscribeEvent
	public void runRetrogenAndLoadChunks(WorldTickEvent e){
		//Retrogen
		if(TO_RETROGEN.size() != 0){
			Chunk chunk = TO_RETROGEN.get(0);
			CommonProxy.WORLD_GEN.generate(RAND, chunk.xPosition, chunk.zPosition, chunk.getWorld(), null, null);
			TO_RETROGEN.remove(0);
		}

		//Prototype chunk loading
		//Only should be called on the server side. Not called every tick, as that would be excessive
		if(!e.world.isRemote && e.world.provider.getDimension() == 0 && e.world.getTotalWorldTime() % 20 == 0){
			PrototypeWorldSavedData data = PrototypeWorldSavedData.get();
			WorldServer world = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			ForgeChunkManager.releaseTicket(loadingTicket);
			loadingTicket = ForgeChunkManager.requestTicket(Main.instance, world, ForgeChunkManager.Type.NORMAL);

			for(PrototypeInfo info : data.prototypes){
				if(info != null && info.owner != null && info.owner.get() != null && !info.owner.get().loadTick()){
					ForgeChunkManager.forceChunk(loadingTicket, info.chunk);
				}
			}
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
				//This method has some labeled continues. Please don't hurt me...
				fluxEvent:
					for(Entry<Long, byte[][][]> datum : data.fieldNodes.entrySet()){
						for(int i = 1; i >= 0; i--){
							for(int j = 0; j < 8; j++){
								for(int k = 0; k < 8; k++){
									float netForce = data.nodeForces.get(datum.getKey())[i][j][k];
									if(i == 0){
										if(datum.getValue()[0][j][k] < datum.getValue()[1][j][k]){
											datum.getValue()[0][j][k] = (byte) Math.max(0, Math.min(127, (int) (Math.max(1, 32 * Math.pow(2, (int) netForce)) + (int) datum.getValue()[0][j][k])));
											if(datum.getValue()[0][j][k] == 127){
												MagicElements.TIME.getVoidEffect().doEffect(e.world, MiscOp.getChunkPosFromLong(datum.getKey()).getBlock(1 + (2 * j), RAND.nextInt(250) + 1, 1 + (2 * k)), 128);
												toRemove.add(datum.getKey());
												continue fluxEvent;
											}else if(datum.getValue()[0][j][k] >= datum.getValue()[1][j][k]){
												toRemove.add(datum.getKey());
												continue fluxEvent;
											}
										}else{
											netForce += datum.getValue()[0][j][k] == 7 ? 0 : ((float) RAND.nextInt(8));
											datum.getValue()[0][j][k] = (byte) Math.max(0, Math.min(127, (int) netForce + (int) datum.getValue()[0][j][k]));
										}
									}else{
										netForce += j == 0 ? 0 : adjacentRateCoefficient * (float) data.nodeForces.get(datum.getKey())[i][j - 1][k];
										netForce += k == 0 ? 0 : adjacentRateCoefficient * (float) data.nodeForces.get(datum.getKey())[i][j][k - 1];
										netForce += j == 7 ? 0 : adjacentRateCoefficient * (float) data.nodeForces.get(datum.getKey())[i][j + 1][k];
										netForce += k == 7 ? 0 : adjacentRateCoefficient * (float) data.nodeForces.get(datum.getKey())[i][j][k + 1];
										
										byte newValue = (byte) Math.max(0, Math.min((int) netForce + 7, 127));
										data.nodeForces.get(datum.getKey())[0][j][k] += Math.abs(newValue - datum.getValue()[1][j][k]) / 2;
										datum.getValue()[1][j][k] = newValue;
									}

									if(i == 0 && datum.getValue()[0][j][k] == 127){
										MagicElements.TIME.getVoidEffect().doEffect(e.world, MiscOp.getChunkPosFromLong(datum.getKey()).getBlock(1 + (2 * j), RAND.nextInt(250) + 1, 1 + (2 * k)), 128);
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
			e.world.profiler.endSection();
		}

		//Time dilation TODO make it work on players (needs to do stuff client side to work on players)
		if(!e.world.isRemote){
			e.world.profiler.startSection(Main.MODNAME + ": Entity Time Dilation");
			HashMap<Long, byte[][][]> fields = FieldWorldSavedData.get(e.world).fieldNodes;
			ArrayList<PrototypeInfo> prototypes = PrototypeWorldSavedData.get().prototypes;
			HashMap<Long, byte[][][]> fieldsProt = FieldWorldSavedData.get(DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID)).fieldNodes;
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
					int offsetX = 8 + entityPos.getX() - play.getPosition().getX();
					int offsetZ = 8 + entityPos.getZ() - play.getPosition().getZ();
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
						
						byte[][][] watchFields = fieldsProt.get(MiscOp.getLongFromChunkPos(prototypes.get(index).chunk));
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

				if(potential == 8){
					continue;
				}
				for(int i = 1; i < potential / 8; i++){
					ent.onUpdate();
				}
				if(RAND.nextInt(8) < potential % 8){
					if(potential > 8){
						ent.onUpdate();
					}
				}else if(potential < 8){
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
}
