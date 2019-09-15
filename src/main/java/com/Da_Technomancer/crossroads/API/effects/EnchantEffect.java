package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantEffect implements IEffect{

	private static final Random RAND = new Random();
	
	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		int range = Math.min(mult, 8);
		ArrayList<ItemEntity> items = (ArrayList<ItemEntity>) worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)), EntityPredicates.IS_ALIVE);
		if(items.size() != 0){
			for(ItemEntity ent : items){
				ItemStack stack = ent.getItem();

				if(stack.isItemEnchanted()){
					continue;
				}

				for(int i = 0; i < stack.getCount(); i++){
					ItemStack created;

					List<EnchantmentData> ench = EnchantmentHelper.buildEnchantmentList(RAND, stack, (int) Math.min(mult, 45), mult >= 64);

					if(ench.isEmpty()){
						break;//Non-enchantable items shouldn't have their stacks recreated
					}

					if(stack.getItem() == Items.BOOK){
						created = new ItemStack(Items.ENCHANTED_BOOK, 1, 0);
					}else{
						created = stack.copy();
						created.setCount(1);
					}

					if(created.getItem() == Items.ENCHANTED_BOOK && ench.size() > 1){
						//Vanilla behavior when enchanting books is to put on 1 fewer enchantments
						ench.remove(0);
					}

					for(EnchantmentData datum : ench){
						if(created.getItem() == Items.ENCHANTED_BOOK){
							EnchantedBookItem.addEnchantment(created, datum);
						}else{
							created.addEnchantment(datum.enchantment, datum.enchantmentLevel);
						}
					}

					InventoryHelper.spawnItemStack(worldIn, ent.posX, ent.posY, ent.posZ, created);
					ent.remove();
				}
			}
		}
	}

	public static class DisenchantEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
			mult = Math.min(mult, 8);
			ArrayList<ItemEntity> items = (ArrayList<ItemEntity>) worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.add(-mult, -mult, -mult), pos.add(mult, mult, mult)), EntityPredicates.IS_ALIVE);
			if(items.size() != 0){
				for(ItemEntity ent : items){
					if(ent.getItem().getTagCompound() != null && ent.getItem().getTagCompound().hasKey("ench")){
						if(ent.getItem().getItem() == Items.ENCHANTED_BOOK){
							ent.setItem(new ItemStack(Items.BOOK, ent.getItem().getCount()));
						}else{
							ent.getItem().getTagCompound().removeTag("ench");
						}
					}
				}
			}
		}	
	}
}
