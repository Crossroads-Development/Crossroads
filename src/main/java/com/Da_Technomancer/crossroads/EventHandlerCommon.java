package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.CrReflection;
import com.Da_Technomancer.crossroads.API.alchemy.AtmosChargeSavedData;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendPlayerTickCountToClient;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.TemporalAcceleratorTileEntity;
import com.Da_Technomancer.essentials.ReflectionUtil;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public final class EventHandlerCommon{

	@SubscribeEvent
	public void onEntitySpawn(LivingSpawnEvent e){
		for(Entity ent : e.getWorld().loadedEntityList){
			if(ent instanceof EntityGhostMarker){
				EntityGhostMarker mark = (EntityGhostMarker) ent;
				if(mark.getType() == EntityGhostMarker.EnumMarkerType.BLOCK_SPAWNING && mark.data != null && mark.getPositionVector().subtract(e.getEntity().getPositionVector()).length() <= mark.data.getInt("range")){
					e.setCanceled(true);
					return;
				}
			}
		}


		if(e.getEntity() instanceof CreeperEntity && (float) AtmosChargeSavedData.getCharge(e.getWorld()) / (float) AtmosChargeSavedData.getCapacity() >= 0.9F && (CRConfig.atmosEffect.get() & 2) == 2){
			CompoundNBT nbt = new CompoundNBT();
			e.getEntityLiving().writeEntityToNBT(nbt);
			nbt.putBoolean("powered", true);
			e.getEntityLiving().readEntityFromNBT(nbt);
		}
	}

	private static final Random RAND = new Random();
	private static final ArrayList<Chunk> TO_RETROGEN = new ArrayList<>();
	protected static Ticket loadingTicket;

	/**
	 * Only should be called on the virtual server side.
	 */
	public static void updateLoadedPrototypeChunks(){
		PrototypeWorldSavedData data = PrototypeWorldSavedData.get(false);

		ArrayList<ChunkPos> toLoad = new ArrayList<>();
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
				ServerWorld protWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
				if(protWorld == null){
					DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
					protWorld = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
				}
				loadingTicket = ForgeChunkManager.requestTicket(Crossroads.instance, protWorld, ForgeChunkManager.Type.NORMAL);
			}

			for(ChunkPos chunk : toLoad){
				ForgeChunkManager.forceChunk(loadingTicket, chunk);
			}
		}
	}

	//The main and sub keys allow differentiating between entities with updateBlocked due to crossroads, and updateBlocked due to other mods. In effect, it is a preemptive compatibility bugfix
	protected static final String MAIN_KEY = "cr_pause";
	protected static final String SUB_KEY = "cr_pause_prior";

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent e){
		//Retrogen
		if(TO_RETROGEN.size() != 0){
			Chunk chunk = TO_RETROGEN.get(0);
			CommonProxy.WORLD_GEN.generate(RAND, chunk.x, chunk.z, chunk.getWorld(), null, null);
			TO_RETROGEN.remove(0);
		}

		//Prototype chunk loading
		//Only should be called on the server side.
		if(!e.world.isRemote && e.phase == TickEvent.Phase.START && e.world.provider.getDimension() == 0){
			e.world.getProfiler().startSection(Crossroads.MODNAME + "-Prototype Loading Control");
			if(e.world.getGameTime() % 20 == 0){
				updateLoadedPrototypeChunks();
			}
			e.world.getProfiler().endSection();

			BeamManager.beamStage = (int) (e.world.getGameTime() % BeamManager.BEAM_TIME);
			//BeamManager.resetVisual = e.world.getGameTime() % (20 * BeamManager.BEAM_TIME) < BeamManager.BEAM_TIME;
			BeamManager.cycleNumber = e.world.getGameTime() / BeamManager.BEAM_TIME;
		}


		//Time Dilation
		if(!e.world.isRemote && e.phase == TickEvent.Phase.START){
			e.world.getProfiler().startSection(Crossroads.MODNAME + ": Entity Time Dilation");
			ArrayList<TemporalAcceleratorTileEntity.Region> timeStoppers = new ArrayList<>();
			for(TileEntity te : e.world.tickableTileEntities){
				if(te instanceof TemporalAcceleratorTileEntity && ((TemporalAcceleratorTileEntity) te).stoppingTime()){
					timeStoppers.add(((TemporalAcceleratorTileEntity) te).getRegion());
				}
			}

			for(Entity ent : e.world.loadedEntityList){
				CompoundNBT entNBT = ent.getPersistentData();
				if(entNBT.getBoolean(MAIN_KEY)){
					if(!entNBT.getBoolean(SUB_KEY)){
						ent.updateBlocked = false;
					}
					entNBT.putBoolean(MAIN_KEY, false);
					entNBT.putBoolean(SUB_KEY, false);
				}

				for(TemporalAcceleratorTileEntity.Region region : timeStoppers){
					if(region.inRegion(ent.getPosition())){
						entNBT.putBoolean(MAIN_KEY, true);
						if(ent.updateBlocked){
							entNBT.putBoolean(SUB_KEY, true);
						}else{
							ent.updateBlocked = true;
						}
						if(ent instanceof ServerPlayerEntity){
							CrossroadsPackets.network.sendTo(new SendPlayerTickCountToClient(0), (ServerPlayerEntity) ent);
						}
						break;
					}
				}
			}
			e.world.getProfiler().endSection();
		}

		//Temporal Entropy decay
		if(!e.world.isRemote && e.phase == TickEvent.Phase.START){
			EntropySavedData.addEntropy(e.world, -CRConfig.entropyDecayRate.get());
		}


		//Atmospheric overcharge effect
		if(!e.world.isRemote && (CRConfig.atmosEffect.get() & 1) == 1){
			e.world.getProfiler().startSection(Crossroads.MODNAME + ": Overcharge lightning effects");
			float chargeLevel = (float) AtmosChargeSavedData.getCharge(e.world) / (float) AtmosChargeSavedData.getCapacity();
			if(chargeLevel > 0.5F){
				Iterator<Chunk> iterator = e.world.getPersistentChunkIterable(((ServerWorld) (e.world)).getPlayerChunkMap().getChunkIterator());
				while(iterator.hasNext()){
					Chunk chunk = iterator.next();
					int j = chunk.x * 16;
					int k = chunk.z * 16;
					chunk.enqueueRelightChecks();
					chunk.onTick(false);

					if (e.world.provider.canDoLightning(chunk) && e.world.rand.nextInt(350_000 - (int) (300_000 * chargeLevel)) == 0){
						//Determine the position of the lightning strike
						int tarX = j + e.world.rand.nextInt(16);
						int tarZ = k + e.world.rand.nextInt(16);
						int tarY = e.world.getPrecipitationHeight(new BlockPos(tarX, 0, tarZ)).getY();
						BlockPos tarPos;

						//Lightning bolts are attracted by nearby entities
						//Uses a slightly different formula from vanilla for reasons of efficiency
						ArrayList<LivingEntity> ents = new ArrayList<>();
						chunk.getEntitiesOfTypeWithinAABB(LivingEntity.class, new AxisAlignedBB(tarX - 3, tarY - 3, tarZ - 3, tarX + 3, e.world.getHeight() + 3, tarZ + 3), ents, new Predicate<LivingEntity>(){
							public boolean apply(@Nullable LivingEntity ent){
								return ent != null && ent.isEntityAlive() && e.world.canSeeSky(ent.getPosition());
							}
						});


						if(ents.isEmpty()){
							if(tarY == -1){
								tarY += 2;
							}
							tarPos = new BlockPos(tarX, tarY, tarZ);
						}else{
							tarPos = ents.get(e.world.rand.nextInt(ents.size())).getPosition();
						}


						if(e.world.getGameRules().getBoolean("doMobSpawning") && e.world.rand.nextDouble() < e.world.getDifficultyForLocation(tarPos).getAdditionalDifficulty() * 0.01D){
							SkeletonHorseEntity entityskeletonhorse = new SkeletonHorseEntity(e.world);
							entityskeletonhorse.setTrap(true);
							entityskeletonhorse.setGrowingAge(0);
							entityskeletonhorse.setPosition(tarX, tarY, tarZ);
							e.world.addEntity(entityskeletonhorse);
							e.world.addLightningBolt(new LightningBoltEntity(e.world, tarX, tarY, tarZ, true));
						}else{
							e.world.addLightningBolt(new LightningBoltEntity(e.world, tarX, tarY, tarZ, false));
						}
					}
				}
			}
			e.world.getProfiler().endSection();
		}
	}

	@SubscribeEvent
	public void buildRetrogenList(ChunkDataEvent.Load e) {
		if (!CRConfig.retrogen.getString().isEmpty()) {
			CompoundNBT tag = e.getData().getCompound(Crossroads.MODID);
			e.getData().put(Crossroads.MODID, tag);

			if (!tag.contains(CRConfig.retrogen.getString())) {
				tag.putBoolean(CRConfig.retrogen.getString(), true);
				TO_RETROGEN.add(e.getChunk());
			}
		}
	}

	@SubscribeEvent
	public void craftGoggles(AnvilUpdateEvent e){
		if(e.getLeft().getItem() == CRItems.moduleGoggles){
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(lens.matchesRecipe(e.getRight()) && (!e.getLeft().hasTag() || !e.getLeft().getTag().contains(lens.name()))){
					ItemStack out = e.getLeft().copy();
					if(!out.hasTag()){
						out.put(new CompoundNBT());
					}
					e.setCost((int) Math.pow(2, out.getTag().getSize()));
					out.getTag().putBoolean(lens.name(), true);
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

		if(e.getEntity() instanceof ServerPlayerEntity){
			StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) e.getEntity());
		}
	}

	@SubscribeEvent
	public void damageTaken(LivingHurtEvent e){
		if(e.getSource() == DamageSource.FALL){
			LivingEntity ent = e.getEntityLiving();

			ItemStack boots = ent.getItemStackFromSlot(EquipmentSlotType.FEET);
			if(boots.getItem() == CRItems.chickenBoots){
				e.setCanceled(true);
				ent.getEntityWorld().playSound(null, ent.posX, ent.posY, ent.posZ, SoundEvents.ENTITY_CHICKEN_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
				return;
			}

			if(ent instanceof PlayerEntity){
				PlayerEntity player = (PlayerEntity) ent;
				if(player.inventory.clearMatchingItems(s -> s.getItem() == CRItems.nitroglycerin, -1) > 0){
					player.world.createExplosion(null, player.posX, player.posY, player.posZ, 5F, Explosion.Mode.BREAK);
				}
			}
		}
	}


	private static final Field explosionPower = ReflectionUtil.reflectField(CrReflection.EXPLOSION_POWER);
	private static final Field explosionSmoking = ReflectionUtil.reflectField(CrReflection.EXPLOSION_SMOKE);

	@SubscribeEvent
	public void modifyExplosion(ExplosionEvent.Start e){
		boolean perpetuate = false;
		for(Entity ent : e.getWorld().loadedEntityList){
			if(ent instanceof EntityGhostMarker){
				EntityGhostMarker mark = (EntityGhostMarker) ent;
				if(mark.getType() == EntityGhostMarker.EnumMarkerType.EQUALIBRIUM && mark.data != null && mark.getPositionVector().subtract(e.getExplosion().getPosition()).length() <= mark.data.getInt("range")){
					e.setCanceled(true);
					return;
				}else if(mark.getType() == EntityGhostMarker.EnumMarkerType.VOID_EQUALIBRIUM && mark.data != null && mark.getPositionVector().subtract(e.getExplosion().getPosition()).length() <= mark.data.getInt("range")){
					perpetuate = true;
				}
			}
		}

		if(perpetuate && explosionPower != null && explosionSmoking != null){
			EntityGhostMarker marker = new EntityGhostMarker(e.getWorld(), EntityGhostMarker.EnumMarkerType.DELAYED_EXPLOSION, 5);
			marker.setPosition(e.getExplosion().getPosition().x, e.getExplosion().getPosition().y, e.getExplosion().getPosition().z);
			CompoundNBT data = new CompoundNBT();
			try{
				data.putFloat("power", explosionPower.getFloat(e.getExplosion()));
				data.putBoolean("smoking", explosionSmoking.getBoolean(e.getExplosion()));
			}catch(IllegalAccessException ex){
				Crossroads.logger.error("Failed to perpetuate explosion. Dim: " + e.getWorld().provider.getDimension() + "; Pos: " + e.getExplosion().getPosition());
			}
			marker.data = data;
			e.getWorld().addEntity(marker);
		}
	}
}