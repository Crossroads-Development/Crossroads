package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CrossroadsFluids;
import com.Da_Technomancer.crossroads.gui.container.SteamBoilerContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class SteamBoilerTileEntity extends InventoryTE{

	@ObjectHolder("steam_boiler")
	private static TileEntityType<SteamBoilerTileEntity> type = null;

	public static final int BATCH_SIZE = 100;
	public static final int[] TIERS = {100, 200, 300, 400, 500};

	public SteamBoilerTileEntity(){
		super(type, 1);//Salt
		fluidProps[0] = new TankProperty(8_000, true, false, (Fluid f) -> f == Fluids.WATER || f == CrossroadsFluids.distilledWater.still);
		fluidProps[1] = new TankProperty(8_000, false, true, fluid -> true);
	}
	
	@Override
	public int fluidTanks(){
		return 2;//0: Water; 1: Steam
	}
	
	@Override
	public boolean useHeat(){
		return true;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		super.addInfo(chat, player, hit);
		chat.add(new TranslationTextComponent("tt.crossroads.steam_boiler.salt", inventory[0].getCount()));
	}

	@Override
	public void tick(){
		super.tick();
		if(world.isRemote){
			return;
		}

		int tier = HeatUtil.getHeatTier(temp, TIERS);
		
		if(tier != -1){
			temp -= EnergyConverters.degPerSteamBucket() * (tier + 1) * (double) BATCH_SIZE / 1000D;

			int fluidCap = fluidProps[0].capacity;
			
			if(fluids[0].getAmount() >= BATCH_SIZE && fluidCap - fluids[1].getAmount() >= BATCH_SIZE && inventory[0].getCount() < 64){
				boolean salty = fluids[0].getFluid() == Fluids.WATER;

				int batches = Math.min(tier + 1, fluids[0].getAmount() / BATCH_SIZE);
				batches = Math.min(batches, (fluidCap - fluids[1].getAmount()) / BATCH_SIZE);
				if(salty){
					batches = Math.min(batches, 64 - inventory[0].getCount());
				}
				fluids[0].shrink(batches * BATCH_SIZE);
				if(fluids[1].isEmpty()){
					fluids[1] = new FluidStack(CrossroadsFluids.steam.still, BATCH_SIZE * batches);
				}else{
					fluids[1].grow(BATCH_SIZE * batches);
				}

				if(salty){
					if(inventory[0].isEmpty()){
						inventory[0] = new ItemStack(CRItems.dustSalt, batches);
					}else{
						inventory[0].grow(batches);
					}
				}
			}
			markDirty();
		}
	}

	@Override
	public void remove(){
		super.remove();
		waterOpt.invalidate();
		steamOpt.invalidate();
	}

	private final LazyOptional<IFluidHandler> waterOpt = LazyOptional.of(() -> new FluidHandler(0));
	private final LazyOptional<IFluidHandler> steamOpt = LazyOptional.of(() -> new FluidHandler(1));

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){

		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(facing == null){
				return (LazyOptional<T>) globalFluidOpt;
			}
			if(facing == Direction.UP){
				return (LazyOptional<T>) steamOpt;
			}
			return (LazyOptional<T>) waterOpt;
		}

		if(capability == Capabilities.HEAT_CAPABILITY && (facing == null || facing == Direction.DOWN)){
			return (LazyOptional<T>) heatOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;//Automation is not allowed to interact with the salt slot
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;//Automation is not allowed to interact with the salt slot
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.steam_boiler");
	}


	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new SteamBoilerContainer(i, playerInventory, createContainerBuf());
	}
}
