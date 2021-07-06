package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BloodSample extends Item implements IPerishable{

	private static final long LIFETIME = 20 * 60 * 60 * 2;
	private static final int PERM_EFFECT_CUTOFF = Integer.MAX_VALUE / 4;//We assume any effect on a mob over this duration was originally a permanent effect; this is not a flawless method
	private static final String KEY = "cr_genetics";

	public BloodSample(){
		super(new Item.Properties().stacksTo(1));//Not added to any creative tab
		String name = "blood_sample";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	public ItemStack withEntityData(ItemStack stack, LivingEntity source){
		EntityTemplate template = new EntityTemplate();
		template.setEntityName(source.getType().getRegistryName());
		template.setRespawning(source.getPersistentData().getBoolean(EntityTemplate.RESPAWNING_KEY));
		template.setLoyal(source.getPersistentData().getBoolean(EntityTemplate.LOYAL_KEY));

		Collection<EffectInstance> effects = source.getActiveEffects();
		int degrade = 0;
		ArrayList<EffectInstance> permanentEffects = new ArrayList<>(0);
		for(EffectInstance instance : effects){
			if(CRPotions.HEALTH_PENALTY_EFFECT.getRegistryName().equals(instance.getEffect().getRegistryName())){
				//This is the health penalty, interpret as degradation
				degrade += instance.getAmplifier() + 1;
			}else if(!instance.getEffect().isInstantenous() && instance.getDuration() > PERM_EFFECT_CUTOFF){
				permanentEffects.add(new EffectInstance(instance));//Copy the value to prevent changes in the mutable instance
			}
		}
		template.setDegradation(degrade);
		template.setEffects(permanentEffects);

		stack.getOrCreateTag().put(KEY, template.serializeNBT());
		return stack;
	}

	public EntityTemplate getEntityTypeData(ItemStack stack){
		CompoundNBT nbt = stack.getOrCreateTag();
		EntityTemplate template = new EntityTemplate();
		template.deserializeNBT(nbt.getCompound(KEY));
		return template;
	}

	@Override
	public long getLifetime(){
		return LIFETIME;
	}

	@Override
	public double getFreezeTemperature(){
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag){
		EntityTemplate template = getEntityTypeData(stack);
		template.addTooltip(tooltip, 2);
		IPerishable.addTooltip(stack, world, tooltip);
	}
}
