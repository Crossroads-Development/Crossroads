package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class MagentaBread extends Item{

	protected MagentaBread(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).food(new Food.Builder().setAlwaysEdible().hunger(20).saturation(0.5F).effect(new EffectInstance(Effects.JUMP_BOOST, 3600, 20), 1).effect(new EffectInstance(Effects.SPEED, 3600, 100), 1).effect(new EffectInstance(Effects.NAUSEA, 3600, 10), 0.5F).build()));
		String name = "magenta_bread";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.mag_bread.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
