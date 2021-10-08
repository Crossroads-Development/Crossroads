package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.AtmosChargeSavedData;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.API.technomancy.RespawnInventorySavedData;
import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import com.Da_Technomancer.crossroads.items.technomancy.TechnomancyArmor;
import com.Da_Technomancer.crossroads.world.CRWorldGen;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class EventHandlerCommon{

	private static final Field entityList = ReflectionUtil.reflectField(CRReflection.ENTITY_LIST);

	@SubscribeEvent
	@SuppressWarnings({"unused", "unchecked"})
	public void onEntitySpawn(LivingSpawnEvent.CheckSpawn e){
		if(entityList != null && e.getWorld() instanceof ServerLevel){
			ServerLevel world = (ServerLevel) e.getWorld();
			world.getProfiler().push(Crossroads.MODNAME + ": Ghost marker spawn prevention");
			Map<UUID, Entity> entities;
			try{
				entities = (Map<UUID, Entity>) entityList.get(world);
			}catch(IllegalAccessException | ClassCastException ex){
				Crossroads.logger.error(ex);
				world.getProfiler().pop();
				return;
			}
			for(Entity ent : entities.values()){
				if(ent instanceof EntityGhostMarker){
					EntityGhostMarker mark = (EntityGhostMarker) ent;
					if(mark.getMarkerType() == EntityGhostMarker.EnumMarkerType.BLOCK_SPAWNING && mark.data != null && mark.position().subtract(e.getEntity().position()).length() <= mark.data.getInt("range")){
						e.setResult(Event.Result.DENY);
						world.getProfiler().pop();
						return;
					}
				}
			}
			world.getProfiler().pop();
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void chargeCreepers(LivingSpawnEvent.SpecialSpawn e){
		if(e.getWorld() instanceof ServerLevel && e.getEntity() instanceof Creeper && (CRConfig.atmosEffect.get() & 2) == 2 && (float) AtmosChargeSavedData.getCharge((ServerLevel) e.getWorld()) / (float) AtmosChargeSavedData.getCapacity() >= 0.9F){
			CompoundTag nbt = new CompoundTag();
			e.getEntityLiving().addAdditionalSaveData(nbt);
			nbt.putBoolean("powered", true);
			e.getEntityLiving().readAdditionalSaveData(nbt);
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
//					if(region.inRegion(ent.getPosition())){
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
		if(!e.world.isClientSide && (CRConfig.atmosEffect.get() & 1) == 1){
			e.world.getProfiler().push(Crossroads.MODNAME + ": Overcharge lightning effects");
			float chargeLevel = (float) AtmosChargeSavedData.getCharge((ServerLevel) e.world) / (float) AtmosChargeSavedData.getCapacity();
			if(chargeLevel > 0.5F && getLoadedChunks != null && spawnRadius != null){
				//1.14
				//Very similar to vanilla logic in ServerWorld::tickEnvironment as called by ServerChunkProvider::tickChunks
				//Re-implemented due to the vanilla methods doing far more than just lightning
				try{
					Iterable<ChunkHolder> iterable = (Iterable<ChunkHolder>) getLoadedChunks.invoke(((ServerChunkCache) e.world.getChunkSource()).chunkMap);
					for(ChunkHolder holder : iterable){
						Optional<LevelChunk> opt = holder.getEntityTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left();
						if(opt.isPresent()){
							ChunkPos chunkPos = opt.get().getPos();
							if(!(boolean) spawnRadius.invoke(((ServerChunkCache) e.world.getChunkSource()).chunkMap, chunkPos)){
								int i = chunkPos.getMinBlockX();
								int j = chunkPos.getMinBlockZ();
								if(e.world.random.nextInt(350_000 - (int) (300_000F * chargeLevel)) == 0){//The vanilla default is 1/100_000; atmos charging ranges from 1/200_000 to 1/50_000
									BlockPos strikePos = e.world.getBlockRandomPos(i, 0, j, 15);
									if(adjustPosForLightning != null){
										//This is a minor detail of the implementation- we only do it if the reflection worked
										strikePos = (BlockPos) adjustPosForLightning.invoke(e.world, strikePos);//Vanilla lightning logic is evil- if there's a nearby entity (including players), hit them instead of the random block
									}
									DifficultyInstance difficulty = e.world.getCurrentDifficultyAt(strikePos);
									//There's a config for this because at high atmos levels, it can quickly get annoying to have a world flooded with skeleton horses
									boolean spawnHorsemen = CRConfig.atmosLightningHorsemen.get() && e.world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && e.world.random.nextDouble() < difficulty.getEffectiveDifficulty() * 0.01D;
									if(spawnHorsemen){
										SkeletonHorse skeletonHorse = EntityType.SKELETON_HORSE.create(e.world);
										skeletonHorse.setTrap(true);//It's a trap!
										skeletonHorse.setAge(0);
										skeletonHorse.setPos(strikePos.getX(), strikePos.getY(), strikePos.getZ());
										e.world.addFreshEntity(skeletonHorse);
									}

									LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(e.world);
									lightning.moveTo(Vec3.atBottomCenterOf(strikePos));//Set strike position/set position
									e.world.addFreshEntity(lightning);
								}
							}
						}
					}
				}catch(Exception ex){//I was going to itemize the exceptions, but there's three different reflection calls and a bunch of chunk level logic, so it got ridiculous
					Crossroads.logger.catching(ex);
				}
			}
			e.world.getProfiler().pop();
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void technoArmorCrafting(AnvilUpdateEvent e){
		ItemStack inputLeft = e.getLeft();
		if(inputLeft.getItem() instanceof TechnomancyArmor){
			CompoundTag nbt = e.getLeft().getOrCreateTag();

			//Add netherite armor
			if(!TechnomancyArmor.isReinforced(inputLeft) && CRConfig.technoArmorReinforce.get()){
				ItemStack inputRight = e.getRight();
				if(inputLeft.getItem() == CRItems.armorGoggles && inputRight.getItem() == Items.NETHERITE_HELMET || inputLeft.getItem() == CRItems.propellerPack && inputRight.getItem() == Items.NETHERITE_CHESTPLATE || inputLeft.getItem() == CRItems.armorToolbelt && inputRight.getItem() == Items.NETHERITE_LEGGINGS || inputLeft.getItem() == CRItems.armorEnviroBoots && inputRight.getItem() == Items.NETHERITE_BOOTS){
					e.setOutput(TechnomancyArmor.setReinforced(inputLeft.copy(), true));
					e.setMaterialCost(1);
					e.setCost(CRConfig.technoArmorCost.get() * 10);
					return;
				}
			}

			//Add lenses to goggles
			if(inputLeft.getItem() == CRItems.armorGoggles){
				for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
					if(lens.matchesRecipe(e.getRight()) && !nbt.contains(lens.toString())){
						ItemStack out = inputLeft.copy();
						int cost = CRConfig.technoArmorCost.get();
						for(EnumGoggleLenses otherLens : EnumGoggleLenses.values()){
							if(nbt.contains(otherLens.toString())){
								cost *= 2;
							}
						}
						e.setCost(cost);
						out.getOrCreateTag().putBoolean(lens.toString(), false);
						e.setOutput(out);
						e.setMaterialCost(1);
						return;
					}
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

			ItemStack boots = ent.getItemBySlot(EquipmentSlot.FEET);
			if(boots.getItem() == CRItems.chickenBoots){
				e.setCanceled(true);
				ent.getCommandSenderWorld().playSound(null, ent.getX(), ent.getY(), ent.getZ(), SoundEvents.CHICKEN_HURT, SoundSource.PLAYERS, 2.5F, 1F);
				return;
			}

			if(ent instanceof Player){
				//Players who take damage with certain tag-defined items in their inventory explode
				Player player = (Player) ent;
				boolean foundExplosion = false;
				if(player.inventory.offhand.get(0).getItem().is(CRItemTags.EXPLODE_IF_KNOCKED)){
					player.inventory.offhand.set(0, ItemStack.EMPTY);
					foundExplosion = true;
				}

				for(int i = 0; i < player.inventory.items.size(); i++){
					if(player.inventory.items.get(i).getItem().is(CRItemTags.EXPLODE_IF_KNOCKED)){
						player.inventory.items.set(i, ItemStack.EMPTY);
						foundExplosion = true;
					}
				}
				if(foundExplosion){
					player.level.explode(null, player.getX(), player.getY(), player.getZ(), 5F, Explosion.BlockInteraction.BREAK);
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void enviroBootsProtect(LivingAttackEvent e){
		//Provides immunity from magma block damage and fall damage when wearing enviro_boots
		if(e.getSource() == DamageSource.HOT_FLOOR || e.getSource() == DamageSource.FALL && e.getEntityLiving().getItemBySlot(EquipmentSlot.FEET).getItem() == CRItems.armorEnviroBoots){
			e.setCanceled(true);
		}
	}


	private static final Field explosionPower = ReflectionUtil.reflectField(CRReflection.EXPLOSION_POWER);
	private static final Field explosionSmoking = ReflectionUtil.reflectField(CRReflection.EXPLOSION_SMOKE);
	private static final Field explosionMode = ReflectionUtil.reflectField(CRReflection.EXPLOSION_MODE);

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void modifyExplosion(ExplosionEvent.Start e){
		if(entityList == null || !(e.getWorld() instanceof ServerLevel)){
			return;
		}

		ServerLevel world = (ServerLevel) e.getWorld();
		world.getProfiler().push(Crossroads.MODNAME + ": Explosion modification");
		Map<UUID, Entity> entities;
		try{
			entities = (Map<UUID, Entity>) entityList.get(e.getWorld());
		}catch(IllegalAccessException ex){
			Crossroads.logger.error(ex);
			world.getProfiler().pop();
			return;
		}
		boolean perpetuate = false;
		for(Entity ent : entities.values()){
			if(ent instanceof EntityGhostMarker){
				EntityGhostMarker mark = (EntityGhostMarker) ent;
				if(mark.getMarkerType() == EntityGhostMarker.EnumMarkerType.EQUILIBRIUM && mark.data != null && mark.position().subtract(e.getExplosion().getPosition()).length() <= mark.data.getInt("range")){
					e.setCanceled(true);//Equilibrium beams cancel explosions
					world.getProfiler().pop();
					return;
				}else if(mark.getMarkerType() == EntityGhostMarker.EnumMarkerType.VOID_EQUILIBRIUM && mark.data != null && mark.position().subtract(e.getExplosion().getPosition()).length() <= mark.data.getInt("range")){
					perpetuate = true;//Void-equilibrium beams cause explosions to repeat, by spawning a marker that recreates the explosion 5 ticks later
				}
			}
		}

		if(perpetuate && explosionPower != null && explosionSmoking != null && explosionMode != null){
			EntityGhostMarker marker = new EntityGhostMarker(e.getWorld(), EntityGhostMarker.EnumMarkerType.DELAYED_EXPLOSION, 5);
			marker.setPos(e.getExplosion().getPosition().x, e.getExplosion().getPosition().y, e.getExplosion().getPosition().z);
			CompoundTag data = new CompoundTag();
			try{
				data.putFloat("power", explosionPower.getFloat(e.getExplosion()));
				data.putBoolean("flaming", explosionSmoking.getBoolean(e.getExplosion()));
				data.putString("blast_type", ((Explosion.BlockInteraction) explosionMode.get(e.getExplosion())).name());
			}catch(IllegalAccessException ex){
				Crossroads.logger.error("Failed to perpetuate explosion. Dim: " + MiscUtil.getDimensionName(e.getWorld()) + "; Pos: " + e.getExplosion().getPosition());
			}
			marker.data = data;
			world.addFreshEntity(marker);
		}
		world.getProfiler().pop();
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void rebuildConfigData(ModConfig.Reloading e){
		if(e.getConfig().getModId().equals(Crossroads.MODID) && e.getConfig().getType() == ModConfig.Type.SERVER){
			GearFactory.init();
			OreSetup.loadConfig();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	@SuppressWarnings("unused")
	public void addWorldgen(BiomeLoadingEvent e){
		CRWorldGen.addWorldgen(e);
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	@SuppressWarnings("unused")
	public void savePlayerHotbar(LivingDeathEvent e){
		try{
			LivingEntity ent = e.getEntityLiving();
			if(ent instanceof Player && !ent.getCommandSenderWorld().isClientSide && ent.getItemBySlot(EquipmentSlot.LEGS).getItem() == CRItems.armorToolbelt && !ent.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)){
				Player player = (Player) ent;
				ItemStack[] savedInv = new ItemStack[10];
				//Hotbar
				for(int i = 0; i < 9; i++){
					savedInv[i] = player.inventory.items.get(i);
					player.inventory.items.set(i, ItemStack.EMPTY);
				}
				//Offhand
				savedInv[9] = player.inventory.offhand.get(0);
				player.inventory.offhand.set(0, ItemStack.EMPTY);

				ServerLevel world = (ServerLevel) player.getCommandSenderWorld();
				HashMap<UUID, ItemStack[]> savedMap = RespawnInventorySavedData.getMap(world);
				UUID playerId = player.getGameProfile().getId();
				if(savedMap.containsKey(playerId)){
					//There are already saved items for this player
					//This shouldn't happen, but we drop any saved items in this case
					for(ItemStack stack : savedMap.get(playerId)){
						Containers.dropItemStack(world, player.getX(), player.getY(), player.getZ(), stack);
					}
				}
				savedMap.put(playerId, savedInv);
				RespawnInventorySavedData.markDirty(world);
			}
		}catch(Exception ex){
			Crossroads.logger.error("Error while saving player hotbar for toolbelt", ex);
		}
	}

	@SubscribeEvent()
	@SuppressWarnings("unused")
	public void loadPlayerHotbar(PlayerEvent.PlayerRespawnEvent e){
		try{
			Player player = e.getPlayer();
			Level world = player.getCommandSenderWorld();
			if(!e.isEndConquered() && !world.isClientSide){
				ServerLevel worldServ = (ServerLevel) world;
				HashMap<UUID, ItemStack[]> savedMap = RespawnInventorySavedData.getMap(worldServ);
				UUID playerId = player.getGameProfile().getId();
				if(savedMap.containsKey(playerId)){
					//Give the player the items stored in the map, and remove the map entry
					ItemStack[] savedItems = savedMap.get(playerId);
					savedMap.remove(playerId);
					RespawnInventorySavedData.markDirty(worldServ);

					//For each item, try to return it to the original slot, or add it generically otherwise
					//Hotbar
					for(int i = 0; i < 9; i++){
						player.inventory.add(i, savedItems[i]);
					}

					//Offhand
					if(!savedItems[9].isEmpty()){
						if(player.inventory.offhand.get(0).isEmpty()){
							player.inventory.offhand.set(0, savedItems[9]);
						}else{
							player.drop(savedItems[9], false);
						}
					}

					//Add the items that didn't fit in the original slot to the inventory
					//Hotbar
					for(int i = 0; i < 9; i++){
						if(!savedItems[i].isEmpty()){
							player.drop(savedItems[i], false);
						}
					}
				}
			}
		}catch(Exception ex){
			Crossroads.logger.error("Error while restoring player hotbar for toolbelt", ex);
		}
	}

	@SubscribeEvent()
	@SuppressWarnings("unused")
	public void trackDeaths(LivingDeathEvent e){
		//For genetically modified mobs with respawning, if they died without the respawn marker effect, create a ghost marker to respawn them
		LivingEntity entity = e.getEntityLiving();
		EntityTemplate template;
		if(!entity.level.isClientSide && (template = EntityTemplate.getTemplateFromEntity(entity)).isRespawning()){
			int delay = CRConfig.respawnDelay.get();
			delay *= 20;//Convert from seconds to ticks
			//Ensure it doesn't have the marker effect and this is enabled in config
			if(!entity.hasEffect(EntityTemplate.getRespawnMarkerEffect()) && delay > 0){
				EntityGhostMarker marker = new EntityGhostMarker(entity.level, EntityGhostMarker.EnumMarkerType.RESPAWNING, delay);
				marker.setPos(entity.getX(), entity.getY(), entity.getZ());
				marker.data = template.serializeNBT();
				entity.level.addFreshEntity(marker);
			}
		}
	}

	@SubscribeEvent()
	@SuppressWarnings("unused")
	public void appendDrops(LivingDropsEvent e){
		LivingEntity ent = e.getEntityLiving();
		if(!ent.level.isClientSide){

			//Drop souls
			if(ent.hasEffect(CRPotions.TRANSIENT_EFFECT)){
				//Drop count is based on entity type
				int soulCount;

				//Players and 'fake' living drop no souls (anti-exploit)
				if(ent instanceof Player || ent instanceof ArmorStand){
					soulCount = 0;
				}else if(ent.getMobType() == MobType.UNDEAD){
					soulCount = 1;//Undead give 1
				}else if(ent instanceof AbstractVillager || ent.getMobType() == MobType.ILLAGER){
					soulCount = 8;//'People' type creatures give a full soul cluster worth
				}else{
					soulCount = 2;//Most things give 2
				}
				if(soulCount > 0){
					e.getDrops().add(new ItemEntity(ent.level, ent.getX(), ent.getY(), ent.getZ(), new ItemStack(CRItems.soulShard, soulCount)));
				}
			}

			//Drop brain
			if(ent instanceof Merchant){
				DamageSource damageSource = e.getSource();
				//Known issue: This can have a false positive if other mods allow dealing direct damage by a method other than melee attacking with the weapon in the mainhand
				if(damageSource.getClass() == EntityDamageSource.class && damageSource.getDirectEntity() instanceof LivingEntity && ((LivingEntity) damageSource.getDirectEntity()).getMainHandItem().getItem() == CRItems.brainHarvester){
					ItemStack brain = new ItemStack(CRItems.villagerBrain, 1);
					CRItems.villagerBrain.setOffers(brain, ((Merchant) ent).getOffers());
					CRItems.villagerBrain.getSpoilTime(brain, ent.level);
					e.getDrops().add(new ItemEntity(ent.level, ent.getX(), ent.getY(), ent.getZ(), brain));
				}
			}
		}
	}
}