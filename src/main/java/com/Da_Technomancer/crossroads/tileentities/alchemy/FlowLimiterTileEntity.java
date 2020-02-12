package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.sun.tools.javac.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class FlowLimiterTileEntity extends AlchemyCarrierTE{

	@ObjectHolder("flow_limiter")
	private static TileEntityType<FlowLimiterTileEntity> type = null;
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
			BlockState state = world.getBlockState(pos);
			if(state.has(ESProperties.FACING)){
				facing = state.get(ESProperties.FACING);
				return facing;
			}
			return Direction.DOWN;
		}
		return facing;
	}

	public void wrench(){
		facing = null;
	}

	public void cycleLimit(ServerPlayerEntity player){
		limitIndex += 1;
		limitIndex %= LIMITS.length;
		markDirty();
		CRPackets.sendPacketToPlayer(player, new SendChatToClient(List.of(new TranslationTextComponent("tt.crossroads.flow_limiter.mode", LIMITS[limitIndex])), 25856));//CHAT_ID chosen at random
	}

	@Override
	protected void performTransfer(){
		Direction side = world.getBlockState(pos).get(ESProperties.FACING);
		TileEntity te = world.getTileEntity(pos.offset(side));
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
		Direction outSide = world.getBlockState(pos).get(ESProperties.FACING);
		output[outSide.getIndex()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().getIndex()] = EnumTransferMode.INPUT;
		return output;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		limitIndex = Math.min(nbt.getInt("limit"), LIMITS.length - 1);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("limit", limitIndex);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).get(ESProperties.FACING).getAxis())){
			return (LazyOptional<T>) chemOpt;
		}
		return super.getCapability(cap, side);
	}
}
