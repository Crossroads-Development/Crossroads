package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.beams.*;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.StasisStorageContainer;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public class StasisStorageTileEntity extends InventoryTE implements IBeamCapable, IItemCapable{

	public static final BlockEntityType<StasisStorageTileEntity> TYPE = CRTileEntity.createType(StasisStorageTileEntity::new, CRBlocks.stasisStorage);

	private long lastTick;

	private final IItemHandler itemHandler = new ItemHandler();
	private final IBeamHandler beamHandler = new BeamHandler();

	public StasisStorageTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
	}

	public float getRedstone(){
		//Return average lifetime remaining for the contents, in seconds
		return AbstractNutrientEnvironmentTileEntity.getAverageLifetime(level, inventory) / 20F;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		long gameTime = level.getGameTime();

		if(gameTime != lastTick){
			//Don't allow tick accelerating this step, or the life span of the contents will actually increase
			for(ItemStack stack : inventory){
				if(stack.getItem() instanceof IPerishable perishable){
					//We reverse the age, without freezing, to prevent damage of ICultivatable
					IPerishable.setSpoilTime(stack, IPerishable.getAndInitSpoilTime(stack, level) + 1, 0);
				}
			}
		}
		lastTick = gameTime;
		setChanged();
	}

	//Called whenever a TileEntity is loaded
	@Override
	public void clearRemoved(){
		super.clearRemoved();
		//Server side only
		if(!level.isClientSide()){
			//While this block is unloaded, the gametime has still been advancing,
			//so the stored items have decayed without this block countering that
			//When we reload, we do a single large freeze operation to account for time spent unloaded, plus a small extra as a buffer
			long gameTime = level.getGameTime();

			if(gameTime > lastTick && lastTick != 0){
				for(ItemStack stack : inventory){
					if(stack.getItem() instanceof IPerishable){
						IPerishable.setSpoilTime(stack, IPerishable.getAndInitSpoilTime(stack, level) + gameTime - lastTick + 5, 0);
					}
				}
			}
			lastTick = gameTime;
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		lastTick = nbt.getLong("last_tick");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putLong("last_tick", lastTick);
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Nullable
	@Override
	public IBeamHandler getBeamHandler(Direction dir){
		return beamHandler;
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
		return Component.translatable("container.crossroads.stasis_storage");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new StasisStorageContainer(id, playerInventory, createContainerBuf());
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(BeamUnit mag){
			if(mag.isEmpty()){
				return;
			}

			EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
			if(align == EnumBeamAlignments.TIME && !mag.isVoidVariant()){
				//Time beams only
				//Rewind time by the power of the beam * BEAM_TIME ticks
				int rewind = mag.getPower() * BeamUtil.BEAM_TIME;
				long gameTime = level.getGameTime();
				for(ItemStack stack : inventory){
					if(stack.getItem() instanceof IPerishable perishable){
						long remaining = IPerishable.getAndInitSpoilTime(stack, level) - gameTime;
						long limit = perishable.getLifetime();
						if(remaining < limit){
							//Don't allow rewinding beyond the original lifetime
							long singleRewind = 0;
							if(remaining < 0){
								//Instantly fully unspoil spoiled items
								singleRewind += -remaining;
							}
							singleRewind = Math.min(rewind + singleRewind, limit - remaining);
							IPerishable.setSpoilTime(stack, remaining + singleRewind, gameTime);
						}
					}
				}
			}
		}
	}
}
