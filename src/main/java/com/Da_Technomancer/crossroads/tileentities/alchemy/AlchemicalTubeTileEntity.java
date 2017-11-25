package com.Da_Technomancer.crossroads.tileentities.alchemy;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AbstractAlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class AlchemicalTubeTileEntity extends AbstractAlchemyCarrierTE implements IIntReceiver{

	private final Integer[] connectMode = {0, 0, 0, 0, 0, 0};

	public AlchemicalTubeTileEntity(){
		super();
	}

	public AlchemicalTubeTileEntity(boolean glass){
		super(glass);
	}

	public Integer[] getConnectMode(boolean forRender){
		return forRender ? new Integer[] {Math.max(0, connectMode[0]), Math.max(0, connectMode[1]), Math.max(0, connectMode[2]), Math.max(0, connectMode[3]), Math.max(0, connectMode[4]), Math.max(0, connectMode[5])} : connectMode;
	}

	public void markSideChanged(int index){
		markDirty();
		ModPackets.network.sendToAllAround(new SendIntToClient(index, connectMode[index], pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	@Override
	public void receiveInt(int identifier, int message, @Nullable EntityPlayerMP sender){
		if(identifier < 6){
			connectMode[identifier] = message;
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}
	
	@Override
	protected void performTransfer(){
		for(int i = 0; i < 6; i++){
			EnumFacing side = EnumFacing.getFront(i);
			TileEntity te = null;
			if(connectMode[i] != -1){
				te = world.getTileEntity(pos.offset(side));
				if(te == null){
					if(connectMode[i] != 0){
						connectMode[i] = 0;
						markSideChanged(i);
					}
					continue;
				}
				if(!te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())){
					if(connectMode[i] != 0){
						connectMode[i] = 0;
						markSideChanged(i);
					}
					continue;
				}

				IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
				EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
				if(cont != EnumContainerType.NONE && (cont == EnumContainerType.GLASS ? !glass : glass)){
					if(connectMode[i] != 0){
						connectMode[i] = 0;
						markSideChanged(i);
					}
					continue;
				}

				if(connectMode[i] == 0){
					connectMode[i] = 1;
					markSideChanged(i);
					continue;
				}else if(amount != 0 && connectMode[i] == 1){
					if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
						correctReag();
						markDirty();
					}
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		for(int i = 0; i < 6; i++){
			connectMode[i] = nbt.getInteger("mode_" + i);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		for(int i = 0; i < 6; i++){
			nbt.setInteger("mode_" + i, connectMode[i]);
		}
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound out = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			out.setInteger("mode_" + i, connectMode[i]);
		}
		return out;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || connectMode[side.getIndex()] != -1)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || connectMode[side.getIndex()] != -1)){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = new EnumTransferMode[6];
		for(int i = 0; i < 6; i++){
			output[i] = connectMode[i] <= 0 ? EnumTransferMode.NONE : connectMode[i] == 1 ? EnumTransferMode.OUTPUT : EnumTransferMode.INPUT;
		}
		return output;
	}
}
