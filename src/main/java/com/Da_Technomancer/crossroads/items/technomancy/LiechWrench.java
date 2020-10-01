package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class LiechWrench extends Item{

	private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	public LiechWrench(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).addToolType(ToolType.PICKAXE, ItemTier.STONE.getHarvestLevel()).addToolType(ToolType.SHOVEL, ItemTier.STONE.getHarvestLevel()).addToolType(ToolType.AXE, ItemTier.STONE.getHarvestLevel()).addToolType(ToolType.HOE, ItemTier.STONE.getHarvestLevel()).addToolType(ToolType.get("wrench"), 0).maxStackSize(1));
		String name = "liech_wrench";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		//This item is registered as a wrench in the wrench tag

		//Attributes
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 4, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4D, AttributeModifier.Operation.ADDITION));
		attributeModifiers = builder.build();
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player){
		return true;
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.liech_wrench.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state){
		Material mat = state.getMaterial();
		if(mat == Material.WOOD || mat == Material.PLANTS || mat == Material.TALL_PLANTS){
			return 4F;
		}

		for(ToolType type : getToolTypes(stack)){
			if(state.getBlock().isToolEffective(state, type)){
				return 4F;
			}
		}
		if(mat == Material.IRON || mat == Material.ANVIL || mat == Material.ROCK){
			return 4F;
		}
		return 1.0F;
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn){
		int i = ItemTier.STONE.getHarvestLevel();
		if (blockIn.getHarvestTool() == ToolType.PICKAXE) {
			return i >= blockIn.getHarvestLevel();
		}
		Material material = blockIn.getMaterial();
		return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		//Acts as a stone sword tier melee weapon
		return slot == EquipmentSlotType.MAINHAND ? attributeModifiers : super.getAttributeModifiers(slot, stack);
	}
}
