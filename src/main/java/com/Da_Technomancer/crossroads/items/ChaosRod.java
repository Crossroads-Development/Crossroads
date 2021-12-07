package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ChaosRod extends Item{
	
	protected ChaosRod(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "chaos_rod";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand){
		if(worldIn.isClientSide){
			playerIn.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1, 1);
			return InteractionResultHolder.success(playerIn.getItemInHand(hand));
		}
		Vec3 change = playerIn.getLookAngle().scale(8);
		playerIn.teleportTo(playerIn.getX() + change.x, playerIn.getY() + change.y, playerIn.getZ() + change.z);
		//Long story short, Potus4mine is the username of the person who found an exploit, which I left in only for them. 
		if(playerIn.getGameProfile().getName().equals("Potus4mine") ? playerIn.getEffect(MobEffects.WEAKNESS) != null : playerIn.getEffect(MobEffects.GLOWING) != null){
			playerIn.hurt(DamageSource.DRAGON_BREATH, 5F);
		}
		playerIn.addEffect(new MobEffectInstance(playerIn.getGameProfile().getName().equals("Potus4mine") ? MobEffects.WEAKNESS : MobEffects.GLOWING, 100, 0));
		return InteractionResultHolder.success(playerIn.getItemInHand(hand));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.chaos_rod.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}
}
