package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.AtmosChargeSavedData;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class EventHandlerCommon{

	private static final Field entityList = ReflectionUtil.reflectField(CRReflection.ENTITY_LIST);

	@SubscribeEvent
	@SuppressWarnings({"unused", "unchecked"})
	public void onEntitySpawn(LivingSpawnEvent.CheckSpawn e){
		if(entityList != null && e.getWorld() instanceof ServerWorld){
			ServerWorld world = (ServerWorld) e.getWorld();
			Map<UUID, Entity> entities;
			try{
				entities = (Map<UUID, Entity>) entityList.get(world);
			}catch(IllegalAccessException | ClassCastException ex){
				Crossroads.logger.error(ex);
				return;
			}
			for(Entity ent : entities.values()){
				if(ent instanceof EntityGhostMarker){
					EntityGhostMarker mark = (EntityGhostMarker) ent;
					if(mark.getMarkerType() == EntityGhostMarker.EnumMarkerType.BLOCK_SPAWNING && mark.data != null && mark.getPositionVec().subtract(e.getEntity().getPositionVec()).length() <= mark.data.getInt("range")){
						e.setResult(Event.Result.DENY);
						return;
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	@SuppressWarnings("unused")
	public void chargeCreepers(LivingSpawnEvent.SpecialSpawn e){
		if(e.getWorld() instanceof ServerWorld && e.getEntity() instanceof CreeperEntity && (CRConfig.atmosEffect.get() & 2) == 2 && (float) AtmosChargeSavedData.getCharge((ServerWorld) e.getWorld()) / (float) AtmosChargeSavedData.getCapacity() >= 0.9F){
			CompoundNBT nbt = new CompoundNBT();
			e.getEntityLiving().writeAdditional(nbt);
			nbt.putBoolean("powered", true);
			e.getEntityLiving().readAdditional(nbt);
		}
	}

	//	//The main and sub keys allow differentiating between entities with updateBlocked due to crossroads, and updateBlocked due to other mods. In effect, it is a preemptive compatibility bugfix
//	protected static final String MAIN_KEY = "cr_pause";
//	protected static final String SUB_KEY = "cr_pause_prior";
	private static final Method getLoadedChunks = ReflectionUtil.reflectMethod(CRReflection.LOADED_CHUNKS);
	private static final Method spawnRadius = ReflectionUtil.reflectMethod(CRReflection.SPAWN_RADIUS);
	private static final Method adjustPosForLightning = ReflectionUtil.reflectMethod(CRReflection.LIGHTNING_POS);

	@SubscribeEvent
	@SuppressWarnings({"unused", "unchecked"})
	public void worldTick(TickEvent.WorldTickEvent e){

//		//Time Dilation
//		//Forge for MC1.14 killed the entity hook that made time slowing/stopping work (Entity::updateBlock field was removed)
//		//Press F to pay your respects to the signature feature of Technomancy
//		if(!e.world.isRemote && e.phase == TickEvent.Phase.START){
//			e.world.getProfiler().startSection(Crossroads.MODNAME + ": Entity Time Dilation");
//			ArrayList<TemporalAcceleratorTileEntity.Region> timeStoppers = new ArrayList<>();
//			for(TileEntity te : e.world.tickableTileEntities){
//				if(te instanceof TemporalAcceleratorTileEntity && ((TemporalAcceleratorTileEntity) te).stoppingTime()){
//					timeStoppers.add(((TemporalAcceleratorTileEntity) te).getRegion());
//				}
//			}
//
//			for(Entity ent : e.world.loadedEntityList){
//				CompoundNBT entNBT = ent.getPersistentData();
//				if(entNBT.getBoolean(MAIN_KEY)){
//					if(!entNBT.getBoolean(SUB_KEY)){
//						ent.updateBlocked = false;
//					}
//					entNBT.putBoolean(MAIN_KEY, false);
//					entNBT.putBoolean(SUB_KEY, false);
//				}
//
//				for(TemporalAcceleratorTileEntity.Region region : timeStoppers){
//					if(region.inRegion(ent.func_233580_cy_())){
//						entNBT.putBoolean(MAIN_KEY, true);
//						if(ent.updateBlocked){
//							entNBT.putBoolean(SUB_KEY, true);
//						}else{
//							ent.updateBlocked = true;
//						}
//						if(ent instanceof ServerPlayerEntity){
//							CrossroadsPackets.network.sendTo(new SendPlayerTickCountToClient(0), (ServerPlayerEntity) ent);
//						}
//						break;
//					}
//				}
//			}
//			e.world.getProfiler().endSection();
//		}


		//Atmospheric overcharge effect
		if(!e.world.isRemote && (CRConfig.atmosEffect.get() & 1) == 1){
			e.world.getProfiler().startSection(Crossroads.MODNAME + ": Overcharge lightning effects");
			float chargeLevel = (float) AtmosChargeSavedData.getCharge((ServerWorld) e.world) / (float) AtmosChargeSavedData.getCapacity();
			if(chargeLevel > 0.5F && getLoadedChunks != null && spawnRadius != null){
				//1.14
				//Very similar to vanilla logic in ServerWorld::tickEnvironment as called by ServerChunkProvider::tickChunks
				//Re-implemented due to the vanilla methods doing far more than just lightning
				try{
					Iterable<ChunkHolder> iterable = (Iterable<ChunkHolder>) getLoadedChunks.invoke(((ServerChunkProvider) e.world.getChunkProvider()).chunkManager);
					for(ChunkHolder holder : iterable){
						Optional<Chunk> opt = holder.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_CHUNK).left();
						if(opt.isPresent()){
							ChunkPos chunkPos = opt.get().getPos();
							if(!(boolean) spawnRadius.invoke(((ServerChunkProvider) e.world.getChunkProvider()).chunkManager, chunkPos)){
								int i = chunkPos.getXStart();
								int j = chunkPos.getZStart();
								if(e.world.rand.nextInt(350_000 - (int) (300_000F * chargeLevel)) == 0){//The vanilla default is 1/100_000; atmos charging ranges from 1/200_000 to 1/50_000
									BlockPos strikePos = e.world.getBlockRandomPos(i, 0, j, 15);
									if(adjustPosForLightning != null){
										//This is a minor detail of the implementation- we only do it if the reflection worked
										strikePos = (BlockPos) adjustPosForLightning.invoke(e.world, strikePos);//Vanilla lightning logic is evil- if there's a nearby entity (including players), hit them instead of the random block
									}
									DifficultyInstance difficulty = e.world.getDifficultyForLocation(strikePos);
									//There's a config for this because at high atmos levels, it can quickly get annoying to have a world flooded with skeleton horses
									boolean spawnHorsemen = CRConfig.atmosLightningHorsemen.get() && e.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && e.world.rand.nextDouble() < difficulty.getAdditionalDifficulty() * 0.01D;
									if(spawnHorsemen){
										SkeletonHorseEntity skeletonHorse = EntityType.SKELETON_HORSE.create(e.world);
										skeletonHorse.setTrap(true);//It's a trap!
										skeletonHorse.setGrowingAge(0);
										skeletonHorse.setPosition(strikePos.getX(), strikePos.getY(), strikePos.getZ());
										e.world.addEntity(skeletonHorse);
									}

									LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(e.world);
									lightning.func_233576_c_(Vector3d.func_237492_c_(strikePos));//Set strike position/set position
									e.world.addEntity(lightning);
								}
							}
						}
					}
				}catch(Exception ex){//I was going to itemize the exceptions, but there's three different reflection calls and a bunch of chunk level logic, so it got ridiculous
					Crossroads.logger.catching(ex);
				}
			}
			e.world.getProfiler().endSection();
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void craftGoggles(AnvilUpdateEvent e){
		if(e.getLeft().getItem() == CRItems.moduleGoggles){
			if(!e.getLeft().hasTag()){
				e.getLeft().setTag(new CompoundNBT());
			}
			CompoundNBT nbt = e.getLeft().getTag();
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(lens.matchesRecipe(e.getRight()) && !nbt.contains(lens.toString())){
					ItemStack out = e.getLeft().copy();
					int cost = 1;
					for(EnumGoggleLenses otherLens : EnumGoggleLenses.values()){
						if(nbt.contains(otherLens.toString())){
							cost *= 2;
						}
					}
					e.setCost(cost);
					out.getTag().putBoolean(lens.toString(), false);
					e.setOutput(out);
					e.setMaterialCost(1);
					break;
				}
			}
		}
	}

//	@SubscribeEvent
//	@SuppressWarnings("unused")
//	public void syncPlayerTagToClient(EntityJoinWorldEvent e){
//		//The down-side of using this event is that every time the player switches dimension, the update data has to be resent.
//
//		if(e.getEntity() instanceof ServerPlayerEntity){
//			StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) e.getEntity());
//		}
//	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void damageTaken(LivingHurtEvent e){
		if(e.getSource() == DamageSource.FALL){
			LivingEntity ent = e.getEntityLiving();

			ItemStack boots = ent.getItemStackFromSlot(EquipmentSlotType.FEET);
			if(boots.getItem() == CRItems.chickenBoots){
				e.setCanceled(true);
				ent.getEntityWorld().playSound(null, ent.getPosX(), ent.getPosY(), ent.getPosZ(), SoundEvents.ENTITY_CHICKEN_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
				return;
			}

			if(ent instanceof PlayerEntity){
				//Players who take damage with certain tag-defined items in their inventory explode
				PlayerEntity player = (PlayerEntity) ent;
				boolean foundExplosion = false;
				if(player.inventory.offHandInventory.get(0).getItem().isIn(CRItemTags.EXPLODE_IF_KNOCKED)){
					player.inventory.offHandInventory.set(0, ItemStack.EMPTY);
					foundExplosion = true;
				}

				for(int i = 0; i < player.inventory.mainInventory.size(); i++){
					if(player.inventory.mainInventory.get(i).getItem().isIn(CRItemTags.EXPLODE_IF_KNOCKED)){
						player.inventory.mainInventory.set(i, ItemStack.EMPTY);
						foundExplosion = true;
					}
				}
				if(foundExplosion){
					player.world.createExplosion(null, player.getPosX(), player.getPosY(), player.getPosZ(), 5F, Explosion.Mode.BREAK);
				}
			}
		}
	}


	private static final Field explosionPower = ReflectionUtil.reflectField(CRReflection.EXPLOSION_POWER);
	private static final Field explosionSmoking = ReflectionUtil.reflectField(CRReflection.EXPLOSION_SMOKE);
	private static final Field explosionMode = ReflectionUtil.reflectField(CRReflection.EXPLOSION_MODE);

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void modifyExplosion(ExplosionEvent.Start e){
		if(entityList == null || !(e.getWorld() instanceof ServerWorld)){
			return;
		}

		Map<UUID, Entity> entities;
		try{
			entities = (Map<UUID, Entity>) entityList.get(e.getWorld());
		}catch(IllegalAccessException ex){
			Crossroads.logger.error(ex);
			return;
		}
		boolean perpetuate = false;
		for(Entity ent : entities.values()){
			if(ent instanceof EntityGhostMarker){
				EntityGhostMarker mark = (EntityGhostMarker) ent;
				if(mark.getMarkerType() == EntityGhostMarker.EnumMarkerType.EQUILIBRIUM && mark.data != null && mark.getPositionVec().subtract(e.getExplosion().getPosition()).length() <= mark.data.getInt("range")){
					e.setCanceled(true);//Equilibrium beams cancel explosions
					return;
				}else if(mark.getMarkerType() == EntityGhostMarker.EnumMarkerType.VOID_EQUILIBRIUM && mark.data != null && mark.getPositionVec().subtract(e.getExplosion().getPosition()).length() <= mark.data.getInt("range")){
					perpetuate = true;//Void-equilibrium beams cause explosions to repeat, by spawning a marker that recreates the explosion 5 ticks later
				}
			}
		}

		if(perpetuate && explosionPower != null && explosionSmoking != null && explosionMode != null){
			EntityGhostMarker marker = new EntityGhostMarker(e.getWorld(), EntityGhostMarker.EnumMarkerType.DELAYED_EXPLOSION, 5);
			marker.setPosition(e.getExplosion().getPosition().x, e.getExplosion().getPosition().y, e.getExplosion().getPosition().z);
			CompoundNBT data = new CompoundNBT();
			try{
				data.putFloat("power", explosionPower.getFloat(e.getExplosion()));
				data.putBoolean("flaming", explosionSmoking.getBoolean(e.getExplosion()));
				data.putString("blast_type", ((Explosion.Mode) explosionMode.get(e.getExplosion())).name());
			}catch(IllegalAccessException ex){
				Crossroads.logger.error("Failed to perpetuate explosion. Dim: " + MiscUtil.getDimensionName(e.getWorld()) + "; Pos: " + e.getExplosion().getPosition());
			}
			marker.data = data;
			e.getWorld().addEntity(marker);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void rebuildConfigData(ModConfig.Reloading e){
		if(e.getConfig().getModId().equals(Crossroads.MODID) && e.getConfig().getType() == ModConfig.Type.SERVER){
			GearFactory.init();
			OreSetup.loadConfig();
		}
	}
}