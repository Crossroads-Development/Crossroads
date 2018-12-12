package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.fluids.BlockDistilledWater;
import com.Da_Technomancer.crossroads.fluids.BlockSteam;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SteamBoilerTileEntity extends InventoryTE{

	public static final int BATCH_SIZE = 100;
	public static final int[] TIERS = {100, 200, 300, 400, 500};

	public SteamBoilerTileEntity(){
		super(1);//Salt
		fluidProps[0] = new TankProperty(0,10_000, true, false, (Fluid f) -> f == FluidRegistry.WATER || f == BlockDistilledWater.getDistilledWater());
		fluidProps[1] = new TankProperty(1,10_000, false, true, null);
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
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
		chat.add(inventory[0].getCount() + "/64 salt");
	}

	@Override
	public void update(){
		super.update();
		if(world.isRemote){
			return;
		}

		int tier = HeatUtil.getHeatTier(temp, TIERS);
		
		if(tier != -1){
			temp -= EnergyConverters.degPerSteamBucket(false) * (tier + 1) * (double) BATCH_SIZE / 1000D;

			int fluidCap = fluidProps[0].getCapacity();
			
			if(fluids[0] != null && fluids[0].amount >= BATCH_SIZE && (fluids[1] == null || fluidCap - fluids[1].amount >= BATCH_SIZE) && inventory[0].getCount() < 64){
				boolean salty = fluids[0].getFluid() == FluidRegistry.WATER;

				int batches = Math.min(tier + 1, fluids[0].amount / BATCH_SIZE);
				batches = Math.min(batches, fluids[1] == null ? fluidCap / BATCH_SIZE : (fluidCap - fluids[1].amount) / BATCH_SIZE);
				if(salty){
					batches = Math.min(batches, 64 - inventory[0].getCount());
				}
				fluids[0].amount -= batches * BATCH_SIZE;
				if(fluids[0].amount == 0){
					fluids[0] = null;
				}

				if(fluids[1] == null){
					fluids[1] = new FluidStack(BlockSteam.getSteam(), BATCH_SIZE * batches);
				}else{
					fluids[1].amount += BATCH_SIZE * batches;
				}

				if(salty){
					if(inventory[0].isEmpty()){
						inventory[0] = new ItemStack(ModItems.dustSalt, batches);
					}else{
						inventory[0].grow(batches);
					}
				}
			}
			markDirty();
		}
	}

	private final FluidHandler waterHandler = new FluidHandler(0);
	private final FluidHandler steamHandler = new FluidHandler(1);
	private final FluidHandler innerHandler = new FluidHandler(-1);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){

		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(facing == null){
				return (T) innerHandler;
			}
			if(facing == EnumFacing.UP){
				return (T) steamHandler;
			}
			return (T) waterHandler;
		}

		if(capability == Capabilities.HEAT_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)){
			return (T) heatHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;//Automation is not allowed to interact with the salt slot
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;//Automation is not allowed to interact with the salt slot
	}

	@Override
	public String getName(){
		return "container.steam_boiler";
	}
}
