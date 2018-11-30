package com.Da_Technomancer.crossroads.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChaosRod extends Item{
	
	public ChaosRod(){
		String name = "chaos_rod";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		if(worldIn.isRemote){
			playerIn.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1, 1);
			return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
		}
		Vec3d change = playerIn.getLookVec().scale(8);
		playerIn.setPositionAndUpdate(playerIn.posX + change.x, playerIn.posY + change.y, playerIn.posZ + change.z);
		//Long story short, Potus4mine is the username of the person who found an exploit, which I left in only for them. 
		if(playerIn.getGameProfile().getName().equals("Potus4mine") ? playerIn.getActivePotionEffect(MobEffects.WEAKNESS) != null : playerIn.getActivePotionEffect(MobEffects.GLOWING) != null){
			playerIn.attackEntityFrom(DamageSource.DRAGON_BREATH, 5F);
		}
		playerIn.addPotionEffect(new PotionEffect(playerIn.getGameProfile().getName().equals("Potus4mine") ? MobEffects.WEAKNESS : MobEffects.GLOWING, 100, 0));
		return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("It seems familiar...");
	}
}
