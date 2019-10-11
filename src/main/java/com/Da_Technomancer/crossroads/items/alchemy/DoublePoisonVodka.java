package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class DoublePoisonVodka extends Item{

	private static final DamageSource ALC_DAMAGE = new DamageSource("vodka").setDamageBypassesArmor().setDamageAllowedInCreativeMode().setDamageIsAbsolute();

	public DoublePoisonVodka(){
		String name = "double_poison_vodka";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
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
			entityLiving.attackEntityFrom(ALC_DAMAGE, 10_000);
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
		return 144000;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("666% Alcohol Content");
		tooltip.add("TEMP UNTIL DOCS ARE DONE:");
		tooltip.add("Burns as fuel for an extremely long time");
		tooltip.add("Made with poisonous potato, salt of vitriol, Poisonous Potato Vodka");
	}
}
