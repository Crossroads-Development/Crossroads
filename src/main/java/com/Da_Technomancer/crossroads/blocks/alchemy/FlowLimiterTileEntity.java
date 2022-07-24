package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;

public class FlowLimiterTileEntity extends AlchemyCarrierTE{

	public static final BlockEntityType<FlowLimiterTileEntity> TYPE = CRTileEntity.createType(FlowLimiterTileEntity::new, CRBlocks.flowLimiterGlass, CRBlocks.flowLimiterCrystal);

	private static final int[] LIMITS = new int[] {1, 2, 4, 8, 16, 32, 64};

	private int limitIndex = 0;
	private Direction facing = null;

	public FlowLimiterTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public FlowLimiterTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	public Direction getFacing(){
		if(facing == null){
			BlockState state = level.getBlockState(worldPosition);
			if(state.hasProperty(CRProperties.FACING)){
				facing = state.getValue(CRProperties.FACING);
				return facing;
			}
			return Direction.DOWN;
		}
		return facing;
	}

	public void wrench(){
		facing = null;
	}

	public void cycleLimit(ServerPlayer player){
		limitIndex += 1;
		limitIndex %= LIMITS.length;
		setChanged();
		ArrayList<Component> chat = new ArrayList<>(1);
		chat.add(Component.translatable("tt.crossroads.flow_limiter.mode", LIMITS[limitIndex]));
		CRPackets.sendPacketToPlayer(player, new SendChatToClient(chat, 25856));//CHAT_ID chosen at random
	}

	@Override
	protected void performTransfer(){
		Direction side = getBlockState().getValue(CRProperties.FACING);
		BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
		LazyOptional<IChemicalHandler> otherOpt;
		if(contents.getTotalQty() == 0 || te == null || !(otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent()){
			return;
		}

		IChemicalHandler otherHandler = otherOpt.orElseThrow(NullPointerException::new);
		EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
		if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
			return;
		}

		int limit = LIMITS[limitIndex];
		ReagentMap transferReag = new ReagentMap();
		for(IReagent type : contents.keySetReag()){
			int qty = contents.getQty(type);
			int specificLimit = Math.min(qty, limit - otherHandler.getContent(type));
			if(specificLimit > 0){
				transferReag.transferReagent(type, specificLimit, contents);
			}
		}

		boolean changed = otherHandler.insertReagents(transferReag, side.getOpposite(), handler);
		for(IReagent type : transferReag.keySetReag()){
			contents.transferReagent(type, transferReag.getQty(type), transferReag);
		}

		if(changed){
			correctReag();
			setChanged();
		}
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		Direction outSide = level.getBlockState(worldPosition).getValue(CRProperties.FACING);
		output[outSide.get3DDataValue()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().get3DDataValue()] = EnumTransferMode.INPUT;
		return output;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		limitIndex = Math.min(nbt.getInt("limit"), LIMITS.length - 1);
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("limit", limitIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == getBlockState().getValue(CRProperties.FACING).getAxis())){
			return (LazyOptional<T>) chemOpt;
		}
		return super.getCapability(cap, side);
	}
}
