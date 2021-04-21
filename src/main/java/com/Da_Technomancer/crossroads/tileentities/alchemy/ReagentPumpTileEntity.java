package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class ReagentPumpTileEntity extends AlchemyCarrierTE{

	@ObjectHolder("reagent_pump")
	private static TileEntityType<ReagentPumpTileEntity> type = null;

	@SuppressWarnings("unchecked")//Darn Java, not being able to verify arrays of parameterized types. Bah Humbug!
	protected final LazyOptional<IChemicalHandler>[] neighCache = new LazyOptional[] {LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty(), LazyOptional.empty()};

	public ReagentPumpTileEntity(){
		super(type);
	}

	public ReagentPumpTileEntity(boolean glass){
		super(type, glass);
	}

	@Override
	protected void performTransfer(){
		EnumTransferMode[] modes = getModes();
		for(int i = 0; i < 6; i++){
			Direction side = Direction.from3DDataValue(i);

			LazyOptional<IChemicalHandler> otherOpt = neighCache[side.get3DDataValue()];
			if(!neighCache[side.get3DDataValue()].isPresent()){
				TileEntity te = level.getBlockEntity(worldPosition.relative(side));
				if(te != null){
					otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite());
					neighCache[side.get3DDataValue()] = otherOpt;
				}
			}
			if(otherOpt.isPresent()){
				IChemicalHandler otherHandler = otherOpt.orElseThrow(NullPointerException::new);

				//Check container type
				EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
				if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
					continue;
				}

				if(modes[i].isOutput() && contents.getTotalQty() != 0){
					if(otherHandler.insertReagents(contents, side.getOpposite(), handler, true)){
						correctReag();
						setChanged();
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (LazyOptional<T>) chemOpt;
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.INPUT, EnumTransferMode.INPUT, EnumTransferMode.INPUT, EnumTransferMode.INPUT};
		boolean outUp = level.getBlockState(worldPosition).getValue(CRProperties.ACTIVE);
		if(outUp){
			output[Direction.UP.get3DDataValue()] = EnumTransferMode.OUTPUT;
			output[Direction.DOWN.get3DDataValue()] = EnumTransferMode.INPUT;
		}else{
			output[Direction.UP.get3DDataValue()] = EnumTransferMode.INPUT;
			output[Direction.DOWN.get3DDataValue()] = EnumTransferMode.OUTPUT;
		}
		return output;
	}
}
