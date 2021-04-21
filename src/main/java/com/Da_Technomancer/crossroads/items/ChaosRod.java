package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
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
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "chaos_rod";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand){
		if(worldIn.isClientSide){
			playerIn.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1, 1);
			return ActionResult.success(playerIn.getItemInHand(hand));
		}
		Vector3d change = playerIn.getLookAngle().scale(8);
		playerIn.teleportTo(playerIn.getX() + change.x, playerIn.getY() + change.y, playerIn.getZ() + change.z);
		//Long story short, Potus4mine is the username of the person who found an exploit, which I left in only for them. 
		if(playerIn.getGameProfile().getName().equals("Potus4mine") ? playerIn.getEffect(Effects.WEAKNESS) != null : playerIn.getEffect(Effects.GLOWING) != null){
			playerIn.hurt(DamageSource.DRAGON_BREATH, 5F);
		}
		playerIn.addEffect(new EffectInstance(playerIn.getGameProfile().getName().equals("Potus4mine") ? Effects.WEAKNESS : Effects.GLOWING, 100, 0));
		return ActionResult.success(playerIn.getItemInHand(hand));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.chaos_rod.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}
}
