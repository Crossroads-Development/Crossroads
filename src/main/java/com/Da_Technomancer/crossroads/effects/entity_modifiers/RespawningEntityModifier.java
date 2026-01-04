package com.Da_Technomancer.crossroads.effects.entity_modifiers;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifier;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifierType;
import com.Da_Technomancer.crossroads.api.witchcraft.SimpleEntityModifierType;
import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public record RespawningEntityModifier(int complexity, int soulComplexity) implements IEntityModifier{

	private static final String RESPAWNING_KEY = "cr_respawning";

	public static final IEntityModifierType<RespawningEntityModifier> TYPE_INSTANCE = new SimpleEntityModifierType<>("respawning", RespawningEntityModifier::new);

	private static boolean canRespawn(Entity entity, int delayConfig){
		if(entity instanceof LivingEntity living && living.getPersistentData().getBoolean(RESPAWNING_KEY)){
			//Ensure it doesn't have the marker effect and this is enabled in config
			return !living.hasEffect(EntityTemplate.getRespawnMarkerEffect()) && delayConfig > 0;
		}
		return false;
	}

	public static void handleEntityDeath(LivingEntity entity){
		//For genetically modified mobs with respawning, if they died without the respawn marker effect, create a ghost marker to respawn them
		int delay = CRConfig.respawnDelay.get() * 20;//Convert from seconds to ticks
		if(entity.level() instanceof ServerLevel sLevel && canRespawn(entity, delay)){
			if(CRConfig.respawnChance.get() <= Math.random()){
				//The bell tolls for thee
				CRSounds.playSoundServer(entity.level(), entity.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.HOSTILE, 3F, 0.5F);
				AABB entityBoundingBox = entity.getBoundingBox();
				CRParticles.summonParticlesFromServer(sLevel, ParticleTypes.TRIAL_OMEN, 5, (entityBoundingBox.minX + entityBoundingBox.minX) / 2D, (entityBoundingBox.minY + entityBoundingBox.minY) / 2D, (entityBoundingBox.minZ + entityBoundingBox.minZ) / 2D, entityBoundingBox.getXsize() / 2D, entityBoundingBox.getYsize() / 2D, entityBoundingBox.getZsize() / 2D, 0, 0, 0, 0, 0, 0, true);
			}else{
				//Queue up it respawning
				EntityGhostMarker marker = new EntityGhostMarker(entity.level(), EntityGhostMarker.EnumMarkerType.RESPAWNING, delay);
				marker.setPos(entity.getX(), entity.getY(), entity.getZ());
				marker.data = new CompoundTag();
				marker.data.put(EntityTemplate.TEMPLATE_KEY, EntityTemplate.getTemplateFromEntity(entity).serializeNBT(entity.registryAccess()));
				entity.level().addFreshEntity(marker);
			}
		}
	}

	@Override
	public Entity apply(Entity entity){
		CompoundTag nbt = entity.getPersistentData();
		nbt.putBoolean(RESPAWNING_KEY, true);
		return entity;
	}

	private static final Component NAME = Component.translatable("ent_mod.respawning");

	@Override
	public Component getName(@Nullable EntityType<?> entityType, @Nullable Level level){
		return NAME;
	}
}
