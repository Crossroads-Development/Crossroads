package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.advancements.*;
import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.alchemy.AtmosChargeSavedData;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.api.technomancy.RespawnInventorySavedData;
import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifier;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifierType;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.blocks.alchemy.GlasswareHolderTileEntity;
import com.Da_Technomancer.crossroads.blocks.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.rotary.WindingTableTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.loot_modifiers.CRLootModifiers;
import com.Da_Technomancer.crossroads.effects.entity_modifiers.RespawningEntityModifier;
import com.Da_Technomancer.crossroads.entity.CREntities;
import com.Da_Technomancer.crossroads.entity.CRMobDamage;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import com.Da_Technomancer.crossroads.entity.EntityHopperHawk;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.entity.mob_effects.Sedation;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.ArmorGoggles;
import com.Da_Technomancer.crossroads.items.technomancy.TechnomancyArmor;
import com.Da_Technomancer.crossroads.world.CRWorldGen;
import com.Da_Technomancer.essentials.Essentials;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.items.ESItems;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityMobGriefingEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.*;
import java.util.function.Supplier;

public class EventHandlerCommon{

	@EventBusSubscriber()
	public static class CRModEventsCommon{

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerCapabilities(RegisterCapabilitiesEvent e){
			CRCapabilities.registerCapabilities(e);
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void register(RegisterEvent e){
			e.register(Registries.BLOCK, helper -> {
				CRBlocks.registerBlocks();
//				CRMaterialLibrary.loadConfig();
				CRFluids.init();
				CRBlocks.registerBlocks(helper);
			});

			e.register(Registries.ITEM, helper -> {
				CRItems.registerItems();
				CRFluids.init();
				CRItems.registerItems(helper);
			});

			e.register(NeoForgeRegistries.Keys.FLUID_TYPES, helper -> {
				CRFluids.init();
				registerAll(helper, CRFluids.toRegisterType);
			});

			e.register(Registries.FLUID, helper -> {
				CRFluids.init();
				registerAll(helper, CRFluids.toRegisterFluid);
			});

			e.register(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, helper -> {
				CRLootModifiers.init();
				registerAll(helper, CRLootModifiers.toRegister);
			});

			e.register(Registries.ENTITY_TYPE, helper -> {
				CREntities.init();
				registerAll(helper, CREntities.toRegister);
			});

			e.register(Registries.BLOCK_ENTITY_TYPE, helper -> {
				CRTileEntity.init();
				registerAll(helper, CRTileEntity.toRegister);
			});

			e.register(Registries.FEATURE, helper -> {
				CRWorldGen.init();
				registerAll(helper, CRWorldGen.toRegisterFeature);
			});

			e.register(Registries.SOUND_EVENT, helper -> {
				registerAll(helper, CRSounds.soundsToRegister);
			});

			e.register(Registries.PARTICLE_TYPE, helper -> {
				CRParticles.init();
				registerAll(helper, CRParticles.toRegister);
			});

			e.register(Registries.RECIPE_SERIALIZER, helper -> {
				CRRecipes.init();
				registerAll(helper, CRRecipes.toRegisterSerializer);
			});

			e.register(Registries.RECIPE_TYPE, helper -> {
				CRRecipes.init();
				registerAll(helper, CRRecipes.toRegisterType);
			});
			e.register(Registries.PLACEMENT_MODIFIER_TYPE, helper -> {
				CRWorldGen.init();
				registerAll(helper, CRWorldGen.toRegisterModifier);
			});

			e.register(Registries.CREATIVE_MODE_TAB, helper -> {
				CRItems.MAIN_CREATIVE_TAB = CreativeModeTab.builder()
						.title(Component.translatable("item_group." + CRItems.MAIN_CREATIVE_TAB_ID))
						.icon(() -> new ItemStack(CRItems.omnimeter))
						.displayItems((params, output) -> {
									for(Supplier<ItemStack[]> itemsToAdd : CRItems.creativeTabItems.get(CRItems.MAIN_CREATIVE_TAB_ID)){
										for(ItemStack itemToAdd : itemsToAdd.get()){
											output.accept(itemToAdd);
										}
									}
								}
						).build();
				helper.register(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, CRItems.MAIN_CREATIVE_TAB_ID), CRItems.MAIN_CREATIVE_TAB);

				CRItems.HEAT_CABLE_CREATIVE_TAB = CreativeModeTab.builder()
						.title(Component.translatable("item_group." + CRItems.HEAT_CABLE_CREATIVE_TAB_ID))
						.icon(() -> new ItemStack(CRBlocks.HEAT_CABLES.get(HeatInsulators.WOOL)))
						.displayItems((params, output) -> {
									for(Supplier<ItemStack[]> itemsToAdd : CRItems.creativeTabItems.get(CRItems.HEAT_CABLE_CREATIVE_TAB_ID)){
										for(ItemStack itemToAdd : itemsToAdd.get()){
											output.accept(itemToAdd);
										}
									}
								}
						).build();
				helper.register(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, CRItems.HEAT_CABLE_CREATIVE_TAB_ID), CRItems.HEAT_CABLE_CREATIVE_TAB);

				CRItems.GEAR_CREATIVE_TAB = CreativeModeTab.builder()
						.title(Component.translatable("item_group." + CRItems.GEAR_CREATIVE_TAB_ID))
						.icon(() -> CRItems.smallGear.withMaterial(CRMaterialLibrary.findMaterial("copper"), 1))
						.displayItems((params, output) -> {
									for(Supplier<ItemStack[]> itemsToAdd : CRItems.creativeTabItems.get(CRItems.GEAR_CREATIVE_TAB_ID)){
										for(ItemStack itemToAdd : itemsToAdd.get()){
											output.accept(itemToAdd);
										}
									}
								}
						).build();
				helper.register(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, CRItems.GEAR_CREATIVE_TAB_ID), CRItems.GEAR_CREATIVE_TAB);
			});

			e.register(Registries.TRIGGER_TYPE, helper -> {
				registerThing(helper, "beam_alignment", BeamAlignmentTrigger.INSTANCE);
				registerThing(helper, "golem_built", GolemBuiltTrigger.INSTANCE);
				registerThing(helper, "clone_spawned", CloneSpawnedTrigger.INSTANCE);
				registerThing(helper, "gateway_travel", GatewayTravelTrigger.INSTANCE);
				registerThing(helper, "dirt_cable", DirtCableTrigger.INSTANCE);
				registerThing(helper, "atmos_charge", AtmosChargeTrigger.INSTANCE);
			});

			e.register(Registries.ITEM_SUB_PREDICATE_TYPE, helper -> {
				registerThing(helper, "goggle_lens", new ItemSubPredicate.Type<>(ItemGoggleLensPredicate.CODEC));
			});
		}

		public static <T> void registerThing(RegisterEvent.RegisterHelper<T> helper, String regKey, T toRegister){
			assert regKey != null && toRegister != null;
			helper.register(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, regKey), toRegister);
		}

		public static <T> void registerAll(RegisterEvent.RegisterHelper<T> helper, Map<String, T> toRegister){
			for(Map.Entry<String, T> regEntry : toRegister.entrySet()){
				String regKey = regEntry.getKey();
				T regValue = regEntry.getValue();
				assert regKey != null && regValue != null;
				helper.register(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, regKey), regValue);
			}
			toRegister.clear();
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void registerEntityAttributes(EntityAttributeCreationEvent e){
			e.put(EntityHopperHawk.type, EntityHopperHawk.createAttributes());
		}

		@SuppressWarnings("unused")
		@SubscribeEvent
		public static void register(RegisterPayloadHandlersEvent e){
			CRPackets.registerPayloads(e);
		}

		@SubscribeEvent
		@SuppressWarnings("unused")
		public static void rebuildConfigData(ModConfigEvent.Loading e){
			if(e.getConfig().getModId().equals(Crossroads.MODID) && e.getConfig().getType() == ModConfig.Type.SERVER){
				CRMaterialLibrary.loadConfig();
			}
		}

		@SubscribeEvent
		@SuppressWarnings("unused")
		public static void rebuildConfigData(ModConfigEvent.Reloading e){
			if(e.getConfig().getModId().equals(Crossroads.MODID) && e.getConfig().getType() == ModConfig.Type.SERVER){
				CRMaterialLibrary.loadConfig();
			}
		}
	}

//	private static final Field entityList = ReflectionUtil.reflectField(CRReflection.ENTITY_LIST);

	@SubscribeEvent
	@SuppressWarnings({"unused"})
	public void onEntitySpawn(FinalizeSpawnEvent e){
		if(e.getLevel() instanceof ServerLevel world){

			//Block spawning with closure beams
			world.getProfiler().push(Crossroads.MODNAME + ": Ghost marker spawn prevention");
//			Map<UUID, Entity> entities;
//			try{
//				entities = (Map<UUID, Entity>) entityList.get(world);
//			}catch(IllegalAccessException | ClassCastException ex){
//				Crossroads.logger.error(ex);
//				world.getProfiler().pop();
//				return;
//			}
			for(Entity ent : world.getAllEntities()){
				if(ent instanceof EntityGhostMarker mark){
					if(mark.getMarkerType() == EntityGhostMarker.EnumMarkerType.BLOCK_SPAWNING && mark.data != null && mark.position().subtract(e.getEntity().position()).length() <= mark.data.getInt("range")){
						e.setSpawnCancelled(true);
						world.getProfiler().pop();
						return;
					}
				}
			}
			world.getProfiler().pop();

			//Charge creepers when atmosphere is charged
			if(e.getEntity() instanceof Creeper && (CRConfig.atmosEffect.get() & 2) == 2 && (float) AtmosChargeSavedData.getCharge((ServerLevel) e.getLevel()) / (float) AtmosChargeSavedData.getCapacity() >= 0.9F){
				CompoundTag nbt = new CompoundTag();
				e.getEntity().addAdditionalSaveData(nbt);
				nbt.putBoolean("powered", true);
				e.getEntity().readAdditionalSaveData(nbt);
			}
		}
	}

	//	//The main and sub keys allow differentiating between entities with updateBlocked due to crossroads, and updateBlocked due to other mods. In effect, it is a preemptive compatibility bugfix
//	protected static final String MAIN_KEY = "cr_pause";
//	protected static final String SUB_KEY = "cr_pause_prior";
//	private static final Method getLoadedChunks = ReflectionUtil.reflectMethod(CRReflection.LOADED_CHUNKS);
//	private static final Method adjustPosForLightning = ReflectionUtil.reflectMethod(CRReflection.LIGHTNING_POS);

	@SubscribeEvent
	@SuppressWarnings({"unused", "unchecked"})
	public void worldTick(LevelTickEvent.Pre e){

		Level level = e.getLevel();

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
		if(!level.isClientSide && (CRConfig.atmosEffect.get() & 1) == 1){
			level.getProfiler().push(Crossroads.MODNAME + ": Overcharge lightning effects");
			float chargeLevel = (float) AtmosChargeSavedData.getCharge((ServerLevel) level) / (float) AtmosChargeSavedData.getCapacity();
			if(chargeLevel > 0.5F){
				//1.14
				//Very similar to vanilla logic in ServerWorld::tickEnvironment as called by ServerChunkProvider::tickChunks
				//Re-implemented due to the vanilla methods doing far more than just lightning
				try{
					Iterable<ChunkHolder> iterable = ((ServerChunkCache) level.getChunkSource()).chunkMap.getChunks();
					for(ChunkHolder holder : iterable){
						ChunkResult<LevelChunk> opt = holder.getEntityTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK);
						if(opt.isSuccess()){
							ChunkPos chunkPos = opt.orElseThrow(NullPointerException::new).getPos();
							if(!((ServerChunkCache) level.getChunkSource()).chunkMap.getPlayersCloseForSpawning(chunkPos).isEmpty()){
								int i = chunkPos.getMinBlockX();
								int j = chunkPos.getMinBlockZ();
								if(level.random.nextInt(350_000 - (int) (300_000F * chargeLevel)) == 0){//The vanilla default is 1/100_000; atmos charging ranges from 1/200_000 to 1/50_000
									BlockPos strikePos = level.getBlockRandomPos(i, 0, j, 15);
									strikePos = ((ServerLevel) level).findLightningTargetAround(strikePos);//Vanilla lightning logic is evil- if there's a nearby entity (including players), hit them instead of the random block
									DifficultyInstance difficulty = level.getCurrentDifficultyAt(strikePos);
									//There's a config for this because at high atmos levels, it can quickly get annoying to have a world flooded with skeleton horses
									boolean spawnHorsemen = CRConfig.atmosLightningHorsemen.get() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && level.random.nextDouble() < difficulty.getEffectiveDifficulty() * 0.01D;
									if(spawnHorsemen){
										SkeletonHorse skeletonHorse = EntityType.SKELETON_HORSE.create(level);
										skeletonHorse.setTrap(true);//It's a trap!
										skeletonHorse.setAge(0);
										skeletonHorse.setPos(strikePos.getX(), strikePos.getY(), strikePos.getZ());
										level.addFreshEntity(skeletonHorse);
									}

									LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
									lightning.moveTo(Vec3.atBottomCenterOf(strikePos));//Set strike position/set position
									level.addFreshEntity(lightning);
								}
							}
						}
					}
				}catch(Exception ex){//I was going to itemize the exceptions, but there's three different reflection calls and a bunch of chunk level logic, so it got ridiculous
					Crossroads.logger.catching(ex);
				}
			}
			level.getProfiler().pop();
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void anvilCrafting(AnvilUpdateEvent e){
		ItemStack inputLeft = e.getLeft();
		ItemStack inputRight = e.getRight();

		//Technomancy armor
		if(inputLeft.getItem() instanceof TechnomancyArmor){
			//Add netherite armor
			if(!TechnomancyArmor.isReinforced(inputLeft) && CRConfig.technoArmorReinforce.get()){
				Item outputItem = null;
				Item inputItem1 = inputLeft.getItem();
				Item inputItem2 = inputRight.getItem();
				if(inputItem1 == CRItems.armorGoggles && inputItem2 == Items.NETHERITE_HELMET){
					outputItem = CRItems.armorGogglesReinforced;
				}else if(inputItem1 == CRItems.propellerPack && inputItem2 == Items.NETHERITE_CHESTPLATE){
					outputItem = CRItems.propellerPackReinforced;
				}else if(inputItem1 == CRItems.armorToolbelt && inputItem2 == Items.NETHERITE_LEGGINGS){
					outputItem = CRItems.armorToolbeltReinforced;
				}else if(inputItem1 == CRItems.armorEnviroBoots && inputItem2 == Items.NETHERITE_BOOTS){
					outputItem = CRItems.armorEnviroBootsReinforced;
				}
				if(outputItem != null){
					e.setOutput(inputLeft.transmuteCopy(outputItem));
					e.setMaterialCost(1);
					e.setCost(CRConfig.technoArmorCost.get() * 10);
					return;
				}
			}

			//Add lenses to goggles
			if(inputLeft.getItem() == CRItems.armorGoggles){
				ArmorGoggles.LensesSet lenses = inputLeft.getOrDefault(CRItems.GOGGLE_LENSES_DATA, new ArmorGoggles.LensesSet(Object2BooleanMaps.emptyMap()));
				for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
					if(lens.matchesRecipe(inputRight) && !lenses.lenses().containsKey(lens)){
						ItemStack out = inputLeft.copy();
						e.setCost(CRConfig.technoArmorCost.get() * (long) Math.pow(2, lenses.lenses().size()));
						Object2BooleanMap<EnumGoggleLenses> newLenses = new Object2BooleanOpenHashMap<>(lenses.lenses());
						newLenses.put(lens, false);
						out.set(CRItems.GOGGLE_LENSES_DATA, new ArmorGoggles.LensesSet(newLenses));
						e.setOutput(out);
						e.setMaterialCost(1);
						return;
					}
				}
			}
		}

		//Repair broken mainspring
		if(inputLeft.getItem() instanceof WindingTableTileEntity.IWindableItem windingItem && inputRight.getItem() == CRItems.mainspring && windingItem.isBroken(inputLeft)){
			ItemStack out = inputLeft.copy();
			windingItem.setBrokenState(out, false);
			windingItem.setWindLevel(out, 0);
			e.setCost(2);
			e.setOutput(out);
			e.setMaterialCost(1);
			return;
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
	public void damageTaken(LivingDamageEvent.Post e){
		if(e.getSource().is(DamageTypeTags.IS_FALL) && e.getNewDamage() > 0){
			if(e.getEntity() instanceof Player player){
				//Players who take damage with certain tag-defined items in their inventory explode
				boolean foundExplosion = false;
				if(CraftingUtil.tagContains(CRItemTags.EXPLODE_IF_KNOCKED, player.getInventory().offhand.get(0).getItem())){
					player.getInventory().offhand.set(0, ItemStack.EMPTY);
					foundExplosion = true;
				}

				for(int i = 0; i < player.getInventory().items.size(); i++){
					if(CraftingUtil.tagContains(CRItemTags.EXPLODE_IF_KNOCKED, player.getInventory().items.get(i).getItem())){
						player.getInventory().items.set(i, ItemStack.EMPTY);
						foundExplosion = true;
					}
				}
				if(foundExplosion){
					player.level().explode(null, player.getX(), player.getY(), player.getZ(), 5F, Level.ExplosionInteraction.TNT);
				}
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void enviroBootsProtect(LivingDamageEvent.Pre e){
		//Provides immunity from magma block damage and fall damage when wearing enviro_boots
		if((e.getSource().is(DamageTypes.HOT_FLOOR) || e.getSource().is(DamageTypeTags.IS_FALL)) && e.getEntity().getItemBySlot(EquipmentSlot.FEET).getItem() == CRItems.armorEnviroBoots){
			e.setNewDamage(0);
			return;
		}

		if(e.getSource().is(DamageTypeTags.IS_FALL)){
			LivingEntity ent = e.getEntity();

			ItemStack boots = ent.getItemBySlot(EquipmentSlot.FEET);
			if(boots.getItem() == CRItems.chickenBoots){
				e.setNewDamage(0);
				ent.getCommandSenderWorld().playSound(null, ent.getX(), ent.getY(), ent.getZ(), SoundEvents.CHICKEN_HURT, SoundSource.PLAYERS, 2.5F, 1F);
				return;
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void modifyExplosion(ExplosionEvent.Start e){
		if(!(e.getLevel() instanceof ServerLevel world)){
			return;
		}

		if(e.getExplosion().getDirectSourceEntity() instanceof Creeper creeper){
			//Creeper explosions don't trigger a death event; we catch them this way
			RespawningEntityModifier.handleEntityDeath(creeper);
		}

		world.getProfiler().push(Crossroads.MODNAME + ": Explosion modification");
//		Map<UUID, Entity> entities;
//		try{
//			entities = (Map<UUID, Entity>) entityList.get(e.getWorld());
//		}catch(IllegalAccessException ex){
//			Crossroads.logger.error(ex);
//			world.getProfiler().pop();
//			return;
//		}
		for(Entity ent : world.getAllEntities()){
			if(ent instanceof EntityGhostMarker mark){
				if(mark.getMarkerType() == EntityGhostMarker.EnumMarkerType.EQUILIBRIUM && mark.data != null && mark.position().subtract(e.getExplosion().center()).length() <= mark.data.getInt("range")){
					e.setCanceled(true);//Equilibrium beams cancel explosions
					world.getProfiler().pop();
					return;
				}
			}
		}
		world.getProfiler().pop();
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void stopWitherGrief(EntityMobGriefingEvent e){
		//Equilibrium beams cancel explosions, including block destruction attack by withers
		if(e.getEntity() instanceof WitherBoss wither && wither.level() instanceof ServerLevel world){
			world.getProfiler().push(Crossroads.MODNAME + ": Explosion modification");
			for(Entity ent : world.getAllEntities()){
				if(ent instanceof EntityGhostMarker mark){
					if(mark.getMarkerType() == EntityGhostMarker.EnumMarkerType.EQUILIBRIUM && mark.data != null && mark.position().subtract(wither.position()).length() <= mark.data.getInt("range")){
						e.setCanGrief(false);
						world.getProfiler().pop();
						return;
					}
				}
			}
			world.getProfiler().pop();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	@SuppressWarnings("unused")
	public void savePlayerHotbar(LivingDeathEvent e){
		try{
			LivingEntity ent = e.getEntity();
			if(ent instanceof Player player && !ent.getCommandSenderWorld().isClientSide && ent.getItemBySlot(EquipmentSlot.LEGS).getItem() == CRItems.armorToolbelt && !ent.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)){
				ItemStack[] savedInv = new ItemStack[10];
				//Hotbar
				for(int i = 0; i < 9; i++){
					savedInv[i] = player.getInventory().items.get(i);
					player.getInventory().items.set(i, ItemStack.EMPTY);
				}
				//Offhand
				savedInv[9] = player.getInventory().offhand.get(0);
				player.getInventory().offhand.set(0, ItemStack.EMPTY);

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
			Player player = e.getEntity();
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
						player.getInventory().add(i, savedItems[i]);
					}

					//Offhand
					if(!savedItems[9].isEmpty()){
						if(player.getInventory().offhand.get(0).isEmpty()){
							player.getInventory().offhand.set(0, savedItems[9]);
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
		if(!e.getSource().is(CRMobDamage.NON_VIABLE)){//if non-viable (max health less than or equal to 0), don't let it respawn
			RespawningEntityModifier.handleEntityDeath(e.getEntity());
		}
	}

	public static final TagKey<EntityType<?>> GHOST_MOB = CraftingUtil.getTagKey(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "ghost"));
	public static final TagKey<EntityType<?>> NO_SOUL_DROP_MOB = CraftingUtil.getTagKey(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "soul_drop_blacklist"));
	public static final TagKey<EntityType<?>> HUMANOID_MOB = CraftingUtil.getTagKey(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("minecraft", "undead"));

	@SubscribeEvent()
	@SuppressWarnings("unused")
	public void appendDrops(LivingDropsEvent e){
		LivingEntity ent = e.getEntity();
		if(!ent.level().isClientSide){

			//Drop souls
			if(ent.hasEffect(CRPotions.TRANSIENT_EFFECT)){
				//Drop count is based on entity type
				int soulCount;

				//Players and 'fake' living drop no souls (anti-exploit)
				if(CraftingUtil.tagContains(NO_SOUL_DROP_MOB, ent.getType())){
					soulCount = 0;
				}else if(CraftingUtil.tagContains(GHOST_MOB, ent.getType())){
					soulCount = 4;//'Ghost' type creatures give a full soul cluster worth
				}else if(CraftingUtil.tagContains(EntityTypeTags.UNDEAD, ent.getType())){
					soulCount = 1;//Undead give 1
				}else if(CraftingUtil.tagContains(HUMANOID_MOB, ent.getType())){
					soulCount = 4;//'People' type creatures give a full soul cluster worth
				}else{
					soulCount = 2;//Most things give 2
				}
				if(soulCount > 0){
					e.getDrops().add(new ItemEntity(ent.level(), ent.getX(), ent.getY(), ent.getZ(), new ItemStack(CRItems.soulShard, soulCount)));
				}
			}

			//Drop brain
			if(ent instanceof Merchant){
				DamageSource damageSource = e.getSource();
				//Only requires holding the weapon, rather than dealing the finishing blow with the weapon
				if(damageSource.getDirectEntity() instanceof LivingEntity && ((LivingEntity) damageSource.getDirectEntity()).getMainHandItem().getItem() == CRItems.brainHarvester){
					ItemStack brain = new ItemStack(CRItems.villagerBrain, 1);
					CRItems.villagerBrain.setOffers(brain, ((Merchant) ent).getOffers());
					IPerishable.getAndInitSpoilTime(brain, ent.level());
					e.getDrops().add(new ItemEntity(ent.level(), ent.getX(), ent.getY(), ent.getZ(), brain));
				}
			}
		}
	}

	@SubscribeEvent()
	@SuppressWarnings("unused")
	public void appendDrops(BabyEntitySpawnEvent e){
		//Cloned animals which have children pass down their traits to their kids

		AgeableMob child = e.getChild();
		if(child != null && !e.isCanceled() && (EntityTemplate.isEntityModified(e.getParentA()) || EntityTemplate.isEntityModified(e.getParentB()))){
			//Traits are going to be a random blend of the two parents
			EntityTemplate templateA = EntityTemplate.getTemplateFromEntity(e.getParentA());
			EntityTemplate templateB = EntityTemplate.getTemplateFromEntity(e.getParentB());
			Map<IEntityModifierType<?>, IEntityModifier> parentAModifiers = templateA.modifiers();
			Map<IEntityModifierType<?>, IEntityModifier> parentBModifiers = templateB.modifiers();
			Set<IEntityModifierType<?>> possibleModifierTypes = parentAModifiers.keySet();
			possibleModifierTypes.addAll(parentBModifiers.keySet());
			Map<IEntityModifierType<?>, IEntityModifier> childModifiers = new HashMap<>();
			Random rand = new Random();
			for(IEntityModifierType<?> modifierType : possibleModifierTypes){
				IEntityModifier modA = parentAModifiers.get(modifierType);
				IEntityModifier modB = parentBModifiers.get(modifierType);
				if(modA == null && modB == null){
					continue;
				}
				if(modA != null ^ modB != null){
					//Only one parent has a trait for this. 50% chance to pass it on
					if(rand.nextBoolean()){
						childModifiers.put(modifierType, modA == null ? modB : modA);
					}
				}else{
					//Both parents have some version of this trait. Combine them for the offspring
					childModifiers.put(modifierType, modifierType.mergeModifiers(modA, modB));
				}
			}
			EntityTemplate childTemplate = new EntityTemplate(MiscUtil.getRegistryName(child.getType(), Registries.ENTITY_TYPE), (templateA.quality() + templateB.quality()) / 2, childModifiers);
			Entity newChild = EntityTemplate.createEntityFromTemplate(childTemplate, (ServerLevel)  (e.getParentA().level()), child.blockPosition(), MobSpawnType.BREEDING, true, false, null);
			if(newChild instanceof AgeableMob newChildAgeable){
				newChildAgeable.setAge(child.getAge());
				e.setChild(newChildAgeable);
			}
		}
	}

	@SubscribeEvent
	public void emptyDispenserActivate(VanillaGameEvent e){
		if(!e.getLevel().isClientSide() && e.getVanillaEvent() == GameEvent.BLOCK_ACTIVATE && e.getContext().affectedState() != null && e.getContext().affectedState().getBlock() == Blocks.DISPENSER){
			//Empty dispenser activated
			BlockPos dispenserPos = BlockPos.containing(e.getEventPosition());
			Direction dispenserDir = e.getContext().affectedState().getValue(DispenserBlock.FACING);
			BlockPos targetPos = dispenserPos.relative(dispenserDir);

			//Remove glassware from glassware stand
			if(e.getLevel().getBlockEntity(targetPos) instanceof GlasswareHolderTileEntity glasswareTE && e.getLevel().getBlockEntity(dispenserPos) instanceof DispenserBlockEntity dispenserTE){
				ItemStack result = glasswareTE.removeGlassware(true);
				dispenserTE.insertItem(result);
			}
		}
	}

	@SubscribeEvent
	public void registerPotionBrewingRecipes(RegisterBrewingRecipesEvent e){
		CRPotions.registerPotionRecipes(e);
	}

	@SubscribeEvent
	public void potionEffectTimesOut(MobEffectEvent.Expired e){
		Sedation.checkForEffectEnd(e.getEntity(), e.getEffectInstance());
	}

	@SubscribeEvent
	public void potionEffectRemoved(MobEffectEvent.Remove e){
		Sedation.checkForEffectEnd(e.getEntity(), e.getEffectInstance());
	}

	@SubscribeEvent
	public void allowWrenchWithSneakOffhand(PlayerInteractEvent.RightClickBlock e){
		//Let me explain what the heck this does:
		//So default vanilla behavior is that shift-right-clicking with an item in your main hand lets the block react to the item
		//BUT if you shift right click with an item in your main hand, but with ANY item in your off-hand, the block doesn't get a chance to react at all
		//Which is really annoying, because a lot of CR machines need to be adjusted by shift-right-clicking with a wrench, and that doesn't work if you also use your offhand for stuff
		//So this specifically allows shift-right-click wrenching CR blocks to still work when you have something in your offhand
		String registryNamespace;
		if((ConfigUtil.isWrench(e.getItemStack()) || e.getItemStack().is(ESItems.linkingTool)) && ((registryNamespace = MiscUtil.getRegistryName(e.getLevel().getBlockState(e.getPos()).getBlock(), Registries.BLOCK).getNamespace()).equals(Crossroads.MODID) || registryNamespace.equals(Essentials.MODID))){
			e.setUseBlock(TriState.TRUE);
		}
	}
}