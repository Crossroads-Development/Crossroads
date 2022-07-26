package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.blocks.alchemy.ReagentFilterTileEntity;
import com.Da_Technomancer.essentials.api.BlockMenuContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ReagentFilterContainer extends BlockMenuContainer<ReagentFilterTileEntity>{

	protected static final MenuType<ReagentFilterContainer> TYPE = CRContainers.createConType(ReagentFilterContainer::new);

	public ReagentFilterContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlot(new StrictSlot(te, 0, 80, 53));
	}

	@Override
	protected int slotCount(){
		return 1;
	}
}
