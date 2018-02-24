package com.Da_Technomancer.crossroads.items.technomancy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleGoggles extends ItemArmor{
	
	public ModuleGoggles(){
		super(ModItems.TECHNOMANCY, 1, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
		String name = "module_goggles";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	/**
	 * Value chosen at random.
	 */
	private static final int CHAT_ID = 718749;
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack){
		if(!world.isRemote && stack.hasTagCompound()){
			ArrayList<String> chat = new ArrayList<String>();
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(stack.getTagCompound().hasKey(lens.name())){
					lens.doEffect(world, player, chat, MiscOp.rayTrace(player, 8));
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
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Lenses:");
		if(stack.hasTagCompound()){
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
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
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(stack.getTagCompound().hasKey(lens.name())){
					path += lens.getTexturePath();
				}
			}
		}
		path += ".png";
		return path;
	}
}
