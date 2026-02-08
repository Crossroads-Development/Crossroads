package com.Da_Technomancer.crossroads.effects.entity_modifiers;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRReflection;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifier;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifierType;
import com.Da_Technomancer.crossroads.api.witchcraft.SimpleEntityModifierType;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.ReflectionUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public record ImprintingEntityModifier(int complexity, int soulComplexity, ResolvableProfile owner) implements IEntityModifier{

	private static final ResolvableProfile FALLBACK_PROFILE = new ResolvableProfile(new GameProfile(UUID.randomUUID(), "Use player blood sample"));

	private static final Codec<IEntityModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("complexity").forGetter(IEntityModifier::complexity), Codec.INT.fieldOf("soul_complexity").forGetter(IEntityModifier::soulComplexity), ResolvableProfile.CODEC.fieldOf("profile").forGetter((IEntityModifier entMod) -> entMod instanceof ImprintingEntityModifier imEntMod ? imEntMod.owner() : FALLBACK_PROFILE)).apply(instance, ImprintingEntityModifier::new));
	private static final StreamCodec<ByteBuf, IEntityModifier> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, IEntityModifier::complexity, ByteBufCodecs.VAR_INT, IEntityModifier::soulComplexity, ResolvableProfile.STREAM_CODEC, (IEntityModifier entMod) -> entMod instanceof ImprintingEntityModifier imEntMod ? imEntMod.owner() : FALLBACK_PROFILE, ImprintingEntityModifier::new);

	public static final IEntityModifierType<ImprintingEntityModifier> TYPE_INSTANCE = new SimpleEntityModifierType<>("imprinting", ImprintingEntityModifier::new, CODEC, STREAM_CODEC);


	private ImprintingEntityModifier(ItemStack stack, int complexity, int soulComplexity){
		this(complexity, soulComplexity, stack.has(CRItems.ENTITY_SOURCE_DATA) ? stack.get(CRItems.ENTITY_SOURCE_DATA).playerProfile().orElse(FALLBACK_PROFILE) : FALLBACK_PROFILE);
	}

//	private static final Method OFFSPRING_SPAWNING_METHOD = ReflectionUtil.reflectMethod(CRReflection.FOX_TRUSTED_UUID);

	@Override
	public Entity apply(Entity entity){
		if(owner == null){
			return entity;
		}
		//Game profiles are a bit clunky
		//Depending on how reliable the authentification servers are being this week, etc, we might not be able to trust UUIDs
		//Also, if the player in question isn't logged in, can't get a player instance either
		//Try our best and blame Mojang for anything that goes wrong
		Player tamingPlayer = null;
		UUID tamingUUID = null;
		if(owner.id().isPresent()){
			tamingUUID = owner.id().get();
			tamingPlayer = entity.level().getPlayerByUUID(tamingUUID);
		}
		if(tamingPlayer == null && owner.name().isPresent()){
			String playerName = owner.name().get();
			for(Player player : entity.level().players()){
				if(playerName.equals(player.getScoreboardName())){
					tamingPlayer = player;
					tamingUUID = tamingPlayer.getUUID();
					break;
				}
			}
		}

		//Auto-tame it to the player
		//There isn't a single method for this. The correct way to set something as tamed varies based on the mob
		//New vanilla tamable mobs may require changes here, and modded tameable mobs are unlikely to work
		if(entity instanceof TamableAnimal animal){
			animal.setTame(true, false);
			if(tamingUUID != null){
				animal.setOwnerUUID(tamingUUID);
			}
		}
		if(entity instanceof AbstractHorse horse){
			if(tamingUUID != null){
				horse.setOwnerUUID(tamingUUID);
				horse.setTamed(true);
			}
		}
		if(tamingUUID != null && entity instanceof Fox mob){
			//As of vanilla MC1.16.5, this is literally only applicable to foxes
			mob.addTrustedUUID(tamingUUID);
//			try{
//				OFFSPRING_SPAWNING_METHOD.invoke(mob, tamingUUID);
//			}catch(IllegalAccessException | InvocationTargetException e){
//				Crossroads.logger.catching(e);
//			}
		}

		return entity;
	}

	@Override
	public Component getName(@Nullable EntityType<?> entityType, @Nullable Level level){
		if(owner != null && owner.name().isPresent()){
			return Component.translatable("ent_mod.imprinting.named", owner.name().get());
		}
		return Component.translatable("ent_mod.imprinting.unnamed");
	}
}
