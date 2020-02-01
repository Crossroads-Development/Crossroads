package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.alchemy.AlchemicalTube;
import com.Da_Technomancer.crossroads.blocks.alchemy.RedsAlchemicalTube;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class RedsAlchemicalTubeTileEntity extends AlchemicalTubeTileEntity{

	@ObjectHolder("reds_alchemical_tube")
	private static TileEntityType<RedsAlchemicalTubeTileEntity> type = null;

	private Boolean lockCache = null;

	public RedsAlchemicalTubeTileEntity(){
		super(type);
	}

	public RedsAlchemicalTubeTileEntity(boolean glass){
		super(type, glass);
	}

	private boolean isLocked(){
		if(lockCache == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() instanceof RedsAlchemicalTube){
				lockCache = state.get(ESProperties.REDSTONE_BOOL);
			}else{
				return true;
			}
		}
		return lockCache;
	}

	public void wipeCache(){
		lockCache = null;
		updateState();
	}

	@Override
	protected void updateState(){
		BlockState state = world.getBlockState(pos);
		BlockState newState = state;
		if(state.getBlock() instanceof AlchemicalTube){
			for(int i = 0; i < 6; i++){
				newState = newState.with(CRProperties.CONDUIT_SIDES[i], !isLocked() && hasMatch[i] ? configure[i] : EnumTransferMode.NONE);
			}
		}
		if(state != newState){
			world.setBlockState(pos, newState, 2);
		}
	}

	@Override
	protected void performTransfer(){
		if(!isLocked()){
			super.performTransfer();
		}
	}

	@Override
	protected boolean allowConnect(Direction side){
		return !isLocked() && super.allowConnect(side);
	}
}
