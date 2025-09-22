package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.ICreativeTabPopulatingItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class LeydenJar extends Item implements ICreativeTabPopulatingItem{

	public static final int MAX_CHARGE = 100_000;
	public static final ICapabilityProvider<ItemStack, Void, IEnergyStorage> ENERGY_STORAGE_PROVIDER = (stack, emptyContext) -> new ItemEnergyHandler(stack);

	protected LeydenJar(){
		super(new Properties().stacksTo(1));
		String name = "leyden_jar";
//		hasSubtypes = true;
		CRItems.queueForRegister(name, this);
	}

	public static int getCharge(ItemStack stack){
		if(stack.getItem() == CRItems.leydenJar){
			return stack.getOrDefault(CRItems.ELECTRIC_CHARGE_DATA, 0);
		}else{
			return 0;
		}
	}

	public static void setCharge(ItemStack stack, int chargeIn){
		stack.set(CRItems.ELECTRIC_CHARGE_DATA, chargeIn);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.leyden_jar.desc"));
		tooltip.add(Component.translatable("tt.crossroads.leyden_jar.stats", getCharge(stack), MAX_CHARGE));
		tooltip.add(Component.translatable("tt.crossroads.leyden_jar.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public ItemStack[] populateCreativeTab(){
		ItemStack chargedStack = new ItemStack(this, 1);
		setCharge(chargedStack, MAX_CHARGE);
		return new ItemStack[] {new ItemStack(this, 1), chargedStack};
	}

	private static class ItemEnergyHandler implements IEnergyStorage{

		private final ItemStack stack;

		public ItemEnergyHandler(ItemStack jarStack){
			this.stack = jarStack;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int currentCharge = getEnergyStored();
			int energyReceived = Math.min(getMaxEnergyStored() - currentCharge, maxReceive);
			if(!simulate){
				LeydenJar.setCharge(stack, currentCharge + energyReceived);
			}
			return energyReceived;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			int currentCharge = getEnergyStored();
			int energyExtracted = Math.min(currentCharge, maxExtract);
			if(!simulate){
				LeydenJar.setCharge(stack, currentCharge - energyExtracted);
			}
			return energyExtracted;
		}

		@Override
		public int getEnergyStored(){
			return LeydenJar.getCharge(stack);
		}

		@Override
		public int getMaxEnergyStored(){
			return LeydenJar.MAX_CHARGE;
		}

		@Override
		public boolean canExtract(){
			return true;
		}

		@Override
		public boolean canReceive(){
			return true;
		}
	}
}
