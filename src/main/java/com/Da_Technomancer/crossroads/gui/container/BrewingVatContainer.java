package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.BrewingVatTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class BrewingVatContainer extends MachineContainer<BrewingVatTileEntity>{


	public final IntDeferredRef craftProgress;

	public BrewingVatContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(CRContainers.BREWING_VAT_CONTAINER.get(), id, playerInv, buf);
		craftProgress = new IntDeferredRef(te::getProgess, te.getLevel().isClientSide);
		addDataSlot(craftProgress);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 44, 50));//Ingredient input
		addSlot(new StrictSlot(te, 1, 8, 14));//Potion input
		addSlot(new StrictSlot(te, 2, 8, 32));//Potion input
		addSlot(new StrictSlot(te, 3, 8, 50));//Potion input
		addSlot(new OutputSlot(te, 4, 80, 14));//Potion output
		addSlot(new OutputSlot(te, 5, 80, 32));//Potion output
		addSlot(new OutputSlot(te, 6, 80, 50));//Potion output
	}
}
