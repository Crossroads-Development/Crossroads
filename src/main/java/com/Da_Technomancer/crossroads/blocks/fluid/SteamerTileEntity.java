package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SteamerContainer;
import com.Da_Technomancer.essentials.api.BlockUtil;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SteamerTileEntity extends InventoryTE{

	public static final BlockEntityType<SteamerTileEntity> TYPE = CRTileEntity.createType(SteamerTileEntity::new, CRBlocks.steamer);

	public static final int FLUID_USE = 200;//Steam per tick
	public static final int REQUIRED = 50;//Number of processing ticks

	private final IFluidHandler steamHandler = new FluidHandler(0);
	private final IFluidHandler waterHandler = new FluidHandler(1);

	private int progress = 0;

	public SteamerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 2);
		fluidProps[0] = new TankProperty(10_000, true, false, f -> CraftingUtil.tagContains(CRFluids.STEAM, f));
		fluidProps[1] = new TankProperty(10_000, false, true);
		initFluidManagers();
	}

	@Override
	public int fluidTanks(){
		return 2;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(Component.translatable("tt.crossroads.boilerplate.progress", progress, REQUIRED));
		super.addInfo(chat, player, hit);
	}

	public int getProgress(){
		return progress;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		RecipeHolder<SmokingRecipe> rec;
		SingleRecipeInput input = new SingleRecipeInput(inventory[0]);
		if(!inventory[0].isEmpty() && (rec = level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, input, level).orElse(null)) != null && (inventory[1].isEmpty() || BlockUtil.sameItem(rec.value().getResultItem(level.registryAccess()), inventory[1]) && inventory[1].getCount() < inventory[1].getMaxStackSize())){
			//Check fluids
			if(fluids[0].getAmount() >= FLUID_USE && fluidProps[1].capacity - fluids[1].getAmount() >= FLUID_USE){
				if(fluids[1].isEmpty()){
					fluids[1] = new FluidStack(CRFluids.distilledWater.getStill(), FLUID_USE);
				}else{
					fluids[1].grow(FLUID_USE);
				}

				fluids[0].shrink(FLUID_USE);

				if(++progress >= REQUIRED){
					progress = 0;
					if(inventory[1].isEmpty()){
						inventory[1] = rec.value().assemble(input, level.registryAccess());
					}else{
						inventory[1].grow(1);
					}
					inventory[0].shrink(1);
				}

				setChanged();
			}
		}else{
			progress = 0;
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		progress = nbt.getInt("progress");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("progress", progress);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && !stack.isEmpty() && level.getRecipeManager().getRecipeFor(RecipeType.SMOKING, new SingleRecipeInput(stack), level).isPresent();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;
	}

	@Override
	@Nullable
	public IFluidHandler getFluidHandler(Direction dir){
		if(dir == Direction.UP || dir == Direction.DOWN){
			return waterHandler;
		}else if(dir != null){
			return steamHandler;
		}
		return super.getFluidHandler(dir);
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.steamer");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new SteamerContainer(id, playerInventory, createContainerBuf());
	}
}
