package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.beams.BeamUtil;
import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ObjectHolder(Crossroads.MODID)
public class EntityGhostMarker extends Entity{

	private static final EntityDataAccessor<Integer> LIFESPAN = SynchedEntityData.defineId(EntityGhostMarker.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<String> MARKER_TYPE = SynchedEntityData.defineId(EntityGhostMarker.class, EntityDataSerializers.STRING);

	@ObjectHolder("ghost_marker")
	public static EntityType<EntityGhostMarker> type = null;

	private long time;
	public CompoundTag data;

	public EntityGhostMarker(EntityType<EntityGhostMarker> type, Level worldIn){
		super(type, worldIn);
		setNoGravity(true);
		noPhysics = true;
	}

	public EntityGhostMarker(Level worldIn, @Nonnull EnumMarkerType markerType){
		this(worldIn, markerType, markerType.defaultLifespan);
	}

	public EntityGhostMarker(Level worldIn, @Nonnull EnumMarkerType markerType, int lifespan){
		super(type, worldIn);
		time = worldIn.getGameTime();
		setNoGravity(true);
		noPhysics = true;
		entityData.set(LIFESPAN, lifespan);
		entityData.set(MARKER_TYPE, markerType.name());
	}

	@Nonnull
	public EnumMarkerType getMarkerType(){
		String typeName = entityData.get(MARKER_TYPE);
		try{
			return EnumMarkerType.valueOf(typeName);
		}catch(IllegalArgumentException e){
			Crossroads.logger.warn("Missing ghost marker type: " + typeName);
			remove(RemovalReason.DISCARDED);
			return EnumMarkerType.NONE;
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag nbt){
		time = nbt.getLong("time");
		if(nbt.contains("data")){
			data = nbt.getCompound("data");
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag nbt){
		nbt.putLong("time", time);
		if(data != null){
			nbt.put("data", data);
		}
	}

	@Override
	public Packet<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void defineSynchedData(){
		entityData.define(LIFESPAN, 5);
		entityData.define(MARKER_TYPE, EnumMarkerType.NONE.name());
	}

	@Override
	public void tick(){
		super.tick();


		if(time != level.getGameTime()){
			time = level.getGameTime();//World time check to avoid tick-acceleration
			EnumMarkerType markType = getMarkerType();

			if(!level.isClientSide){
				//Decrease lifespan
				int lifespan = entityData.get(LIFESPAN);
				lifespan -= 1;
				entityData.set(LIFESPAN, lifespan);
				//Remove expired markers
				if(lifespan <= 0){
					if(markType.expireEffect != null){
						markType.expireEffect.accept(this);
					}
					remove(RemovalReason.DISCARDED);
				}
			}else if(markType.particleSupplier != null && time % 2 == 0){
				//Client side, particles
				level.addParticle(markType.particleSupplier.get(), getX() + level.random.nextGaussian(), getY() + level.random.nextGaussian(), getZ() + level.random.nextGaussian(), 0, 0, 0);
			}
		}
	}

	public enum EnumMarkerType{

		NONE(1, null, null),//Used for errors
		EQUILIBRIUM(BeamUtil.BEAM_TIME + 1, null, null),
		BLOCK_SPAWNING(BeamUtil.BEAM_TIME + 1, null, null),
		RESPAWNING(30 * 60, (EntityGhostMarker marker) -> {
			//Used for genetically modified entities which are respawning after death

			int penaltyTime = CRConfig.respawnPenaltyDuration.get() * 20;//Converted to ticks from seconds
			EntityTemplate template = new EntityTemplate();
			if(marker.data != null && !marker.level.isClientSide){
				template.deserializeNBT(marker.data);
				Entity created = EntityTemplate.spawnEntityFromTemplate(template, (ServerLevel) marker.level, marker.blockPosition(), MobSpawnType.COMMAND, false, false, null, null);
				if(created instanceof LivingEntity entity){
					entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, penaltyTime));
					entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, penaltyTime));
					entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, penaltyTime));
					entity.addEffect(new MobEffectInstance(EntityTemplate.getRespawnMarkerEffect(), penaltyTime));
				}
			}
		}, () -> ParticleTypes.TOTEM_OF_UNDYING);
		private final int defaultLifespan;
		private final Consumer<EntityGhostMarker> expireEffect;
		@Nullable
		private final Supplier<ParticleOptions> particleSupplier;

		EnumMarkerType(int defaultLifespan, @Nullable Consumer<EntityGhostMarker> expireEffect, @Nullable Supplier<ParticleOptions> particles){
			this.defaultLifespan = defaultLifespan;
			this.expireEffect = expireEffect;
			this.particleSupplier = particles;
		}
	}
}
