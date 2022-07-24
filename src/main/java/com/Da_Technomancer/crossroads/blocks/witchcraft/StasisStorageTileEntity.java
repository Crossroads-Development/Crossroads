package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.BeamUtil;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.StasisStorageContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nullable;

public class StasisStorageTileEntity extends InventoryTE{

	public static final BlockEntityType<StasisStorageTileEntity> TYPE = CRTileEntity.createType(StasisStorageTileEntity::new, CRBlocks.stasisStorage);

	private long lastTick;

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
					perishable.setSpoilTime(stack, perishable.getSpoilTime(stack, level) + 1, 0);
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
				if(stack.getItem() instanceof IPerishable perishable){
					perishable.setSpoilTime(stack, perishable.getSpoilTime(stack, level) + gameTime - lastTick + 1, 0);
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
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putLong("last_tick", lastTick);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
		beamOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);
	private final LazyOptional<IBeamHandler> beamOpt = LazyOptional.of(BeamHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.BEAM_CAPABILITY){
			return (LazyOptional<T>) beamOpt;
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
			if(align == EnumBeamAlignments.TIME && mag.getVoid() == 0){
				//Time beams only
				//Rewind time by the power of the beam * BEAM_TIME ticks
				int rewind = mag.getPower() * BeamUtil.BEAM_TIME;
				long gameTime = level.getGameTime();
				for(ItemStack stack : inventory){
					if(stack.getItem() instanceof IPerishable perishable){
						long remaining = perishable.getSpoilTime(stack, level) - gameTime;
						long limit = perishable.getLifetime();
						if(remaining < limit){
							//Don't allow rewinding beyond the original lifetime
							long singleRewind = Math.min(rewind, limit - remaining);
							perishable.setSpoilTime(stack, remaining + singleRewind, gameTime);
						}
					}
				}
			}
		}
	}
}
