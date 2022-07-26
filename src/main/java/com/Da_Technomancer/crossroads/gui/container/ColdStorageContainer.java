package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.ColdStorageTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ColdStorageContainer extends MachineContainer<ColdStorageTileEntity>{

	protected static final MenuType<ColdStorageContainer> TYPE = CRContainers.createConType(ColdStorageContainer::new);

	public ColdStorageContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE, id, playerInv, data);
	}

	@Override
	protected void addSlots(){
		for(int row = 0; row < te.getContainerSize() / 9; row++){
			for(int column = 0; column < 9; column++){
				addSlot(new StrictSlot(te, row * 9 + column, 8 + column * 18, 18 + row * 18));
			}
		}
	}
}
