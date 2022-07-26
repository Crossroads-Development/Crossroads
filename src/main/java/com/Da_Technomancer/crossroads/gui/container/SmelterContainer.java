package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.heat.SmelterTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class SmelterContainer extends MachineContainer<SmelterTileEntity>{

	protected static final MenuType<SmelterContainer> TYPE = CRContainers.createConType(SmelterContainer::new);

	public final IntDeferredRef cookProg;

	public SmelterContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE, id, playerInv, data);
		cookProg = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(cookProg);
	}

	@Override
	protected void addSlots(){
		// Input slot, ID 0
		addSlot(new StrictSlot(te, 0, 56, 35));

		// Output slot, ID 1
		addSlot(new OutputSlot(te, 1, 116, 35));
	}
}
