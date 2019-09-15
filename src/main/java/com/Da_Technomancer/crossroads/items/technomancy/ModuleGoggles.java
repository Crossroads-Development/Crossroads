package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModuleGoggles extends ArmorItem{

	public ModuleGoggles(){
		super(CrossroadsItems.TECHNOMANCY, 1, EquipmentSlotType.HEAD);
		setMaxStackSize(1);
		String name = "module_goggles";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}

	/**
	 * Value chosen at random.
	 */
	private static final int CHAT_ID = 718749;

	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack){
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
				ModPackets.network.sendTo(new SendChatToClient(out.toString(), CHAT_ID), (ServerPlayerEntity) player);
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
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
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type){
		StringBuilder path = new StringBuilder(Crossroads.MODID + ":textures/models/armor/goggles/goggle");
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
