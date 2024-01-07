package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ArmorGoggles extends TechnomancyArmor{

	public ArmorGoggles(){
		super(Type.HELMET);
		String name = "module_goggles";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public void onInventoryTick(ItemStack stack, Level world, Player player, int slotIndex, int selectedIndex){
		if(slotIndex - 36 < 4){//Is equipped
			CompoundTag nbt;
			if(!world.isClientSide && (nbt = stack.getTag()) != null){
				for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
					if(nbt.contains(lens.toString())){
						if(!lens.useKey() || nbt.getBoolean(lens.toString())){
							lens.doEffect(world, player);
						}
					}
				}
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(Component.translatable("tt.crossroads.goggles.lenses"));
		CompoundTag nbt = stack.getTag();
		boolean hasLens = false;
		if(nbt != null && !nbt.isEmpty()){
			String enabled = MiscUtil.localize("tt.crossroads.goggles.enabled");
			String disabled = MiscUtil.localize("tt.crossroads.goggles.disabled");
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(nbt.contains(lens.toString())){
					//Displaying the enabled/disabled parameter is optional. By default, diamond and quartz lenses don't
					tooltip.add(Component.translatable("tt.crossroads.goggles." + lens.toString(), nbt.getBoolean(lens.toString()) ? enabled : disabled));
					hasLens = true;
				}
			}
		}
		if(!hasLens){
			tooltip.add(Component.translatable("tt.crossroads.goggles.none"));
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type){
		StringBuilder path = new StringBuilder(Crossroads.MODID + ":textures/models/armor/goggles/");
		if(isReinforced(stack)){
			path.append("reinf_goggle");
		}else{
			path.append("goggle");
		}
		CompoundTag nbt = stack.getTag();
		if(nbt != null){
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(nbt.contains(lens.toString())){
					path.append(lens.getTexturePath());
				}
			}
		}
		path.append(".png");
		return path.toString();
	}

	@Override
	public ItemStack[] populateCreativeTab(){
		ItemStack[] result = new ItemStack[4];
		result[0] = new ItemStack(this, 1);
		result[1] = setReinforced(new ItemStack(this, 1), true);
		ItemStack unarmoredLenses = new ItemStack(this, 1);
		ItemStack armoredLenses = new ItemStack(this, 1);
		for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
			unarmoredLenses.getOrCreateTag().putBoolean(lens.toString(), false);
			armoredLenses.getOrCreateTag().putBoolean(lens.toString(), false);
		}

		result[2] = unarmoredLenses;
		result[3] = setReinforced(armoredLenses, true);
		return result;
	}
}
