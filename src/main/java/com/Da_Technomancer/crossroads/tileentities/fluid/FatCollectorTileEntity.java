package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.fluids.BlockLiquidFat;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class FatCollectorTileEntity extends InventoryTE{

	public static final int[] TIERS = {100, 120, 140, 160, 180};
	public static final double[] EFFICIENCY = {0.8D, 1D, 1.2D, 1D, 0.8D};
	private static final double USE_PER_VALUE = 2D;

	public FatCollectorTileEntity(){
		super(1);
		fluidProps[0] = new TankProperty(0, 2_000, false, true);
	}

	@Override
	public int fluidTanks(){
		return 1;
	}

	@Override
	public boolean useHeat(){
		return true;
	}

	@Override
	public void update(){
		super.update();

		int tier = HeatUtil.getHeatTier(temp, TIERS);

		if(tier != -1 && !inventory[0].isEmpty()){
			int liqAm = (int) (((ItemFood) inventory[0].getItem()).getHealAmount(inventory[0]) + ((ItemFood) inventory[0].getItem()).getSaturationModifier(inventory[0]));
			double heatUse = ((double) liqAm) * USE_PER_VALUE;
			liqAm *= EnergyConverters.FAT_PER_VALUE;
			liqAm *= EFFICIENCY[tier];
			if(liqAm <= (fluidProps[0].getCapacity() - (fluids[0] == null ? 0 : fluids[0].amount))){
				temp -= heatUse;
				inventory[0].shrink(1);
				fluids[0] = new FluidStack(BlockLiquidFat.getLiquidFat(), liqAm + (fluids[0] == null ? 0 : fluids[0].amount));
			}
		}
	}

	private final IItemHandler itemHandler = new ItemHandler(null);
	private final IFluidHandler mainHandler = new FluidHandler(0);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != EnumFacing.DOWN && facing != EnumFacing.UP){
			return (T) mainHandler;
		}
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return (T) heatHandler;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return stack.getItem() instanceof ItemFood && stack.getItem() != ModItems.edibleBlob;
	}

	@Override
	public String getName(){
		return "container.fat_collector";
	}
}
