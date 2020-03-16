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
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModuleGoggles extends ArmorItem{

	private static final IArmorMaterial TECHNOMANCY_MAT = new TechnoMat();

	public ModuleGoggles(){
		super(TECHNOMANCY_MAT, EquipmentSlotType.HEAD, new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
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
		if(!world.isRemote && (nbt = stack.getTag()) != null){
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
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.goggles.lenses"));
		CompoundNBT nbt = stack.getTag();
		if(nbt != null && !nbt.isEmpty()){
			String enabled = MiscUtil.localize("tt.crossroads.goggles.enabled");
			String disabled = MiscUtil.localize("tt.crossroads.goggles.disabled");
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(nbt.contains(lens.toString())){
					//Displaying the enabled/disabled parameter is optional. By default, diamond and quartz lenses don't
					tooltip.add(new TranslationTextComponent("tt.crossroads.goggles." + lens.toString(), nbt.getBoolean(lens.toString()) ? enabled : disabled));
				}
			}
		}else{
			tooltip.add(new TranslationTextComponent("tt.crossroads.goggles.none"));
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type){
		StringBuilder path = new StringBuilder(Crossroads.MODID + ":textures/models/armor/goggles/goggle");
		CompoundNBT nbt = stack.getTag();
		if(nbt != null){
			for(EnumGoggleLenses lens : EnumGoggleLenses.values()){
				if(nbt.contains(lens.name())){
					path.append(lens.getTexturePath());
				}
			}
		}
		path.append(".png");
		return path.toString();
	}

	private static class TechnoMat implements IArmorMaterial{

		@Override
		public int getDurability(EquipmentSlotType slotIn){
			return 0;
		}

		@Override
		public int getDamageReductionAmount(EquipmentSlotType slotIn){
			return 0;
		}

		@Override
		public int getEnchantability(){
			return 0;
		}

		@Override
		public SoundEvent getSoundEvent(){
			return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
		}

		@Override
		public Ingredient getRepairMaterial(){
			return Ingredient.EMPTY;
		}

		@Override
		public String getName(){
			return "technomancy";
		}

		@Override
		public float getToughness(){
			return 0;
		}
	}
}
