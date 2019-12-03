package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
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
		super(CRItems.itemProp);
		String name = "poison_vodka";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public int getUseDuration(ItemStack stack){
		return 32;
	}

	@Override
	public UseAction getUseAction(ItemStack stack){
		return UseAction.DRINK;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
	}

	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving){
		PlayerEntity player = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;

		if(!worldIn.isRemote){
			entityLiving.addPotionEffect(new EffectInstance(Effects.NAUSEA, DURATION, 3));
			entityLiving.addPotionEffect(new EffectInstance(Effects.SLOWNESS, DURATION, 3));
			entityLiving.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, DURATION, 3));
			entityLiving.addPotionEffect(new EffectInstance(Effects.UNLUCK, DURATION, 3));
			entityLiving.addPotionEffect(new EffectInstance(Effects.HUNGER, DURATION, 1));
			entityLiving.addPotionEffect(new EffectInstance(Effects.POISON, DURATION, 3));
			entityLiving.addPotionEffect(new EffectInstance(Effects.STRENGTH, DURATION, 2));
			entityLiving.addPotionEffect(new EffectInstance(Effects.RESISTANCE, DURATION, 2));
			entityLiving.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, DURATION, 0));
		}

		if(player == null){
			stack.shrink(1);
			if(stack.isEmpty()){
				return new ItemStack(Items.GLASS_BOTTLE);
			}
		}else{
			player.addStat(Stats.ITEM_USED.get(this));

			if(!player.isCreative()){
				stack.shrink(1);
				if(stack.isEmpty()){
					return new ItemStack(Items.GLASS_BOTTLE);
				}
				player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		return stack;
	}

	@Override
	public int getBurnTime(ItemStack itemStack){
		return 72000;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.poison_vodka.quip").setStyle(MiscUtil.TT_QUIP));
		//TODO
//		tooltip.add("TEMP UNTIL DOCS ARE DONE:");
//		tooltip.add("Adds a ton of buffs and debuffs when drunk, turning you into a tank");
//		tooltip.add("Burns as fuel for an extremely long time");
//		tooltip.add("Made with poisonous potato, salt of vitriol, glass bottle");
	}
}
