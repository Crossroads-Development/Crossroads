package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.ColdStorageContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class ColdStorageTileEntity extends InventoryTE{

	@ObjectHolder("cold_storage")
	public static BlockEntityType<ColdStorageTileEntity> TYPE = null;

	public static final double LOSS_PER_ITEM = 2;

	private long lastTick;

	public ColdStorageTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 18);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	public float getRedstone(){
		//Return average lifetime remaining for the contents, in seconds
		return AbstractNutrientEnvironmentTileEntity.getAverageLifetime(level, inventory) / 20F;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		long gameTime = level.getGameTime();

		double preTemp = temp;
		double biomeTemp = HeatUtil.convertBiomeTemp(level, worldPosition);

		for(ItemStack stack : inventory){
			if(stack.getItem() instanceof IPerishable){
				if(gameTime != lastTick){
					//Don't allow tick accelerating this step, or the life span of the contents will actually increase
					((IPerishable) stack.getItem()).freeze(stack, level, preTemp, 1);
				}

				if(temp < biomeTemp){
					temp = Math.min(temp + LOSS_PER_ITEM, biomeTemp);
				}
			}
		}
		lastTick = gameTime;
		setChanged();
	}

	@Override
	public void onLoad(){
		super.onLoad();
		//While this block is unloaded, the gametime has still been advancing,
		//so the stored items have decayed without this block countering that
		//When we reload, we do a single large freeze operation to account for time spent unloaded, plus a small extra as a buffer
		long gameTime = level.getGameTime();
		if(gameTime > lastTick){
			for(ItemStack stack : inventory){
				if(stack.getItem() instanceof IPerishable){
					((IPerishable) stack.getItem()).freeze(stack, level, temp, gameTime - lastTick + 1);
				}
			}
		}
		lastTick = gameTime;
//		setChanged(); Note to self: Calling setChanged() in onLoad() freezes the loading process; bad
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		lastTick = nbt.getLong("last_tick");
	}

	@Override
	public CompoundTag m_6945_(CompoundTag nbt){
		nbt = super.m_6945_(nbt);
		nbt.putLong("last_tick", lastTick);
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (LazyOptional<T>) heatOpt;
		}

		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return true;//Output slots
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		if(!super.canPlaceItem(index, stack)){
			return false;
		}
		return stack.getItem() instanceof IPerishable;
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.crossroads.cold_storage");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new ColdStorageContainer(id, playerInventory, createContainerBuf());
	}
}
