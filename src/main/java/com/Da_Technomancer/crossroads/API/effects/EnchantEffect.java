package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantEffect implements IEffect{

	private static final Random RAND = new Random();
	
	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
		ArrayList<EntityItem> items = (ArrayList<EntityItem>) worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-mult, -mult, -mult), pos.add(mult, mult, mult)), EntitySelectors.IS_ALIVE);
		if(items.size() != 0){
			for(EntityItem ent : items){
				if(ent.getItem().isItemEnchanted()){
					continue;
				}

				ItemStack stack = ent.getItem();
				for(int i = 0; i < stack.getCount(); i++){
					ItemStack created = ItemStack.EMPTY;

					List<EnchantmentData> ench = EnchantmentHelper.buildEnchantmentList(RAND, stack, (int) Math.min(mult, 45), mult >= 64);

					if(stack.getItem() == Items.BOOK){
						created = new ItemStack(Items.ENCHANTED_BOOK, 1);
					}

					if(created.getItem() == Items.ENCHANTED_BOOK && ench.size() > 1){
						//Vanilla behavior when enchanting books is to put on 1 fewer enchantments
						ench.remove(0);
					}

					for(EnchantmentData datum : ench){
						if(created.getItem() == Items.ENCHANTED_BOOK){
							ItemEnchantedBook.addEnchantment(created, datum);
						}else{
							created.addEnchantment(datum.enchantment, datum.enchantmentLevel);
						}
					}

					InventoryHelper.spawnItemStack(worldIn, ent.posX, ent.posY, ent.posZ, created);
					ent.setDead();
				}
			}
		}
	}

	public static class DisenchantEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
			ArrayList<EntityItem> items = (ArrayList<EntityItem>) worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-mult, -mult, -mult), pos.add(mult, mult, mult)), EntitySelectors.IS_ALIVE);
			if(items.size() != 0){
				for(EntityItem ent : items){
					if(ent.getItem().getTagCompound() != null && ent.getItem().getTagCompound().hasKey("ench")){
						ent.getItem().getTagCompound().removeTag("ench");
						if(ent.getItem().getItem() == Items.ENCHANTED_BOOK){
							ent.setItem(new ItemStack(Items.BOOK, ent.getItem().getCount()));
						}
					}
				}
			}
		}	
	}
}
