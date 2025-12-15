package com.Da_Technomancer.crossroads.api.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.EventHandlerCommon;
import com.Da_Technomancer.crossroads.api.LazyCache;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.packets.StreamCodecUtils;
import com.Da_Technomancer.crossroads.entity.CRMobDamage;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record EntityTemplate(@Nonnull ResourceLocation entityID, @Nonnull LazyCache<EntityType<?>> entityType, @Nonnull LazyCache<Integer> modifierComplexity, @Nonnull LazyCache<Integer> modifierSoulComplexity, int quality, @Nonnull Map<IEntityModifierType<?>, IEntityModifier> modifiers){

	public static final Codec<EntityTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("entity_id").forGetter(EntityTemplate::entityID),
			ExtraCodecs.NON_NEGATIVE_INT.fieldOf("quality").forGetter(EntityTemplate::quality),
			Codec.dispatchedMap(IEntityModifierType.CODEC, IEntityModifierType::modifierCodec).fieldOf("modifiers").forGetter(EntityTemplate::modifiers)
	).apply(instance, EntityTemplate::new));

	public static final StreamCodec<ByteBuf, EntityTemplate> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, EntityTemplate::entityID,
			ByteBufCodecs.VAR_INT, EntityTemplate::quality,
			StreamCodecUtils.dispatchedMap(IEntityModifierType.STREAM_CODEC, IEntityModifierType::modifierStreamCodec), EntityTemplate::modifiers,
			EntityTemplate::new
	);
	public static final EntityTemplate DEFAULT = new EntityTemplate(ResourceLocation.parse("pig"), 0, new HashMap<>(0));

	public static final String TEMPLATE_KEY = "cr_template";

	private static final int MIN_COMPLEXITY = 1;
	private static final int MIN_SOUL_COMPLEXITY = 0;
	public static final int MIN_QUALITY = 0;
	public static final int MAX_QUALITY = 100;

	public EntityTemplate(ResourceLocation entityID, int quality, Map<IEntityModifierType<?>, IEntityModifier> modifiers){
		this(entityID, new LazyCache<>(() -> BuiltInRegistries.ENTITY_TYPE.get(entityID)), new LazyCache<>(() -> modifiers.values().stream().map(IEntityModifier::complexity).reduce(0, Integer::sum)), new LazyCache<>(() -> modifiers.values().stream().map(IEntityModifier::soulComplexity).reduce(0, Integer::sum)), Math.max(MIN_QUALITY, Math.min(MAX_QUALITY, quality)), new HashMap<>(modifiers));
	}

	public EntityTemplate withQuality(int newQuality){
		if(newQuality == quality){
			return this;
		}
		return new EntityTemplate(entityID, newQuality, modifiers);
	}

	public EntityTemplate withMob(ResourceLocation newEntityID){
		return new EntityTemplate(newEntityID, quality, modifiers);
	}

	public EntityTemplate withModifiers(Map<IEntityModifierType<?>, IEntityModifier> newModifiers){
		return new EntityTemplate(entityID, quality, newModifiers);
	}

	@Nullable
	public EntityType<?> getEntityType(){
		return entityType.get();
	}

	private int baseComplexity(){
		//TODO balance
		if(isCloningForbidden(entityID)){
			return 999;
		}
		EntityType<?> entType = getEntityType();
		if(entType == null){
			return 999;
		}
		if(CraftingUtil.tagContains(EventHandlerCommon.GHOST_MOB, entType)){
			return 0;
		}
		if(CraftingUtil.tagContains(EventHandlerCommon.HUMANOID_MOB, entType)){
			return 50;
		}
		if(CraftingUtil.tagContains(EntityTypeTags.UNDEAD, entType)){
			return 10;
		}
		return 40;
	}

	public int totalComplexity(){
		return Math.max(baseComplexity() + modifierComplexity.get(), MIN_COMPLEXITY);
	}

	private int baseSoulComplexity(){
		if(entityID.equals(ResourceLocation.parse("minecraft:player"))){
			//The player can have a soul, as a treat
			return 1;
		}
		EntityType<?> entType = getEntityType();
		if(entType == null){
			return 0;
		}
		if(CraftingUtil.tagContains(EventHandlerCommon.GHOST_MOB, entType)){
			return 1;
		}

		return 0;
	}

	public int totalSoulComplexity(){
		return Math.max(baseSoulComplexity() + modifierSoulComplexity.get(), MIN_SOUL_COMPLEXITY);
	}

	public Tag serializeNBT(HolderLookup.Provider provider){
		DataResult<Tag> encoded = CODEC.encodeStart(NbtOps.INSTANCE, this);
		if(encoded.isSuccess()){
			return encoded.getOrThrow();
		}
		Crossroads.logger.error("Failed to encode entity template");
		Crossroads.logger.error(this);
		Crossroads.logger.error("Encoding error:" + encoded.error().get().messageSupplier().get());
		return new CompoundTag();
	}

	public static EntityTemplate deserializeNBT(HolderLookup.Provider provider, Tag nbt){
		DataResult<com.mojang.datafixers.util.Pair<EntityTemplate, Tag>> decoded = CODEC.decode(NbtOps.INSTANCE, nbt);
		if(decoded.isSuccess()){
			return decoded.getOrThrow().getFirst();
		}
		Crossroads.logger.error("Failed to decode entity template");
		Crossroads.logger.error("Decoding error:" + decoded.error().get().messageSupplier().get());
		return DEFAULT;
	}

	/**
	 * Adds a tooltip based on the information in this template
	 * Formatted better when called on the client-side!
	 * @param tooltips The tooltip to append to
	 */
	public void addTooltip(List<Component> tooltips, Level level){
		final EntityType<?> entityType = getEntityType();
		//Main line with entity name, complexity, soul complexity, quality
		tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.desc1")
				.append(MiscUtil.asMutable(entityType == null ? Component.literal(entityID.toString()) : entityType.getDescription())).withStyle(MiscUtil.TT_DYNAMIC)
				.append(Component.translatable("tt.crossroads.boilerplate.entity_template.desc2", totalComplexity(), totalSoulComplexity(), quality())));
		//Modifiers
		if(!modifiers.isEmpty()){
			//We want modifierComponents sorted in alphabetical order...
			//...but the system isn't made for that
			//Known issue: If we're on the client, this works fine
			//But if we're on the server, it will be alphabetized based on the server language, but displayed to the user in their client language in (probably) the wrong order
			List<Component> modifierComponents = modifiers.values().stream().map(entityModifier -> entityModifier.getName(entityType, level)).map(component -> Pair.of(component.getString(10), component)).sorted(Comparator.comparing(Pair::getLeft)).map(Pair::getRight).toList();
			int modifierCount = modifierComponents.size();
			MutableComponent modifierComponent = Component.translatable("tt.crossroads.boilerplate.entity_template.modifier.start");
			for(int i = 0; i < modifierCount; i++){
				modifierComponent.append(modifierComponents.get(i));
				if(i + 1 != modifierCount){
					modifierComponent.append(Component.translatable("tt.crossroads.boilerplate.entity_template.modifier.divider"));
				}
			}
			tooltips.add(modifierComponent);
		}
	}

	private static final ResourceLocation HEALTH_PENALTY_ATTRIBUTE = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "degraded_clone");

	@Nullable
	public static Entity spawnEntityFromTemplate(EntityTemplate template, ServerLevel world, BlockPos pos, MobSpawnType reason, boolean offset, boolean unmapped, @Nullable Component customName, @Nullable Player player){
		//Check if the entity is on the blacklist. If so, refuse to spawn
		ResourceLocation entityRegistryName = template.entityID();
		if(isCloningForbidden(entityRegistryName)){
			return null;
		}

		EntityType<?> type = template.getEntityType();
		if(type == null){
			return null;
		}

		float mobHealthPenalty = 0;
		boolean isNonViable = false;
		int quality = template.quality();
		int complexity = template.totalComplexity();
		if(complexity > quality){
			mobHealthPenalty = (quality - complexity) * 0.05F;//-1 is -100% health
			if(mobHealthPenalty <= -.99F){
				//Clone is going to die immediately on spawning
				//Force it, and switch the mob type to something crummy
				type = EntityType.SLIME;
				isNonViable = true;
				mobHealthPenalty = -999;
			}
		}

		//Don't pass the itemstack to the spawn method
		//That parameter is designed for the vanilla spawn egg NBT structure, which we don't use
		//We have to adjust the mob manually after spawning as a result
		Entity created = type.spawn(world, null, player, pos, reason, offset, unmapped);
		if(created == null){
			return null;
		}

		if(customName != null){
			created.setCustomName(customName);
		}

		for(IEntityModifier modifier : template.modifiers().values()){
			created = modifier.apply(created);
		}

		//Degradation
		if(created instanceof LivingEntity entity){
			if(mobHealthPenalty < 0){
				entity.getAttributes().getInstance(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(HEALTH_PENALTY_ATTRIBUTE, mobHealthPenalty, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
			}
		}
		//Save the original EntityTemplate in the entity
		created.getPersistentData().put(TEMPLATE_KEY, template.serializeNBT(world.registryAccess()));
		if(isNonViable){
			created.hurt(CRMobDamage.damageSource(CRMobDamage.NON_VIABLE, world), 1);
		}
		return created;
	}

	public static Holder<MobEffect> getRespawnMarkerEffect(){
		return CRPotions.TRANSIENT_EFFECT;
	}

	public static EntityTemplate getTemplateFromEntity(LivingEntity source){
//		EntityTemplate template = new EntityTemplate();
//		template.setEntityName(MiscUtil.getRegistryName(source.getType(), BuiltInRegistries.ENTITY_TYPE));
//		template.setRespawning(source.getPersistentData().getBoolean(RESPAWNING_KEY));
//		template.setLoyal(source.getPersistentData().getBoolean(LOYAL_KEY));
//		if(source.getPersistentData().contains(OWNER_KEY)){
//			template.setImprintingPlayer(source.getPersistentData().getUUID(OWNER_KEY));
//		}
//
//		if(source.getCustomName() != null){
//			template.setCustomName(source.getCustomName());
//		}
//
//		Collection<MobEffectInstance> effects = source.getActiveEffects();
//		int degrade = 0;
//		ArrayList<MobEffectInstance> permanentEffects = new ArrayList<>(0);
//		for(MobEffectInstance instance : effects){
//			if(MiscUtil.getRegistryName(CRPotions.HEALTH_PENALTY_EFFECT.value(), BuiltInRegistries.MOB_EFFECT).equals(MiscUtil.getRegistryName(instance.getEffect().value(), BuiltInRegistries.MOB_EFFECT))){
//				//This is the health penalty, interpret as degradation
//				degrade += (instance.getAmplifier() + 1) / 2;//We divide by 2, as degradation is measured in hearts
//			}else if(!instance.getEffect().value().isInstantenous() && instance.getDuration() > CRPotions.PERM_EFFECT_CUTOFF){
//				permanentEffects.add(new MobEffectInstance(instance));//Copy the value to prevent changes in the mutable instance
//			}
//		}
//		template.setDegradation(degrade);
//		template.setEffects(permanentEffects);
//		template.setOriginatingUUID(source.getUUID());
//
//		return template;
		Tag savedTemplateData = source.getPersistentData().get(TEMPLATE_KEY);
		if(savedTemplateData != null){
			return deserializeNBT(source.registryAccess(), savedTemplateData).withQuality(0);
		}
		ResourceLocation entityName = MiscUtil.getRegistryName(source.getType(), BuiltInRegistries.ENTITY_TYPE);
		return new EntityTemplate(entityName, 0, new HashMap<>(0));
	}

	public static boolean isCloningForbidden(ResourceLocation entityName){
		if(entityName.equals(ResourceLocation.parse("minecraft:player"))){
			return true;
		}
		List<? extends String> blacklist = CRConfig.cloningBlacklist.get();
		return blacklist.stream().anyMatch(entry -> ResourceLocation.parse(entry).equals(entityName));
	}
}
