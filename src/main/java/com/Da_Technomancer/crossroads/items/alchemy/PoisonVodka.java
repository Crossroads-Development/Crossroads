package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class PoisonVodka extends Item{

	private static final int DURATION = 3600;
	
	public PoisonVodka(){
		String name = "poison_vodka";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack){
		return 32;
	}

	public UseAction getItemUseAction(ItemStack stack){
		return UseAction.DRINK;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
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
			player.addStat(Stats.getObjectUseStats(this));

			if(!player.capabilities.isCreativeMode){
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
	public int getItemBurnTime(ItemStack itemStack){
		return 72000;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Not the desert!");
		tooltip.add("TEMP UNTIL DOCS ARE DONE:");
		tooltip.add("Adds a ton of buffs and debuffs when drunk, turning you into a tank");
		tooltip.add("Burns as fuel for an extremely long time");
		tooltip.add("Made with poisonous potato, salt of vitriol, glass bottle");
	}
}
