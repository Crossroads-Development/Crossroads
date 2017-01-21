package com.Da_Technomancer.crossroads.items;

import java.util.List;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleGoggles extends ItemArmor{
	
	public ModuleGoggles(){
		super(ModItems.TECHNOMANCY, 1, EntityEquipmentSlot.HEAD);
		this.setMaxStackSize(1);
		String name = "moduleGoggles";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack){
		if(!world.isRemote && stack.hasTagCompound()){
			for(GoggleLenses lens : GoggleLenses.values()){
				if(stack.getTagCompound().hasKey(lens.name())){
					lens.doEffect(world, player);
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		tooltip.add("Lenses:");
		if(stack.hasTagCompound()){
			for(GoggleLenses lens : GoggleLenses.values()){
				if(stack.getTagCompound().hasKey(lens.name())){
					tooltip.add('-' + lens.name());
				}
			}
		}else{
			tooltip.add("-NONE");
		}
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type){
		String path = Main.MODID + ":textures/models/armor/goggles/goggle";
		if(stack.hasTagCompound()){
			for(GoggleLenses lens : GoggleLenses.values()){
				if(stack.getTagCompound().hasKey(lens.name())){
					path += '_' + lens.getTexturePath();
				}
			}
		}
		path += ".png";
		return path;
	}
}
