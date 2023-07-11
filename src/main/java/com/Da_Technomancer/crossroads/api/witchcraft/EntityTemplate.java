package com.Da_Technomancer.crossroads.api.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRReflection;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.essentials.api.ReflectionUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EntityTemplate implements INBTSerializable<CompoundTag>{

	public static final String RESPAWNING_KEY = "cr_respawning";
	public static final String LOYAL_KEY = "cr_loyal";
	public static final String OWNER_KEY = "cr_owner";

	private static final Method OFFSPRING_SPAWNING_METHOD = ReflectionUtil.reflectMethod(CRReflection.OFFSPRING_SPAWN_EGG);

	private ResourceLocation entityName;
	@Nullable
	private Component customName;
	private boolean loyal;
	private boolean respawning;
	private ArrayList<MobEffectInstance> effects;//Durations of these effects are ignored when applying
	private int degradation;
	@Nullable
	private UUID imprintingPlayer;
	@Nullable
	private UUID originatingUUID;//UUID of the entity that created this template; data is usually dropped after processing

	//Cache generated based on entity name
	private EntityType<?> entityType;

	public EntityTemplate(){

	}

	public EntityTemplate(EntityTemplate template){
		this.entityName = template.entityName;
		this.entityType = null;
		this.loyal = template.loyal;
		this.respawning = template.respawning;
		this.effects = template.effects;
		this.degradation = template.degradation;
		this.originatingUUID = template.originatingUUID;
		this.customName = template.customName;
		this.imprintingPlayer = template.imprintingPlayer;
	}

	public ResourceLocation getEntityName(){
		return entityName;
	}

	public void setEntityName(ResourceLocation entityName){
		this.entityName = entityName;
		entityType = null;
	}

	@Nullable
	public EntityType<?> getEntityType(){
		if(entityType == null){
			//Generate a cache based on entityName
			entityType = entityName == null ? null : ForgeRegistries.ENTITY_TYPES.getValue(entityName);
		}
		return entityType;
	}

	public boolean isLoyal(){
		return loyal;
	}

	public void setLoyal(boolean loyal){
		this.loyal = loyal;
	}

	public boolean isRespawning(){
		return respawning;
	}

	public void setRespawning(boolean respawning){
		this.respawning = respawning;
	}

	@Nullable
	public Component getCustomName(){
		return customName;
	}

	public void setCustomName(@Nullable Component customName){
		this.customName = customName;
	}

	@Nullable
	public UUID getImprintingPlayer(){
		return imprintingPlayer;
	}

	public void setImprintingPlayer(@Nullable UUID imprintingPlayer){
		this.imprintingPlayer = imprintingPlayer;
	}

	@Nonnull
	public ArrayList<MobEffectInstance> getEffects(){
		if(effects == null){
			effects = new ArrayList<>(0);
		}
		return effects;
	}

	public void setEffects(ArrayList<MobEffectInstance> effects){
		this.effects = effects;
	}

	public int getDegradation(){
		return degradation;
	}

	public void setDegradation(int degradation){
		this.degradation = degradation;
	}

	@Nullable
	public UUID getOriginatingUUID(){
		return originatingUUID;
	}

	public void setOriginatingUUID(@Nullable UUID originatingUUID){
		this.originatingUUID = originatingUUID;
	}

	@Override
	public CompoundTag serializeNBT(){
		CompoundTag nbt = new CompoundTag();
		nbt.putString("entity_name", entityName == null ? "" : entityName.toString());
		nbt.putBoolean("loyal", loyal);
		nbt.putBoolean("respawning", respawning);
		ListTag potions = new ListTag();
		if(effects != null){
			for(MobEffectInstance instance : effects){
				potions.add(instance.save(new CompoundTag()));
			}
		}
		nbt.put("potions", potions);
		nbt.putInt("degradation", degradation);
		if(customName != null){
			nbt.putString("custom_name", Component.Serializer.toJson(customName));
		}
		if(imprintingPlayer != null){
			nbt.putUUID("imprinting_player", imprintingPlayer);
		}
		if(originatingUUID != null){
			nbt.putUUID("originating_uuid", originatingUUID);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt){
		String name = nbt.getString("entity_name");
		entityName = name == null || name.length() == 0 ? null : new ResourceLocation(name);
		entityType = null;
		loyal = nbt.getBoolean("loyal");
		respawning = nbt.getBoolean("respawning");
		ListTag potions = nbt.getList("potions", 10);//ID 10 is CompoundNBT
		effects = new ArrayList<>();
		for(int i = 0; i < potions.size(); i++){
			effects.add(MobEffectInstance.load(potions.getCompound(i)));
		}
		degradation = nbt.getInt("degradation");
		if(nbt.contains("custom_name")){
			customName = Component.Serializer.fromJson(nbt.getString("custom_name"));
		}
		if(nbt.contains("imprinting_player")){
			imprintingPlayer = nbt.getUUID("imprinting_player");
		}
		if(nbt.contains("originating_uuid")){
			originatingUUID = nbt.getUUID("originating_uuid");
		}
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		EntityTemplate that = (EntityTemplate) o;
		return loyal == that.loyal && respawning == that.respawning && degradation == that.degradation && entityName.equals(that.entityName) && Objects.equals(customName, that.customName) && Objects.equals(effects, that.effects) && Objects.equals(imprintingPlayer, that.imprintingPlayer) && Objects.equals(originatingUUID, that.originatingUUID);
	}

	@Override
	public int hashCode(){
		return Objects.hash(entityName, customName, loyal, respawning, effects, degradation, imprintingPlayer, originatingUUID);
	}

	public boolean isDummyTemplate(){
		return entityName == null;
	}

	/**
	 * Adds a tooltip based on the information in this template
	 * @param tooltips The tooltip to append to
	 * @param maxLines The maximum lines to append. This value will be ignored for essential information
	 */
	public void addTooltip(List<Component> tooltips, int maxLines){
		if(isDummyTemplate()){
			tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.dummy"));
			return;
		}

		getEntityType();//Builds the cache

		int linesUsed = 0;

		if(entityName == null){
			tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.type.missing"));
			linesUsed += 1;
			//Error message, nothing else
		}else{
			tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.type").append(entityType == null ? Component.literal(entityName.toString()) : entityType.getDescription()));

			if(maxLines <= 4){
				//Only a few lines; fit degradation, loyalty, and respawning onto one line
				MutableComponent detailsCompon = Component.translatable("tt.crossroads.boilerplate.entity_template.degradation", degradation);
				if(loyal){
					detailsCompon.append(Component.translatable("tt.crossroads.boilerplate.entity_template.separator"));
					if(imprintingPlayer != null){
						detailsCompon.append(Component.translatable("tt.crossroads.boilerplate.entity_template.loyal.preset"));
					}else{
						detailsCompon.append(Component.translatable("tt.crossroads.boilerplate.entity_template.loyal"));
					}
				}
				if(respawning){
					detailsCompon.append(Component.translatable("tt.crossroads.boilerplate.entity_template.separator"));
					detailsCompon.append(Component.translatable("tt.crossroads.boilerplate.entity_template.respawning"));
				}
				tooltips.add(detailsCompon);
				linesUsed += 1;
			}else{
				//Degradation, loyalty, and respawning all get separate lines
				tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.degradation", degradation));
				linesUsed += 1;
				if(loyal){
					if(imprintingPlayer != null){
						tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.loyal.preset"));
					}else{
						tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.loyal"));
					}
					linesUsed += 1;
				}
				if(respawning){
					tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.respawning"));
					linesUsed += 1;
				}
			}

			int effectCount = effects.size();
			int needExtension = Math.max(0, effectCount - (maxLines - linesUsed));
			boolean limitPower = CRConfig.limitPermanentPotionStrength.get();
			for(MobEffectInstance effect : effects){
				if(linesUsed < maxLines || needExtension > 0 && linesUsed < maxLines - 1){
					if(limitPower){
						//Don't display the potion level
						tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.potion").append(effect.getEffect().getDisplayName()));
					}else{
						tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.potion").append(effect.getEffect().getDisplayName()).append(" ").append(Component.translatable("enchantment.level." + (effect.getAmplifier() + 1))));
					}
					linesUsed++;
				}else{
					break;
				}
			}
			if(needExtension > 0){
				tooltips.add(Component.translatable("tt.crossroads.boilerplate.entity_template.potion.additional", needExtension));
			}
		}
	}

	@Nullable
	public static Entity spawnEntityFromTemplate(EntityTemplate template, ServerLevel world, BlockPos pos, MobSpawnType reason, boolean offset, boolean unmapped, @Nullable Component customName, @Nullable Player player){
		//Check if the entity is on the blacklist. If so, refuse to spawn
		ResourceLocation entityRegistryName = template.getEntityName();
		if(!isCloningAllowed(entityRegistryName)){
			return null;
		}

		EntityType<?> type = template.getEntityType();
		if(type == null){
			return null;
		}

		//Don't pass the itemstack to the spawn method
		//That parameter is designed for the vanilla spawn egg NBT structure, which we don't use
		//We have to adjust the mob manually after spawning as a result
		Entity created = type.spawn(world, null, player, pos, reason, offset, unmapped);
		if(created == null){
			return null;
		}
		//Custom name
		//The custom name parameter is used preferentially, with a custom name on the template as a fallback
		customName = customName == null ? template.customName : customName;
		if(customName != null){
			created.setCustomName(customName);
		}
		//NBT traits
		CompoundTag nbt = created.getPersistentData();
		nbt.putBoolean(EntityTemplate.LOYAL_KEY, template.isLoyal());
		if(template.getImprintingPlayer() != null){
			nbt.putUUID(EntityTemplate.OWNER_KEY, template.getImprintingPlayer());
		}
		nbt.putBoolean(EntityTemplate.RESPAWNING_KEY, template.isRespawning());

		if(created instanceof LivingEntity entity){
			//Degradation
			int degradeConfig = CRConfig.degradationPenalty.get();
			if(template.getDegradation() > 0 && degradeConfig > 0){
				CRPotions.applyAsPermanent(entity, new MobEffectInstance(CRPotions.HEALTH_PENALTY_EFFECT, Integer.MAX_VALUE, template.getDegradation() * degradeConfig - 1));
			}

			//Potion effects
			ArrayList<MobEffectInstance> rawEffects = template.getEffects();
			for(MobEffectInstance effect : rawEffects){
				CRPotions.applyAsPermanent(entity, effect);
			}

			//Loyalty
			if(template.isLoyal()){
				//Prevent despawning
				if(created instanceof Mob){
					((Mob) created).setPersistenceRequired();
				}

				Player tamingPlayer = null;
				//If a target taming player is already set, use that for imprinting
				//Otherwise, fallback to the player who placed the mob
				if(template.getImprintingPlayer() != null){
					tamingPlayer = world.getPlayerByUUID(template.getImprintingPlayer());
					if(tamingPlayer == null){
						//Will be null if the player isn't currently online in the world; a fake player is used as a substitute
						tamingPlayer = FakePlayerFactory.get(world, new GameProfile(template.getImprintingPlayer(), null));
					}
				}
				if(tamingPlayer == null){
					tamingPlayer = player;
				}

				//Auto-tame it to the player who spawned it
				if(tamingPlayer != null){
					//There isn't a single method for this. The correct way to set something as tamed varies based on the mob
					//New vanilla tamable mobs may require changes here, and modded tameable mobs are unlikely to work
					if(created instanceof TamableAnimal){
						((TamableAnimal) created).tame(tamingPlayer);
					}else if(created instanceof AbstractHorse){
						((AbstractHorse) created).tameWithName(tamingPlayer);
					}else if(created instanceof Mob && OFFSPRING_SPAWNING_METHOD != null){
						//As of vanilla MC1.16.5, this is literally only applicable to foxes
						try{
							OFFSPRING_SPAWNING_METHOD.invoke(created, tamingPlayer, created);
						}catch(IllegalAccessException | InvocationTargetException e){
							Crossroads.logger.catching(e);
						}
					}
				}
			}
		}

		return created;
	}

	public static MobEffect getRespawnMarkerEffect(){
		return CRPotions.TRANSIENT_EFFECT;
	}

	public static EntityTemplate getTemplateFromEntity(LivingEntity source){
		EntityTemplate template = new EntityTemplate();
		template.setEntityName(MiscUtil.getRegistryName(source.getType(), ForgeRegistries.ENTITY_TYPES));
		template.setRespawning(source.getPersistentData().getBoolean(RESPAWNING_KEY));
		template.setLoyal(source.getPersistentData().getBoolean(LOYAL_KEY));
		if(source.getPersistentData().contains(OWNER_KEY)){
			template.setImprintingPlayer(source.getPersistentData().getUUID(OWNER_KEY));
		}

		if(source.getCustomName() != null){
			template.setCustomName(source.getCustomName());
		}

		Collection<MobEffectInstance> effects = source.getActiveEffects();
		int degrade = 0;
		ArrayList<MobEffectInstance> permanentEffects = new ArrayList<>(0);
		for(MobEffectInstance instance : effects){
			if(MiscUtil.getRegistryName(CRPotions.HEALTH_PENALTY_EFFECT, ForgeRegistries.MOB_EFFECTS).equals(MiscUtil.getRegistryName(instance.getEffect(), ForgeRegistries.MOB_EFFECTS))){
				//This is the health penalty, interpret as degradation
				degrade += (instance.getAmplifier() + 1) / 2;//We divide by 2, as degradation is measured in hearts
			}else if(!instance.getEffect().isInstantenous() && instance.getDuration() > CRPotions.PERM_EFFECT_CUTOFF){
				permanentEffects.add(new MobEffectInstance(instance));//Copy the value to prevent changes in the mutable instance
			}
		}
		template.setDegradation(degrade);
		template.setEffects(permanentEffects);
		template.setOriginatingUUID(source.getUUID());

		return template;
	}

	public static boolean isCloningAllowed(ResourceLocation entityName){
		if(entityName.equals(new ResourceLocation("minecraft:player"))){
			return false;
		}
		List<? extends String> blacklist = CRConfig.cloningBlacklist.get();
		return blacklist.stream().noneMatch(entry -> new ResourceLocation(entry).equals(entityName));
	}
}
