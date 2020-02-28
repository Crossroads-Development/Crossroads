package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

	public LiechWrench(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).addToolType(ToolType.PICKAXE, 1).addToolType(ToolType.SHOVEL, 1).addToolType(ToolType.AXE, 1).addToolType(ToolType.get("wrench"), 0).maxStackSize(1));
		String name = "liech_wrench";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		//This item is registered as a wrench in the wrench tag
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
		return 1.0F;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EquipmentSlotType.MAINHAND){
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 4, AttributeModifier.Operation.ADDITION));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4D, AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}
}
