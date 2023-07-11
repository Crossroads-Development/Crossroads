package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.api.witchcraft.ICultivatable;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class VillagerBrain extends Item implements ICultivatable{

	private static final long LIFETIME = 30 * 60 * 20;//30 minutes
	private static final String TRADES = "cr_trades";
	private static final String CURRENT_TRADE = "cr_current_trade";

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
		return new MerchantOffers(stack.getOrCreateTagElement(TRADES));
	}

	public void setOffers(ItemStack stack, MerchantOffers offers){
		stack.getOrCreateTag().put(TRADES, offers.createTag());
	}

	public MerchantOffer getCurrentOffer(ItemStack stack){
		CompoundTag nbt = stack.getOrCreateTag();
		int tradeIndex = nbt.getInt(CURRENT_TRADE);
		MerchantOffers offers = getOffers(stack);
		if(offers.size() == 0){
			return null;
		}
		if(tradeIndex >= offers.size()){
			tradeIndex %= offers.size();
			nbt.putInt(CURRENT_TRADE, tradeIndex);
		}

		MerchantOffer currentOffer = offers.get(tradeIndex);
		//If this has been frozen, make the trade worse
		if(wasFrozen(stack)){
			currentOffer.setSpecialPriceDiff(4);
		}
		return currentOffer;
	}

	public void incrementCurrentOffer(ItemStack stack){
		CompoundTag nbt = stack.getOrCreateTag();
		MerchantOffers offers = getOffers(stack);
		if(offers.size() != 0){
			int tradeIndex = nbt.getInt(CURRENT_TRADE);
			tradeIndex = (tradeIndex + 1) % offers.size();
			nbt.putInt(CURRENT_TRADE, tradeIndex);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		ItemStack held = player.getItemInHand(hand);
		incrementCurrentOffer(held);
		return InteractionResultHolder.success(held);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		MerchantOffer offer = getCurrentOffer(stack);
		if(offer == null){
			//No trades
			tooltip.add(Component.translatable("tt.crossroads.villager_brain.trade.none"));
		}else if(offer.getCostB().isEmpty()){
			//Single input trade
			tooltip.add(Component.translatable("tt.crossroads.villager_brain.trade.single", getDisplayParameter(offer.getCostA()), offer.getCostA().getCount(), getDisplayParameter(offer.getResult()), offer.getResult().getCount()));
		}else{
			//Dual input trade
			tooltip.add(Component.translatable("tt.crossroads.villager_brain.trade.dual", getDisplayParameter(offer.getCostA()), offer.getCostA().getCount(), getDisplayParameter(offer.getCostB()), offer.getCostB().getCount(), getDisplayParameter(offer.getResult()), offer.getResult().getCount()));
		}
		ICultivatable.addTooltip(stack, world, tooltip);
		tooltip.add(Component.translatable("tt.crossroads.village_brain.desc"));
	}

	private static Object getDisplayParameter(ItemStack stack){
		int totalEnchants = 0;
		Component firstEnchantName = null;

		if(stack.isEnchanted()){
			//Doesn't work on enchanted books
			ListTag enchantList = stack.getEnchantmentTags();
			totalEnchants = enchantList.size();
			CompoundTag compoundnbt = enchantList.getCompound(0);

			Enchantment firstEnchant = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(compoundnbt.getString("id")));
			if(firstEnchant != null){
				firstEnchantName = firstEnchant.getFullname(compoundnbt.getInt("lvl"));
			}
		}
		if(stack.getItem() instanceof EnchantedBookItem){
			ListTag enchantList = EnchantedBookItem.getEnchantments(stack);
			totalEnchants = enchantList.size();
			CompoundTag compoundnbt = enchantList.getCompound(0);
			Enchantment firstEnchant = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(compoundnbt.getString("id")));
			if(firstEnchant != null){
				firstEnchantName = firstEnchant.getFullname(compoundnbt.getInt("lvl"));
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
