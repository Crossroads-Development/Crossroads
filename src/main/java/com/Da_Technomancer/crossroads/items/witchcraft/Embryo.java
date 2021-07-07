package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.API.witchcraft.ICultivatable;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Embryo extends Item implements ICultivatable{

	private static final long LIFETIME = 30 * 60 * 20;//30 minutes
	private static final String KEY = "cr_genetics";
	private static final int FREEZE_DEGRADE = 1;

	public Embryo(){
		super(new Item.Properties().stacksTo(1));//Not added to any creative tab
		String name = "embryo";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public long getLifetime(){
		return LIFETIME;
	}

	@Override
	public double getFreezeTemperature(){
		return 0;
	}

	/**
	 * Writes the template and freeze history to the stack
	 * The passed template may be modified
	 * @param stack The stack to write to
	 * @param template The template, as would be returned from getEntityTypeData
	 * @param wasFrozen Whether this item should have been frozen in the past
	 */
	public void withEntityTypeData(ItemStack stack, EntityTemplate template, boolean wasFrozen){
		CompoundNBT nbt = stack.getOrCreateTag();
		setWasFrozen(stack, wasFrozen);
		if(wasFrozen){
			//getEntityTypeData inflates the degradation value for frozen items. We account for this here
			template.setDegradation(template.getDegradation() - FREEZE_DEGRADE);
		}
		nbt.put(KEY, template.serializeNBT());
	}

	public EntityTemplate getEntityTypeData(ItemStack stack){
		CompoundNBT nbt = stack.getOrCreateTag();
		EntityTemplate template = new EntityTemplate();
		template.deserializeNBT(nbt.getCompound(KEY));

		//If this was frozen, increase degradation
		//Do not modify the underlying saved template
		if(wasFrozen(stack)){
			template.setDegradation(template.getDegradation() + FREEZE_DEGRADE);
		}

		return template;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag){
		EntityTemplate template = getEntityTypeData(stack);
		template.addTooltip(tooltip, 2);
		ICultivatable.addTooltip(stack, world, tooltip);
	}

	@Nullable
	@Override
	public CultivationTrade getCultivationTrade(ItemStack self, World world){
		//Produces (near)-copies of itself, using soul clusters &/or genetic plasmids as applicable
		
		if(isSpoiled(self, world)){
			return null;
		}
		ItemStack created = new ItemStack(this, 1);
		EntityTemplate template = getEntityTypeData(self);
		//The new item carries over the degradation due to freezing, but can be further damaged by freezing
		withEntityTypeData(created, template, false);

		ItemStack ingr1 = ItemStack.EMPTY;
		ItemStack ingr2 = ItemStack.EMPTY;
		if(template.getEffects().size() > 0){
			//Require as many genetic plasmids as there are effects applied
			ingr1 = new ItemStack(CRItems.potionExtension, template.getEffects().size());
		}
		if(template.isRespawning()){
			if(ingr1.isEmpty()){
				ingr1 = new ItemStack(CRItems.soulCluster);
			}else{
				ingr2 = new ItemStack(CRItems.soulCluster);
			}
		}

		return new CultivationTrade(ingr1, ingr2, created);
	}
}
