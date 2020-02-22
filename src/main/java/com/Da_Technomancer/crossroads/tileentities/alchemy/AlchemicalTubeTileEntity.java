package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class AlchemicalTubeTileEntity extends AlchemyCarrierTE{

	@ObjectHolder("alchemical_tube")
	private static TileEntityType<AlchemicalTubeTileEntity> type = null;

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
	protected EnumTransferMode[] getModes(){
		BlockState state = getBlockState();
		EnumTransferMode[] modes = new EnumTransferMode[6];
		for(int i = 0; i < 6; i++){
			modes[i] = state.get(CRProperties.CONDUIT_SIDES_SINGLE[i]);
		}
		return modes;
	}

	private void updateConnect(int side, boolean newVal){
		BlockState state = getBlockState();
		if(state.get(CRProperties.HAS_MATCH_SIDES[side]) != newVal){
			state = state.with(CRProperties.HAS_MATCH_SIDES[side], newVal);
			world.setBlockState(pos, state);
			updateContainingBlockInfo();
		}
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
					updateConnect(i, true);

					if(contents.getTotalQty() != 0 && modes[i] == EnumTransferMode.OUTPUT){
						if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
							correctReag();
							markDirty();
						}
					}
				}else{
					updateConnect(i, false);
				}
			}
		}
	}

	protected boolean allowConnect(Direction side){
		//Lazy clooge to let redstone tubes work
		return side == null || getBlockState().get(CRProperties.CONDUIT_SIDES_SINGLE[side.getIndex()]).isConnection();
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
