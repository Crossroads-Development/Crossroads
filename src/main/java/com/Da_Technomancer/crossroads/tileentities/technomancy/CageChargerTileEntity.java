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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class CageChargerTileEntity extends TileEntity implements IInfoTE{

	@ObjectHolder("cage_charger")
	private static TileEntityType<CageChargerTileEntity> type = null;
	
	private ItemStack cage = ItemStack.EMPTY;

	public CageChargerTileEntity(){
		super(type);
	}
	
	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		if(!cage.isEmpty()){
			BeamUnit stored = BeamCage.getStored(cage);
			chat.add(new TranslationTextComponent("tt.crossroads.beam_cage.energy", stored.getEnergy(), BeamCage.CAPACITY));
			chat.add(new TranslationTextComponent("tt.crossroads.beam_cage.potential", stored.getPotential(), BeamCage.CAPACITY));
			chat.add(new TranslationTextComponent("tt.crossroads.beam_cage.stability", stored.getStability(), BeamCage.CAPACITY));
			chat.add(new TranslationTextComponent("tt.crossroads.beam_cage.void", stored.getVoid(), BeamCage.CAPACITY));
		}else{
			chat.add(new TranslationTextComponent("tt.crossroads.cage_charger.empty"));
		}
	}
	
	public void setCage(ItemStack cage){
		this.cage = cage;
		markDirty();
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
	public void remove(){
		super.remove();
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
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		if(!cage.isEmpty()){
			nbt.put("inv", cage.write(new CompoundNBT()));
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		cage = ItemStack.read(nbt.getCompound("inv"));
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
					world.setBlockState(pos, CRBlocks.cageCharger.getDefaultState().with(CRProperties.ACTIVE, true), 2);
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
					markDirty();
					world.setBlockState(pos, CRBlocks.cageCharger.getDefaultState().with(CRProperties.ACTIVE, false), 2);
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
		public void setMagic(BeamUnit mag){
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
				markDirty();
			}
		}
	}
}
