package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.IceboxRec;
import com.Da_Technomancer.crossroads.gui.container.IceboxContainer;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class IceboxTileEntity extends InventoryTE implements IHeatCapable, IItemCapable{

	public static final BlockEntityType<IceboxTileEntity> TYPE = CRTileEntity.createType(IceboxTileEntity::new, CRBlocks.icebox);

	public static final int RATE = 10;
	public static final int MIN_TEMP = -20;

	private int burnTime;
	private int maxBurnTime = 0;

	private IItemHandler itemHandler = new ItemHandler();

	public IceboxTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
	}

	public int getCoolProg(){
		return maxBurnTime == 0 ? 0 : 100 * burnTime / maxBurnTime;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(burnTime != 0){
			if(temp > MIN_TEMP){
				temp = Math.max(MIN_TEMP, temp - RATE);
			}
			if(--burnTime == 0){
				level.setBlock(worldPosition, CRBlocks.icebox.defaultBlockState().setValue(CRProperties.ACTIVE, false), 18);
			}
			setChanged();
		}

		Optional<RecipeHolder<IceboxRec>> rec;
		if(burnTime == 0 && (rec = level.getRecipeManager().getRecipeFor(CRRecipes.COOLING_TYPE, this, level)).isPresent()){
			burnTime = Math.round(rec.get().value().getCooling());
			maxBurnTime = burnTime;
			Item item = inventory[0].getItem();
			inventory[0].shrink(1);
			if(inventory[0].isEmpty() && item.hasCraftingRemainingItem(inventory[0])){
				inventory[0] = item.getCraftingRemainingItem(inventory[0]);
			}
			level.setBlock(worldPosition, CRBlocks.icebox.defaultBlockState().setValue(CRProperties.ACTIVE, true), 18);
			setChanged();
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		burnTime = nbt.getInt("burn");
		maxBurnTime = nbt.getInt("max_burn");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("burn", burnTime);
		nbt.putInt("max_burn", maxBurnTime);
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		if(dir == Direction.UP || dir == null){
			return heatHandler;
		}
		return null;
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return index == 0 && level.getRecipeManager().getRecipeFor(CRRecipes.COOLING_TYPE, new SingleRecipeInput(stack), level).isPresent();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.icebox");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new IceboxContainer(id, playerInv, createContainerBuf());
	}
}
