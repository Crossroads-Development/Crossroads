package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
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
				marker.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				CompoundNBT rangeData = new CompoundNBT();
				rangeData.putInt("range", power);
				marker.data = rangeData;
				worldIn.addEntity(marker);
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
					try{
						List<Biome.SpawnListEntry> list = worldServ.getChunkProvider().generator.getPossibleCreatures(EntityClassification.MONSTER, pos);
						list = ForgeEventFactory.getPotentialSpawns(worldServ, EntityClassification.MONSTER, pos, list);
						if(list != null && list.size() != 0){
							//Vanilla style spawning would spawn a group of mobs at a time (with group size defined by the SpawnListEntry). We only want to spawn 1 mob at a time
							Biome.SpawnListEntry entry = list.get(RAND.nextInt(list.size()));
							Entity ent = entry.entityType.create(worldIn);
							ent.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
							Event.Result r = ent instanceof MobEntity ? ForgeEventFactory.canEntitySpawn((MobEntity) ent, worldServ, pos.getX(), pos.getY(), pos.getZ(), null, SpawnReason.SPAWNER) : Event.Result.DEFAULT;
							if(r == Event.Result.ALLOW || r == Event.Result.DEFAULT){
								if(ent instanceof MobEntity){
									((MobEntity) ent).onInitialSpawn(worldServ, worldServ.getDifficultyForLocation(pos), SpawnReason.SPAWNER, null, null);//Gives mobs weapons/armor, makes slimes not have glitched health, and other essential things
								}
								worldServ.addEntity(ent);
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
