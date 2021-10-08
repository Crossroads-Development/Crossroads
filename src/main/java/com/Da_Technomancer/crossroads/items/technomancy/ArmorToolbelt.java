package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ArmorToolbelt extends TechnomancyArmor{

	public ArmorToolbelt(){
		super(EquipmentSlot.LEGS);
		String name = "toolbelt";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslatableComponent("tt.crossroads.toolbelt.desc"));
	}

	//All the magic happens in EventHandlerCommon
}
