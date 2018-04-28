package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.HashSet;

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
}
