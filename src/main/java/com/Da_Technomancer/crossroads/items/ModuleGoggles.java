package com.Da_Technomancer.crossroads.items;

import java.util.ArrayList;
import java.util.List;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
		setMaxStackSize(1);
		String name = "moduleGoggles";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	/**
	 * Initial value chosen at random.
	 */
	private static final int CHAT_ID = 718749;
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack){
		if(!world.isRemote && stack.hasTagCompound()){
			ArrayList<String> chat = new ArrayList<String>();
			for(GoggleLenses lens : GoggleLenses.values()){
				if(stack.getTagCompound().hasKey(lens.name())){
					lens.doEffect(world, player, chat);
				}
			}
			if(!chat.isEmpty()){
				String out = "";
				for(String line : chat){
					if(!out.equals("")){
						out += "\n";
					}
					out += line;
				}
				ModPackets.network.sendTo(new SendChatToClient(out, CHAT_ID), (EntityPlayerMP) player);
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
					path += lens.getTexturePath();
				}
			}
		}
		path += ".png";
		return path;
	}
}
