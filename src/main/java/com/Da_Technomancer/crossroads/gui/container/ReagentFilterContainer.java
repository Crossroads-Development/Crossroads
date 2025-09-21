package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.blocks.alchemy.ReagentFilterTileEntity;
import com.Da_Technomancer.essentials.api.BlockMenuContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ReagentFilterContainer extends BlockMenuContainer<ReagentFilterTileEntity>{


	public ReagentFilterContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(CRContainers.REAGENT_FILTER_CONTAINER.get(), id, playerInv, buf);
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
