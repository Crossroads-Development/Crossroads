package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class LeydenJar extends Item{

	public static final int MAX_CHARGE = 100_000;
	
	protected LeydenJar(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "leyden_jar";
//		hasSubtypes = true;
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}
	
	public static int getCharge(ItemStack stack){
		CompoundNBT nbt = stack.getTag();
		if(stack.getItem() == CRItems.leydenJar && nbt != null){
			return nbt.getInt("charge");
		}else{
			return 0;
		}
	}
	
	public static void setCharge(ItemStack stack, int chargeIn){
		CompoundNBT nbt = stack.getTag();
		if(nbt != null){
			nbt.putInt("charge", Math.min(chargeIn, MAX_CHARGE));
		}else{
			nbt = new CompoundNBT();
			nbt.putInt("charge", Math.min(chargeIn, MAX_CHARGE));
			stack.setTag(nbt);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.leyden_jar.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.leyden_jar.stats", getCharge(stack), MAX_CHARGE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.leyden_jar.quip").func_230530_a_(MiscUtil.TT_QUIP));
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
		if(isInGroup(group)){
			items.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			setCharge(stack, MAX_CHARGE);
			items.add(stack);
		}
	}
}
