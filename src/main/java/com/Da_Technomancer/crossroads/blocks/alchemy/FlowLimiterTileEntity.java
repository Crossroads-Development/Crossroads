package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.apache.commons.lang3.tuple.Pair;


import org.joml.Vector3f;

import javax.annotation.Nullable;

public class FlowLimiterTileEntity extends ReagentHolderTE{

	public static final BlockEntityType<FlowLimiterTileEntity> TYPE = CRTileEntity.createType(FlowLimiterTileEntity::new, CRBlocks.flowLimiterGlass, CRBlocks.flowLimiterCrystal);

	private static final int[] LIMITS = new int[] {1, 2, 4, 8, 16, 32, 64};

	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE_X = new Pair[] {Pair.of(new Vector3f(0, 7 / 16F, 7 / 16F), new Vector3f(1, 1F - 7 / 16F, 1F - 7 / 16F))};
	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE_Y = new Pair[] {Pair.of(new Vector3f(7 / 16F, 0, 7 / 16F), new Vector3f(1F - 7 / 16F, 1, 1F - 7 / 16F))};
	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE_Z = new Pair[] {Pair.of(new Vector3f(7 / 16F, 7 / 16F, 0), new Vector3f(1F - 7 / 16F, 1F - 7 / 16F, 1))};

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
		MiscUtil.displayMessage(player, Component.translatable("tt.crossroads.flow_limiter.mode", LIMITS[limitIndex]));
	}

	@Override
	protected void performTransfer(boolean ignorePhase){
		long worldTick = level.getGameTime();
		if(lastActTick == worldTick){
			//Already acted upon this tick
			return;
		}

		EnumTransferMode[] modes = getModes();
		EnumContainerType channel = getChannel();
		for(int i = 0; i < 6; i++){
			if(modes[i].isOutput()){
				Direction side = Direction.from3DDataValue(i);
				BlockPos adj = worldPosition.relative(side);
				IChemicalHandler otherChemHandler;
				if(contents.getTotalQty() <= 0 || (otherChemHandler = level.getCapability(CRCapabilities.CHEMICAL_CAPABILITY, adj, side.getOpposite())) == null){
					continue;
				}

				EnumContainerType otherChannel = otherChemHandler.getChannel(side.getOpposite());
				EnumTransferMode otherMode = otherChemHandler.getMode(side.getOpposite());
				if(!channel.connectsWith(otherChannel) || !modes[i].connectsWith(otherMode)){
					continue;
				}

				int limit = LIMITS[limitIndex];
				ReagentMap transferReag = new ReagentMap();
				for(IReagent type : contents.keySetReag()){
					int qty = contents.getQty(type);
					int specificLimit = Math.min(qty, limit - otherChemHandler.getContent(type));
					if(specificLimit > 0){
						transferReag.transferReagent(type, specificLimit, contents);
					}
				}

				boolean changed = otherChemHandler.insertReagents(transferReag, side.getOpposite(), chemHandler);
				for(IReagent type : transferReag.keySetReag()){
					contents.transferReagent(type, transferReag.getQty(type), transferReag);
				}

				if(changed){
					lastActTick = worldTick;
					correctReag();
					setChanged();
				}
			}
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
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		limitIndex = Math.min(nbt.getInt("limit"), LIMITS.length - 1);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("limit", limitIndex);
	}

	@Override
	@Nullable
	public IChemicalHandler getChemicalHandler(Direction dir){
		if(dir == null || dir.getAxis() == getBlockState().getValue(CRProperties.FACING).getAxis()){
			return chemHandler;
		}
		return null;
	}

	@Override
	public Pair<Vector3f, Vector3f>[] getRenderVolumes(){
		return switch(getFacing().getAxis()){
			case X -> RENDER_SHAPE_X;
			case Y -> RENDER_SHAPE_Y;
			case Z -> RENDER_SHAPE_Z;
		};
	}
}
