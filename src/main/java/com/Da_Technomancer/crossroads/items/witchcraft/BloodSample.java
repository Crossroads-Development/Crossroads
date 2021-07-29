package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BloodSample extends Item implements IPerishable{

	private static final long LIFETIME = 20 * 60 * 60 * 2;
	private static final String KEY = "cr_genetics";

	public BloodSample(){
		this("blood_sample");
	}

	public BloodSample(String name){
		super(new Item.Properties().stacksTo(1));//Not added to any creative tab
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	public ItemStack withEntityData(ItemStack stack, LivingEntity source){
		EntityTemplate template = EntityTemplate.getTemplateFromEntity(source);
		return withEntityData(stack, template);
	}

	public ItemStack withEntityData(ItemStack stack, EntityTemplate template){
		stack.getOrCreateTag().put(KEY, template.serializeNBT());
		return stack;
	}

	public static EntityTemplate getEntityTypeData(ItemStack stack){
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
		tooltip.add(new TranslationTextComponent("tt.crossroads.blood_sample.craft"));
		EntityTemplate template = getEntityTypeData(stack);
		template.addTooltip(tooltip, 2);
		IPerishable.addTooltip(stack, world, tooltip);
	}
}
