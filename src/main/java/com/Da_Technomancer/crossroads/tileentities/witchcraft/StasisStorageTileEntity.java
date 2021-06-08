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
public class StasisStorageTileEntity extends InventoryTE{

	@ObjectHolder("stasis_storage")
	public static TileEntityType<StasisStorageTileEntity> type = null;

	private long lastTick;

	public StasisStorageTileEntity(){
		super(type, 1);
	}

	public float getRedstone(){
		//Return average lifetime remaining for the contents, in seconds
		int totalLifetime = 0;//Ticks
		int itemCount = 0;
		long currentTime = level.getGameTime();
		for(ItemStack stack : inventory){
			if(stack.getItem() instanceof IPerishable){
				long spoilTime = ((IPerishable) stack.getItem()).getSpoilTime(stack, level);
				itemCount++;
				if(spoilTime > currentTime){
					totalLifetime += spoilTime - currentTime;
				};
			}
		}
		if(itemCount == 0){
			return 0;
		}
		return (float) totalLifetime / itemCount / 20;
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
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crossroads.stasis_storage");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity){
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
