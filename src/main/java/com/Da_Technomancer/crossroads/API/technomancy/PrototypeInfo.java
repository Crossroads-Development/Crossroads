package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

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
	 * The prototype dimension chunk should only be force loaded if this A: Is not null, and B: Does not contain null.
	 */
	@Nullable
	public WeakReference<IPrototypeOwner> owner;
	
	public final ChunkPos chunk;
	
	/**
	 * @param ports Needs to have a capacity of 6. May contain null.
	 * @param portPos The chunk relative position of each port. Must contain null if, and only if, ports contains null at the same index. Must have a capacity of 6.
	 * @param chunk The ChunkPos of this Prototype's chunk within the prototype dimension.
	 */
	public PrototypeInfo(@Nonnull PrototypePortTypes[] ports, @Nonnull BlockPos[] portPos, @Nonnull ChunkPos chunk){
		for(int i = 0; i < 6; i++){
			this.ports[i] = ports[i];
			this.portPos[i] = portPos[i];
		}
		this.chunk = chunk;
	}
	
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		for(int i = 0; i < 6; i++){
			if(ports[i] != null){
				nbt.setString("port" + i, ports[i].name());
				nbt.setLong("pos" + i, portPos[i].toLong());
			}
		}
		nbt.setLong("chunk", MiscUtil.getLongFromChunkPos(chunk));
		return nbt;
	}
	
	public static PrototypeInfo readFromNBT(CompoundNBT nbt){
		PrototypePortTypes[] ports = new PrototypePortTypes[6];
		BlockPos[] portPos = new BlockPos[6];
		for(int i = 0; i < 6; i++){
			ports[i] = nbt.hasKey("port" + i) ? PrototypePortTypes.valueOf(nbt.getString("port" + i)) : null;
			portPos[i] = nbt.hasKey("pos" + i) ? BlockPos.fromLong(nbt.getLong("pos" + i)) : null;
		}
		return new PrototypeInfo(ports, portPos, MiscUtil.getChunkPosFromLong(nbt.getLong("chunk")));
	}
}
