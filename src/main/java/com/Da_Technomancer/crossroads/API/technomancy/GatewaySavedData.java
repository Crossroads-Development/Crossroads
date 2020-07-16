package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GatewaySavedData extends WorldSavedData{

	/**
	 * Determines a reserved address based on world seed that may not be assigned to new gateways
	 * This address is being reserved for a feature to be added in the future TODO
	 * @param w The world, to get the seed from
	 * @return An address that can not be assigned to gateways
	 */
	public static GatewayAddress getReservedAddress(@Nonnull ServerWorld w){
		EnumBeamAlignments[] address = new EnumBeamAlignments[4];
		address[0] = EnumBeamAlignments.RIFT;//Fix the first alignment as rift
		long seed = w.getSeed();
		Random rand = new Random(seed);
		//The other 3 alignments are chosen randomly based on seed
		for(int i = 1; i < address.length; i++){
			address[i] = GatewayAddress.getLegalEntry(rand.nextInt(Integer.MAX_VALUE));
		}
		return new GatewayAddress(address);
	}

	/**
	 * Generates a unique new address and registers it
	 * @param w The world for the address to point to
	 * @param pos The position for the address to point to
	 * @return The newly generated address, or null if generating an address was impossible
	 */
	@Nullable
	public static GatewayAddress requestAddress(@Nonnull ServerWorld w, @Nonnull BlockPos pos){
		GatewaySavedData data = get(w);
		if(data.addressBook.size() >= (int) Math.pow(GatewayAddress.LEGAL_VALS.length, 4)){
			Crossroads.logger.warn("Ran out of Technomancy Gateway Addresses! No new gateways can be built");
			Crossroads.logger.warn("Let the mod author know that someone managed to hit the limit :)");
			return null;//Every single possible address has been assigned. Impressive?
		}

		//Generate a unique new address
		EnumBeamAlignments[] address = new EnumBeamAlignments[4];
		GatewayAddress reserved = getReservedAddress(w);
		GatewayAddress gateAdd;
		do{
			for(int i = 0; i < 4; i++){
				address[i] = GatewayAddress.getLegalEntry(w.rand.nextInt(GatewayAddress.LEGAL_VALS.length));
			}
			gateAdd = new GatewayAddress(address);
		}while(data.addressBook.containsKey(gateAdd) || gateAdd.equals(reserved));//Generate a new address every time the generated address is already in use

		//Register this new address in the addressBook
		data.addressBook.put(gateAdd, new GatewayAddress.Location(pos, w));
		data.markDirty();

		return gateAdd;
	}

	/**
	 * Unregisters an address
	 * @param w Any server world
	 * @param address The address to unregister
	 */
	public static void releaseAddress(@Nonnull ServerWorld w, @Nullable GatewayAddress address){
		if(address != null){
			GatewaySavedData data = get(w);
			data.addressBook.remove(address);
			data.markDirty();
		}
	}

	/**
	 * Finds the destination an address points to
	 * @param w Any non-null server world
	 * @param address The address to lookup
	 * @return The mapped destination. Null if address was null or the address was not registered
	 */
	@Nullable
	public static GatewayAddress.Location lookupAddress(@Nonnull ServerWorld w, @Nullable GatewayAddress address){
		GatewaySavedData data = get(w);
		return address == null ? null : data.addressBook.get(address);
	}

	private static GatewaySavedData get(ServerWorld world){
		//We want all dimensions to share the same saved data,
		//So we always reference the overworld instance
		DimensionSavedDataManager storage;
		if(world.func_234922_V_().func_240901_a_().equals(DimensionType.field_235999_c_.func_240901_a_())){
			storage = world.getSavedData();
		}else{
			storage = world.getServer().func_241755_D_().getSavedData();//MCP note: getOverworld
		}
		GatewaySavedData data;
		try{
			data = storage.getOrCreate(GatewaySavedData::new, ID);
		}catch(NullPointerException e){
			Crossroads.logger.error("Failed GatewaySavedData get due to null DimensionSavedDataManager", e);
			return new GatewaySavedData();//Blank storage that prevents actual read/write, but avoids a crash
		}
		return data;
	}

	public static final String ID = Crossroads.MODID + "_gateways";

	private final Map<GatewayAddress, GatewayAddress.Location> addressBook = new HashMap<>();

	private GatewaySavedData(){
		super(ID);
	}

	@Override
	public void read(CompoundNBT nbt){
		addressBook.clear();
		int i = 0;
		while(nbt.contains("key_" + i)){
			addressBook.put(GatewayAddress.deserialize(nbt.getInt("key_" + i)), new GatewayAddress.Location(nbt.getLong("pos_" + i), nbt.getString("dim_" + i)));
			i++;
		}
		nbt.getInt("atmos_charge");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		int i = 0;
		for(Map.Entry<GatewayAddress, GatewayAddress.Location> entry : addressBook.entrySet()){
			nbt.putInt("key_" + i, entry.getKey().serialize());
			nbt.putLong("pos_" + i, entry.getValue().pos.toLong());
			nbt.putString("dim_" + i, entry.getValue().dim.toString());
			i++;
		}

		return nbt;
	}
}
