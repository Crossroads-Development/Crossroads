package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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
						ItemStack stack = ent.getItem();
						if(stack.getTag() != null && (stack.getItem() == Items.ENCHANTED_BOOK || stack.getTag().contains("Enchantments"))){
							if(stack.getItem() == Items.ENCHANTED_BOOK){
								ent.setItem(new ItemStack(Items.BOOK, stack.getCount()));
							}else{
								stack.getTag().remove("Enchantments");
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

						List<EnchantmentData> ench = EnchantmentHelper.buildEnchantmentList(RAND, stack, Math.min(power, 45), power >= 64);

						if(ench.isEmpty()){
							//Non-enchantable items should be skipped
							continue;
						}

//						for(int i = 0; i < stack.getCount(); i++){
						ItemStack created;

						if(CRConfig.enchantDestruction.get() && RAND.nextInt(100) < power){
							//Destroy the item
							created = ItemStack.EMPTY;
							worldIn.addParticle(ParticleTypes.SMOKE, ent.getPosX(), ent.getPosY(), ent.getPosZ(), 0, 0, 0);
							worldIn.playSound(null, ent.getPosX(), ent.getPosY(), ent.getPosZ(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 1, 1);
						}else{
							if(stack.getItem() == Items.BOOK){
								created = new ItemStack(Items.ENCHANTED_BOOK, 1);
								if(ench.size() > 1){
									ench.remove(0);//Vanilla behavior when enchanting books is to put on 1 fewer enchantments
								}
							}else{
								created = stack.copy();
								created.setCount(1);
							}

							for(EnchantmentData datum : ench){
								if(created.getItem() == Items.ENCHANTED_BOOK){
									EnchantedBookItem.addEnchantment(created, datum);
								}else{
									created.addEnchantment(datum.enchantment, datum.enchantmentLevel);
								}
							}
						}

						InventoryHelper.spawnItemStack(worldIn, ent.getPosX(), ent.getPosY(), ent.getPosZ(), created);
						ent.getItem().shrink(1);
						if(ent.getItem().isEmpty()){
							ent.remove();
						}
						return;//Only enchant 1 item
					}
				}
			}
		}
	}
}
