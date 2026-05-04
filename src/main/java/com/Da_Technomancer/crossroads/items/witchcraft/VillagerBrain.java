package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.api.witchcraft.ICultivatable;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class VillagerBrain extends Item implements ICultivatable{

	private static final long LIFETIME = 30 * 60 * 20;//30 minutes

	public VillagerBrain(){
		super(new Properties().stacksTo(1).rarity(CRItems.BOBO_RARITY));//Not added to any creative tab
		String name = "villager_brain";
		CRItems.queueForRegister(name, this, null);
	}

	@Override
	public long getLifetime(){
		return LIFETIME;
	}

	@Override
	public double getFreezeTemperature(){
		return 0;
	}

	public MerchantOffers getOffers(ItemStack stack){
		return stack.getOrDefault(CRItems.VILLAGER_TRADES_DATA, new MerchantOffers());
	}

	public void setOffers(ItemStack stack, MerchantOffers offers){
		stack.set(CRItems.VILLAGER_TRADES_DATA, offers);
	}

	public MerchantOffer getCurrentOffer(ItemStack stack){
		int tradeIndex = stack.getOrDefault(CRItems.VILLAGER_TRADE_SELECTION_DATA, 0);
		MerchantOffers offers = getOffers(stack);
		if(offers.size() == 0){
			return null;
		}
		if(tradeIndex >= offers.size()){
			tradeIndex %= offers.size();
			stack.set(CRItems.VILLAGER_TRADE_SELECTION_DATA, tradeIndex);
		}

		MerchantOffer currentOffer = offers.get(tradeIndex);
		//If this has been frozen, make the trade worse
		if(wasFrozen(stack)){
			currentOffer.setSpecialPriceDiff(10);
		}
		return currentOffer;
	}

	public void incrementCurrentOffer(ItemStack stack){
		MerchantOffers offers = getOffers(stack);
		if(offers.size() != 0){
			int tradeIndex = stack.getOrDefault(CRItems.VILLAGER_TRADE_SELECTION_DATA, 0);
			tradeIndex = (tradeIndex + 1) % offers.size();
			stack.set(CRItems.VILLAGER_TRADE_SELECTION_DATA, tradeIndex);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		ItemStack held = player.getItemInHand(hand);
		incrementCurrentOffer(held);
		return InteractionResultHolder.success(held);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		MerchantOffer offer = getCurrentOffer(stack);
		if(offer == null){
			//No trades
			tooltip.add(Component.translatable("tt.crossroads.villager_brain.trade.none"));
		}else if(offer.getCostB().isEmpty()){
			//Single input trade
			tooltip.add(Component.translatable("tt.crossroads.villager_brain.trade.single", getDisplayParameter(offer.getCostA(), context), offer.getCostA().getCount(), getDisplayParameter(offer.getResult(), context), offer.getResult().getCount()));
		}else{
			//Dual input trade
			tooltip.add(Component.translatable("tt.crossroads.villager_brain.trade.dual", getDisplayParameter(offer.getCostA(), context), offer.getCostA().getCount(), getDisplayParameter(offer.getCostB(), context), offer.getCostB().getCount(), getDisplayParameter(offer.getResult(), context), offer.getResult().getCount()));
		}
		ICultivatable.addTooltip(stack, context.level(), tooltip);
		tooltip.add(Component.translatable("tt.crossroads.village_brain.desc"));
	}

	private static Object getDisplayParameter(ItemStack stack, Item.TooltipContext context){
		int totalEnchants = 0;
		Component firstEnchantName = null;

		if(stack.has(DataComponents.STORED_ENCHANTMENTS)){
			ItemEnchantments enchants = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
			totalEnchants = enchants.size();
			ArrayList<Component> enchantmentComponents = new ArrayList<>(totalEnchants);
			enchants.addToTooltip(context, enchantmentComponents::add, TooltipFlag.NORMAL);
			if(!enchantmentComponents.isEmpty()){
				firstEnchantName = enchantmentComponents.get(0);
			}
		}else if(stack.isEnchanted()){
			//Doesn't work on enchanted books
			ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
			totalEnchants = enchants.size();
			ArrayList<Component> enchantmentComponents = new ArrayList<>(totalEnchants);
			enchants.addToTooltip(context, enchantmentComponents::add, TooltipFlag.NORMAL);
			if(!enchantmentComponents.isEmpty()){
				firstEnchantName = enchantmentComponents.get(0);
			}
		}

		if(firstEnchantName != null){
			if(totalEnchants > 1){
				return Component.translatable("tt.crossroads.villager_brain.item.enchant.multi", stack.getHoverName(), firstEnchantName, totalEnchants, totalEnchants - 1);
			}else{
				return Component.translatable("tt.crossroads.villager_brain.item.enchant", stack.getHoverName(), firstEnchantName);
			}
		}
		return stack.getHoverName();
	}

	@Nullable
	@Override
	public CultivationTrade getCultivationTrade(ItemStack self, Level world){
		//Performs villager trades
		if(IPerishable.isSpoiled(self, world)){
			return null;
		}

		MerchantOffer offer = getCurrentOffer(self);
		if(offer == null){
			return null;
		}
		return new CultivationTrade(offer.getCostA(), offer.getCostB(), offer.getResult());
	}
}
