package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@ObjectHolder(Crossroads.MODID)
public class AlchemicalTubeTileEntity extends AlchemyCarrierTE implements ConduitBlock.IConduitTE<EnumTransferMode>{

	@ObjectHolder("alchemical_tube")
	public static BlockEntityType<AlchemicalTubeTileEntity> TYPE = null;

	protected boolean[] matches = new boolean[6];
	protected EnumTransferMode[] modes = ConduitBlock.IConduitTE.genModeArray(EnumTransferMode.INPUT);

	public AlchemicalTubeTileEntity(BlockPos pos, BlockState state){
		this(TYPE, pos, state);
	}

	protected AlchemicalTubeTileEntity(BlockEntityType<? extends AlchemicalTubeTileEntity> type, BlockPos pos, BlockState state){
		super(type, pos, state);
	}

	public AlchemicalTubeTileEntity(BlockPos pos, BlockState state, boolean glass){
		this(TYPE, pos, state, glass);
	}

	protected AlchemicalTubeTileEntity(BlockEntityType<? extends AlchemicalTubeTileEntity> type, BlockPos pos, BlockState state, boolean glass){
		super(type, pos, state, glass);
	}

	@Override
	public void setBlockState(BlockState state){
		super.setBlockState(state);
		//When adjusting a side to lock, we need to invalidate the optional in case a side was disconnected
		chemOpt.invalidate();
		chemOpt = LazyOptional.of(() -> handler);
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
		BlockEntity neighTE = level.getBlockEntity(worldPosition.relative(face));
		//Check for a neighbor w/ an alchemy reagent handler of a compatible channel
		LazyOptional<IChemicalHandler> otherOpt;
		return neighTE != null && (otherOpt = neighTE.getCapability(Capabilities.CHEMICAL_CAPABILITY, face.getOpposite())).isPresent() && otherOpt.orElseThrow(NoSuchFieldError::new).getChannel(face.getOpposite()).connectsWith(glass ? EnumContainerType.GLASS : EnumContainerType.CRYSTAL);
	}

	@Nonnull
	@Override
	public boolean[] getHasMatch(){
		return matches;
	}

	@Override
	protected void performTransfer(){
		EnumTransferMode[] modes = getModes();
		for(int i = 0; i < 6; i++){
			Direction side = Direction.from3DDataValue(i);
			BlockEntity te;
			if(modes[i].isConnection()){
				te = level.getBlockEntity(worldPosition.relative(side));
				LazyOptional<IChemicalHandler> otherOpt;
				IChemicalHandler otherHandler;
				if(te != null && (otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent() && (otherHandler = otherOpt.orElseThrow(NullPointerException::new)).getChannel(side.getOpposite()).connectsWith(glass ? EnumContainerType.GLASS : EnumContainerType.CRYSTAL)){
					setData(i, true, modes[i]);

					if(contents.getTotalQty() != 0 && modes[i] == EnumTransferMode.OUTPUT){
						if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
							correctReag();
							setChanged();
						}
					}
				}else{
					setData(i, false, modes[i]);
				}
			}
		}
	}

	protected boolean allowConnect(Direction side){
		//Lazy clooge to let redstone tubes work
		return side == null || modes[side.get3DDataValue()].isConnection();
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		ConduitBlock.IConduitTE.writeConduitNBT(nbt, this);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		ConduitBlock.IConduitTE.readConduitNBT(nbt, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && allowConnect(side)){
			return (LazyOptional<T>) chemOpt;
		}
		return super.getCapability(cap, side);
	}
}
