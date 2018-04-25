package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;

public class RedsAlchemicalTubeTileEntity extends AlchemyCarrierTE implements IIntReceiver{

	/**
	 * 0: Locked
	 * 1: Out
	 * 2: In
	 */
	private Integer[] connectMode = null;
	private final boolean[] hasMatch = new boolean[6];

	private boolean locked = true;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	public RedsAlchemicalTubeTileEntity(){
		super();
	}

	public RedsAlchemicalTubeTileEntity(boolean glass){
		super(glass);
	}

	private void init(){
		if(connectMode == null){
			connectMode = world.isRemote ? new Integer[] {0, 0, 0, 0, 0, 0} : new Integer[] {1, 1, 1, 1, 1, 1};
		}
	}

	public void setLocked(boolean lockIn){
		init();
		locked = lockIn;
		markDirty();
		for(int i = 0; i < 6; i++){
			ModPackets.network.sendToAllAround(new SendIntToClient(i, locked || !hasMatch[i] ? 0 : connectMode[i], pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
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

	public void markSideChanged(int index){
		init();
		markDirty();
		ModPackets.network.sendToAllAround(new SendIntToClient(index, hasMatch[index] ? connectMode[index] : 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	@Override
	public void receiveInt(int identifier, int message, @Nullable EntityPlayerMP sender){
		if(identifier < 6){
			init();
			connectMode[identifier] = message;
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	public void update(){
		init();
		super.update();
	}

	@Override
	protected void performTransfer(){
		init();
		if(locked){
			return;
		}

		for(int i = 0; i < 6; i++){
			EnumFacing side = EnumFacing.getFront(i);
			TileEntity te;
			
			if(connectMode[i] != 0){
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
				if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
					if(connectMode[i] != 0){
						connectMode[i] = 0;
						markSideChanged(i);
					}
					continue;
				}

				if(!hasMatch[i]){
					hasMatch[i] = true;
					markSideChanged(i);
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
		connectMode = new Integer[] {0, 0, 0, 0, 0, 0};
		for(int i = 0; i < 6; i++){
			connectMode[i] = Math.max(0, nbt.getInteger("mode_" + i));
			hasMatch[i] = nbt.getBoolean("match_" + i);
		}
		locked = nbt.getBoolean("lock");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(connectMode != null){
			for(int i = 0; i < 6; i++){
				nbt.setInteger("mode_" + i, connectMode[i]);
				nbt.setBoolean("match_" + i, hasMatch[i]);
			}
		}
		nbt.setBoolean("lock", locked);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound out = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			out.setInteger("mode_" + i, hasMatch[i] ? connectMode[i] : 0);
		}
		return out;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && !locked && (side == null || connectMode[side.getIndex()] != 0)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && !locked && (side == null || connectMode[side.getIndex()] != 0)){
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
