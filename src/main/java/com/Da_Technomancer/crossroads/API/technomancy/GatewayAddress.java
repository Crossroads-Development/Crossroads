package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

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

	public static class Location{

		public final BlockPos pos;
		public final ResourceLocation dim;

		public Location(BlockPos pos, World world){
			this.pos = pos.toImmutable();
			this.dim = world.dimension.getType().getRegistryName();
		}

		public Location(long posSerial, String dimSerial){
			this.pos = BlockPos.fromLong(posSerial);
			this.dim = new ResourceLocation(dimSerial);
		}

		@Nullable
		public World evalDim(MinecraftServer server){
			try{
				DimensionType dimType = DimensionType.byName(dim);
				if(dimType == null){
					return null;//Only happens if a dimension is unregistered
				}
				return DimensionManager.getWorld(server, dimType, true, true);
			}catch(Exception e){
				return null;
			}
		}

		@Nullable
		public GatewayFrameTileEntity evalTE(MinecraftServer server){
			World w = evalDim(server);
			if(w == null){
				return null;
			}
			//Load the chunk
			ChunkPos chunkPos = new ChunkPos(pos);
			((ServerChunkProvider) (w.getChunkProvider())).registerTicket(TicketType.PORTAL, chunkPos, 3, pos);
			TileEntity te = w.getTileEntity(pos);
			if(te instanceof GatewayFrameTileEntity){
				return (GatewayFrameTileEntity) te;
			}
			return null;
		}

		@Override
		public boolean equals(Object o){
			if(this == o){
				return true;
			}
			if(o == null || getClass() != o.getClass()){
				return false;
			}
			Location location = (Location) o;
			return dim == location.dim &&
					pos.equals(location.pos);
		}

		@Override
		public int hashCode(){
			return Objects.hash(pos, dim);
		}
	}
}
