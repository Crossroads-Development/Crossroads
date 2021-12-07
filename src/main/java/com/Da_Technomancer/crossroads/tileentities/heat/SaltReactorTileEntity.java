package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SaltReactorContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class SaltReactorTileEntity extends InventoryTE{

	@ObjectHolder("salt_reactor")
	public static BlockEntityType<SaltReactorTileEntity> TYPE = null;

	public static final int WATER_USE = 200;
	public static final double COOLING = 5D;
	public static final int FUEL_DURATION = 20;

	private int fuelTime = 0;

	public SaltReactorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
		fluidProps[0] = new TankProperty(20 * WATER_USE, true, false, CRFluids.DISTILLED_WATER::contains);//Distilled water
		fluidProps[1] = new TankProperty(20 * WATER_USE, false, true);//Water
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public boolean useHeat(){
		return true;
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
	public void load(CompoundTag nbt){
		super.load(nbt);
		fuelTime = nbt.getInt("fuel_time");
	}

	@Override
	public CompoundTag m_6945_(CompoundTag nbt){
		super.m_6945_(nbt);
		nbt.putInt("fuel_time", fuelTime);
		return nbt;
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
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) globalFluidOpt;
		}

		if(capability == Capabilities.HEAT_CAPABILITY && facing == Direction.DOWN){
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
		return index == 0 && CRItemTags.SALT_REACTOR_COOLANT.contains(stack.getItem());
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.salt_reactor");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new SaltReactorContainer(id, playerInv, createContainerBuf());
	}
}
