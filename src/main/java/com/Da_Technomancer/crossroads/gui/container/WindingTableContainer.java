package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.rotary.WindingTableTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class WindingTableContainer extends MachineContainer<WindingTableTileEntity>{


	public final IntDeferredRef progRef;

	public WindingTableContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(CRContainers.WINDING_TABLE_CONTAINER.get(), id, playerInv, buf);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 80, 47));
	}
}
