package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SaltReactorContainer;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public class SaltReactorTileEntity extends InventoryTE implements IHeatCapable, IFluidCapable, IItemCapable{

	public static final BlockEntityType<SaltReactorTileEntity> TYPE = CRTileEntity.createType(SaltReactorTileEntity::new, CRBlocks.saltReactor);

	public static final int WATER_USE = 200;
	public static final double COOLING = 5D;
	public static final int FUEL_DURATION = 20;

	private int fuelTime = 0;

	private final IItemHandler itemHandler = new ItemHandler();

	public SaltReactorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
		fluidProps[0] = new TankProperty(20 * WATER_USE, true, false, f -> CraftingUtil.tagContains(CRFluids.DISTILLED_WATER, f));//Distilled water
		fluidProps[1] = new TankProperty(20 * WATER_USE, false, true);//Water
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(fuelTime == 0 && fluids[0].getAmount() >= WATER_USE && fluidProps[1].capacity - fluids[1].getAmount() >= WATER_USE && !inventory[0].isEmpty()){
			//Consume fuel
			fluids[0].shrink(WATER_USE);
			inventory[0].shrink(1);

			if(fluids[1].isEmpty()){
				fluids[1] = new FluidStack(Fluids.WATER, WATER_USE);
			}else{
				fluids[1].grow(WATER_USE);
			}
			setChanged();
			fuelTime = FUEL_DURATION;
		}
		if(fuelTime > 0){
			fuelTime -= 1;
			temp = Math.max(HeatUtil.ABSOLUTE_ZERO, temp - COOLING);
			setChanged();
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		fuelTime = nbt.getInt("fuel_time");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("fuel_time", fuelTime);
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction dir){
		return globalFluidHandler;
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		if(dir == Direction.DOWN){
			return heatHandler;
		}
		return null;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && CraftingUtil.tagContains(CRItemTags.SALT_REACTOR_COOLANT, stack.getItem());
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.salt_reactor");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new SaltReactorContainer(id, playerInv, createContainerBuf());
	}
}
