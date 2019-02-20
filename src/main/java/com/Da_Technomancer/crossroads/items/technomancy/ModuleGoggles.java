package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModuleGoggles extends ItemArmor{

	public ModuleGoggles(){
		super(ModItems.TECHNOMANCY, 1, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
		String name = "module_goggles";
		setTranslationKey(name);
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
			ArrayList<String> chat = new ArrayList<>();
			RayTraceResult ray = MiscUtil.rayTrace(player, 8);
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(stack.getTagCompound().getBoolean(lens.name())){
					lens.doEffect(world, player, chat, ray);
				}
			}
			if(!chat.isEmpty()){
				StringBuilder out = new StringBuilder();
				for(String line : chat){
					if(out.length() != 0){
						out.append("\n");
					}
					out.append(line);
				}
				ModPackets.network.sendTo(new SendChatToClient(out.toString(), CHAT_ID), (EntityPlayerMP) player);
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
					if(lens.shouldShowState()){
						tooltip.add('-' + lens.name() + "-" + (stack.getTagCompound().getBoolean(lens.name()) ? "ENABLED" : "DISABLED"));
					}else{
						tooltip.add('-' + lens.name());
					}
				}
			}
		}else{
			tooltip.add("-NONE");
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type){
		StringBuilder path = new StringBuilder(Main.MODID + ":textures/models/armor/goggles/goggle");
		if(stack.hasTagCompound()){
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(stack.getTagCompound().hasKey(lens.name())){
					path.append(lens.getTexturePath());
				}
			}
		}
		path.append(".png");
		return path.toString();
	}
}
