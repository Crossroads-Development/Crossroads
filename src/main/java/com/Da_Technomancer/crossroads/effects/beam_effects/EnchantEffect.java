package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.List;

public class EnchantEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			int range = (int) Math.sqrt(power) / 2;
			List<ItemEntity> items = beamHit.getNearbyEntities(ItemEntity.class, range, null);
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
			}else if(items.size() != 0){
				for(ItemEntity ent : items){
					ItemStack stack = ent.getItem();

					if(stack.isEnchanted()){
						continue;
					}

					List<EnchantmentInstance> ench = EnchantmentHelper.selectEnchantment(beamHit.getWorld().random, stack, Math.min(power, 45), power >= 64);

					if(ench.isEmpty()){
						//Non-enchantable items should be skipped
						continue;
					}

//						for(int i = 0; i < stack.getCount(); i++){
					ItemStack created;

					if(CRConfig.enchantDestruction.get() && beamHit.getWorld().random.nextInt(100) < power){
						//Destroy the item
						created = ItemStack.EMPTY;
						beamHit.getWorld().addParticle(ParticleTypes.SMOKE, ent.getX(), ent.getY(), ent.getZ(), 0, 0, 0);
						beamHit.getWorld().playSound(null, ent.getX(), ent.getY(), ent.getZ(), SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 1, 1);
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

					Containers.dropItemStack(beamHit.getWorld(), ent.getX(), ent.getY(), ent.getZ(), created);
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
