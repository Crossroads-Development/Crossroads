package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.FireboxContainer;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public class FireboxTileEntity extends InventoryTE implements IHeatCapable, IItemCapable{

	public static final BlockEntityType<FireboxTileEntity> TYPE = CRTileEntity.createType(FireboxTileEntity::new, CRBlocks.firebox);

	public static final int POWER = 10;
	private static final int MAX_TEMP = 15_000;

	private int burnTime;
	private int maxBurnTime = 0;

	public FireboxTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
	}

	public int getBurnProg(){
		return maxBurnTime == 0 ? 0 : 100 * burnTime / maxBurnTime;
	}

	@Override
	public void serverTick(){
		super.serverTick();
		if(burnTime != 0){
			temp = Math.min(MAX_TEMP, temp + POWER);
			if(--burnTime == 0){
				level.setBlock(worldPosition, CRBlocks.firebox.defaultBlockState(), 18);
			}
			setChanged();
		}

		int fuelBurn;
		if(burnTime == 0 && (fuelBurn = inventory[0].getBurnTime(null)) != 0){
			fuelBurn *= CRConfig.fireboxFuelMult.get();
			int configLimit = CRConfig.fireboxCap.get();
			if(configLimit >= 0){
				fuelBurn = Math.min(fuelBurn, configLimit);
			}
			burnTime = fuelBurn;
			maxBurnTime = burnTime;
			Item item = inventory[0].getItem();
			inventory[0].shrink(1);
			if(inventory[0].isEmpty() && item.hasCraftingRemainingItem(inventory[0])){
				inventory[0] = item.getCraftingRemainingItem(inventory[0]);
			}
			level.setBlock(worldPosition, CRBlocks.firebox.defaultBlockState().setValue(CRProperties.ACTIVE, true), 18);
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

	private ItemHandler itemHandler = new ItemHandler();

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
		return index == 0 && stack.getBurnTime(null) != 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 0 && !canPlaceItem(index, stack);//Allow removing empty buckets
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.firebox");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new FireboxContainer(id, playerInv, createContainerBuf());
	}
}
