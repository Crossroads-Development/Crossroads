package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.blocks.witchcraft.BloodBeamLinkerTileEntity;
import com.Da_Technomancer.essentials.api.BlockMenuContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class BloodBeamLinkerContainer extends BlockMenuContainer<BloodBeamLinkerTileEntity>{

	protected static final MenuType<BloodBeamLinkerContainer> TYPE = CRContainers.createConType(BloodBeamLinkerContainer::new);

	public BloodBeamLinkerContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
	}

	@Override
	protected void addSlots(){
		// Blood slot, ID 0
		addSlot(new StrictSlot(te, 0, 80, 47));
	}

	@Override
	protected int slotCount(){
		return 1;
	}
}
