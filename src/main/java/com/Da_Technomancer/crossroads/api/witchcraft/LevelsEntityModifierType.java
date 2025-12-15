package com.Da_Technomancer.crossroads.api.witchcraft;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class LevelsEntityModifierType implements IEntityModifierType<LevelsEntityModifierType.LeveledEntityModifier>{

	private final String id;
	private final int maxLevel;
	private final BiFunction<Entity, Integer, Entity> applyFromLevel;
	private final Codec<IEntityModifier> codec;
	private final StreamCodec<ByteBuf, IEntityModifier> streamCodec;
	private final MutableComponent baseDescription;

	public LevelsEntityModifierType(String id, int maxLevel, BiFunction<Entity, Integer, Entity> applyFromLevel, Component baseDescription){
		this.id = id;
		this.maxLevel = maxLevel;
		assert maxLevel > 0;
		this.applyFromLevel = applyFromLevel;
		this.codec = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("complexity").forGetter(IEntityModifier::complexity), Codec.INT.fieldOf("soul_complexity").forGetter(IEntityModifier::soulComplexity), Codec.INT.fieldOf("level").forGetter((IEntityModifier entMod) -> entMod instanceof LeveledEntityModifier levelMod ? levelMod.level : 1)).apply(instance, this::createModifier));
		this.streamCodec = StreamCodec.composite(ByteBufCodecs.VAR_INT, IEntityModifier::complexity, ByteBufCodecs.VAR_INT, IEntityModifier::soulComplexity, ByteBufCodecs.VAR_INT, (IEntityModifier entMod) -> entMod instanceof LeveledEntityModifier levelMod ? levelMod.level : 1, this::createModifier);
		this.baseDescription = MiscUtil.asMutable(baseDescription);
	}

	@Override
	public Codec<IEntityModifier> modifierCodec(){
		return codec;
	}

	@Override
	public StreamCodec<ByteBuf, IEntityModifier> modifierStreamCodec(){
		return streamCodec;
	}

	@Nonnull
	@Override
	public String getModifierID(){
		return id;
	}

	@Nonnull
	@Override
	public LeveledEntityModifier mergeModifiers(@Nonnull IEntityModifier oldModifier, @Nonnull IEntityModifier newModifier){
		if(newModifier instanceof LeveledEntityModifier newModifierLeveled && newModifierLeveled.type == this){
			if(oldModifier instanceof LeveledEntityModifier oldModifierLeveled && oldModifierLeveled.type == this){
				int newLevel = oldModifierLeveled.level + newModifierLeveled.level;
				newLevel = Math.min(newLevel, maxLevel);
				float levelScale = (float) (newLevel - oldModifierLeveled.level) / newModifierLeveled.level;
				return createModifier(oldModifierLeveled.complexity + Math.round(levelScale * newModifierLeveled.complexity), oldModifierLeveled.soulComplexity + Math.round(levelScale * newModifierLeveled.soulComplexity), newLevel);
			}
			//Should never happen
			return newModifierLeveled;
		}
		//Should REALLY never happen
		assert false;
		return null;
	}

	private LeveledEntityModifier createModifier(int complexity, int soulComplexity, int level){
		level = Math.max(1, Math.min(level, maxLevel));
		return new LeveledEntityModifier(this, complexity, soulComplexity, level);
	}

	@Nonnull
	@Override
	public LeveledEntityModifier createModifier(ItemStack craftingItem, int complexity, int soulComplexity){
		return createModifier(complexity, soulComplexity, 1);
	}

	public record LeveledEntityModifier(LevelsEntityModifierType type, int complexity, int soulComplexity, int level) implements IEntityModifier{

		@Override
		public Entity apply(Entity entity){
			return type.applyFromLevel.apply(entity, level);
		}

		@Override
		public Component getName(@Nullable EntityType<?> entityType, @Nullable Level world){
			if(type.maxLevel == 1){
				return type.baseDescription;
			}
			return type.baseDescription.copy().append(Component.translatable("enchantment.level." + level));
		}
	}
}
