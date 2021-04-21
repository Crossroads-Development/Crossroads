package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ArmorToolbelt extends TechnomancyArmor{

	public ArmorToolbelt(){
		super(EquipmentSlotType.LEGS);
		String name = "toolbelt";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tt.crossroads.toolbelt.desc"));
	}

	//All the magic happens in EventHandlerCommon
}
