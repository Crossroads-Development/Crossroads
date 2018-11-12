package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.fluids.BlockLiquidFat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.List;

public class FatFeederTileEntity extends InventoryTE{

	public FatFeederTileEntity(){
		super(0);
		fluidProps[0] = new TankProperty(0, 10_000, true, true, (Fluid f) -> f == BlockLiquidFat.getLiquidFat());
	}

	private static final int BREED_AMOUNT = 200;

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(fluids[0] != null){
			float range = (float) Math.abs(fluids[0].amount - fluidProps[0].getCapacity() / 2) / (float) (fluidProps[0].getCapacity() / 2);
			range = (1F - range) * 12 + 4;
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.subtract(new Vec3i(range, range, range)), pos.add(new Vec3i(range, range, range))), EntitySelectors.IS_ALIVE);
			for(EntityPlayer play : players){
				FoodStats food = play.getFoodStats();
				int added = Math.min(fluids[0].amount / EnergyConverters.FAT_PER_VALUE, 40 - (food.getFoodLevel() + (int) food.getSaturationLevel()));
				if(added < 4){
					continue;
				}
				fluids[0].amount -= added * EnergyConverters.FAT_PER_VALUE;
				int hungerAdded = Math.min(20 - food.getFoodLevel(), added);
				//The way saturation is coded is weird (defined relative to hunger), and the best way to do this is through nbt.
				NBTTagCompound nbt = new NBTTagCompound();
				food.writeNBT(nbt);
				nbt.setInteger("foodLevel", hungerAdded + food.getFoodLevel());
				nbt.setFloat("foodSaturationLevel", Math.min(20F - food.getSaturationLevel(), added - hungerAdded) + food.getSaturationLevel());
				food.readNBT(nbt);
				markDirty();
				if(fluids[0].amount <= 0){
					fluids[0] = null;
					return;
				}
			}

			List<EntityAnimal> animals = world.getEntitiesWithinAABB(EntityAnimal.class, new AxisAlignedBB(pos.subtract(new Vec3i(range, range, range)), pos.add(new Vec3i(range, range, range))), EntitySelectors.IS_ALIVE);

			if(animals.size() >= 32){
				return;
			}

			for(EntityAnimal anim : animals){
				if(fluids[0].amount >= BREED_AMOUNT && anim.getGrowingAge() == 0 && !anim.isInLove()){
					anim.setInLove(null);
					fluids[0].amount -= BREED_AMOUNT;
					if(fluids[0].amount <= 0){
						fluids[0] = null;
						return;
					}
				}
			}
		}
	}

	private final FluidHandler mainHandler = new FluidHandler(0);

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (T) mainHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public String getName(){
		return "Fat Feeder";
	}
}
