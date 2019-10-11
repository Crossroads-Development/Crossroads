package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class RedsAlchemicalTubeTileEntity extends AlchemicalTubeTileEntity{

	private boolean locked = true;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	public RedsAlchemicalTubeTileEntity(){
		super();
	}

	public RedsAlchemicalTubeTileEntity(boolean glass){
		super(glass);
	}

	public void setLocked(boolean lockIn){
		init();
		locked = lockIn;
		markDirty();
		for(int i = 0; i < 6; i++){
			CrossroadsPackets.network.sendToAllAround(new SendIntToClient((byte) i, locked || !hasMatch[i] ? 0 : connectMode[i], pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}

	@Override
	protected void performTransfer(){
		if(!locked){
			super.performTransfer();
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		locked = nbt.getBoolean("lock");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("lock", locked);
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT out = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			out.putInt("mode_" + i, !locked && hasMatch[i] ? connectMode[i] : 0);
		}
		return out;
	}


	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || connectMode[side.getIndex()] != 0)){
			return !locked;//The locked check is deferred until the return to prevent calling super.hasCapability, which the normal alchemical tube would return true on
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || connectMode[side.getIndex()] != 0)){
			return !locked ? (T) handler : null;
		}
		return super.getCapability(cap, side);
	}
}
