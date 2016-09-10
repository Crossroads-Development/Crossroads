package com.Da_Technomancer.crossroads.API.effects;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlaceEffect implements IEffect{

	@SuppressWarnings("deprecation")
	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		ArrayList<EntityItem> items = (ArrayList<EntityItem>) worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-mult, -mult, -mult), pos.add(mult, mult, mult)), EntitySelectors.IS_ALIVE);
		if(items != null && items.size() != 0){
			for(EntityItem ent : items){
				if(ent.getEntityItem() != null && ent.getEntityItem().getItem() instanceof ItemBlock && worldIn.isAirBlock(ent.getPosition())){
					worldIn.setBlockState(ent.getPosition(), ((ItemBlock) ent.getEntityItem().getItem()).getBlock().getStateFromMeta(ent.getEntityItem().getMetadata()));
					if(--ent.getEntityItem().stackSize <= 0){
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