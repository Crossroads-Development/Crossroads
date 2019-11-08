package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CrossroadsFluids;
import com.Da_Technomancer.crossroads.gui.container.FatFeederContainer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class FatFeederTileEntity extends InventoryTE{

	@ObjectHolder("fat_feeder")
	private static TileEntityType<FatFeederTileEntity> type = null;

	private static final int BREED_AMOUNT = 200;
	public static final int MIN_RANGE = 4;
	public static final int MAX_RANGE = 16;

	public FatFeederTileEntity(){
		super(type, 0);
		fluidProps[0] = new TankProperty(10_000, true, true, (Fluid f) -> f == CrossroadsFluids.liquidFat.still);
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}

		//Player feeding
		float range = (float) Math.abs(fluids[0].getAmount() - fluidProps[0].capacity / 2) / (float) (fluidProps[0].capacity / 2);
		range = (1F - range) * (MAX_RANGE - MIN_RANGE) + MIN_RANGE;
		List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos.subtract(new Vec3i(range, range, range)), pos.add(new Vec3i(range, range, range))), EntityPredicates.IS_ALIVE);
		for(PlayerEntity play : players){
			FoodStats food = play.getFoodStats();
			int added = Math.min(fluids[0].getAmount() / EnergyConverters.FAT_PER_VALUE, 40 - (food.getFoodLevel() + (int) food.getSaturationLevel()));
			if(added < 4){
				continue;
			}
			fluids[0].shrink(added * EnergyConverters.FAT_PER_VALUE);
			int hungerAdded = Math.min(20 - food.getFoodLevel(), added);
			//The way saturation is coded is weird (defined relative to hunger), and the best way to do this is through nbt.
			CompoundNBT nbt = new CompoundNBT();
			food.write(nbt);
			nbt.putInt("foodLevel", hungerAdded + food.getFoodLevel());
			nbt.putFloat("foodSaturationLevel", Math.min(20F - food.getSaturationLevel(), added - hungerAdded) + food.getSaturationLevel());
			food.read(nbt);
			markDirty();
		}


		//Animal breeding
		if(fluids[0].getAmount() < BREED_AMOUNT){
			return;
		}

		List<AgeableEntity> animals = world.getEntitiesWithinAABB(AgeableEntity.class, new AxisAlignedBB(pos.subtract(new Vec3i(range, range, range)), pos.add(new Vec3i(range, range, range))), EntityPredicates.IS_ALIVE);

		//Cap out animal feeding at 64, to prevent flooding the world with animals
		if(animals.size() >= 64){
			return;
		}

		//Bobo feature: If this is placed on an Emerald Block, it can feed villagers to make them willing to breed without feeding/trading. It does not bypass the village size requirement
		boolean canBreedVillagers = world.getBlockState(pos.down()).getBlock() == Blocks.EMERALD_BLOCK;

		for(AgeableEntity ent : animals){
			if(ent instanceof AnimalEntity){
				AnimalEntity anim = (AnimalEntity) ent;
				if(fluids[0].getAmount() >= BREED_AMOUNT && anim.getGrowingAge() == 0 && !anim.isInLove()){
					anim.setInLove(null);
					fluids[0].shrink(BREED_AMOUNT);
				}
			}else if(ent instanceof VillagerEntity && canBreedVillagers){
				VillagerEntity vill = (VillagerEntity) ent;

				//TODO villager breeding changed
				/*
				if(fluids[0].getAmount() >= BREED_AMOUNT && vill.getGrowingAge() == 0 && !vill.getIsWillingToMate(false)){
					vill.setIsWillingToMate(true);
					fluids[0].shrink(BREED_AMOUNT);
				}
				*/
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return (LazyOptional<T>) globalFluidOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.fat_feeder");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new FatFeederContainer(id, playerInv, createContainerBuf());
	}
}
