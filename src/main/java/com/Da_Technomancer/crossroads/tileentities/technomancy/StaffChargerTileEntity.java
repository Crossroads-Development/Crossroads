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

public class StaffChargerTileEntity extends TileEntity{


	private final IMagicHandler magicHandler = new MagicHandler();
	private ItemStack staff = ItemStack.EMPTY;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}
	
	public void setStaff(ItemStack staff){
		this.staff = staff;
	}
	
	public ItemStack getStaff(){
		return staff;
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == EnumFacing.DOWN || side == null)){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == EnumFacing.DOWN || side == null)){
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(!staff.isEmpty()){
			staff.writeToNBT(nbt);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if(nbt.hasKey("Count"))
		staff = new ItemStack(nbt);
	}
	
	private class MagicHandler implements IMagicHandler{
		
		@Override
		public void setMagic(MagicUnit mag){
			if(mag != null && staff != null){
				if(staff.getTagCompound() == null){
					staff.setTagCompound(new NBTTagCompound());
				}
				NBTTagCompound nbt = staff.getTagCompound();
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
