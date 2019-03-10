package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.teamacronymcoders.base.nbt.NBT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class ReagentPumpTileEntity extends AlchemyCarrierTE implements IIntReceiver{

	protected final boolean[] hasMatch = new boolean[6];

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	public ReagentPumpTileEntity(){
		super();
	}

	public ReagentPumpTileEntity(boolean glass){
		super(glass);
	}

	public Boolean[] getMatches(){
		return new Boolean[] {hasMatch[0], hasMatch[1], hasMatch[2], hasMatch[3], hasMatch[4], hasMatch[5]};
	}

	protected void markSideChanged(){
		int message = 0;
		for(int i = 0; i < 6; i++){
			if(hasMatch[i]){
				message |= 1 << i;
			}
		}
		ModPackets.network.sendToAllAround(new SendIntToClient((byte) 1, message, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	@Override
	public void receiveInt(byte identifier, int message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == 1){
			for(int i = 0; i < 6; i++){
				hasMatch[i] = ((message >>> i) & 1) == 1;
			}
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	protected void performTransfer(){
		EnumTransferMode[] modes = getModes();
		boolean changedMatch = false;
		for(int i = 0; i < 6; i++){
			EnumFacing side = EnumFacing.byIndex(i);
			TileEntity te = world.getTileEntity(pos.offset(side));
			IChemicalHandler otherHandler;
			if(te != null && (otherHandler = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())) != null){

				//Check container type
				EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
				if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
					if(hasMatch[i]){
						hasMatch[i] = false;
						changedMatch = true;
					}
					continue;
				}

				if(!hasMatch[i]){
					hasMatch[i] = true;
					changedMatch = true;
				}

				if(modes[i].isOutput() && contents.getTotalQty() != 0){
					if(otherHandler.insertReagents(contents, side.getOpposite(), handler, true)){
						correctReag();
						markDirty();
					}
				}
			}else if(hasMatch[i]){
				hasMatch[i] = false;
				changedMatch = true;
			}
		}

		if(changedMatch){
			markSideChanged();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		for(int i = 0; i < 6; i++){
			hasMatch[i] = nbt.getBoolean("match_" + i);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		for(int i = 0; i < 6; i++){
			nbt.setBoolean("match_" + i, hasMatch[i]);
		}
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			nbt.setBoolean("match_" + i, hasMatch[i]);
		}
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.INPUT, EnumTransferMode.INPUT, EnumTransferMode.INPUT, EnumTransferMode.INPUT};
		boolean outUp = world.getBlockState(pos).getValue(Properties.ACTIVE);
		if(outUp){
			output[EnumFacing.UP.getIndex()] = EnumTransferMode.OUTPUT;
			output[EnumFacing.DOWN.getIndex()] = EnumTransferMode.INPUT;
		}else{
			output[EnumFacing.UP.getIndex()] = EnumTransferMode.INPUT;
			output[EnumFacing.DOWN.getIndex()] = EnumTransferMode.OUTPUT;
		}
		return output;
	}
}
