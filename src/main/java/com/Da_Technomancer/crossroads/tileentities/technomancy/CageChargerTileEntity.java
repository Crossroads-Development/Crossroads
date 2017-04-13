package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class CageChargerTileEntity extends TileEntity{


	private final IMagicHandler magicHandler = new MagicHandler();
	private ItemStack cage = ItemStack.EMPTY;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}
	
	public void setCage(ItemStack cage){
		this.cage = cage;
		markDirty();
	}
	
	public ItemStack getCage(){
		return cage;
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != EnumFacing.DOWN){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != EnumFacing.DOWN){
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(!cage.isEmpty()){
			nbt.setTag("inv", cage.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		cage = new ItemStack(nbt.getCompoundTag("inv"));
	}
	
	private class MagicHandler implements IMagicHandler{
		
		@Override
		public void setMagic(MagicUnit mag){
			if(mag != null && cage != null){
				if(cage.getTagCompound() == null){
					cage.setTagCompound(new NBTTagCompound());
				}
				NBTTagCompound nbt = cage.getTagCompound();
				int energy = nbt.getInteger("stored_" + MagicElements.ENERGY.name());
				energy += mag.getEnergy();
				energy = Math.min(1024, energy);
				nbt.setInteger("stored_" + MagicElements.ENERGY.name(), energy);
				
				int potential = nbt.getInteger("stored_" + MagicElements.POTENTIAL.name());
				potential += mag.getPotential();
				potential = Math.min(1024, potential);
				nbt.setInteger("stored_" + MagicElements.POTENTIAL.name(), potential);
				
				int stability = nbt.getInteger("stored_" + MagicElements.STABILITY.name());
				stability += mag.getStability();
				stability = Math.min(1024, stability);
				nbt.setInteger("stored_" + MagicElements.STABILITY.name(), stability);
				
				int voi = nbt.getInteger("stored_" + MagicElements.VOID.name());
				voi += mag.getVoid();
				voi = Math.min(1024, voi);
				nbt.setInteger("stored_" + MagicElements.VOID.name(), voi);
			}
		}
	}
}
