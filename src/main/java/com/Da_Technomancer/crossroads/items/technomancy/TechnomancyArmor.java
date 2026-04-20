package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class TechnomancyArmor extends ArmorItem{

	protected final ArmorItem.Type armorType;
	protected final boolean reinforced;

	public TechnomancyArmor(ArmorItem.Type type, boolean reinforced){
		super(reinforced ? CRItems.TECHNOMANCY_REINFORCED_ARMOR_MATERIAL : CRItems.TECHNOMANCY_ARMOR_MATERIAL, type, new Properties().stacksTo(1).fireResistant().durability(type.getDurability(reinforced ? 37 : 15)));
		this.armorType = type;
		this.reinforced = reinforced;
	}

	public static boolean isReinforced(ItemStack stack){
		return stack.getItem() instanceof TechnomancyArmor armor && armor.reinforced;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		if(reinforced){
			tooltip.add(Component.translatable("tt.crossroads.technomancy_armor.reinforced").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_RED)));
		}
	}
}
