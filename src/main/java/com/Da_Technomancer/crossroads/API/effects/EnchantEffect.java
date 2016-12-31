package com.Da_Technomancer.crossroads.API.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnchantEffect implements IEffect{

	private final Random rand = new Random();
	
	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		mult = Math.min(mult, 45);
		ArrayList<EntityItem> items = (ArrayList<EntityItem>) worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-mult, -mult, -mult), pos.add(mult, mult, mult)), EntitySelectors.IS_ALIVE);
		if(items != null && items.size() != 0){
			for(EntityItem ent : items){
				List<EnchantmentData> ench = EnchantmentHelper.buildEnchantmentList(this.rand, ent.getEntityItem(), (int) mult, mult >= 32);
				
				if(ench == null || ent.getEntityItem().isItemEnchanted()){
					continue;
				}
				
				if(ent.getEntityItem().getItem() == Items.BOOK){
					ent.setEntityItemStack(new ItemStack(Items.ENCHANTED_BOOK, 1));
				}
				
				for(EnchantmentData datum : ench){
					if(ent.getEntityItem().getItem() == Items.ENCHANTED_BOOK){
						//While vanilla behavior when enchanting books is to put on 1 fewer enchantments, for the EnchantEffect this does not occur. THIS IS NOT A BUG.
						Items.ENCHANTED_BOOK.addEnchantment(ent.getEntityItem(), datum);
					}else{
						ent.getEntityItem().addEnchantment(datum.enchantmentobj, datum.enchantmentLevel);
					}
				}
			}
		}
	}

	public static class DisenchantEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			ArrayList<EntityItem> items = (ArrayList<EntityItem>) worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-mult, -mult, -mult), pos.add(mult, mult, mult)), EntitySelectors.IS_ALIVE);
			if(items != null && items.size() != 0){
				for(EntityItem ent : items){
					if(ent.getEntityItem().getTagCompound() != null && ent.getEntityItem().getTagCompound().hasKey("ench")){
						ent.getEntityItem().getTagCompound().removeTag("ench");
					}
				}
			}
		}	
	}
}
