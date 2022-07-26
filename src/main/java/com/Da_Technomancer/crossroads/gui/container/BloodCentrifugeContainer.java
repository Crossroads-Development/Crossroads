package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.BloodCentrifugeTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;

public class BloodCentrifugeContainer extends MachineContainer<BloodCentrifugeTileEntity>{

	protected static final MenuType<BloodCentrifugeContainer> TYPE = CRContainers.createConType(BloodCentrifugeContainer::new);

	public DataSlot progRef;

	public BloodCentrifugeContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progRef);
	}

	@Override
	protected void addSlots(){
		// input 0
		addSlot(new StrictSlot(te, 0, 25, 36));
		// input 1
		addSlot(new StrictSlot(te, 1, 25, 54));
		// output 2
		addSlot(new OutputSlot(te, 2, 125, 36));
		// output 3
		addSlot(new OutputSlot(te, 3, 125, 54));
	}
}
