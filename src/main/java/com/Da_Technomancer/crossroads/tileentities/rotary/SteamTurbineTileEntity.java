package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SteamTurbineContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.templates.ModuleTE.TankProperty;

@ObjectHolder(Crossroads.MODID)
public class SteamTurbineTileEntity extends InventoryTE{

	@ObjectHolder("steam_turbine")
	public static BlockEntityType<SteamTurbineTileEntity> type = null;

	public static final double INERTIA = 80D;
	private static final int CAPACITY = 10_000;
	public static final int LIMIT = 5;

	public SteamTurbineTileEntity(BlockPos pos, BlockState state){
		super(type, 0);
		fluidProps[0] = new TankProperty(CAPACITY, false, true);
		fluidProps[1] = new TankProperty(CAPACITY, true, false, CRFluids.STEAM::contains);
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
	public void tick(){
		super.tick();
		
		if(level.isClientSide){
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
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.steam_turbine");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new SteamTurbineContainer(id, playerInv, createContainerBuf());
	}
}
