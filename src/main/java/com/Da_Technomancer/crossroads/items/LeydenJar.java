package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class LeydenJar extends Item{

	public static final int MAX_CHARGE = 100_000;
	
	protected LeydenJar(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "leyden_jar";
//		hasSubtypes = true;
		CRItems.toRegister.put(name, this);
	}
	
	public static int getCharge(ItemStack stack){
		CompoundTag nbt = stack.getTag();
		if(stack.getItem() == CRItems.leydenJar && nbt != null){
			return nbt.getInt("charge");
		}else{
			return 0;
		}
	}
	
	public static void setCharge(ItemStack stack, int chargeIn){
		CompoundTag nbt = stack.getTag();
		if(nbt != null){
			nbt.putInt("charge", Math.min(chargeIn, MAX_CHARGE));
		}else{
			nbt = new CompoundTag();
			nbt.putInt("charge", Math.min(chargeIn, MAX_CHARGE));
			stack.setTag(nbt);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.leyden_jar.desc"));
		tooltip.add(Component.translatable("tt.crossroads.leyden_jar.stats", getCharge(stack), MAX_CHARGE));
		tooltip.add(Component.translatable("tt.crossroads.leyden_jar.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(allowedIn(group)){
			items.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			setCharge(stack, MAX_CHARGE);
			items.add(stack);
		}
	}
}
