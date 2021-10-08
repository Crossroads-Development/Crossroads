package com.Da_Technomancer.crossroads.API.witchcraft;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.entity.*;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;

public class EntityTemplate implements INBTSerializable<CompoundTag>{

	public static final String RESPAWNING_KEY = "cr_respawning";
	public static final String LOYAL_KEY = "cr_loyal";

	private static final Method OFFSPRING_SPAWNING_METHOD = ReflectionUtil.reflectMethod(CRReflection.OFFSPRING_SPAWN_EGG);

	private ResourceLocation entityName;
	private boolean loyal;
	private boolean respawning;
	private ArrayList<MobEffectInstance> effects;//Durations of these effects are ignored when applying
	private int degradation;

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
	}

	public EntityTemplate(ResourceLocation entityName, boolean loyal, boolean respawning, ArrayList<MobEffectInstance> effects, int degradation){
		this.entityName = entityName;
		this.loyal = loyal;
		this.respawning = respawning;
		this.effects = effects;
		this.degradation = degradation;
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
			entityType = entityName == null ? null : ForgeRegistries.ENTITIES.getValue(entityName);
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

	@Override
	public CompoundTag serializeNBT(){
		CompoundTag nbt = new CompoundTag();
		nbt.putString("entity_name", entityName.toString());
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
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt){
		entityName = new ResourceLocation(nbt.getString("entity_name"));
		entityType = null;
		loyal = nbt.getBoolean("loyal");
		respawning = nbt.getBoolean("respawning");
		ListTag potions = nbt.getList("potions", 10);//ID 10 is CompoundNBT
		effects = new ArrayList<>();
		for(int i = 0; i < potions.size(); i++){
			effects.add(MobEffectInstance.load(potions.getCompound(i)));
		}
		degradation = nbt.getInt("degradation");
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
		return loyal == that.loyal &&
				respawning == that.respawning &&
				degradation == that.degradation &&
				Objects.equals(entityName, that.entityName) &&
				Objects.equals(effects, that.effects);
	}

	@Override
	public int hashCode(){
		return Objects.hash(entityName, loyal, respawning, effects, degradation);
	}

	/**
	 * Adds a tooltip based on the information in this template
	 * @param tooltips The tooltip to append to
	 * @param maxLines The maximum lines to append. This value will be ignored for essential information
	 */
	public void addTooltip(List<Component> tooltips, int maxLines){
		getEntityType();//Builds the cache

		int linesUsed = 0;

		if(entityName == null){
			tooltips.add(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.type.missing"));
			linesUsed += 1;
			//Error message, nothing else
		}else{
			tooltips.add(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.type").append(entityType == null ? new TextComponent(entityName.toString()) : entityType.getDescription()));

			if(maxLines <= 4){
				//Only a few lines; fit degradation, loyalty, and respawning onto one line
				BaseComponent detailsCompon = new TranslatableComponent("tt.crossroads.boilerplate.entity_template.degradation", degradation);
				if(loyal){
					detailsCompon.append(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.separator"));
					detailsCompon.append(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.loyal"));
				}
				if(respawning){
					detailsCompon.append(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.separator"));
					detailsCompon.append(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.respawning"));
				}
				tooltips.add(detailsCompon);
				linesUsed += 1;
			}else{
				//Degredation, loyalty, and respawning all get separate lines
				tooltips.add(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.degradation", degradation));
				linesUsed += 1;
				if(loyal){
					tooltips.add(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.loyal"));
					linesUsed += 1;
				}
				if(respawning){
					tooltips.add(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.respawning"));
					linesUsed += 1;
				}
			}

			int effectCount = effects.size();
			int needExtension = Math.max(0, effectCount - (maxLines - linesUsed));
			for(MobEffectInstance effect : effects){
				if(linesUsed < maxLines || needExtension > 0 && linesUsed < maxLines - 1){
					tooltips.add(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.potion").append(effect.getEffect().getDisplayName()).append(" ").append(new TranslatableComponent("enchantment.level." + (effect.getAmplifier() + 1))));
					linesUsed++;
				}else{
					break;
				}
			}
			if(needExtension > 0){
				tooltips.add(new TranslatableComponent("tt.crossroads.boilerplate.entity_template.potion.additional", needExtension));
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
		Entity created = type.spawn(world, null, customName, player, pos, reason, offset, unmapped);
		LivingEntity entity;
		if(created == null){
			return null;
		}

		//NBT traits
		CompoundTag nbt = created.getPersistentData();
		nbt.putBoolean(EntityTemplate.LOYAL_KEY, template.isLoyal());
		nbt.putBoolean(EntityTemplate.RESPAWNING_KEY, template.isRespawning());

		if(created instanceof LivingEntity){
			entity = (LivingEntity) created;

			//Degradation
			int degradeConfig = CRConfig.degradationPenalty.get();
			if(template.getDegradation() > 0 && degradeConfig > 0){
				entity.addEffect(new MobEffectInstance(CRPotions.HEALTH_PENALTY_EFFECT, Integer.MAX_VALUE, template.getDegradation() * degradeConfig - 1));
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

				//Auto-tame it to the player who spawned it
				if(player != null){
					//There isn't a single method for this. The correct way to set something as tamed varies based on the mob
					//New vanilla tamable mobs may require changes here, and modded tameable mobs are unlikely to work
					if(created instanceof TamableAnimal){
						((TamableAnimal) created).tame(player);
					}else if(created instanceof AbstractHorse){
						((AbstractHorse) created).tameWithName(player);
					}else if(created instanceof Mob && OFFSPRING_SPAWNING_METHOD != null){
						//As of vanilla MC1.16.5, this is literally only applicable to foxes
						try{
							OFFSPRING_SPAWNING_METHOD.invoke(created, player, created);
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
		template.setEntityName(source.getType().getRegistryName());
		template.setRespawning(source.getPersistentData().getBoolean(RESPAWNING_KEY));
		template.setLoyal(source.getPersistentData().getBoolean(LOYAL_KEY));

		Collection<MobEffectInstance> effects = source.getActiveEffects();
		int degrade = 0;
		ArrayList<MobEffectInstance> permanentEffects = new ArrayList<>(0);
		for(MobEffectInstance instance : effects){
			if(CRPotions.HEALTH_PENALTY_EFFECT.getRegistryName().equals(instance.getEffect().getRegistryName())){
				//This is the health penalty, interpret as degradation
				degrade += (instance.getAmplifier() + 1) / 2;//We divide by 2, as degradation is measured in hearts
			}else if(!instance.getEffect().isInstantenous() && instance.getDuration() > CRPotions.PERM_EFFECT_CUTOFF){
				permanentEffects.add(new MobEffectInstance(instance));//Copy the value to prevent changes in the mutable instance
			}
		}
		template.setDegradation(degrade);
		template.setEffects(permanentEffects);

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
