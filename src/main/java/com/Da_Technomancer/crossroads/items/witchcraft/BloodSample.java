package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class BloodSample extends Item implements IPerishable{

	private static final long LIFETIME = 20 * 60 * 60 * 2;
	private static final String KEY = "cr_genetics";

	public BloodSample(){
		super(new Item.Properties().stacksTo(1));//Not added to any creative tab
		String name = "blood_sample";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	public ItemStack withEntityData(ItemStack stack, LivingEntity source){
		CompoundNBT nbt = stack.getOrCreateTagElement(KEY);
		nbt.putString("entity_type", source.getType().getRegistryName().toString());
		nbt.putUUID("entity_uuid", source.getUUID());
		return stack;
	}

	@Nullable
	public ResourceLocation getEntityTypeData(ItemStack stack){
		CompoundNBT nbt = stack.getTagElement(KEY);
		if(nbt != null && nbt.contains("entity_type")){
			return new ResourceLocation(nbt.getString("entity_type"));
		}
		return null;
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
		ResourceLocation entityRegName = getEntityTypeData(stack);
		EntityType<?> type = entityRegName == null ? null : ForgeRegistries.ENTITIES.getValue(entityRegName);
		if(entityRegName == null){
			tooltip.add(new TranslationTextComponent("tt.crossroads.blood_sample.type.missing"));
		}else{
			tooltip.add(new TranslationTextComponent("tt.crossroads.blood_sample.type").append(type == null ? new StringTextComponent(entityRegName.toString()) : type.getDescription()));
		}
		IPerishable.addTooltip(stack, world, tooltip);
	}
}
