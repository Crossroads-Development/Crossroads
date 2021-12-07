package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantEffect extends BeamEffect{

	private static final Random RAND = new Random();

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			int range = (int) Math.sqrt(power) / 2;
			ArrayList<ItemEntity> items = (ArrayList<ItemEntity>) worldIn.getEntitiesOfClass(ItemEntity.class, new AABB(pos.offset(-range, -range, -range), pos.offset(range + 1, range + 1, range + 1)), EntitySelector.ENTITY_STILL_ALIVE);
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

						List<EnchantmentInstance> ench = EnchantmentHelper.selectEnchantment(RAND, stack, Math.min(power, 45), power >= 64);

						if(ench.isEmpty()){
							//Non-enchantable items should be skipped
							continue;
						}

//						for(int i = 0; i < stack.getCount(); i++){
						ItemStack created;

						if(CRConfig.enchantDestruction.get() && RAND.nextInt(100) < power){
							//Destroy the item
							created = ItemStack.EMPTY;
							worldIn.addParticle(ParticleTypes.SMOKE, ent.getX(), ent.getY(), ent.getZ(), 0, 0, 0);
							worldIn.playSound(null, ent.getX(), ent.getY(), ent.getZ(), SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 1, 1);
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

							for(EnchantmentInstance datum : ench){
								if(created.getItem() == Items.ENCHANTED_BOOK){
									EnchantedBookItem.addEnchantment(created, datum);
								}else{
									created.enchant(datum.enchantment, datum.level);
								}
							}
						}

						Containers.dropItemStack(worldIn, ent.getX(), ent.getY(), ent.getZ(), created);
						ent.getItem().shrink(1);
						if(ent.getItem().isEmpty()){
							ent.remove(Entity.RemovalReason.DISCARDED);
						}
						return;//Only enchant 1 item
					}
				}
			}
		}
	}
}
