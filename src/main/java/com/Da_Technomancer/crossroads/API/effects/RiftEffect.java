package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import net.minecraft.entity.*;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class RiftEffect extends BeamEffect{

	private static final Random RAND = new Random();

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				//Place a marker to prevent mob spawns (via event handler)
				EntityGhostMarker marker = new EntityGhostMarker(worldIn, EntityGhostMarker.EnumMarkerType.BLOCK_SPAWNING);
				marker.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				CompoundNBT rangeData = new CompoundNBT();
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
				ServerWorld worldServ = (ServerWorld) worldIn;
				if(RAND.nextInt(256) < power){
					boolean peaceful = worldServ.getDifficulty() == Difficulty.PEACEFUL || CRConfig.riftSpawnDrops.get();
					try{
						List<MobSpawnInfo.Spawners> list = worldServ.getChunkSource().generator.getMobsAt(worldIn.getBiome(pos), worldServ.structureFeatureManager(), EntityClassification.MONSTER, pos);
						list = ForgeEventFactory.getPotentialSpawns(worldServ, EntityClassification.MONSTER, pos, list);
						if(list != null && list.size() != 0){
							//Vanilla style spawning would spawn a group of mobs at a time (with group size defined by the SpawnListEntry). We only want to spawn 1 mob at a time
							MobSpawnInfo.Spawners entry = list.get(RAND.nextInt(list.size()));
							Entity ent = entry.type.create(worldIn);
							ent.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
							Event.Result r = ent instanceof MobEntity ? ForgeEventFactory.canEntitySpawn((MobEntity) ent, worldServ, pos.getX(), pos.getY(), pos.getZ(), null, SpawnReason.SPAWNER) : Event.Result.DEFAULT;
							if(r == Event.Result.ALLOW || r == Event.Result.DEFAULT){
								if(peaceful){//In peaceful, we spawn the mob drops instead of the entity
									if(ent instanceof LivingEntity && worldServ.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)){
										LivingEntity lEnt = (LivingEntity) ent;

										//All the methods for this are protected, so we re-implement them here

										//LivingEntity::dropLoot
										ResourceLocation resourcelocation = lEnt.getLootTable();
										LootTable loottable = worldServ.getServer().getLootTables().get(resourcelocation);
										LootContext.Builder lootcontext$builder = new LootContext.Builder(worldServ).withRandom(worldServ.random).withParameter(LootParameters.THIS_ENTITY, lEnt).withParameter(LootParameters.ORIGIN, lEnt.position()).withParameter(LootParameters.DAMAGE_SOURCE, GrowEffect.POTENTIAL_VOID).withOptionalParameter(LootParameters.KILLER_ENTITY, null).withOptionalParameter(LootParameters.DIRECT_KILLER_ENTITY, null);
										LootContext ctx = lootcontext$builder.create(LootParameterSets.ENTITY);
										loottable.getRandomItems(ctx).forEach(lEnt::spawnAtLocation);

										//We don't implement/access LivingEntity::dropSpecialItems, because that is entity specific and usually irrelevant for newly spawned mobs
									}
								}else{
									if(ent instanceof MobEntity){
										((MobEntity) ent).finalizeSpawn(worldServ, worldServ.getCurrentDifficultyAt(pos), SpawnReason.SPAWNER, null, null);//Gives mobs weapons/armor, makes slimes not have glitched health, and other essential things
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
