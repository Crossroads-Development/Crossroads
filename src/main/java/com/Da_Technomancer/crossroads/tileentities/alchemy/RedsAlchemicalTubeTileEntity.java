package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class RedsAlchemicalTubeTileEntity extends AlchemicalTubeTileEntity{

	@ObjectHolder("reds_alchemical_tube")
	private static TileEntityType<RedsAlchemicalTubeTileEntity> type = null;

	public RedsAlchemicalTubeTileEntity(){
		super(type);
	}

	public RedsAlchemicalTubeTileEntity(boolean glass){
		super(type, glass);
	}

	private boolean isUnlocked(){
		return getBlockState().get(ESProperties.REDSTONE_BOOL);
	}

	@Override
	protected void performTransfer(){
		if(isUnlocked()){
			super.performTransfer();
		}
	}

	@Override
	protected boolean allowConnect(Direction side){
		return isUnlocked() && super.allowConnect(side);
	}
}
