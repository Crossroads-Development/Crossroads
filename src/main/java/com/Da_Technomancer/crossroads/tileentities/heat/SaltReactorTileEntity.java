package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class SaltReactorTileEntity extends InventoryTE{

	private static final int WATER_USE = 200;
	private static final double COOLING = 5D;

	public SaltReactorTileEntity(){
		super(1);
		fluidProps[0] = new TankProperty(0, 16 * WATER_USE, true, false, (Fluid f) -> f == BlockDistilledWater.getDistilledWater());//Distilled water
		fluidProps[1] = new TankProperty(1, 16 * WATER_USE, false, true);//Water
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

		if(temp >= -273D + COOLING && fluids[0] != null && fluids[0].amount >= WATER_USE && fluidProps[1].getCapacity() - (fluids[1] == null ? 0 : fluids[1].amount) >= WATER_USE && !inventory[0].isEmpty()){
			temp -= COOLING;
			if((fluids[0].amount -= WATER_USE) <= 0){
				fluids[0] = null;
			}

			inventory[0].shrink(1);

			if(fluids[1] == null){
				fluids[1] = new FluidStack(FluidRegistry.WATER, WATER_USE);
			}else{
				fluids[1].amount += WATER_USE;
			}
			markDirty();
		}
	}

	private final IItemHandler itemHandler = new ItemHandler(null);
	private final FluidHandler innerFluidHandler = new FluidHandler(-1);
	private final FluidHandler inputFluidHandler = new FluidHandler(0);
	private final FluidHandler outputFluidHandler = new FluidHandler(1);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(facing == null){
				return (T) innerFluidHandler;
			}

			if(facing == Direction.UP){
				return (T) inputFluidHandler;
			}
			return (T) outputFluidHandler;
		}

		if(capability == Capabilities.HEAT_CAPABILITY && (facing == Direction.DOWN || facing == null)){
			return (T) heatHandler;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return index == 0 && MiscUtil.hasOreDict(stack, "dustSalt");
	}

	@Override
	public String getName(){
		return "container.salt_reactor";
	}
}
