package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.rotary.MillstoneTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;

public class MillstoneContainer extends MachineContainer<MillstoneTileEntity>{

	protected static final MenuType<MillstoneContainer> TYPE = CRContainers.createConType(MillstoneContainer::new);

	public DataSlot progRef;

	public MillstoneContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progRef);
	}

	@Override
	protected void addSlots(){
		// input 0
		addSlot(new StrictSlot(te, 0, 80, 17));

		// output 1-3
		for(int x = 0; x < 3; x++){
			addSlot(new OutputSlot(te, 1 + x, 62 + (x * 18), 53));
		}
	}
}
