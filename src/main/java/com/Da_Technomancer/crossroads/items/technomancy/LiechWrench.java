package com.Da_Technomancer.crossroads.items.technomancy;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LiechWrench extends Item{

	public LiechWrench(){
		String name = "liech_wrench";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHarvestLevel("pickaxe", 1);
		setHarvestLevel("shovel", 1);
		setHarvestLevel("axe", 1);
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, PlayerEntity player){
		return true;
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable PlayerEntity player, @Nullable BlockState blockState){
		return 1;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("The poor man's multitool");
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state){
		Material mat = state.getMaterial();
		if(mat == Material.WOOD || mat == Material.PLANTS || mat == Material.VINE){
			return 4F;
		}

		for(String type : getToolClasses(stack)){
			if(state.getBlock().isToolEffective(type, state)){
				return 4F;
			}
		}
		return 1.0F;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EquipmentSlotType.MAINHAND){
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 4, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.4D, 0));
		}

		return multimap;
	}
}
