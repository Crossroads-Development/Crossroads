package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;

import net.minecraft.nbt.NBTTagCompound;

/**
 * 
 * Contains the port info needed to interact with a prototype chunk.
 */
public class PrototypeInfo{
	
	/**
	 * The index of each member of this array corresponds to the index an EnumFacing.
	 */
	public final PrototypePortTypes[] ports = new PrototypePortTypes[6];
	
	/**
	 * 
	 * @param ports Needs to have a capacity of 6.
	 */
	public PrototypeInfo(PrototypePortTypes[] ports){
		for(int i = 0; i < 6; i++){
			this.ports[i] = ports[i];
		}
	}
	
	protected NBTTagCompound writeToNBT(NBTTagCompound nbt){
		for(int i = 0; i < 6; i++){
			if(ports[i] != null){
				nbt.setString("port" + i, ports[i].name());
			}
		}
		return nbt;
	}
	
	protected static PrototypeInfo readFromNBT(NBTTagCompound nbt){
		return new PrototypeInfo(new PrototypePortTypes[] {nbt.hasKey("port0") ? PrototypePortTypes.valueOf(nbt.getString("port0")) : null, nbt.hasKey("port1") ? PrototypePortTypes.valueOf(nbt.getString("port1")) : null, nbt.hasKey("port2") ? PrototypePortTypes.valueOf(nbt.getString("port2")) : null, nbt.hasKey("port3") ? PrototypePortTypes.valueOf(nbt.getString("port3")) : null, nbt.hasKey("port4") ? PrototypePortTypes.valueOf(nbt.getString("port4")) : null, nbt.hasKey("port5") ? PrototypePortTypes.valueOf(nbt.getString("port5")) : null});
	}
}
