package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.essentials.packets.ClientPacket;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

@SuppressWarnings("serial")
public class AddVisualToClient extends ClientPacket{

	public CompoundNBT nbt;

	private static final Field[] FIELDS = fetchFields(AddVisualToClient.class, "nbt");

	@SuppressWarnings("unused")
	public AddVisualToClient(){

	}

	public AddVisualToClient(CompoundNBT nbt){
		this.nbt = nbt;
	}

	@Nonnull
	@Override
	protected Field[] getFields(){
		return FIELDS;
	}

	@Override
	protected void run(){
		SafeCallable.effectsToRender.add(RenderUtil.visualFactories[nbt.getInt("id")].apply(nbt));
	}
}
