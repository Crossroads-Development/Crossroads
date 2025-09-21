package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.blocks.beams.BeamExtractorTileEntity;
import com.Da_Technomancer.essentials.api.BlockMenuContainer;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class BeamExtractorContainer extends BlockMenuContainer<BeamExtractorTileEntity>{


	public final IntDeferredRef progRef;

	public BeamExtractorContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(CRContainers.BEAM_EXTRACTOR_CONTAINER.get(), id, playerInv, buf);

		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progRef);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlot(new StrictSlot(te, 0, 80, 47));
	}

	@Override
	protected int slotCount(){
		return 1;
	}
}
