package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class LiechWrench extends Item{

	private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	public LiechWrench(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).addToolType(ToolType.PICKAXE, Tiers.STONE.getLevel()).addToolType(ToolType.SHOVEL, Tiers.STONE.getLevel()).addToolType(ToolType.AXE, Tiers.STONE.getLevel()).addToolType(ToolType.HOE, Tiers.STONE.getLevel()).addToolType(ToolType.get("wrench"), 0).stacksTo(1));
		String name = "liech_wrench";
		setRegistryName(name);
		CRItems.toRegister.add(this);
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
		tooltip.add(new TranslatableComponent("tt.crossroads.liech_wrench.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state){
		Material mat = state.getMaterial();
		if(mat == Material.WOOD || mat == Material.PLANT || mat == Material.REPLACEABLE_PLANT){
			return 4F;
		}

		for(ToolType type : getToolTypes(stack)){
			if(state.getBlock().isToolEffective(state, type)){
				return 4F;
			}
		}
		if(mat == Material.METAL || mat == Material.HEAVY_METAL || mat == Material.STONE){
			return 4F;
		}
		return 1.0F;
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState blockIn){
		int i = Tiers.STONE.getLevel();
		if (blockIn.getHarvestTool() == ToolType.PICKAXE) {
			return i >= blockIn.getHarvestLevel();
		}
		Material material = blockIn.getMaterial();
		return material == Material.STONE || material == Material.METAL || material == Material.HEAVY_METAL;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack){
		//Acts as a stone sword tier melee weapon
		return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getAttributeModifiers(slot, stack);
	}
}
