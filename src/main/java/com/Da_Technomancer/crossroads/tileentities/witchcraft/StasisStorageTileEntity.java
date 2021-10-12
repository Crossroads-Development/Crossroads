package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.BeamUtil;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.StasisStorageContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE.ItemHandler;

@ObjectHolder(Crossroads.MODID)
public class StasisStorageTileEntity extends InventoryTE{

	@ObjectHolder("stasis_storage")
	public static BlockEntityType<StasisStorageTileEntity> type = null;

	private long lastTick;

	public StasisStorageTileEntity(BlockPos pos, BlockState state){
		super(type, 1);
	}

	public float getRedstone(){
		//Return average lifetime remaining for the contents, in seconds
		return AbstractNutrientEnvironmentTileEntity.getAverageLifetime(level, inventory) / 20F;
	}

	@Override
	public void tick(){
		super.tick();

		if(level.isClientSide){
			return;
		}

		long gameTime = level.getGameTime();

		if(gameTime != lastTick){
			//Don't allow tick accelerating this step, or the life span of the contents will actually increase
			for(ItemStack stack : inventory){
				if(stack.getItem() instanceof IPerishable){
					//We reverse the age, without freezing, to prevent damage of ICultivatable
					IPerishable perishable = (IPerishable) stack.getItem();
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
				if(stack.getItem() instanceof IPerishable){
					IPerishable perishable = (IPerishable) stack.getItem();
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
	public CompoundTag save(CompoundTag nbt){
		nbt = super.save(nbt);
		nbt.putLong("last_tick", lastTick);
		return nbt;
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
		return new TranslatableComponent("container.crossroads.stasis_storage");
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
					if(stack.getItem() instanceof IPerishable){
						IPerishable perishable = (IPerishable) stack.getItem();
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
