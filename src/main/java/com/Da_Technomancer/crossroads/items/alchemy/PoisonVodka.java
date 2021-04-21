package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class PoisonVodka extends Item{

	private static final int DURATION = 3600;

	public PoisonVodka(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS));
		String name = "poison_vodka";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public int getUseDuration(ItemStack stack){
		return 32;
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack){
		return UseAction.DRINK;
	}

	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
		playerIn.startUsingItem(handIn);
		return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getItemInHand(handIn));
	}

	public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving){
		PlayerEntity player = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;

		if(!worldIn.isClientSide){
			entityLiving.addEffect(new EffectInstance(Effects.WITHER, DURATION, 0));
			entityLiving.addEffect(new EffectInstance(Effects.CONFUSION, DURATION, 0));
			entityLiving.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, DURATION, 3));
			entityLiving.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, DURATION, 2));
			entityLiving.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, DURATION, 2));
		}

		if(player == null){
			stack.shrink(1);
			if(stack.isEmpty()){
				return new ItemStack(Items.GLASS_BOTTLE);
			}
		}else{
			player.awardStat(Stats.ITEM_USED.get(this));

			if(!player.isCreative()){
				stack.shrink(1);
				if(stack.isEmpty()){
					return new ItemStack(Items.GLASS_BOTTLE);
				}
				player.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		return stack;
	}

	@Override
	public int getBurnTime(ItemStack itemStack){
		return 72000;
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.poison_vodka.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
