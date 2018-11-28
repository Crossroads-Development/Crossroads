package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class ReagentPumpTileEntity extends AlchemyCarrierTE{

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

	@Override
	protected void performTransfer(){
		EnumTransferMode[] modes = getModes();
		for(int i = 0; i < 6; i++){
			if(modes[i].isOutput()){
				EnumFacing side = EnumFacing.byIndex(i);
				TileEntity te = world.getTileEntity(pos.offset(side));
				if(amount <= 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())){
					continue;
				}

				IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
				EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
				if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
					continue;
				}

				if(amount != 0){
					if(otherHandler.insertReagents(contents, side.getOpposite(), handler, true)){
						correctReag();
						markDirty();
					}
				}
			}
		}
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
