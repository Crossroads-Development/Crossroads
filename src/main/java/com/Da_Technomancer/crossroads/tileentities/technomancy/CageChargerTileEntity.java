package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class CageChargerTileEntity extends TileEntity implements IInfoTE{

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		if(!cage.isEmpty()){
			BeamUnit cageBeam = BeamCage.getStored(cage);
			if(cageBeam == null){
				cageBeam = new BeamUnit(0, 0, 0, 0);
			}
			chat.add("Stored: [Energy: " + cageBeam.getEnergy() + ", Potential: " + cageBeam.getPotential() + ", Stability: " + cageBeam.getStability() + ", Void: " + cageBeam.getVoid() + "]");
		}
	}
	
	private final IBeamHandler magicHandler = new BeamHandler();
	private ItemStack cage = ItemStack.EMPTY;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}
	
	public void setCage(ItemStack cage){
		this.cage = cage;
		markDirty();
	}
	
	public ItemStack getCage(){
		return cage;
	}

	private final RedsHandler redsHandler = new RedsHandler();
	private final ItemHandler itemHandler = new ItemHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY || cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY || cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY){
			return (T) magicHandler;
		}
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redsHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) itemHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		if(!cage.isEmpty()){
			nbt.put("inv", cage.writeToNBT(new CompoundNBT()));
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		cage = new ItemStack(nbt.getCompound("inv"));
	}

	private class ItemHandler implements IItemHandler{

		@Override
		public int getSlots(){
			return 1;
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot){
			return cage;
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			if(isItemValid(slot, stack) && cage.isEmpty()){
				if(!simulate){
					cage = stack;
					markDirty();
					world.setBlockState(pos, CrossroadsBlocks.cageCharger.getDefaultState().with(Properties.ACTIVE, true));
				}

				return ItemStack.EMPTY;
			}

			return stack;
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot == 0 && !cage.isEmpty() && amount > 0){
				ItemStack stored = cage;

				if(!simulate){
					ItemStack out = cage;
					cage = ItemStack.EMPTY;
					markDirty();
					world.setBlockState(pos, CrossroadsBlocks.cageCharger.getDefaultState().with(Properties.ACTIVE, false));
					return out;
				}

				return cage;
			}

			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return slot == 0 && stack.getItem() == CrossroadsItems.beamCage;
		}
	}

	private class RedsHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean measure){
			if(measure){
				if(!cage.isEmpty()){
					BeamUnit cageBeam = BeamCage.getStored(cage);
					return cageBeam == null ? 0 : cageBeam.getPower();
				}
			}
			return 0;
		}
	}
	
	private class BeamHandler implements IBeamHandler{
		
		@Override
		public void setMagic(BeamUnit mag){
			if(mag != null && cage != null){
				BeamUnit cageBeam = BeamCage.getStored(cage);
				if(cageBeam == null){
					cageBeam = new BeamUnit(0, 0, 0, 0);
				}


				int energy = cageBeam.getEnergy();
				int potential = cageBeam.getPotential();
				int stability = cageBeam.getStability();
				int voi = cageBeam.getVoid();

				energy += mag.getEnergy();
				energy = Math.min(BeamCage.CAPACITY, energy);

				potential += mag.getPotential();
				potential = Math.min(BeamCage.CAPACITY, potential);

				stability += mag.getStability();
				stability = Math.min(BeamCage.CAPACITY, stability);

				voi += mag.getVoid();
				voi = Math.min(BeamCage.CAPACITY, voi);

				cageBeam = new BeamUnit(energy, potential, stability, voi);

				if(cageBeam.getPower() != 0){
					markDirty();
				}

				BeamCage.storeBeam(cage, cageBeam.getPower() == 0 ? null : cageBeam);
			}
		}
	}
}
