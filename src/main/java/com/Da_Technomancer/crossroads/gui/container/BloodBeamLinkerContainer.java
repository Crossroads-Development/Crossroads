package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.blocks.witchcraft.BloodBeamLinkerTileEntity;
import com.Da_Technomancer.essentials.api.BlockMenuContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class BloodBeamLinkerContainer extends BlockMenuContainer<BloodBeamLinkerTileEntity>{


	public BloodBeamLinkerContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(CRContainers.BLOOD_BEAM_LINKER_CONTAINER.get(), id, playerInv, buf);
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
