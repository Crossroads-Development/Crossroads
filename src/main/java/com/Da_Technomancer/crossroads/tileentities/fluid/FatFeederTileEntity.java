package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.FatFeederContainer;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.List;

import com.Da_Technomancer.crossroads.API.templates.ModuleTE.FluidTankHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE.TankProperty;

@ObjectHolder(Crossroads.MODID)
public class FatFeederTileEntity extends InventoryTE{

	@ObjectHolder("fat_feeder")
	private static BlockEntityType<FatFeederTileEntity> type = null;

	private static final int BREED_AMOUNT = 200;
	public static final int MIN_RANGE = 4;
	public static final int MAX_RANGE = 16;

	public FatFeederTileEntity(){
		super(type, 0);
		fluidProps[0] = new TankProperty(10_000, true, true, CRFluids.LIQUID_FAT::contains);
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
		List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(worldPosition.subtract(new Vec3i(range, range, range)), worldPosition.offset(new Vec3i(range, range, range))), EntitySelector.ENTITY_STILL_ALIVE);
		for(Player play : players){
			FoodData food = play.getFoodData();
			int added = Math.min(fluids[0].getAmount() / CRConfig.fatPerValue.get(), 40 - (food.getFoodLevel() + (int) food.getSaturationLevel()));
			if(added < 4){
				continue;
			}
			fluids[0].shrink(added * CRConfig.fatPerValue.get());
			int hungerAdded = Math.min(20 - food.getFoodLevel(), added);
			//The way saturation is coded is weird (defined relative to hunger), and the best way to do this is through nbt.
			CompoundTag nbt = new CompoundTag();
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

		List<AgableMob> animals = level.getEntitiesOfClass(AgableMob.class, new AABB(worldPosition.subtract(new Vec3i(range, range, range)), worldPosition.offset(new Vec3i(range, range, range))), EntitySelector.ENTITY_STILL_ALIVE);

		//Cap out animal feeding at 64, to prevent flooding the world with animals
		if(animals.size() >= 64){
			return;
		}

		//Bobo feature: If this is placed on an Emerald Block, it can feed villagers to make them willing to breed without feeding/trading.
		boolean canBreedVillagers = Tags.Blocks.STORAGE_BLOCKS_EMERALD.contains(level.getBlockState(worldPosition.below()).getBlock());

		for(AgableMob ent : animals){
			if(ent instanceof Animal){
				Animal anim = (Animal) ent;
				if(fluids[0].getAmount() >= BREED_AMOUNT && anim.getAge() == 0 && !anim.isInLove()){
					anim.setInLove(null);
					fluids[0].shrink(BREED_AMOUNT);
					setChanged();
				}
			}else if(ent instanceof Villager && canBreedVillagers){
				Villager vill = (Villager) ent;

				//Vanilla villager bread reqs. as of MC1.14:
				//Must have foodLevel >= 12, growing age == 0
				if(fluids[0].getAmount() >= BREED_AMOUNT && vill.getAge() == 0 && vill.getAge() == 0 && !vill.canBreed()){
					//We need to increase the villager's foodLevel. This is a private field with no setters
					//We can adjust it indirectly, by saving the villager to NBT, modifying the NBT, and then reading from it
					CompoundTag villNBT = new CompoundTag();
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
	public Component getDisplayName(){
		return new TranslatableComponent("container.fat_feeder");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new FatFeederContainer(id, playerInv, createContainerBuf());
	}
}
