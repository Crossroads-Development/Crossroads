package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;

public class ChunkUnlockerTileEntity extends TileEntity{


	private final IMagicHandler magicHandler = new MagicHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == EnumFacing.UP){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == EnumFacing.UP){
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
	}

	private int timer = COOLDOWN;
	private static final int COOLDOWN = 32;//8 seconds
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		timer = nbt.getInteger("timer");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("timer", timer);
		return nbt;
	}
	
	private class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			if(MagicElements.getElement(mag) == MagicElements.TIME && mag.getVoid() == 0){
				if(!FieldWorldSavedData.get(world).fieldNodes.containsKey(MiscOp.getLongFromChunkPos(new ChunkPos(pos))) && --timer <= 0){
					//This is routed through to the block via world in order to easily add a 1 tick delay.
					world.updateBlockTick(pos, ModBlocks.chunkUnlocker, 1, 1);
					timer = COOLDOWN;
				}
			}else{
				FieldWorldSavedData.get(world).fieldNodes.remove(MiscOp.getLongFromChunkPos(new ChunkPos(pos)));
				timer = COOLDOWN;
			}
		}
	}
}
