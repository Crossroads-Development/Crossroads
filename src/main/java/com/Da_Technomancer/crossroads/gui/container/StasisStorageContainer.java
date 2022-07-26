package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.StasisStorageTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class StasisStorageContainer extends MachineContainer<StasisStorageTileEntity>{

	protected static final MenuType<StasisStorageContainer> TYPE = CRContainers.createConType(StasisStorageContainer::new);

	public StasisStorageContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE, id, playerInv, data);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 80, 18));
	}
}
