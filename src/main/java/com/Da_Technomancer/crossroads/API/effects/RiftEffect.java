package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.Random;

public class RiftEffect extends BeamEffect{

	private static final Random RAND = new Random();

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				//Place a marker to prevent mob spawns (via event handler)
				EntityGhostMarker marker = new EntityGhostMarker(worldIn, EntityGhostMarker.EnumMarkerType.BLOCK_SPAWNING);
				marker.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				CompoundTag rangeData = new CompoundTag();
				rangeData.putInt("range", power);
				marker.data = rangeData;
				worldIn.addFreshEntity(marker);
			}else{
//				BlockState state = worldIn.getBlockState(pos);
//				//Turn Purpur blocks into shulkers
//				if(state.getBlock() == Blocks.PURPUR_BLOCK){
//					worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
//					ShulkerEntity shulker = EntityType.SHULKER.create(worldIn);
//					shulker.setAttachmentPos(pos);
//					shulker.setPosition(pos.getX(), pos.getY(), pos.getZ());
//					worldIn.addEntity(shulker);
//					return;
//				}
//
				//Spawn mobs
				ServerLevel worldServ = (ServerLevel) worldIn;
				if(RAND.nextInt(256) < power){
					boolean peaceful = worldServ.getDifficulty() == Difficulty.PEACEFUL || CRConfig.riftSpawnDrops.get();
					try{
						WeightedRandomList<MobSpawnSettings.SpawnerData> list = worldServ.getChunkSource().f_8328_.getMobsAt(worldIn.getBiome(pos), worldServ.structureFeatureManager(), MobCategory.MONSTER, pos);
//						list = ForgeEventFactory.getPotentialSpawns(worldServ, MobCategory.MONSTER, pos, list);
						if(!list.isEmpty()){
							//Vanilla style spawning would spawn a group of mobs at a time (with group size defined by the SpawnListEntry). We only want to spawn 1 mob at a time
							MobSpawnSettings.SpawnerData entry = list.getRandom(RAND).orElseThrow();
							Entity ent = entry.type.create(worldIn);
							ent.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
							Event.Result r = ent instanceof Mob ? ForgeEventFactory.canEntitySpawn((Mob) ent, worldServ, pos.getX(), pos.getY(), pos.getZ(), null, MobSpawnType.SPAWNER) : Event.Result.DEFAULT;
							if(r == Event.Result.ALLOW || r == Event.Result.DEFAULT){
								if(peaceful){//In peaceful, we spawn the mob drops instead of the entity
									if(ent instanceof LivingEntity lEnt && worldServ.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)){
										//All the methods for this are protected, so we re-implement them here

										//LivingEntity::dropLoot
										ResourceLocation resourcelocation = lEnt.getLootTable();
										LootTable loottable = worldServ.getServer().getLootTables().get(resourcelocation);
										LootContext.Builder lootcontext$builder = new LootContext.Builder(worldServ).withRandom(worldServ.random).withParameter(LootContextParams.THIS_ENTITY, lEnt).withParameter(LootContextParams.ORIGIN, lEnt.position()).withParameter(LootContextParams.DAMAGE_SOURCE, GrowEffect.POTENTIAL_VOID).withOptionalParameter(LootContextParams.KILLER_ENTITY, null).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, null);
										LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
										loottable.getRandomItems(ctx).forEach(lEnt::spawnAtLocation);

										//We don't implement/access LivingEntity::dropSpecialItems, because that is entity specific and usually irrelevant for newly spawned mobs
									}
								}else{
									if(ent instanceof Mob){
										((Mob) ent).finalizeSpawn(worldServ, worldServ.getCurrentDifficultyAt(pos), MobSpawnType.SPAWNER, null, null);//Gives mobs weapons/armor, makes slimes not have glitched health, and other essential things
									}
									worldServ.addFreshEntity(ent);
								}
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
}
