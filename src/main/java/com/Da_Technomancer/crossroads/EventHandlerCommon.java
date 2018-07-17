package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.alchemy.AtmosChargeSavedData;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendPlayerTickCountToClient;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.API.technomancy.ChunkField;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public final class EventHandlerCommon{

	@SubscribeEvent
	public void onEntitySpawn(LivingSpawnEvent e){
		if(e.getEntity() instanceof EntityCreeper && (float) AtmosChargeSavedData.getCharge(e.getWorld()) / (float) AtmosChargeSavedData.CAPACITY >= 0.9F && (ModConfig.getConfigInt(ModConfig.atmosEffect, false) & 2) == 2){
			NBTTagCompound nbt = new NBTTagCompound();
			e.getEntityLiving().writeEntityToNBT(nbt);
			nbt.setBoolean("powered", true);
			e.getEntityLiving().readEntityFromNBT(nbt);
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

		if(emptyTicket ? !toLoad.isEmpty() : !toLoad.containsAll(loadingTicket.getChunkList()) || !loadingTicket.getChunkList().containsAll(toLoad)){
			ForgeChunkManager.releaseTicket(loadingTicket);
			if(toLoad.isEmpty()){
				loadingTicket = null;
			}else{
				WorldServer protWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
				if(protWorld == null){
					DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
					protWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
				}
				loadingTicket = ForgeChunkManager.requestTicket(Main.instance, protWorld, ForgeChunkManager.Type.NORMAL);
			}

			for(ChunkPos chunk : toLoad){
				ForgeChunkManager.forceChunk(loadingTicket, chunk);
			}
		}
	}

	@SubscribeEvent
	public void worldTick(WorldTickEvent e){
		//Retrogen
		if(TO_RETROGEN.size() != 0){
			Chunk chunk = TO_RETROGEN.get(0);
			CommonProxy.WORLD_GEN.generate(RAND, chunk.x, chunk.z, chunk.getWorld(), null, null);
			TO_RETROGEN.remove(0);
		}

		//Prototype chunk loading
		//Only should be called on the server side.
		if(!e.world.isRemote && e.phase == Phase.START && e.world.provider.getDimension() == 0){
			e.world.profiler.startSection(Main.MODNAME + "-Prototype Loading Control");
			if(e.world.getTotalWorldTime() % 20 == 0){
				updateLoadedPrototypeChunks();
			}
			e.world.profiler.endSection();

			BeamManager.beamStage = (int) (e.world.getTotalWorldTime() % BeamManager.BEAM_TIME);
			BeamManager.resetVisual = e.world.getTotalWorldTime() % (20 * BeamManager.BEAM_TIME) < BeamManager.BEAM_TIME;
			BeamManager.cycleNumber = e.world.getTotalWorldTime() / BeamManager.BEAM_TIME;
		}

		//Field calculations
		if(!e.world.isRemote && e.world.getTotalWorldTime() % 5 == 0 && e.phase == TickEvent.Phase.END){
			e.world.profiler.startSection(Main.MODNAME + "-Field Calculations");
			FieldWorldSavedData data = FieldWorldSavedData.get(e.world);
			HashSet<Long> toRemove = new HashSet<Long>();

			for(Entry<Long, ChunkField> datum : data.fieldNodes.entrySet()){
				Long key = datum.getKey();
				try{
					if(datum.getValue().tick(e.world, MiscOp.getChunkPosFromLong(key))){
						toRemove.add(key);
					}
				}catch(Exception ex){
					toRemove.add(key);
					Main.logger.log(Level.ERROR, "Caught an exception while calculating fields in dim: " + e.world.provider.getDimension() + ", ChunkPos: " + MiscOp.getChunkPosFromLong(key).toString(), ex);
				}
			}

			for(long remove : toRemove){
				data.fieldNodes.remove(remove);
			}

			e.world.profiler.endSection();
		}

		//Time Dilation
		if(!e.world.isRemote && e.phase == Phase.START){
			e.world.profiler.startSection(Main.MODNAME + ": Entity Time Dilation");
			HashMap<Long, ChunkField> fields = FieldWorldSavedData.get(e.world).fieldNodes;
			ArrayList<PrototypeInfo> prototypes = PrototypeWorldSavedData.get(false).prototypes;
			WorldServer prototypeWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			HashMap<Long, ChunkField> fieldsProt = prototypeWorld == null ? null : FieldWorldSavedData.get(prototypeWorld).fieldNodes;
			//A copy of the original list is used to avoid ConcurrentModificationExceptions that arise from entities removing themselves when ticked.
			ArrayList<Entity> entities = new ArrayList<Entity>(e.world.loadedEntityList);
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
					ChunkField entFields = fields.get(entityChunk);
					int chunkRelX = MiscOp.getChunkRelativeCoord(entityPos.getX());
					int chunkRelZ = MiscOp.getChunkRelativeCoord(entityPos.getZ());
					potential = 1 + entFields.nodes[chunkRelX][chunkRelZ];
					if(entFields.nodes[chunkRelX][chunkRelZ] > entFields.flux){
						potential = 0;
					}
				}

				if(fieldsProt != null){
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

							ChunkField watchFields = fieldsProt.get(MiscOp.getLongFromChunkPos(prototypes.get(index).chunk));
							if(watchFields != null){
								potential *= 1 + watchFields.nodes[offsetX][offsetZ];
								potential /= 8;
								if(watchFields.nodes[offsetX][offsetZ] > watchFields.flux){
									potential = 0;
								}
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

		//Atmospheric overcharge effect
		if(!e.world.isRemote && (ModConfig.getConfigInt(ModConfig.atmosEffect, false) & 1) == 1){
			e.world.profiler.startSection(Main.MODNAME + ": Overcharge lightning effects");
			float chargeLevel = (float) AtmosChargeSavedData.getCharge(e.world) / (float) AtmosChargeSavedData.CAPACITY;
			if(chargeLevel > 0.5F){
				Iterator<Chunk> iterator = e.world.getPersistentChunkIterable(((WorldServer) (e.world)).getPlayerChunkMap().getChunkIterator());
				while(iterator.hasNext()){
					Chunk chunk = iterator.next();
					int j = chunk.x * 16;
					int k = chunk.z * 16;
					chunk.enqueueRelightChecks();
					chunk.onTick(false);

					if (e.world.provider.canDoLightning(chunk) && e.world.rand.nextInt(350_000 - (int) (300_000 * chargeLevel)) == 0){
						BlockPos blockpos = adjustPosToNearbyEntity(((WorldServer) (e.world)), new BlockPos(j + e.world.rand.nextInt(16), 0, k + e.world.rand.nextInt(16)));
						DifficultyInstance difficultyinstance = e.world.getDifficultyForLocation(blockpos);

						if (e.world.getGameRules().getBoolean("doMobSpawning") && e.world.rand.nextDouble() < (double)difficultyinstance.getAdditionalDifficulty() * 0.01D){
							EntitySkeletonHorse entityskeletonhorse = new EntitySkeletonHorse(e.world);
							entityskeletonhorse.setTrap(true);
							entityskeletonhorse.setGrowingAge(0);
							entityskeletonhorse.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
							e.world.spawnEntity(entityskeletonhorse);
							e.world.addWeatherEffect(new EntityLightningBolt(e.world, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), true));
						}else{
							e.world.addWeatherEffect(new EntityLightningBolt(e.world, (double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), false));
						}
					}
				}
			}
			e.world.profiler.endSection();
		}
	}

	/**
	 * A re-implementation of a protected WorldServer method
	 */
	private static BlockPos adjustPosToNearbyEntity(WorldServer w, BlockPos pos){
		BlockPos blockpos = w.getPrecipitationHeight(pos);
		AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos, new BlockPos(blockpos.getX(), w.getHeight(), blockpos.getZ()))).grow(3.0D);
		List<EntityLivingBase> list = w.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, new Predicate<EntityLivingBase>(){
			public boolean apply(@Nullable EntityLivingBase p_apply_1_){
				return p_apply_1_ != null && p_apply_1_.isEntityAlive() && w.canSeeSky(p_apply_1_.getPosition());
			}
		});

		if (!list.isEmpty()){
			return ((EntityLivingBase)list.get(w.rand.nextInt(list.size()))).getPosition();
		}else{
			if (blockpos.getY() == -1){
				blockpos = blockpos.up(2);
			}

			return blockpos;
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
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(lens.matchesRecipe(e.getRight()) && (!e.getLeft().hasTagCompound() || !e.getLeft().getTagCompound().hasKey(lens.name()))){
					ItemStack out = e.getLeft().copy();
					if(!out.hasTagCompound()){
						out.setTagCompound(new NBTTagCompound());
					}
					e.setCost((int) Math.pow(2, out.getTagCompound().getSize()));
					out.getTagCompound().setBoolean(lens.name(), true);
					e.setOutput(out);
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

	@SubscribeEvent
	public void damageTaken(LivingHurtEvent e){
		if(e.getSource() == DamageSource.FALL){
			EntityLivingBase ent = e.getEntityLiving();

			ItemStack boots = ent.getItemStackFromSlot(EntityEquipmentSlot.FEET);
			if(boots.getItem() == ModItems.chickenBoots && boots.getItemDamage() != ModItems.chickenBoots.getMaxDamage(boots)){
				e.setCanceled(true);
				boots.damageItem(Math.min((int) e.getAmount(), ModItems.chickenBoots.getMaxDamage(boots) - boots.getItemDamage()), ent);
				ent.getEntityWorld().playSound(null, ent.posX, ent.posY, ent.posZ, SoundEvents.ENTITY_CHICKEN_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
				return;
			}

			if(ent instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer) ent;
				if(player.inventory.clearMatchingItems(ModItems.nitroglycerin, -1, -1, null) > 0){
					player.world.createExplosion(null, player.posX, player.posY, player.posZ, 5F, true);
				}
			}
		}
	}


	private static final Field explosionPower;
	private static final Field explosionSmoking;

	static{
		Field holderPower = null;
		Field holderSmoking = null;
		try{
			for(Field f : Explosion.class.getDeclaredFields()){
				if("field_77280_f".equals(f.getName()) || "size".equals(f.getName())){
					holderPower = f;
					holderPower.setAccessible(true);
				}else if("field_82755_b".equals(f.getName()) || "damagesTerrain".equals(f.getName())){
					holderSmoking = f;
					holderSmoking.setAccessible(true);
				}
			}
			//For no apparent reason ReflectionHelper consistently crashes in an obfus. environment for me with the forge method, so the above for loop is used instead.
		}catch(Exception e){
			Main.logger.catching(e);
		}
		explosionPower = holderPower;
		explosionSmoking = holderSmoking;
		if(explosionPower == null){
			Main.logger.error("Reflection to get explosionPower failed. Disabling relevant feature(s).");
		}
		if(explosionSmoking == null){
			Main.logger.error("Reflection to get explosionSmoking failed. Disabling relevant feature(s).");
		}
	}


	@SubscribeEvent
	public void modifyExplosion(ExplosionEvent.Start e){
		boolean perpetuate = false;
		for(Entity ent : e.getWorld().loadedEntityList){
			if(ent instanceof EntityGhostMarker){
				EntityGhostMarker mark = (EntityGhostMarker) ent;
				if(mark.getType() == EntityGhostMarker.EnumMarkerType.EQUALIBRIUM && mark.data != null && mark.getPositionVector().subtract(e.getExplosion().getPosition()).lengthSquared() <= mark.data.getInteger("range")){
					e.setCanceled(true);
					return;
				}else if(mark.getType() == EntityGhostMarker.EnumMarkerType.VOID_EQUALIBRIUM && mark.data != null && mark.getPositionVector().subtract(e.getExplosion().getPosition()).lengthSquared() <= mark.data.getInteger("range")){
					perpetuate = true;
				}
			}
		}

		if(perpetuate && explosionPower != null && explosionSmoking != null){
			EntityGhostMarker marker = new EntityGhostMarker(e.getWorld(), EntityGhostMarker.EnumMarkerType.DELAYED_EXPLOSION, 5);
			marker.setPosition(e.getExplosion().getPosition().x, e.getExplosion().getPosition().y, e.getExplosion().getPosition().z);
			NBTTagCompound data = new NBTTagCompound();
			try{
				data.setFloat("power", explosionPower.getFloat(e.getExplosion()));
				data.setBoolean("smoking", explosionSmoking.getBoolean(e.getExplosion()));
			}catch(IllegalAccessException ex){
				Main.logger.error("Failed to perpetuate explosion. Dim: " + e.getWorld().provider.getDimension() + "; Pos: " + e.getExplosion().getPosition());
			}
			marker.data = data;
			e.getWorld().spawnEntity(marker);
		}
	}
}
