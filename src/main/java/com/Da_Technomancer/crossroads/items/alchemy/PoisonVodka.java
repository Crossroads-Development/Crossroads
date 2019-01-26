package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
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
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack){
		return 32;
	}

	public EnumAction getItemUseAction(ItemStack stack){
		return EnumAction.DRINK;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving){
		EntityPlayer player = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;

		if(!worldIn.isRemote){
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, DURATION, 3));
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, DURATION, 3));
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, DURATION, 3));
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.UNLUCK, DURATION, 3));
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.HUNGER, DURATION, 1));
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.POISON, DURATION, 3));
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, DURATION, 2));
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, DURATION, 2));
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, DURATION, 0));
		}

		if(player == null){
			stack.shrink(1);
			if(stack.isEmpty()){
				return new ItemStack(Items.GLASS_BOTTLE);
			}
		}else{
			player.addStat(StatList.getObjectUseStats(this));

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
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Not the desert!");
		tooltip.add("TEMP UNTIL DOCS ARE DONE:");
		tooltip.add("Adds a ton of buffs and debuffs when drunk, turning you into a tank");
		tooltip.add("Burns as fuel for an extremely long time");
		tooltip.add("Made with poisonous potato, salt of vitriol, glass bottle");
	}
}
