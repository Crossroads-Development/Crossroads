package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;
import java.util.List;

public class LiechWrench extends Item{

	private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	public LiechWrench(){
		super(new Properties().stacksTo(1));
		String name = "liech_wrench";
		CRItems.queueForRegister(name, this);
		//This item is registered as a wrench in the wrench tag

		//Attributes
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 4, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4D, AttributeModifier.Operation.ADDITION));
		attributeModifiers = builder.build();
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player){
		return true;
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.liech_wrench.quip").setStyle(MiscUtil.TT_QUIP));
	}

	private static boolean canDig(BlockState blockIn){
		return blockIn.is(BlockTags.MINEABLE_WITH_AXE) || blockIn.is(BlockTags.MINEABLE_WITH_HOE) || blockIn.is(BlockTags.MINEABLE_WITH_PICKAXE) || blockIn.is(BlockTags.MINEABLE_WITH_SHOVEL);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state){
		return canDig(state) ? 4F : 1F;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockIn){
		int i = Tiers.STONE.getLevel();
		if(i < 3 && blockIn.is(BlockTags.NEEDS_DIAMOND_TOOL)){
			return false;
		}else if(i < 2 && blockIn.is(BlockTags.NEEDS_IRON_TOOL)){
			return false;
		}else{
			return (i >= 1 || !blockIn.is(BlockTags.NEEDS_STONE_TOOL)) && canDig(blockIn);
		}
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack){
		//Acts as a stone sword tier melee weapon
		return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getAttributeModifiers(slot, stack);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction){
		return toolAction == ConfigUtil.WRENCH_ACTION || ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction) || ToolActions.HOE_DIG == toolAction || ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) || ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
	}
}
