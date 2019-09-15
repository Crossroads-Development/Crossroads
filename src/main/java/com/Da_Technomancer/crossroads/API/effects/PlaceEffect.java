package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;

public class PlaceEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		List<ItemEntity> items = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.add(-mult, -mult, -mult), pos.add(mult, mult, mult)), EntityPredicates.IS_ALIVE);
		if(items.size() != 0){
			FakePlayer placer = FakePlayerFactory.get((ServerWorld) worldIn, new GameProfile(null, Crossroads.MODID + "-place_effect-" + worldIn.provider.getDimension()));
			for(ItemEntity ent : items){
				if(!ent.getItem().isEmpty() && ent.getItem().getItem() instanceof BlockItem && ((BlockItem) ent.getItem().getItem()).getBlock().canPlaceBlockAt(worldIn, ent.getPosition())){
					worldIn.setBlockState(ent.getPosition(), ((BlockItem) ent.getItem().getItem()).getBlock().getStateForPlacement(worldIn, ent.getPosition(), Direction.DOWN, 0, 0, 0, ent.getItem().getMetadata(), placer, Hand.MAIN_HAND));
					Block block = ((BlockItem) ent.getItem().getItem()).getBlock();
					block.onBlockPlacedBy(worldIn, ent.getPosition(), worldIn.getBlockState(ent.getPosition()), placer, ent.getItem());
					SoundType soundtype = block.getSoundType(worldIn.getBlockState(pos), worldIn, pos, null);
					worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					ent.getItem().shrink(1);
					if(ent.getItem().getCount() <= 0){
						ent.remove();
					}
				}
			}
		}
	}

	public static class BreakEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
			String[] bannedBlocks = CrossroadsConfig.getConfigStringList(CrossroadsConfig.destroyBlacklist, false);
			String id = worldIn.getBlockState(pos).getBlock().getRegistryName().toString();
			for(String s : bannedBlocks){
				if(s.equals(id)){
					return;
				}
			}
			worldIn.destroyBlock(pos, true);
		}
	}
}