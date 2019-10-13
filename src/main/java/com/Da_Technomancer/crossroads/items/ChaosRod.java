package com.Da_Technomancer.crossroads.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChaosRod extends Item{
	
	public ChaosRod(){
		String name = "chaos_rod";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		CRItems.toRegister.add(this);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand){
		if(worldIn.isRemote){
			playerIn.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
			return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
		}
		Vec3d change = playerIn.getLookVec().scale(8);
		playerIn.setPositionAndUpdate(playerIn.posX + change.x, playerIn.posY + change.y, playerIn.posZ + change.z);
		//Long story short, Potus4mine is the username of the person who found an exploit, which I left in only for them. 
		if(playerIn.getGameProfile().getName().equals("Potus4mine") ? playerIn.getActivePotionEffect(Effects.WEAKNESS) != null : playerIn.getActivePotionEffect(Effects.GLOWING) != null){
			playerIn.attackEntityFrom(DamageSource.DRAGON_BREATH, 5F);
		}
		playerIn.addPotionEffect(new EffectInstance(playerIn.getGameProfile().getName().equals("Potus4mine") ? Effects.WEAKNESS : Effects.GLOWING, 100, 0));
		return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("It seems familiar...");
	}
}
