package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.ClientPacket;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class SendDimLoadToClient extends ClientPacket{

	public int[] dims;
	public boolean wipePrevious = false;

	private static final Field[] FIELDS = fetchFields(SendDimLoadToClient.class, "dims", "wipePrevious");

	@SuppressWarnings("unused")
	public SendDimLoadToClient(){

	}

	public SendDimLoadToClient(int[] dims){
		this.dims = dims;
	}
	
	public SendDimLoadToClient(Integer[] dims){
		this.dims = new int[dims.length];
		for(int i = 0; i < dims.length; i++){
			this.dims[i] = dims[i];
		}
	}

	public SendDimLoadToClient(int[] dims, boolean wipePrevious){
		this.dims = dims;
		this.wipePrevious = wipePrevious;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		if(wipePrevious){
			//TODO
//			for(int i : DimensionManager.getDimensions(ModDimensions.workspaceDimType)){
//				DimensionManager.unregisterDimension(i);
//			}
		}
		for(int i : dims){
//			if(!DimensionManager.isDimensionRegistered(i)){
//				DimensionManager.registerDimension(i, ModDimensions.workspaceDimType);
//			}
		}
	}
}
