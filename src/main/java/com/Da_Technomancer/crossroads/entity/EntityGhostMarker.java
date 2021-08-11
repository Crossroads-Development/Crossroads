package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.beams.BeamUtil;
import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ObjectHolder(Crossroads.MODID)
public class EntityGhostMarker extends Entity{

	private static final DataParameter<Integer> LIFESPAN = EntityDataManager.defineId(EntityGhostMarker.class, DataSerializers.INT);
	private static final DataParameter<String> MARKER_TYPE = EntityDataManager.defineId(EntityGhostMarker.class, DataSerializers.STRING);

	@ObjectHolder("ghost_marker")
	public static EntityType<EntityGhostMarker> type = null;

	private long time;
	public CompoundNBT data;

	public EntityGhostMarker(EntityType<EntityGhostMarker> type, World worldIn){
		super(type, worldIn);
		setNoGravity(true);
		noPhysics = true;
	}

	public EntityGhostMarker(World worldIn, @Nonnull EnumMarkerType markerType){
		this(worldIn, markerType, markerType.defaultLifespan);
	}

	public EntityGhostMarker(World worldIn, @Nonnull EnumMarkerType markerType, int lifespan){
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
			remove();
			return EnumMarkerType.NONE;
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT nbt){
		time = nbt.getLong("time");
		if(nbt.contains("data")){
			data = nbt.getCompound("data");
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt){
		nbt.putLong("time", time);
		if(data != null){
			nbt.put("data", data);
		}
	}

	@Override
	public IPacket<?> getAddEntityPacket(){
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
					remove();
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
		VOID_EQUILIBRIUM(BeamUtil.BEAM_TIME + 1, null, null),
		DELAYED_EXPLOSION(BeamUtil.BEAM_TIME, (EntityGhostMarker marker) -> {if(marker.data != null && marker.data.contains("power")) marker.level.explode(marker, marker.getX(), marker.getY(), marker.getZ(), marker.data.getFloat("power"), marker.data.getBoolean("flaming"), Explosion.Mode.valueOf(marker.data.getString("blast_type")));}, null),
		BLOCK_SPAWNING(BeamUtil.BEAM_TIME + 1, null, null),
		RESPAWNING(30 * 60, (EntityGhostMarker marker) -> {
			//Used for genetically modified entities which are respawning after death

			int penaltyTime = CRConfig.respawnPenaltyDuration.get() * 20;//Converted to ticks from seconds
			EntityTemplate template = new EntityTemplate();
			if(marker.data != null && !marker.level.isClientSide){
				template.deserializeNBT(marker.data);
				Entity created = EntityTemplate.spawnEntityFromTemplate(template, (ServerWorld) marker.level, marker.blockPosition(), SpawnReason.COMMAND, false, false, null, null);
				if(created instanceof LivingEntity){
					LivingEntity entity = (LivingEntity) created;
					entity.addEffect(new EffectInstance(Effects.WEAKNESS, penaltyTime));
					entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, penaltyTime));
					entity.addEffect(new EffectInstance(Effects.GLOWING, penaltyTime));
					entity.addEffect(new EffectInstance(EntityTemplate.getRespawnMarkerEffect(), penaltyTime));
				}
			}
		}, () -> ParticleTypes.TOTEM_OF_UNDYING);
		private final int defaultLifespan;
		private final Consumer<EntityGhostMarker> expireEffect;
		@Nullable
		private final Supplier<IParticleData> particleSupplier;

		EnumMarkerType(int defaultLifespan, @Nullable Consumer<EntityGhostMarker> expireEffect, @Nullable Supplier<IParticleData> particles){
			this.defaultLifespan = defaultLifespan;
			this.expireEffect = expireEffect;
			this.particleSupplier = particles;
		}
	}
}
