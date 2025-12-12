package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.FatCollectorContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public class FatCollectorTileEntity extends InventoryTE implements IHeatCapable, IFluidCapable, IItemCapable{

	public static final BlockEntityType<FatCollectorTileEntity> TYPE = CRTileEntity.createType(FatCollectorTileEntity::new, CRBlocks.fatCollector);

	public static final int[] TIERS = {100, 120, 140, 160, 180, 200};
	public static final double[] EFFICIENCY = {0.8D, 1D, 1.2D, 1D, 0.8D, 0};
	private static final double USE_PER_VALUE = 2D;

	public FatCollectorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
		fluidProps[0] = new TankProperty(8_000, false, true);
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 1;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		int tier = HeatUtil.getHeatTier(temp, TIERS);

		FoodProperties food;
		if(tier != -1 && !inventory[0].isEmpty() && (food = inventory[0].getFoodProperties(null)) != null){
			//I don't know why vanilla multiplies saturation by 2, but it does
			int liqAm = Math.min(food.nutrition() + (int) (food.nutrition() * food.saturation() * 2F), fluidProps[0].capacity);
			double heatUse = ((double) liqAm) * USE_PER_VALUE;
			liqAm *= CRConfig.fatPerValue.get();
			liqAm *= EFFICIENCY[tier];
			if(liqAm <= fluidProps[0].capacity - fluids[0].getAmount()){
				temp -= heatUse;
				inventory[0].shrink(1);
				if(fluids[0].isEmpty()){
					fluids[0] = new FluidStack(CRFluids.liquidFat.getStill(), liqAm);
				}else{
					fluids[0].grow(liqAm);
				}
			}
		}
	}

	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction dir){
		if(dir != Direction.DOWN && dir != Direction.UP){
			return globalFluidHandler;
		}
		return null;
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		if(dir == null || dir == Direction.DOWN){
			return heatHandler;
		}
		return null;
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return stack.getFoodProperties(null) != null && stack.getItem() != CRItems.edibleBlob;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.fat_collector");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new FatCollectorContainer(id, playerInv, createContainerBuf());
	}
}
