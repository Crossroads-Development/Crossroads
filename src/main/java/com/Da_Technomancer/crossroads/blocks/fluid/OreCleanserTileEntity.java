package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.OreCleanserRec;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.OreCleanserContainer;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.IFluidCapable;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class OreCleanserTileEntity extends InventoryTE implements IFluidCapable, IItemCapable{

	public static final BlockEntityType<OreCleanserTileEntity> TYPE = CRTileEntity.createType(OreCleanserTileEntity::new, CRBlocks.oreCleanser);

	public static final int WATER_USE = 250;

	private int progress = 0;//Out of 50

	private final IFluidHandler inFluidHandler = new FluidHandler(0);
	private final IFluidHandler outFluidHandler = new FluidHandler(1);

	public OreCleanserTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 2);
		fluidProps[0] = new TankProperty(1_000, true, false, f -> CraftingUtil.tagContains(CRFluids.STEAM, f));//Steam
		fluidProps[1] = new TankProperty(1_000, false, true);//Dirty Water
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	public int getProgress(){
		return Math.min(progress, 50);
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(fluids[0].getAmount() >= WATER_USE && fluidProps[1].capacity - fluids[1].getAmount() >= WATER_USE && !inventory[0].isEmpty()){
			Optional<RecipeHolder<OreCleanserRec>> rec = level.getRecipeManager().getRecipeFor(CRRecipes.ORE_CLEANSER_TYPE, this, level);

			ItemStack created;
			if(!rec.isPresent()){
				created = inventory[0].copy();
				created.setCount(1);
			}else{
				ItemStack res;
				ItemStack result = rec.get().value().getResultItem();
				if(result.isEmpty()){
					res = result;
				}else{
					res = result.copy();
				}
				created = res.copy();
			}

			if(!inventory[1].isEmpty() && (inventory[1].getMaxStackSize() - inventory[1].getCount() < created.getCount() || !BlockUtil.sameItem(created, inventory[1]))){
				return;
			}

			progress++;
			setChanged();
			if(progress < 50){
				return;
			}

			fluids[0].shrink(WATER_USE);

			if(fluids[1].isEmpty()){
				fluids[1] = new FluidStack(CRFluids.dirtyWater.getStill(), WATER_USE);
			}else{
				fluids[1].grow(WATER_USE);
			}

			inventory[0].shrink(1);
			if(inventory[1].isEmpty()){
				inventory[1] = created;
			}else{
				inventory[1].grow(created.getCount());
			}
		}else{
			progress = 0;
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		progress = nbt.getInt("prog");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("prog", progress);
	}

	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction dir){
		return (dir == null ? globalFluidHandler : dir == Direction.UP ? outFluidHandler : inFluidHandler);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.ORE_CLEANSER_TYPE, new SingleRecipeInput(stack), level).isPresent();
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.ore_cleanser");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new OreCleanserContainer(id, playerInv, createContainerBuf());
	}
}
