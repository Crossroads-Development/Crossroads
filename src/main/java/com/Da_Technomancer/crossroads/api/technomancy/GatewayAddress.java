package com.Da_Technomancer.crossroads.api.technomancy;

import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Arrays;

public class GatewayAddress{

	protected static final EnumBeamAlignments[] LEGAL_VALS = new EnumBeamAlignments[8];

	static{
		LEGAL_VALS[0] = EnumBeamAlignments.LIGHT;
		LEGAL_VALS[1] = EnumBeamAlignments.ENCHANTMENT;
		LEGAL_VALS[2] = EnumBeamAlignments.CHARGE;
		LEGAL_VALS[3] = EnumBeamAlignments.TIME;
		LEGAL_VALS[4] = EnumBeamAlignments.RIFT;
		LEGAL_VALS[5] = EnumBeamAlignments.EQUILIBRIUM;
		LEGAL_VALS[6] = EnumBeamAlignments.EXPANSION;
		LEGAL_VALS[7] = EnumBeamAlignments.FUSION;
	}

	private final EnumBeamAlignments[] address = new EnumBeamAlignments[4];

	/**
	 * Instantiates a new instance with a set address
	 * @param addressIn A size 4 non-null array with the address, will be copied to prevent mutability
	 */
	public GatewayAddress(EnumBeamAlignments[] addressIn){
		System.arraycopy(addressIn, 0, address, 0, 4);
	}

	public boolean fullAddress(){
		for(EnumBeamAlignments entry : address){
			if(entry == null){
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns one configured alignment
	 * @param index The index to return, [0-3]
	 * @return The specified configured alignment
	 */
	public EnumBeamAlignments getEntry(int index){
		return address[index];
	}

	public int serialize(){
		int serial = 0;
		for(int i = 0; i < 4; i++){
			serial |= address[i] == null ? 0 : (address[i].ordinal() + 1) << 4*i;
		}
		return serial;
	}

	public static GatewayAddress deserialize(int serial){
		EnumBeamAlignments[] vals = EnumBeamAlignments.values();
		final int mask = 0xF;
		EnumBeamAlignments[] entries = new EnumBeamAlignments[4];
		for(int i = 0; i < 4; i++){
			int subSerial = (serial >>> i*4) & mask;
			entries[i] = subSerial == 0 ? null : vals[subSerial - 1];
		}
		return new GatewayAddress(entries);
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		GatewayAddress that = (GatewayAddress) o;
		return Arrays.equals(address, that.address);
	}

	@Override
	public int hashCode(){
		return serialize();
	}

	public static EnumBeamAlignments getLegalEntry(int index){
		//((a % b) + b) % b is used instead of a % b in order to handle negative indices
		return LEGAL_VALS[((index % LEGAL_VALS.length) + LEGAL_VALS.length) % LEGAL_VALS.length];
	}

	public static int getEntryID(EnumBeamAlignments align){
		for(int i = 0; i < LEGAL_VALS.length; i++){
			if(LEGAL_VALS[i] == align){
				return i;
			}
		}
		return -1;
	}

	@Nullable
	public static IGateway evalTE(Location location, MinecraftServer server){
		Level w = location.evalDim(server);
		if(w == null){
			return null;
		}
		//Load the chunk
		ChunkPos chunkPos = new ChunkPos(location.pos);
		((ServerChunkCache) (w.getChunkSource())).addRegionTicket(TicketType.PORTAL, chunkPos, 3, location.pos);
		BlockEntity te = w.getBlockEntity(location.pos);
		if(te instanceof IGateway gte){
			return gte;
		}
		return null;
	}
}
