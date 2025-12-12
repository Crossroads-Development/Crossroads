package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.api.templates.ICreativeTabPopulatingItem;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArmorGoggles extends TechnomancyArmor implements ICreativeTabPopulatingItem{

	public ArmorGoggles(boolean reinforced){
		super(Type.HELMET, reinforced);
		String name = reinforced ? "module_goggles_reinforced" : "module_goggles";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slotIndex, boolean isSelected){
		if(entity instanceof Player player && player.getItemBySlot(EquipmentSlot.HEAD) == stack){
			if(!world.isClientSide && stack.has(CRItems.GOGGLE_LENSES_DATA)){
				LensesSet lensData = stack.get(CRItems.GOGGLE_LENSES_DATA);
				for(Map.Entry<EnumGoggleLenses, Boolean> lensEntry : lensData.lenses.object2BooleanEntrySet()){
					EnumGoggleLenses lens = lensEntry.getKey();
					if(!lens.requireEnableKey() || lensEntry.getValue()){
						lens.doEffect(world, player);
					}
				}
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		super.appendHoverText(stack, context, tooltip, flag);
		tooltip.add(Component.translatable("tt.crossroads.goggles.lenses"));
		boolean hasLens = false;
		if(stack.has(CRItems.GOGGLE_LENSES_DATA)){
			LensesSet lensSet = stack.get(CRItems.GOGGLE_LENSES_DATA);
			String enabled = MiscUtil.localize("tt.crossroads.goggles.enabled");
			String disabled = MiscUtil.localize("tt.crossroads.goggles.disabled");
			for(Map.Entry<EnumGoggleLenses, Boolean> lensEntry : lensSet.lenses.object2BooleanEntrySet()){
				//Displaying the enabled/disabled parameter is optional. By default, diamond and quartz lenses don't
				tooltip.add(Component.translatable("tt.crossroads.goggles." + lensEntry.getKey().toString(), lensEntry.getValue() ? enabled : disabled));
				hasLens = true;
			}
		}
		if(!hasLens){
			tooltip.add(Component.translatable("tt.crossroads.goggles.none"));
		}
	}

	private static final Pattern ARMOR_LAYER_TEXTURE_PATTERN = Pattern.compile("_blank_(\\d+)_");

	@Override
	public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel){
		ResourceLocation layerTexture = layer.texture(false);
		Matcher matcher = ARMOR_LAYER_TEXTURE_PATTERN.matcher(layerTexture.getPath());
		if(matcher.find() && stack.has(CRItems.GOGGLE_LENSES_DATA)){
			LensesSet lensesSet = stack.get(CRItems.GOGGLE_LENSES_DATA);
			int armorLayerIndex = Integer.decode(matcher.group(1));
			EnumGoggleLenses lens = EnumGoggleLenses.values()[armorLayerIndex];
			if(lensesSet.lenses.containsKey(lens)){
				return lens.getTextureLocation();
			}
		}

		return super.getArmorTexture(stack, entity, slot, layer, innerModel);
	}

	@Nonnull
	@Override
	public ItemStack[] populateCreativeTab(){
		ItemStack[] result = new ItemStack[2];
		result[0] = new ItemStack(this, 1);
		result[1] = new ItemStack(this, 1);
		result[1].set(CRItems.GOGGLE_LENSES_DATA, new LensesSet(new Object2BooleanOpenHashMap<>(EnumGoggleLenses.values(), new boolean[EnumGoggleLenses.values().length])));
		return result;
	}

	/**
	 * @param lenses Presence of an entry indicates that lens is installed, boolean value is whether it is enabled. Treat lenses as immutable.
	 */
	public static record LensesSet(Object2BooleanMap<EnumGoggleLenses> lenses){

		public static final Codec<LensesSet> CODEC = ExtraCodecs.object2BooleanMap(StringRepresentable.fromEnum(EnumGoggleLenses::values)).xmap(LensesSet::new, LensesSet::lenses);
		public static final StreamCodec<ByteBuf, LensesSet> STREAM_CODEC = new StreamCodec<ByteBuf, LensesSet>(){
			@Override
			public LensesSet decode(ByteBuf byteBuf){
				Object2BooleanMap<EnumGoggleLenses> map = new Object2BooleanOpenHashMap<>();
				EnumGoggleLenses[] lensValues = EnumGoggleLenses.values();
				try{
					while(byteBuf.isReadable(2)){
						map.put(lensValues[byteBuf.readByte()], byteBuf.readBoolean());
					}
				}catch(IndexOutOfBoundsException e){
					Crossroads.logger.error(String.format("Error decoding ByteBuffer [%1$s] for LensesSet codec", byteBuf.toString()), e);
				}
				return new LensesSet(map);
			}

			@Override
			public void encode(ByteBuf byteBuf, LensesSet lensesSet){
				for(Map.Entry<EnumGoggleLenses, Boolean> entries : lensesSet.lenses.object2BooleanEntrySet()){
					byteBuf.writeByte(entries.getKey().ordinal());
					byteBuf.writeBoolean(entries.getValue());
				}
			}
		};
	}
}
