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
		addSlot(new StrictSlot(te, 0, 44, 54));//Ingredient input
		addSlot(new StrictSlot(te, 1, 8, 18));//Potion input
		addSlot(new StrictSlot(te, 2, 8, 36));//Potion input
		addSlot(new StrictSlot(te, 3, 8, 54));//Potion input
		addSlot(new OutputSlot(te, 4, 80, 18));//Potion output
		addSlot(new OutputSlot(te, 5, 80, 36));//Potion output
		addSlot(new OutputSlot(te, 6, 80, 54));//Potion output
	}
}
