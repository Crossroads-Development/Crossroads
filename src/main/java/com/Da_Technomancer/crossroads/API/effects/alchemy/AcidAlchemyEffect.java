package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

public class AcidAlchemyEffect implements IAlchEffect{

	public static final DamageSource ACID_DAMAGE = new DamageSource("chemical");

	private static final TagKey<Block> copperBlock = CRItemTags.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation("forge", "storage_blocks/copper"));
	private static final TagKey<Block> tinBlock = CRItemTags.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation("forge", "storage_blocks/tin"));
	private static final TagKey<Block> bronzeBlock = CRItemTags.getTagKey(ForgeRegistries.Keys.BLOCKS, new ResourceLocation("forge", "storage_blocks/bronze"));

	protected int getDamage(){
		return 8;
	}

	protected boolean isRegia(){
		return false;
	}

	@Override
	public void doEffect(Level world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1F, pos.getY() + 1F, pos.getZ() + 1F), EntitySelector.ENTITY_STILL_ALIVE)){
			e.hurt(ACID_DAMAGE, getDamage());
		}

		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == Blocks.BEDROCK && isRegia()){
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(CRItems.bedrockDust, 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}

		ItemStack itemForm = new ItemStack(state.getBlock());
		if(itemForm.isEmpty()){
			return;
		}

		Block block = state.getBlock();
		if(CRItemTags.tagContains(Tags.Blocks.STORAGE_BLOCKS_IRON, block)){
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_INGOT, world.random.nextInt(9) + 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}
		if(CRItemTags.tagContains(Tags.Blocks.STORAGE_BLOCKS_GOLD, block)){
			if(isRegia()){
				Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GOLD_INGOT, world.random.nextInt(9) + 1));
				world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
			return;
		}
		if(CRItemTags.tagContains(copperBlock, block)){
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.COPPER_INGOT, world.random.nextInt(9) + 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}
		if(CRItemTags.tagContains(tinBlock, block)){
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotTin, world.random.nextInt(9) + 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}
		if(CRItemTags.tagContains(bronzeBlock, block)){
			Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotBronze, world.random.nextInt(9) + 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public Component getName(){
		return new TranslatableComponent("effect.acid");
	}
}
