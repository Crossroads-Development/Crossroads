package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class ChunkUnlockerTileEntity extends TileEntity{


	private final IMagicHandler magicHandler = new MagicHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY){
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
	}

	private int timer = COOLDOWN;
	private static final int COOLDOWN = 40;
	
	private class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			if(MagicElements.getElement(mag) == MagicElements.TIME && mag.getVoid() == 0){
				if(--timer <= 0 && !FieldWorldSavedData.get(worldObj).fieldNodes.containsKey(FieldWorldSavedData.getLongFromPos(pos))){
					worldObj.updateBlockTick(pos, ModBlocks.chunkUnlocker, 1, 1);
					timer = COOLDOWN;
				}
			}else{
				FieldWorldSavedData.get(worldObj).fieldNodes.remove(FieldWorldSavedData.getLongFromPos(pos));
				timer = COOLDOWN;
			}
		}
	}
}