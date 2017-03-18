package com.Da_Technomancer.crossroads.API.effects;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.Main;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class PlaceEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		ArrayList<EntityItem> items = (ArrayList<EntityItem>) worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-mult, -mult, -mult), pos.add(mult, mult, mult)), EntitySelectors.IS_ALIVE);
		if(items != null && items.size() != 0){
			FakePlayer placer = FakePlayerFactory.get((WorldServer) worldIn, new GameProfile(null, Main.MODID + "-place_effect-" + worldIn.provider.getDimension()));
			for(EntityItem ent : items){
				if(ent.getEntityItem() != null && ent.getEntityItem().getItem() instanceof ItemBlock && ((ItemBlock) ent.getEntityItem().getItem()).getBlock().canPlaceBlockAt(worldIn, ent.getPosition())){
					worldIn.setBlockState(ent.getPosition(), ((ItemBlock) ent.getEntityItem().getItem()).getBlock().getStateForPlacement(worldIn, ent.getPosition(), EnumFacing.DOWN, 0, 0, 0, ent.getEntityItem().getMetadata(), placer, EnumHand.MAIN_HAND));
					Block block = ((ItemBlock) ent.getEntityItem().getItem()).getBlock();
					block.onBlockPlacedBy(worldIn, ent.getPosition(), worldIn.getBlockState(ent.getPosition()), placer, ent.getEntityItem());
					ent.getEntityItem().shrink(1);
					if(ent.getEntityItem().getCount() <= 0){
						ent.setDead();
					}
				}
			}
		}
	}

	public static class BreakEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			worldIn.destroyBlock(pos, true);
		}
	}
}