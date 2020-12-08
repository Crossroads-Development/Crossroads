package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SteamTurbineContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class SteamTurbineTileEntity extends InventoryTE{

	@ObjectHolder("steam_turbine")
	public static TileEntityType<SteamTurbineTileEntity> type = null;

	public static final double INERTIA = 80D;
	private static final int CAPACITY = 10_000;
	public static final int LIMIT = 5;

	public SteamTurbineTileEntity(){
		super(type, 0);
		fluidProps[0] = new TankProperty(CAPACITY, false, true);
		fluidProps[1] = new TankProperty(CAPACITY, true, false, (Fluid f) -> f == CRFluids.steam.still);
		initFluidManagers();
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected double getMoInertia(){
		return INERTIA;
	}

	@Override
	protected AxleHandler createAxleHandler(){
		return new AngleAxleHandler();
	}

	@Override
	public void tick(){
		super.tick();
		
		if(world.isRemote){
			return;
		}

		if(!fluids[1].isEmpty()){
			int limit = fluids[1].getAmount() / 100;
			limit = Math.min(limit, (CAPACITY - fluids[0].getAmount()) / 100);
			limit = Math.min(limit, LIMIT);
			if(limit != 0){
				fluids[1].shrink(limit * 100);
				if(fluids[0].isEmpty()){
					fluids[0] = new FluidStack(CRFluids.distilledWater.still, 100 * limit);
				}else{
					fluids[0].grow(100 * limit);
				}
				if(axleHandler.axis != null){
					axleHandler.addEnergy(((double) limit) * .1D * (double) CRConfig.steamWorth.get() * CRConfig.jouleWorth.get(), true);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) globalFluidOpt;
		}
		if(capability == Capabilities.AXLE_CAPABILITY && facing == Direction.UP){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.steam_turbine");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new SteamTurbineContainer(id, playerInv, createContainerBuf());
	}
}
