package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@ObjectHolder(Crossroads.MODID)
public class AlchemicalTubeTileEntity extends AlchemyCarrierTE implements ConduitBlock.IConduitTE<EnumTransferMode>{

	@ObjectHolder("alchemical_tube")
	private static TileEntityType<AlchemicalTubeTileEntity> type = null;

	protected boolean[] matches = new boolean[6];
	protected EnumTransferMode[] modes = ConduitBlock.IConduitTE.genModeArray(EnumTransferMode.INPUT);

	public AlchemicalTubeTileEntity(){
		this(type);
	}

	protected AlchemicalTubeTileEntity(TileEntityType<? extends AlchemicalTubeTileEntity> type){
		super(type);
	}

	public AlchemicalTubeTileEntity(boolean glass){
		this(type, glass);
	}

	protected AlchemicalTubeTileEntity(TileEntityType<? extends AlchemicalTubeTileEntity> type, boolean glass){
		super(type, glass);
	}

	@Override
	public void updateContainingBlockInfo(){
		super.updateContainingBlockInfo();
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
		Direction face = Direction.byIndex(side);
		TileEntity neighTE = world.getTileEntity(pos.offset(face));
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
			Direction side = Direction.byIndex(i);
			TileEntity te;
			if(modes[i].isConnection()){
				te = world.getTileEntity(pos.offset(side));
				LazyOptional<IChemicalHandler> otherOpt;
				IChemicalHandler otherHandler;
				if(te != null && (otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent() && (otherHandler = otherOpt.orElseThrow(NullPointerException::new)).getChannel(side.getOpposite()).connectsWith(glass ? EnumContainerType.GLASS : EnumContainerType.CRYSTAL)){
					setData(i, true, modes[i]);

					if(contents.getTotalQty() != 0 && modes[i] == EnumTransferMode.OUTPUT){
						if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
							correctReag();
							markDirty();
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
		return side == null || modes[side.getIndex()].isConnection();
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		ConduitBlock.IConduitTE.writeConduitNBT(nbt, this);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
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
