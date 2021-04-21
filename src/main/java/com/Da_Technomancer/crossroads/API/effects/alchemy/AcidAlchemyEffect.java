package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

public class AcidAlchemyEffect implements IAlchEffect{

	public static final DamageSource ACID_DAMAGE = new DamageSource("chemical");

	private static final ITag<Block> copperBlock = BlockTags.bind("forge:storage_blocks/copper");
	private static final ITag<Block> tinBlock = BlockTags.bind("forge:storage_blocks/tin");
	private static final ITag<Block> bronzeBlock = BlockTags.bind("forge:storage_blocks/bronze");

	protected int getDamage(){
		return 8;
	}

	protected boolean isRegia(){
		return false;
	}

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1F, pos.getY() + 1F, pos.getZ() + 1F), EntityPredicates.ENTITY_STILL_ALIVE)){
			e.hurt(ACID_DAMAGE, getDamage());
		}

		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == Blocks.BEDROCK && isRegia()){
			InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(CRItems.bedrockDust, 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}

		ItemStack itemForm = new ItemStack(state.getBlock());
		if(itemForm.isEmpty()){
			return;
		}

		Block block = state.getBlock();
		if(Tags.Blocks.STORAGE_BLOCKS_IRON.contains(block)){
			InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_INGOT, world.random.nextInt(9) + 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}
		if(Tags.Blocks.STORAGE_BLOCKS_GOLD.contains(block)){
			if(isRegia()){
				InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GOLD_INGOT, world.random.nextInt(9) + 1));
				world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			}
			return;
		}
		if(copperBlock.contains(block)){
			InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotCopper, world.random.nextInt(9) + 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}
		if(tinBlock.contains(block)){
			InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotTin, world.random.nextInt(9) + 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}
		if(bronzeBlock.contains(block)){
			InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotBronze, world.random.nextInt(9) + 1));
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}

	@Override
	public ITextComponent getName(){
		return new TranslationTextComponent("effect.acid");
	}
}
