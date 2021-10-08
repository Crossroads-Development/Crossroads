package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class FlowLimiterTileEntity extends AlchemyCarrierTE{

	@ObjectHolder("flow_limiter")
	private static BlockEntityType<FlowLimiterTileEntity> type = null;
	private static final int[] LIMITS = new int[] {1, 2, 4, 8, 16, 32, 64};

	private int limitIndex = 0;
	private Direction facing = null;

	public FlowLimiterTileEntity(){
		super(type);
	}

	public FlowLimiterTileEntity(boolean glass){
		super(type, glass);
	}

	public Direction getFacing(){
		if(facing == null){
			BlockState state = level.getBlockState(worldPosition);
			if(state.hasProperty(ESProperties.FACING)){
				facing = state.getValue(ESProperties.FACING);
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
		chat.add(new TranslatableComponent("tt.crossroads.flow_limiter.mode", LIMITS[limitIndex]));
		CRPackets.sendPacketToPlayer(player, new SendChatToClient(chat, 25856));//CHAT_ID chosen at random
	}

	@Override
	protected void performTransfer(){
		Direction side = level.getBlockState(worldPosition).getValue(ESProperties.FACING);
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
		Direction outSide = level.getBlockState(worldPosition).getValue(ESProperties.FACING);
		output[outSide.get3DDataValue()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().get3DDataValue()] = EnumTransferMode.INPUT;
		return output;
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		limitIndex = Math.min(nbt.getInt("limit"), LIMITS.length - 1);
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putInt("limit", limitIndex);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == getBlockState().getValue(ESProperties.FACING).getAxis())){
			return (LazyOptional<T>) chemOpt;
		}
		return super.getCapability(cap, side);
	}
}
