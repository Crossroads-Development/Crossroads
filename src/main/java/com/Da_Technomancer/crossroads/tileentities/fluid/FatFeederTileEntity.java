package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.FatFeederContainer;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
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
		fluidProps[0] = new TankProperty(10_000, true, true, f -> f == CRFluids.liquidFat.still);
		initFluidManagers();
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Override
	public void tick(){
		if(level.isClientSide){
			return;
		}

		//Player feeding
		float range = (float) Math.abs(fluids[0].getAmount() - fluidProps[0].capacity / 2) / (float) (fluidProps[0].capacity / 2);
		range = (1F - range) * (MAX_RANGE - MIN_RANGE) + MIN_RANGE;
		List<PlayerEntity> players = level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(worldPosition.subtract(new Vector3i(range, range, range)), worldPosition.offset(new Vector3i(range, range, range))), EntityPredicates.ENTITY_STILL_ALIVE);
		for(PlayerEntity play : players){
			FoodStats food = play.getFoodData();
			int added = Math.min(fluids[0].getAmount() / CRConfig.fatPerValue.get(), 40 - (food.getFoodLevel() + (int) food.getSaturationLevel()));
			if(added < 4){
				continue;
			}
			fluids[0].shrink(added * CRConfig.fatPerValue.get());
			int hungerAdded = Math.min(20 - food.getFoodLevel(), added);
			//The way saturation is coded is weird (defined relative to hunger), and the best way to do this is through nbt.
			CompoundNBT nbt = new CompoundNBT();
			food.addAdditionalSaveData(nbt);
			nbt.putInt("foodLevel", hungerAdded + food.getFoodLevel());
			nbt.putFloat("foodSaturationLevel", Math.min(20F - food.getSaturationLevel(), added - hungerAdded) + food.getSaturationLevel());
			food.readAdditionalSaveData(nbt);
			setChanged();
		}


		//Animal breeding
		if(fluids[0].getAmount() < BREED_AMOUNT){
			return;
		}

		List<AgeableEntity> animals = level.getEntitiesOfClass(AgeableEntity.class, new AxisAlignedBB(worldPosition.subtract(new Vector3i(range, range, range)), worldPosition.offset(new Vector3i(range, range, range))), EntityPredicates.ENTITY_STILL_ALIVE);

		//Cap out animal feeding at 64, to prevent flooding the world with animals
		if(animals.size() >= 64){
			return;
		}

		//Bobo feature: If this is placed on an Emerald Block, it can feed villagers to make them willing to breed without feeding/trading.
		boolean canBreedVillagers = Tags.Blocks.STORAGE_BLOCKS_EMERALD.contains(level.getBlockState(worldPosition.below()).getBlock());

		for(AgeableEntity ent : animals){
			if(ent instanceof AnimalEntity){
				AnimalEntity anim = (AnimalEntity) ent;
				if(fluids[0].getAmount() >= BREED_AMOUNT && anim.getAge() == 0 && !anim.isInLove()){
					anim.setInLove(null);
					fluids[0].shrink(BREED_AMOUNT);
					setChanged();
				}
			}else if(ent instanceof VillagerEntity && canBreedVillagers){
				VillagerEntity vill = (VillagerEntity) ent;

				//Vanilla villager bread reqs. as of MC1.14:
				//Must have foodLevel >= 12, growing age == 0
				if(fluids[0].getAmount() >= BREED_AMOUNT && vill.getAge() == 0 && vill.getAge() == 0 && !vill.canBreed()){
					//We need to increase the villager's foodLevel. This is a private field with no setters
					//We can adjust it indirectly, by saving the villager to NBT, modifying the NBT, and then reading from it
					CompoundNBT villNBT = new CompoundNBT();
					vill.addAdditionalSaveData(villNBT);
					villNBT.putByte("FoodLevel", (byte) (villNBT.getByte("FoodLevel") + 12));
					vill.readAdditionalSaveData(villNBT);
					fluids[0].shrink(BREED_AMOUNT);
					setChanged();
				}
			}
		}
	}

	@Override
	protected IFluidHandler createGlobalFluidHandler(){
		return new FluidTankHandler(0);//Allow pipes to go in both directions
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
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
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
