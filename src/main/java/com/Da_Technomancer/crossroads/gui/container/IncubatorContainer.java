package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.IncubatorTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class IncubatorContainer extends MachineContainer<IncubatorTileEntity>{


	public final IntDeferredRef progressRef;
	public final IntDeferredRef timeRef;

	public IncubatorContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(CRContainers.INCUBATOR_CONTAINER.get(), id, playerInv, buf);
		progressRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progressRef);
		timeRef = new IntDeferredRef(te::getTime, te.getLevel().isClientSide);
		addDataSlot(timeRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 26, 23));//Input 1
		addSlot(new StrictSlot(te, 1, 26, 41));//Input 2
		addSlot(new OutputSlot(te, 2, 98, 32));//Output
	}
}
