package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.List;

public class LeydenJar extends Item{

	public static final int MAX_CHARGE = 100_000;
	
	protected LeydenJar(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "leyden_jar";
//		hasSubtypes = true;
		CRItems.toRegister.put(name, this);
	}
	
	public static int getCharge(ItemStack stack){
		CompoundTag nbt = stack.getTag();
		if(stack.getItem() == CRItems.leydenJar && nbt != null){
			return nbt.getInt("charge");
		}else{
			return 0;
		}
	}
	
	public static void setCharge(ItemStack stack, int chargeIn){
		CompoundTag nbt = stack.getTag();
		if(nbt != null){
			nbt.putInt("charge", Math.min(chargeIn, MAX_CHARGE));
		}else{
			nbt = new CompoundTag();
			nbt.putInt("charge", Math.min(chargeIn, MAX_CHARGE));
			stack.setTag(nbt);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.leyden_jar.desc"));
		tooltip.add(Component.translatable("tt.crossroads.leyden_jar.stats", getCharge(stack), MAX_CHARGE));
		tooltip.add(Component.translatable("tt.crossroads.leyden_jar.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
		if(allowedIn(group)){
			items.add(new ItemStack(this, 1));
			ItemStack stack = new ItemStack(this, 1);
			setCharge(stack, MAX_CHARGE);
			items.add(stack);
		}
	}

	@Override
	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){
		return new ItemEnergyHandler(stack);
	}

	private static class ItemEnergyHandler implements IEnergyStorage, ICapabilityProvider{

		private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> this);

		private final ItemStack stack;

		public ItemEnergyHandler(ItemStack jarStack){
			this.stack = jarStack;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir){
			if(cap == CapabilityEnergy.ENERGY){
				return (LazyOptional<T>) holder;
			}
			return LazyOptional.empty();
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
			if (!simulate){
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
