package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArmorGoggles extends TechnomancyArmor{

	public ArmorGoggles(){
		super(EquipmentSlotType.HEAD);
		String name = "module_goggles";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	/**
	 * Value chosen at random.
	 */
	private static final int CHAT_ID = 718749;

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player){
		CompoundNBT nbt;
		if(!world.isClientSide && (nbt = stack.getTag()) != null){
			ArrayList<ITextComponent> chat = new ArrayList<>();
			BlockRayTraceResult ray = MiscUtil.rayTrace(player, 8);
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(nbt.contains(lens.toString())){
					if(!lens.useKey() || nbt.getBoolean(lens.toString())){
						lens.doEffect(world, player, chat, ray);
					}
				}
			}
			if(!chat.isEmpty()){
				CRPackets.sendPacketToPlayer((ServerPlayerEntity) player, new SendChatToClient(chat, CHAT_ID));
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tt.crossroads.goggles.lenses"));
		CompoundNBT nbt = stack.getTag();
		boolean hasLens = false;
		if(nbt != null && !nbt.isEmpty()){
			String enabled = MiscUtil.localize("tt.crossroads.goggles.enabled");
			String disabled = MiscUtil.localize("tt.crossroads.goggles.disabled");
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(nbt.contains(lens.toString())){
					//Displaying the enabled/disabled parameter is optional. By default, diamond and quartz lenses don't
					tooltip.add(new TranslationTextComponent("tt.crossroads.goggles." + lens.toString(), nbt.getBoolean(lens.toString()) ? enabled : disabled));
					hasLens = true;
				}
			}
		}
		if(!hasLens){
			tooltip.add(new TranslationTextComponent("tt.crossroads.goggles.none"));
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type){
		StringBuilder path = new StringBuilder(Crossroads.MODID + ":textures/models/armor/goggles/");
		if(isReinforced(stack)){
			path.append("reinf_goggle");
		}else{
			path.append("goggle");
		}
		CompoundNBT nbt = stack.getTag();
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
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items){
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
