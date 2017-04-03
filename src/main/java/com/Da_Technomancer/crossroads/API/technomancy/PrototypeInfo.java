package com.Da_Technomancer.crossroads.API.technomancy;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/**
 * 
 * Contains the port info needed to interact with a prototype chunk.
 */
public class PrototypeInfo{
	
	/**
	 * The index of each member of this array corresponds to the index of an EnumFacing.
	 */
	public final PrototypePortTypes[] ports = new PrototypePortTypes[6];
	
	/**
	 * The index of each member of this array corresponds to the index of an EnumFacing.
	 * Positions of the ports within the chunk. (Chunk relative position).
	 */
	public final BlockPos[] portPos = new BlockPos[6];
	
	/**
	 * The prototype dimension chunk should only be force loaded if this is A: Not null, and B: Does not contain null.
	 */
	@Nullable
	public WeakReference<IPrototypeOwner> owner;
	
	public final ChunkPos chunk;
	
	/**
	 * 
	 * @param ports Needs to have a capacity of 6.
	 */
	public PrototypeInfo(PrototypePortTypes[] ports, BlockPos[] portPos, ChunkPos chunk){
		for(int i = 0; i < 6; i++){
			this.ports[i] = ports[i];
			this.portPos[i] = portPos[i];
		}
		this.chunk = chunk;
	}
	
	/**
	 * Convenience method.
	 * @return The total number of ports used.
	 */
	public int getTotalPorts(){
		int count = 0;
		for(int i = 0; i < 6; i++){
			if(ports[i] != null){
				count++;
			}
		}
		return count;
	}
	
	protected NBTTagCompound writeToNBT(NBTTagCompound nbt){
		for(int i = 0; i < 6; i++){
			if(ports[i] != null){
				nbt.setString("port" + i, ports[i].name());
				nbt.setLong("pos" + i, portPos[i].toLong());
			}
		}
		nbt.setLong("chunk", MiscOp.getLongFromChunkPos(chunk));
		return nbt;
	}
	
	protected static PrototypeInfo readFromNBT(NBTTagCompound nbt){
		PrototypePortTypes[] ports = new PrototypePortTypes[6];
		BlockPos[] portPos = new BlockPos[6];
		for(int i = 0; i < 6; i++){
			ports[i] = nbt.hasKey("port" + i) ? PrototypePortTypes.valueOf(nbt.getString("port" + i)) : null;
			portPos[i] = nbt.hasKey("pos" + i) ? BlockPos.fromLong(nbt.getLong("pos" + i)) : null;
		}
		return new PrototypeInfo(ports, portPos, MiscOp.getChunkPosFromLong(nbt.getLong("chunk")));
	}
}
