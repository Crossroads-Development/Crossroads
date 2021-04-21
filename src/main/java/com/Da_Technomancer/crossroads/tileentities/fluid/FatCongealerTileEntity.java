package com.Da_Technomancer.crossroads.tileentities.fluid;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.FatCongealerContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.EdibleBlob;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.tileentities.AbstractShifterTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class FatCongealerTileEntity extends InventoryTE{

	@ObjectHolder("fat_congealer")
	private static TileEntityType<FatCongealerTileEntity> type = null;

	public static final double HUN_PER_SPD = 4D;
	public static final double SAT_PER_SPD = 4D;

	public FatCongealerTileEntity(){
		super(type, 1);
		fluidProps[0] = new TankProperty(10_000, true, false, (Fluid f) -> CRFluids.liquidFat.still == f);
		initFluidManagers();
	}

	@Override
	protected int fluidTanks(){
		return 1;
	}

	@Nonnull
	private Direction getFacing(){
		BlockState state = getBlockState();
		if(state.hasProperty(ESProperties.HORIZ_FACING)){
			return state.getValue(ESProperties.HORIZ_FACING);
		}
		return Direction.NORTH;
	}

	@Override
	public void tick(){
		super.tick();
		if(level.isClientSide){
			return;
		}

		//Eject inventory either into the world or into an inventory.
		//Despite using the method from ItemShifters, this block can't go through transport chutes
		int prevCount = inventory[0].getCount();
		inventory[0] = AbstractShifterTileEntity.ejectItem(level, worldPosition.relative(getFacing()), getFacing(), inventory[0], null);
		if(prevCount != inventory[0].getCount()){
			setChanged();
		}

		//This machine can be disabled by a redstone signal
		if(!level.hasNeighborSignal(worldPosition)){
			TileEntity adjTE;
			LazyOptional<IAxleHandler> otherOpt;
			IAxleHandler topHandler = null;
			IAxleHandler bottomHandler = null;

			int hun = 0;
			int sat = 0;

			if((adjTE = level.getBlockEntity(worldPosition.relative(Direction.UP))) != null && (otherOpt = adjTE.getCapability(Capabilities.AXLE_CAPABILITY, Direction.DOWN)).isPresent()){
				topHandler = otherOpt.orElseThrow(NullPointerException::new);
				hun = (int) Math.min(Math.abs(topHandler.getSpeed()) * HUN_PER_SPD, 20);
			}
			if((adjTE = level.getBlockEntity(worldPosition.relative(Direction.DOWN))) != null && (otherOpt = adjTE.getCapability(Capabilities.AXLE_CAPABILITY, Direction.UP)).isPresent()){
				bottomHandler = otherOpt.orElseThrow(NullPointerException::new);
				sat = (int) Math.min(Math.abs(bottomHandler.getSpeed()) * SAT_PER_SPD, 20);
			}

			if(hun != 0 || sat != 0){
				int fluidUse = CRConfig.fatPerValue.get() * (hun + sat);
				if(fluidUse > fluids[0].getAmount()){
					return;
				}

				if(!inventory[0].isEmpty() && (inventory[0].getCount() == CRItems.edibleBlob.getItemStackLimit(inventory[0]) || EdibleBlob.getHealAmount(inventory[0]) != hun || EdibleBlob.getTrueSat(inventory[0]) != sat)){
					return;//Output is full, or has different stats
				}

				if(topHandler != null){
					topHandler.addEnergy(-hun, false);
				}
				if(bottomHandler != null){
					bottomHandler.addEnergy(-sat, false);
				}
				fluids[0].shrink(fluidUse);

				if(inventory[0].isEmpty()){
					inventory[0] = new ItemStack(CRItems.edibleBlob, 1);
					inventory[0].setTag(EdibleBlob.createNBT(null, hun, sat));
				}else{
					inventory[0].grow(1);
				}
				setChanged();
			}
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	@Override
	public void clearCache(){
		super.clearCache();
		itemOpt.invalidate();
		itemOpt = LazyOptional.of(ItemHandler::new);
	}

	private LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing != Direction.DOWN && facing != Direction.UP && facing != getFacing()){
			return (LazyOptional<T>) globalFluidOpt;
		}
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == null || facing == getFacing())){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return true;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		return false;
	}

	@Override
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.fat_congealer");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
		return new FatCongealerContainer(id, playerInv, createContainerBuf());
	}
}
