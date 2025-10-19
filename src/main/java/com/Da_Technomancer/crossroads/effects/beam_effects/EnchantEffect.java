package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.List;
import java.util.Optional;

public class EnchantEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			int range = (int) Math.sqrt(power) / 2;
			List<ItemEntity> items = beamHit.getNearbyEntities(ItemEntity.class, range, null);
			if(voi){
				if(!items.isEmpty()){
					for(ItemEntity ent : items){
						ItemStack stack = ent.getItem();
						stack.remove(DataComponents.ENCHANTMENTS);
						stack.remove(DataComponents.STORED_ENCHANTMENTS);
						if(stack.getItem() == Items.ENCHANTED_BOOK){
							stack = stack.transmuteCopy(Items.BOOK);
						}
						ent.setItem(stack);
					}
				}
			}else if(!items.isEmpty()){
				for(ItemEntity ent : items){
					ItemStack entStack = ent.getItem();

					if(entStack.isEnchanted()){
						//Skip already enchanted items
						continue;
					}

					ItemStack created = entStack.split(1);
					if(entStack.isEmpty()){
						ent.remove(Entity.RemovalReason.DISCARDED);
					}

					RandomSource random = beamHit.getWorld().getRandom();
					Optional<HolderSet.Named<Enchantment>> allowedEnchantSet;
					if(power >= 64){
						//Allows some treasure enchants, but not the exclusive ones like Swift Sneak
						allowedEnchantSet = beamHit.getWorld().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.ON_RANDOM_LOOT);
					}else{
						allowedEnchantSet = beamHit.getWorld().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.IN_ENCHANTING_TABLE);
					}

					if(allowedEnchantSet.isEmpty()){
						return;//Something is wrong
					}
					List<EnchantmentInstance> ench = EnchantmentHelper.selectEnchantment(random, created, Math.min(power, 45), allowedEnchantSet.get().stream());
					if(created.is(Items.BOOK) && ench.size() > 1){
						ench.remove(random.nextInt(ench.size()));
					}

					if(ench.isEmpty()){
						//Skip non-enchantable items
						continue;
					}

					if(CRConfig.enchantDestruction.get() && beamHit.getWorld().random.nextInt(100) < power){
						//Destroy the item
						beamHit.getWorld().addParticle(ParticleTypes.SMOKE, ent.getX(), ent.getY(), ent.getZ(), 0, 0, 0);
						beamHit.getWorld().playSound(null, ent.getX(), ent.getY(), ent.getZ(), SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 1, 1);
						return;
					}


					if(created.getItem() == Items.BOOK){
						created = new ItemStack(Items.ENCHANTED_BOOK, 1);
						if(ench.size() > 1){
							ench.remove(0);//Vanilla behavior when enchanting books is to put on 1 fewer enchantments
						}
					}

					created = created.getItem().applyEnchantments(created, ench);
					Containers.dropItemStack(beamHit.getWorld(), ent.getX(), ent.getY(), ent.getZ(), created);
					return;
				}
			}
		}
	}
}
