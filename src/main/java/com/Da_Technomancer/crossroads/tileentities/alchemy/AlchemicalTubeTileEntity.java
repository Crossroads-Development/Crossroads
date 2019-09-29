package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;

public class AlchemicalTubeTileEntity extends AlchemyCarrierTE implements IIntReceiver{

	/**
	 * 0: Locked
	 * 1: Out
	 * 2: In
	 */
	protected Integer[] connectMode = null;
	protected final boolean[] hasMatch = new boolean[6];

	public AlchemicalTubeTileEntity(){
		super();
	}

	public AlchemicalTubeTileEntity(boolean glass){
		super(glass);
	}

	public Integer[] getConnectMode(boolean forRender){
		init();
		if(forRender && !world.isRemote){
			Integer[] out = new Integer[6];
			for(int i = 0; i < 6; i++){
				out[i] = hasMatch[i] ? connectMode[i] : 0;
			}
			return out;
		}
		return connectMode;
	}

	protected void init(){
		if(connectMode == null){
			connectMode = world.isRemote ? new Integer[] {0, 0, 0, 0, 0, 0} : new Integer[] {1, 1, 1, 1, 1, 1};
		}
	}

	public void markSideChanged(int index){
		init();
		markDirty();
		CrossroadsPackets.network.sendToAllAround(new SendIntToClient((byte) index, hasMatch[index] ? connectMode[index] : 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	@Override
	public void receiveInt(byte identifier, int message, @Nullable ServerPlayerEntity sender){
		if(identifier < 6){
			init();
			connectMode[identifier] = message;
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	public void tick(){
		init();
		super.tick();
	}

	protected boolean isCompatible(EnumContainerType otherType){
		return otherType == EnumContainerType.NONE || (otherType == EnumContainerType.GLASS) == glass;
	}

	@Override
	protected void performTransfer(){
		init();
		for(int i = 0; i < 6; i++){
			Direction side = Direction.byIndex(i);
			TileEntity te;
			
			if(connectMode[i] != 0){
				te = world.getTileEntity(pos.offset(side));
				IChemicalHandler otherHandler;
				if(te != null && (otherHandler = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())) != null && isCompatible(otherHandler.getChannel(side.getOpposite()))){
					if(!hasMatch[i]){
						hasMatch[i] = true;
						markSideChanged(i);
					}

					if(contents.getTotalQty() != 0 && connectMode[i] == 1){
						if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
							correctReag();
							markDirty();
						}
					}
				}else{
					if(hasMatch[i]){
						hasMatch[i] = false;
						markSideChanged(i);
					}
				}
			}
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		connectMode = new Integer[] {0, 0, 0, 0, 0, 0};
		for(int i = 0; i < 6; i++){
			connectMode[i] = Math.max(0, nbt.getInt("mode_" + i));
			hasMatch[i] = nbt.getBoolean("match_" + i);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		if(connectMode != null){
			for(int i = 0; i < 6; i++){
				nbt.putInt("mode_" + i, connectMode[i]);
				nbt.putBoolean("match_" + i, hasMatch[i]);
			}
		}
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT out = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			out.putInt("mode_" + i, hasMatch[i] ? connectMode[i] : 0);
		}
		return out;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		init();
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || connectMode[side.getIndex()] != 0)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		init();
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || connectMode[side.getIndex()] != 0)){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = new EnumTransferMode[6];
		init();
		for(int i = 0; i < 6; i++){
			output[i] = connectMode[i] == 0 ? EnumTransferMode.NONE : connectMode[i] == 1 ? EnumTransferMode.OUTPUT : EnumTransferMode.INPUT;
		}
		return output;
	}
}
