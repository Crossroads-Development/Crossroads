package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.RadiatorContainer;
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
public class RadiatorTileEntity extends InventoryTE{

	@ObjectHolder("radiator")
	private static TileEntityType<RadiatorTileEntity> type = null;

	public static final int FLUID_USE = 100;

	public RadiatorTileEntity(){
		super(type, 0);
		fluidProps[0] = new TankProperty(10_000, true, false, (Fluid f) -> f == CRFluids.steam.still);
		fluidProps[1] = new TankProperty(10_000, false, true);
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

		if(!level.isClientSide && fluids[0].getAmount() >= FLUID_USE && fluidProps[1].capacity - fluids[1].getAmount() >= FLUID_USE){
			temp += FLUID_USE * (double) CRConfig.steamWorth.get() / 1000;
			if(fluids[1].isEmpty()){
				fluids[1] = new FluidStack(CRFluids.distilledWater.still, FLUID_USE);
			}else{
				fluids[1].grow(FLUID_USE);
			}

			fluids[0].shrink(FLUID_USE);
			setChanged();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){

		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(side == null || side.getAxis() == Direction.Axis.Y){
				return (LazyOptional<T>) globalFluidOpt;
			}
		}

		if(cap == Capabilities.HEAT_CAPABILITY && side != Direction.UP && side != Direction.DOWN){
			return (LazyOptional<T>) heatOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.radiator");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new RadiatorContainer(id, playerInventory, createContainerBuf());
	}
}
