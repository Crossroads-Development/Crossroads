package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ChaosRod extends Item{
	
	protected ChaosRod(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "chaos_rod";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand){
		if(worldIn.isRemote){
			playerIn.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
			return ActionResult.resultSuccess(playerIn.getHeldItem(hand));
		}
		Vector3d change = playerIn.getLookVec().scale(8);
		playerIn.setPositionAndUpdate(playerIn.getPosX() + change.x, playerIn.getPosY() + change.y, playerIn.getPosZ() + change.z);
		//Long story short, Potus4mine is the username of the person who found an exploit, which I left in only for them. 
		if(playerIn.getGameProfile().getName().equals("Potus4mine") ? playerIn.getActivePotionEffect(Effects.WEAKNESS) != null : playerIn.getActivePotionEffect(Effects.GLOWING) != null){
			playerIn.attackEntityFrom(DamageSource.DRAGON_BREATH, 5F);
		}
		playerIn.addPotionEffect(new EffectInstance(playerIn.getGameProfile().getName().equals("Potus4mine") ? Effects.WEAKNESS : Effects.GLOWING, 100, 0));
		return ActionResult.resultSuccess(playerIn.getHeldItem(hand));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.chaos_rod.quip").func_230530_a_(MiscUtil.TT_QUIP));
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}
}
