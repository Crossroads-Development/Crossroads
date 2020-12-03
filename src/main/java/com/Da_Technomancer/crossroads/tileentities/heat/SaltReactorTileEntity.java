package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SaltReactorContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
public class SaltReactorTileEntity extends InventoryTE{

	@ObjectHolder("salt_reactor")
	private static TileEntityType<SaltReactorTileEntity> type = null;

	public static final int WATER_USE = 200;
	public static final double COOLING = 5D;
	public static final int FUEL_DURATION = 20;

	private int fuelTime = 0;

	public SaltReactorTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(20 * WATER_USE, true, false, (Fluid f) -> f == CRFluids.distilledWater.still);//Distilled water
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
	public void tick(){
		super.tick();

		if(world.isRemote){
			return;
		}

		if(fuelTime == 0 && fluids[0].getAmount() >= WATER_USE && fluidProps[1].capacity - fluids[1].getAmount() >= WATER_USE && !inventory[0].isEmpty()){
			//Consume fuel
			fluids[0].shrink(WATER_USE);
			inventory[0].shrink(1);

			if(fluids[1].isEmpty()){
				fluids[1] = new FluidStack(Fluids.WATER, WATER_USE);
			}else{
				fluids[1].grow(WATER_USE);
			}
			markDirty();
			fuelTime = FUEL_DURATION;
		}
		if(fuelTime > 0){
			fuelTime -= 1;
			temp = Math.max(HeatUtil.ABSOLUTE_ZERO, temp - COOLING);
			markDirty();
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		fuelTime = nbt.getInt("fuel_time");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("fuel_time", fuelTime);
		return nbt;
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
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && CRItemTags.SALT_REACTOR_COOLANT.contains(stack.getItem());
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.salt_reactor");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new SaltReactorContainer(id, playerInv, createContainerBuf());
	}
}
