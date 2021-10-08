package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.FatCollectorContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE.ItemHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE.TankProperty;

@ObjectHolder(Crossroads.MODID)
public class FatCollectorTileEntity extends InventoryTE{

	@ObjectHolder("fat_collector")
	private static BlockEntityType<FatCollectorTileEntity> type = null;

	public static final int[] TIERS = {100, 120, 140, 160, 180, 200};
	public static final double[] EFFICIENCY = {0.8D, 1D, 1.2D, 1D, 0.8D, 0};
	private static final double USE_PER_VALUE = 2D;

	public FatCollectorTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(8_000, false, true);
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

		FoodProperties food;
		if(tier != -1 && !inventory[0].isEmpty() && (food = inventory[0].getItem().getFoodProperties()) != null){
			//I don't know why vanilla multiplies saturation by 2, but it does
			int liqAm = Math.min(food.getNutrition() + (int) (food.getNutrition() * food.getSaturationModifier() * 2F), fluidProps[0].capacity);
			double heatUse = ((double) liqAm) * USE_PER_VALUE;
			liqAm *= CRConfig.fatPerValue.get();
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
	public void setRemoved(){
		super.setRemoved();
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
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return stack.getItem().isEdible() && stack.getItem() != CRItems.edibleBlob;
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.fat_collector");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new FatCollectorContainer(id, playerInv, createContainerBuf());
	}
}
