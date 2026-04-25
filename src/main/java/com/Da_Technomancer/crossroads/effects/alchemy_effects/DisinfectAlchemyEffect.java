package com.Da_Technomancer.crossroads.effects.alchemy_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.api.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class DisinfectAlchemyEffect implements IAlchEffect{

	public static final TagKey<Block> SMALL_MUSHROOMS = CraftingUtil.getTagKey(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "small_mushroom"));

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		//Kill mushrooms
		BlockState state = world.getBlockState(pos);
		if(state.is(BlockTags.MOOSHROOMS_SPAWNABLE_ON)){
			//Mycelium
			world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
		}else if(state.is(SMALL_MUSHROOMS)){
			world.destroyBlock(pos, true);
		}else if(state.is(BlockTags.NYLIUM)){
			world.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
		}

		for(LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, AABB.encapsulatingFullBlocks(pos, pos), EntitySelector.ENTITY_STILL_ALIVE)){
			//Misc curing
			e.removeEffect(MobEffects.POISON);
			e.removeEffect(MobEffects.HUNGER);

			if(e instanceof ZombieVillager zomVill){
				//Cure zombie villagers
				zomVill.startConverting(null, 4000);
			}else if(e instanceof MushroomCow mooshroom){
				//De-mushroom mooshrooms
				mooshroom.shear(SoundSource.BLOCKS);
			}
		}
	}

	@Override
	public Component getName(){
		return Component.translatable("effect.disinfect");
	}
}
