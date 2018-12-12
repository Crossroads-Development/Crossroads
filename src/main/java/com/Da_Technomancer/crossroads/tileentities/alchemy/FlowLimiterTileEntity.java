package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class FlowLimiterTileEntity extends AlchemyCarrierTE{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private static final int[] LIMITS = new int[] {1, 2, 4, 8, 16, 32, 64};

	private int limitIndex = 0;

	public FlowLimiterTileEntity(){
		super();
	}

	public FlowLimiterTileEntity(boolean glass){
		super(glass);
	}

	public void cycleLimit(EntityPlayerMP player){
		limitIndex += 1;
		limitIndex %= LIMITS.length;
		markDirty();
		ModPackets.network.sendTo(new SendChatToClient("Reagent movement limit configured to: " + LIMITS[limitIndex], 25856), player);//CHAT_ID chosen at random
	}

	@Override
	protected void performTransfer(){
		EnumFacing side = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
		TileEntity te = world.getTileEntity(pos.offset(side));
		if(contents.getTotalQty() == 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())){
			return;
		}

		IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite());
		EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
		if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
			return;
		}

		int limit = LIMITS[limitIndex];
		ReagentMap transferReag = new ReagentMap();
		for(IReagent type : contents.keySet()){
			int qty = contents.getQty(type);
			int specificLimit = Math.min(qty, limit - otherHandler.getContent(type));
			if(specificLimit > 0){
				transferReag.transferReagent(type, specificLimit, contents);
			}
		}

		boolean changed = otherHandler.insertReagents(transferReag, side.getOpposite(), handler);
		for(IReagent type : transferReag.keySet()){
			contents.transferReagent(type, transferReag.getQty(type), transferReag);
		}

		if(changed){
			correctReag();
			markDirty();
		}
	}



	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		EnumFacing outSide = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
		output[outSide.getIndex()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().getIndex()] = EnumTransferMode.INPUT;
		return output;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		limitIndex = Math.min(nbt.getInteger("limit"), LIMITS.length - 1);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("limit", limitIndex);
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).getValue(EssentialsProperties.FACING).getAxis())){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).getValue(EssentialsProperties.FACING).getAxis())){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}
}
