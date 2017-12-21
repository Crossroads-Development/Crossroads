package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyGlasswareHolderTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class PhialHolderTileEntity extends AlchemyGlasswareHolderTE{

	@Override
	public AbstractGlassware getPhialType(){
		return ModItems.phial;
	}

	@Override
	protected void performTransfer(){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == ModBlocks.phialHolder && state.getValue(Properties.ACTIVE)){
			EnumFacing side = EnumFacing.UP;
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(amount <= 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())){
				return;
			}

			IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
			EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
			if(cont != EnumContainerType.NONE && (cont == EnumContainerType.GLASS ? !glass : glass)){
				return;
			}

			if(amount != 0){
				if(otherHandler.insertReagents(contents, side.getOpposite(), handler, state.getValue(Properties.REDSTONE_BOOL))){
					correctReag();
					markDirty();
				}
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] modes = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		if(world.getBlockState(pos).getValue(Properties.ACTIVE)){
			modes[1] = EnumTransferMode.BOTH;
		}
		return modes;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if((side == null || side == EnumFacing.UP) && cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
			if(world.getBlockState(pos).getValue(Properties.ACTIVE)){
				return true;
			}
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if((side == null || side == EnumFacing.UP) && cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY){
			if(world.getBlockState(pos).getValue(Properties.ACTIVE)){
				return (T) handler;
			}
		}
		return super.getCapability(cap, side);
	}
}
