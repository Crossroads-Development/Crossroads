package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.FatCollectorContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class FatCollectorTileEntity extends InventoryTE{

	@ObjectHolder("fat_collector")
	private static TileEntityType<FatCollectorTileEntity> type = null;

	public static final int[] TIERS = {100, 120, 140, 160, 180, 200};
	public static final double[] EFFICIENCY = {0.8D, 1D, 1.2D, 1D, 0.8D, 0};
	private static final double USE_PER_VALUE = 2D;

	public FatCollectorTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(2_000, false, true);
		initFluidManagers();
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
	public void tick(){
		super.tick();

		int tier = HeatUtil.getHeatTier(temp, TIERS);

		Food food;
		if(tier != -1 && !inventory[0].isEmpty() && (food = inventory[0].getItem().getFood()) != null){
			//I don't know why vanilla multiplies saturation by 2, but it does
			int liqAm = food.getHealing() + (int) (food.getHealing() * food.getSaturation() * 2F);
			double heatUse = ((double) liqAm) * USE_PER_VALUE;
			liqAm *= EnergyConverters.FAT_PER_VALUE;
			liqAm *= EFFICIENCY[tier];
			if(liqAm <= fluidProps[0].capacity - fluids[0].getAmount()){
				temp -= heatUse;
				inventory[0].shrink(1);
				if(fluids[0].isEmpty()){
					fluids[0] = new FluidStack(CRFluids.liquidFat.still, liqAm);
				}else{
					fluids[0].grow(liqAm);
				}
			}
		}
	}

	@Override
	public void remove(){
		super.remove();
		itemOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != Direction.DOWN && facing != Direction.UP){
			return (LazyOptional<T>) globalFluidOpt;
		}
		if(capability == Capabilities.HEAT_CAPABILITY && (facing == null || facing == Direction.DOWN)){
			return (LazyOptional<T>) heatOpt;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return stack.getItem().isFood() && stack.getItem() != CRItems.edibleBlob;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.fat_collector");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new FatCollectorContainer(id, playerInv, createContainerBuf());
	}
}
