package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PigZombieChestsplate extends ArmorItem{

	protected PigZombieChestsplate(){
		super(ChickenBoots.BOBO_MATERIAL, Type.CHESTPLATE, new Properties().stacksTo(1));
		String name = "pig_zombie_chestplate";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer){
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.pig_zombie_chestplate.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player){
		//Believe it or not, it is possible to die of fire while in lava without burning (if it is raining on the player). There is an isInLava check for this reason.
		if(player.getEffect(MobEffects.FIRE_RESISTANCE) == null && (player.isOnFire() || player.isInLava())){
			player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 10, 0, false, false));
			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PIGLIN_HURT, SoundSource.PLAYERS, 2.5F, 1F);
		}
	}
}
