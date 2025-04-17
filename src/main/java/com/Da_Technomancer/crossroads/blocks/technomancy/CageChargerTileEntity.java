package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.beams.IBeamCapable;
import com.Da_Technomancer.crossroads.api.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.BeamCage;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class CageChargerTileEntity extends BlockEntity implements IInfoTE, IBeamCapable, IItemCapable{

	public static final BlockEntityType<CageChargerTileEntity> TYPE = CRTileEntity.createType(CageChargerTileEntity::new, CRBlocks.cageCharger);

	private ItemStack cage = ItemStack.EMPTY;

	private final IBeamHandler beamHandler = new BeamHandler();
	private final IItemHandler itemHandler = new ItemHandler();

	public CageChargerTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(!cage.isEmpty()){
			BeamUnit stored = BeamCage.getStored(cage);
			chat.add(Component.translatable("tt.crossroads.beam_cage.energy", stored.getEnergy(), BeamCage.CAPACITY));
			chat.add(Component.translatable("tt.crossroads.beam_cage.potential", stored.getPotential(), BeamCage.CAPACITY));
			chat.add(Component.translatable("tt.crossroads.beam_cage.stability", stored.getStability(), BeamCage.CAPACITY));
			chat.add(Component.translatable("tt.crossroads.beam_cage.void", stored.getVoid(), BeamCage.CAPACITY));
		}else{
			chat.add(Component.translatable("tt.crossroads.cage_charger.empty"));
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

	@Nullable
	@Override
	public IBeamHandler getBeamHandler(Direction dir){
		return beamHandler;
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		if(!cage.isEmpty()){
			nbt.put("inv", BlockUtil.stackToNBT(cage, pRegistries));
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
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
