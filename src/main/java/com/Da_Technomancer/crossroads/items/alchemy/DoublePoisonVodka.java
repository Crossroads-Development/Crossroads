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
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DoublePoisonVodka extends Item{

	private static final DamageSource ALC_DAMAGE = new DamageSource("vodka").setDamageBypassesArmor().setDamageAllowedInCreativeMode().setDamageIsAbsolute();

	public DoublePoisonVodka(){
		super(CRItems.itemProp);
		String name = "double_poison_vodka";
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
			entityLiving.attackEntityFrom(ALC_DAMAGE, 10_000);
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
		return 144000;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.poison_vodka_double.quip").setStyle(MiscUtil.TT_QUIP));
		//TODO
//		tooltip.add("TEMP UNTIL DOCS ARE DONE:");
//		tooltip.add("Burns as fuel for an extremely long time");
//		tooltip.add("Made with poisonous potato, salt of vitriol, Poisonous Potato Vodka");
	}
}
