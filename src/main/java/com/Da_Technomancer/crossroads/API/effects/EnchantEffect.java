package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantEffect extends BeamEffect{

	private static final Random RAND = new Random();

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			int range = (int) Math.sqrt(power) / 2;
			ArrayList<ItemEntity> items = (ArrayList<ItemEntity>) worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range + 1, range + 1, range + 1)), EntityPredicates.IS_ALIVE);
			if(voi){
				if(items.size() != 0){
					for(ItemEntity ent : items){
						if(ent.getItem().getTag() != null && ent.getItem().getTag().contains("ench")){
							if(ent.getItem().getItem() == Items.ENCHANTED_BOOK){
								ent.setItem(new ItemStack(Items.BOOK, ent.getItem().getCount()));
							}else{
								ent.getItem().getTag().remove("ench");
							}
						}
					}
				}
			}else{
				if(items.size() != 0){
					for(ItemEntity ent : items){
						ItemStack stack = ent.getItem();

						if(stack.isEnchanted()){
							continue;
						}

						for(int i = 0; i < stack.getCount(); i++){
							ItemStack created;

							List<EnchantmentData> ench = EnchantmentHelper.buildEnchantmentList(RAND, stack, Math.min(power, 45), power >= 64);

							if(ench.isEmpty()){
								break;//Non-enchantable items shouldn't have their stacks recreated
							}

							if(stack.getItem() == Items.BOOK){
								created = new ItemStack(Items.ENCHANTED_BOOK, 1);
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
		}
	}
}
