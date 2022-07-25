package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.MiscUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class EdibleBlob extends Item{

	public EdibleBlob(){
		super(new Item.Properties());//Not in a creative tab due to creative giving a version that has no NBT
		String name = "edible_blob";
		CRItems.toRegister.put(name, this);
	}

	public static CompoundTag createNBT(@Nullable CompoundTag base, int hunger, int sat){
		if(base == null){
			base = new CompoundTag();
		}
		base.putInt("food", hunger);
		base.putInt("sat", sat);
		return base;
	}

	public static int getHealAmount(ItemStack stack){
		return stack.hasTag() ? stack.getTag().getInt("food") : 0;
	}

	/**
	 * @param stack The edible blob stack
	 * @return The actual saturation restored
	 */
	public static int getTrueSat(ItemStack stack){
		return stack.hasTag() ? stack.getTag().getInt("sat") : 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		if(stack.hasTag()){
			tooltip.add(Component.translatable("tt.crossroads.edible_blob.food", getHealAmount(stack)));
			tooltip.add(Component.translatable("tt.crossroads.edible_blob.sat", getTrueSat(stack)));
			tooltip.add(Component.translatable("tt.crossroads.edible_blob.quip").setStyle(MiscUtil.TT_QUIP));
		}else{
			tooltip.add(Component.translatable("tt.crossroads.edible_blob.error"));
		}
	}

	@Override
	@Nullable
	// build FoodProperties from NBT on-the-fly
	public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
		CompoundTag tags = stack.getTag();
		if (tags != null) {
			int hun = tags.getInt("food");
			int sat = tags.getInt("sat");
			//make sure old 0 food blobs still give saturation (and avoid a div by 0 error)
			if(hun == 0){hun = 1;}
			float sat_mod = (float) sat / (float) hun;
			return new FoodProperties.Builder().nutrition(hun).saturationMod(sat_mod).meat().alwaysEat().build();
		}
		return null;
	}
	@Override
	public boolean isEdible(){
		return true;
	}
}
