package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.ColdStorageContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class ColdStorageTileEntity extends InventoryTE{

	@ObjectHolder("cold_storage")
	public static TileEntityType<ColdStorageTileEntity> type = null;

	public static final double LOSS_PER_ITEM = 2;

	private long lastTick;

	public ColdStorageTileEntity(){
		super(type, 18);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	public float getRedstone(){
		float val = 0;
		for(ItemStack stack : inventory){
			if(stack.getItem() instanceof IPerishable){
				val += 1;
			}
		}
		return val;
	}

	@Override
	public void tick(){
		super.tick();

		if(level.isClientSide){
			return;
		}

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
		lastTick += gameTime;
//		setChanged(); Note to self: Calling setChanged() in onLoad() freezes the loading process; bad
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		lastTick = nbt.getLong("last_tick");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		nbt = super.save(nbt);
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
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crossroads.cold_storage");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity){
		return new ColdStorageContainer(id, playerInventory, createContainerBuf());
	}
}
