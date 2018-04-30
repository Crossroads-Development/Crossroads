package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;

public class ReagentTankTileEntity extends AlchemyCarrierTE{

	public ReagentTankTileEntity(){
		super();
	}

	public ReagentTankTileEntity(boolean glass){
		super(glass);
	}

	@Override
	public double transferCapacity(){
		return 10_000D;
	}

	public double getAmount(){
		return amount;
	}

	public NBTTagCompound getContentNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(contents[i] != null){
				nbt.setDouble(i + "_am", contents[i].getAmount());
			}
		}
		nbt.setDouble("heat", heat);
		return nbt;
	}

	public void writeContentNBT(NBTTagCompound nbt){
		heat = nbt.getDouble("heat");
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			contents[i] = nbt.hasKey(i + "_am") ? new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am")) : null;
		}
		dirtyReag = true;
	}

	@Override
	public EnumContainerType getChannel(){
		return EnumContainerType.NONE;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH};
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	@Override
	public boolean correctReag(){
		boolean out = super.correctReag();
		boolean destroy = false;

		if(glass){
			for(int i = 0; i < contents.length; i++){
				ReagentStack reag = contents[i];
				if(reag == null){
					continue;
				}
				if(!reag.getType().canGlassContain()){
					heat -= (heat / amount) * reag.getAmount();
					amount -= reag.getAmount();
					destroy |= reag.getType().destroysBadContainer();
					contents[i] = null;
				}
			}
			if(destroy){
				destroyChamber();
				return false;
			}
		}

		return out;
	}

	private boolean broken = false;

	private void destroyChamber(){
		if(!broken){
			broken = true;
			double temp = heat / amount - 273D;
			IBlockState state = world.getBlockState(pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			for(ReagentStack r : contents){
				if(r != null){
					r.getType().onRelease(world, pos, r.getAmount(), temp, r.getPhase(temp), contents);
				}
			}
		}
	}
}
