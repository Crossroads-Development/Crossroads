package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class CageChargerTileEntity extends BlockEntity implements IInfoTE{

	@ObjectHolder("cage_charger")
	private static BlockEntityType<CageChargerTileEntity> type = null;
	
	private ItemStack cage = ItemStack.EMPTY;

	public CageChargerTileEntity(BlockPos pos, BlockState state){
		super(type, pos, state);
	}
	
	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(!cage.isEmpty()){
			BeamUnit stored = BeamCage.getStored(cage);
			chat.add(new TranslatableComponent("tt.crossroads.beam_cage.energy", stored.getEnergy(), BeamCage.CAPACITY));
			chat.add(new TranslatableComponent("tt.crossroads.beam_cage.potential", stored.getPotential(), BeamCage.CAPACITY));
			chat.add(new TranslatableComponent("tt.crossroads.beam_cage.stability", stored.getStability(), BeamCage.CAPACITY));
			chat.add(new TranslatableComponent("tt.crossroads.beam_cage.void", stored.getVoid(), BeamCage.CAPACITY));
		}else{
			chat.add(new TranslatableComponent("tt.crossroads.cage_charger.empty"));
		}
	}
	
	public void setCage(ItemStack cage){
		this.cage = cage;
		setChanged();
	}
	
	public ItemStack getCage(){
		return cage;
	}
	
	public float getRedstone(){
		if(cage.isEmpty()){
			return 0;
		}else{
			return BeamCage.getStored(cage).getPower();
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		beamOpt.invalidate();
		itemOpt.invalidate();
	}

	private final LazyOptional<IBeamHandler> beamOpt = LazyOptional.of(BeamHandler::new);
	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY){
			return (LazyOptional<T>) beamOpt;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		if(!cage.isEmpty()){
			nbt.put("inv", cage.save(new CompoundTag()));
		}
		return nbt;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		cage = ItemStack.of(nbt.getCompound("inv"));
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
					setChanged();
					level.setBlock(worldPosition, CRBlocks.cageCharger.defaultBlockState().setValue(CRProperties.ACTIVE, true), 2);
				}
				return ItemStack.EMPTY;
			}

			return stack;
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot == 0 && !cage.isEmpty() && amount > 0){
				if(!simulate){
					ItemStack out = cage;
					cage = ItemStack.EMPTY;
					setChanged();
					level.setBlock(worldPosition, CRBlocks.cageCharger.defaultBlockState().setValue(CRProperties.ACTIVE, false), 2);
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
			return slot == 0 && stack.getItem() == CRItems.beamCage;
		}
	}
	
	private class BeamHandler implements IBeamHandler{
		
		@Override
		public void setBeam(BeamUnit mag){
			if(!mag.isEmpty() && !cage.isEmpty()){
				BeamUnit cageBeam = BeamCage.getStored(cage);
				int energy = cageBeam.getEnergy();
				int potential = cageBeam.getPotential();
				int stability = cageBeam.getStability();
				int voi = cageBeam.getVoid();

				energy += mag.getEnergy();
				potential += mag.getPotential();
				stability += mag.getStability();
				voi += mag.getVoid();
				cageBeam = new BeamUnit(energy, potential, stability, voi);
				BeamCage.storeBeam(cage, cageBeam);
				setChanged();
			}
		}
	}
}
