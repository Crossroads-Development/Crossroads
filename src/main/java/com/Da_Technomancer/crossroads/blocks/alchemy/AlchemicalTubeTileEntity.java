package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.api.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.apache.commons.lang3.tuple.Pair;


import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class AlchemicalTubeTileEntity extends ReagentHolderTE implements ConduitBlock.IConduitTE<EnumTransferMode>, IChemicalCapable{

	public static final BlockEntityType<AlchemicalTubeTileEntity> TYPE = CRTileEntity.createType(AlchemicalTubeTileEntity::new, CRBlocks.alchemicalTubeGlass, CRBlocks.alchemicalTubeCrystal);

	protected boolean[] matches = new boolean[6];
	protected EnumTransferMode[] modes = ConduitBlock.IConduitTE.genModeArray(EnumTransferMode.INPUT);

	private static final Pair<Vector3f, Vector3f> RENDER_SHAPE_CORE = Pair.of(new Vector3f(7F / 16F, 7F / 16F, 7F / 16F), new Vector3f((16F - 7F) / 16F, (16F - 7F) / 16F, (16F - 7F) / 16F));
	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE_EDGE = new Pair[6];

	static{
		RENDER_SHAPE_EDGE[0] = Pair.of(new Vector3f(7F / 16F, 0, 7F / 16F), new Vector3f((16F - 7F) / 16F, 7F / 16F, (16F - 7F) / 16F));
		RENDER_SHAPE_EDGE[1] = Pair.of(new Vector3f(7F / 16F, (16F - 7F) / 16F, 7F / 16F), new Vector3f((16F - 7F) / 16F, 1, (16F - 7F) / 16F));
		RENDER_SHAPE_EDGE[2] = Pair.of(new Vector3f(7F / 16F, 7F / 16F, 0), new Vector3f((16F - 7F) / 16F, (16F - 7F) / 16F, 7F / 16F));
		RENDER_SHAPE_EDGE[3] = Pair.of(new Vector3f(7F / 16F, 7F / 16F, (16F - 7F) / 16), new Vector3f((16F - 7F) / 16F, (16F - 7F) / 16F, 1));
		RENDER_SHAPE_EDGE[4] = Pair.of(new Vector3f(0, 7F / 16F, 7F / 16F), new Vector3f(7F / 16F, (16F - 7F) / 16F, (16F - 7F) / 16F));
		RENDER_SHAPE_EDGE[5] = Pair.of(new Vector3f((16F - 7F) / 16, 7F / 16F, 7F / 16F), new Vector3f(1, (16F - 7F) / 16F, (16F - 7F) / 16F));
	}

	public AlchemicalTubeTileEntity(BlockPos pos, BlockState state){
		this(TYPE, pos, state);
	}

	protected AlchemicalTubeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){
		super(type, pos, state);
	}

	public AlchemicalTubeTileEntity(BlockPos pos, BlockState state, boolean glass){
		this(TYPE, pos, state, glass);
	}

	protected AlchemicalTubeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean glass){
		super(type, pos, state, glass);
	}

	@Override
	public EnumTransferMode[] getModes(){
		return modes;
	}

	@Nonnull
	@Override
	public EnumTransferMode deserialize(String name){
		return ConduitBlock.IConduitTE.deserializeEnumMode(name);
	}

	@Override
	public boolean hasMatch(int side, EnumTransferMode mode){
		Direction face = Direction.from3DDataValue(side);
		Direction opposite = face.getOpposite();
		BlockEntity neighTE = level.getBlockEntity(worldPosition.relative(face));
		//Check for a neighbor w/ an alchemy reagent handler of a compatible channel
		IChemicalHandler otherChemHandler;

		return neighTE != null && (otherChemHandler = level.getCapability(CRCapabilities.CHEMICAL_CAPABILITY, neighTE.getBlockPos(), face.getOpposite())) != null && otherChemHandler.getChannel(opposite).connectsWith(getChannel()) /*&& otherChemHandler.getMode(opposite).connectsWith(mode)*/;
	}

	@Nonnull
	@Override
	public boolean[] getHasMatch(){
		return matches;
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
			if(modes[i].isConnection()){
				Direction side = Direction.from3DDataValue(i);
				BlockPos worldPos = worldPosition.relative(side);
				IChemicalHandler otherChemHandler;
				if((otherChemHandler = level.getCapability(CRCapabilities.CHEMICAL_CAPABILITY, worldPos, side.getOpposite())) == null){
					setData(i, false, modes[i]);
					continue;
				}


				EnumContainerType otherChannel = otherChemHandler.getChannel(side.getOpposite());
				EnumTransferMode otherMode = otherChemHandler.getMode(side.getOpposite());
				if(!channel.connectsWith(otherChannel)/* || !modes[i].connectsWith(otherMode)*/){
					setData(i, false, modes[i]);
					continue;
				}
				setData(i, true, modes[i]);
				if(contents.getTotalQty() == 0 || !modes[i].isOutput()){
					continue;
				}
				if(otherChemHandler.insertReagents(contents, side.getOpposite(), chemHandler, ignorePhase)){
					lastActTick = worldTick;
					correctReag();
					setChanged();
				}
			}
		}
	}

	protected boolean allowConnect(Direction side){
		//Lazy clooge to let redstone tubes work
		return side == null || modes[side.get3DDataValue()].isConnection();
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		ConduitBlock.IConduitTE.writeConduitNBT(nbt, this);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		ConduitBlock.IConduitTE.readConduitNBT(nbt, this);
	}

	@Override
	public Pair<Vector3f, Vector3f>[] getRenderVolumes(){
		for(EnumMatterPhase phase : EnumMatterPhase.values()){
			if(colorDataOnClient[phase.ordinal()] != null && getBlockState().getBlock() instanceof AlchemicalTube tubeBlock){
				Pair<Vector3f, Vector3f>[] result = Arrays.copyOf(RENDER_SHAPE_EDGE, RENDER_SHAPE_EDGE.length);
				BlockState state = getBlockState();
				for(Direction dir : Direction.values()){
					int dirIndex = dir.get3DDataValue();
					if(!tubeBlock.evaluate(state.getValue(CRProperties.CONDUIT_SIDES_SINGLE[dirIndex]), state, this)){
						result[dirIndex] = null;
					}
				}
				if(!phase.flowsDown()){
					result[Direction.DOWN.get3DDataValue()] = RENDER_SHAPE_CORE;
				}else if(!phase.flowsUp()){
					result[Direction.UP.get3DDataValue()] = RENDER_SHAPE_CORE;
				}
				return result;
			}
		}
		return new Pair[0];
	}

	@Override
	@Nullable
	public IChemicalHandler getChemicalHandler(Direction dir){
		if(allowConnect(dir)){
			return chemHandler;
		}
		return null;
	}
}
