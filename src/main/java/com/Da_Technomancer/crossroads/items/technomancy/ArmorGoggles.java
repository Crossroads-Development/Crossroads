package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArmorGoggles extends TechnomancyArmor{

	public ArmorGoggles(){
		super(EquipmentSlot.HEAD);
		String name = "module_goggles";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	/**
	 * Value chosen at random.
	 */
	private static final int CHAT_ID = 718749;

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player){
		CompoundTag nbt;
		if(!world.isClientSide && (nbt = stack.getTag()) != null){
			ArrayList<Component> chat = new ArrayList<>();
			BlockHitResult ray = MiscUtil.rayTrace(player, 8);
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(nbt.contains(lens.toString())){
					if(!lens.useKey() || nbt.getBoolean(lens.toString())){
						lens.doEffect(world, player, chat, ray);
					}
				}
			}
			if(!chat.isEmpty()){
				CRPackets.sendPacketToPlayer((ServerPlayer) player, new SendChatToClient(chat, CHAT_ID));
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslatableComponent("tt.crossroads.goggles.lenses"));
		CompoundTag nbt = stack.getTag();
		boolean hasLens = false;
		if(nbt != null && !nbt.isEmpty()){
			String enabled = MiscUtil.localize("tt.crossroads.goggles.enabled");
			String disabled = MiscUtil.localize("tt.crossroads.goggles.disabled");
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(nbt.contains(lens.toString())){
					//Displaying the enabled/disabled parameter is optional. By default, diamond and quartz lenses don't
					tooltip.add(new TranslatableComponent("tt.crossroads.goggles." + lens.toString(), nbt.getBoolean(lens.toString()) ? enabled : disabled));
					hasLens = true;
				}
			}
		}
		if(!hasLens){
			tooltip.add(new TranslatableComponent("tt.crossroads.goggles.none"));
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
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(allowdedIn(group)){
			items.add(new ItemStack(this, 1));
			items.add(setReinforced(new ItemStack(this, 1), true));
			ItemStack unarmoredLenses = new ItemStack(this, 1);
			ItemStack armoredLenses = new ItemStack(this, 1);
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				unarmoredLenses.getOrCreateTag().putBoolean(lens.toString(), false);
				armoredLenses.getOrCreateTag().putBoolean(lens.toString(), false);
			}

			items.add(unarmoredLenses);
			items.add(setReinforced(armoredLenses, true));
		}
	}
}
