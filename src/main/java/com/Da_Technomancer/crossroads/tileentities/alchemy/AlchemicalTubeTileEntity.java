package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.alchemy.AlchemicalTube;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
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

	//Whether there exists a block to connect to on each side
	protected final boolean[] hasMatch = new boolean[6];
	//The setting of this block on each side. None means locked, both is unused
	protected final EnumTransferMode[] configure = new EnumTransferMode[] {EnumTransferMode.OUTPUT, EnumTransferMode.OUTPUT, EnumTransferMode.OUTPUT, EnumTransferMode.OUTPUT, EnumTransferMode.OUTPUT, EnumTransferMode.OUTPUT};

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
		return configure;
	}

	public void toggleConfigure(int side){
		switch(configure[side]){
			case INPUT:
				configure[side] = EnumTransferMode.NONE;
				break;
			case OUTPUT:
				configure[side] = EnumTransferMode.INPUT;
				break;
			case NONE:
				configure[side] = EnumTransferMode.OUTPUT;
				break;
			case BOTH:
				//Unsupported
				break;
		}
		updateState();
	}

	protected void updateState(){
		BlockState state = world.getBlockState(pos);
		BlockState newState = state;
		if(state.getBlock() instanceof AlchemicalTube){
			for(int i = 0; i < 6; i++){
				newState = newState.with(CRProperties.CONDUIT_SIDES[i], hasMatch[i] ? configure[i] : EnumTransferMode.NONE);
			}
		}
		if(state != newState){
			world.setBlockState(pos, newState, 2);
		}
	}

	@Override
	protected void performTransfer(){
		boolean changed = false;
		for(int i = 0; i < 6; i++){
			Direction side = Direction.byIndex(i);
			TileEntity te;
			if(configure[i].isConnection()){
				te = world.getTileEntity(pos.offset(side));
				LazyOptional<IChemicalHandler> otherOpt;
				IChemicalHandler otherHandler;
				if(te != null && (otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent() && (otherHandler = otherOpt.orElseThrow(NullPointerException::new)).getChannel(side.getOpposite()).connectsWith(glass ? EnumContainerType.GLASS : EnumContainerType.CRYSTAL)){
					if(!hasMatch[i]){
						hasMatch[i] = true;
						changed = true;
					}

					if(contents.getTotalQty() != 0 && configure[i] == EnumTransferMode.OUTPUT){
						if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
							correctReag();
							markDirty();
						}
					}
				}else{
					if(hasMatch[i]){
						hasMatch[i] = false;
						changed = true;
					}
				}
			}
		}
		if(changed){
			updateState();
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		for(int i = 0; i < 6; i++){
			hasMatch[i] = nbt.getBoolean("match_" + i);
			configure[i] = EnumTransferMode.fromString(nbt.getString("config_" + i));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		for(int i = 0; i < 6; i++){
			nbt.putBoolean("match_" + i, hasMatch[i]);
			nbt.putString("config_" + i, configure[i].getName());
		}
		return nbt;
	}

	protected boolean allowConnect(Direction side){
		//Lazy clooge to let redstone tubes work
		return side == null || configure[side.getIndex()].isConnection();
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
