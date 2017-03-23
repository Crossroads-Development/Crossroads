package com.Da_Technomancer.crossroads.API.technomancy;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class PrototypeOwnerInfo{
	
	public int dim;
	@Nullable
	public BlockPos pos;
	public boolean loaded;
	
	public PrototypeOwnerInfo(int dim, BlockPos pos, boolean loaded){
		this.dim = dim;
		this.pos = pos;
		this.loaded = loaded;
	}
	
	protected NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt.setInteger("dim", dim);
		if(pos != null){
			nbt.setLong("pos", pos.toLong());
		}
		return nbt;
	}
	
	protected static PrototypeOwnerInfo readFromNBT(NBTTagCompound nbt){
		return new PrototypeOwnerInfo(nbt.getInteger("dim"), nbt.hasKey("pos") ? BlockPos.fromLong(nbt.getLong("pos")) : null, false);
	}
}
