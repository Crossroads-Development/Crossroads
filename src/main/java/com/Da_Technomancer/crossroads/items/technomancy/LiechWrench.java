package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class LiechWrench extends Item{

	public LiechWrench(){
		super(new Properties().stacksTo(1).rarity(CRItems.BOBO_RARITY)
				.component(DataComponents.TOOL, new Tool(List.of(Tool.Rule.deniesDrops(Tiers.STONE.getIncorrectBlocksForDrops()), Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_HOE, Tiers.STONE.getSpeed()), Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, Tiers.STONE.getSpeed()), Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, Tiers.STONE.getSpeed()), Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_AXE, Tiers.STONE.getSpeed())), 1.0F, 1))
				.attributes(ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4D, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build()));
		String name = "liech_wrench";
		CRItems.queueForRegister(name, this);
		//This item is registered as a wrench in the wrench tag
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player){
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.liech_wrench.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility toolAction){
		return toolAction == ConfigUtil.WRENCH_ACTION || ItemAbilities.DEFAULT_AXE_ACTIONS.contains(toolAction) || ItemAbilities.HOE_DIG == toolAction || ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
	}
}
