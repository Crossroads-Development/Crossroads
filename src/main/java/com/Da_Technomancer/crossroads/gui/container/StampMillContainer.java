package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.rotary.StampMillTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class StampMillContainer extends MachineContainer<StampMillTileEntity>{

	protected static final MenuType<StampMillContainer> TYPE = CRContainers.createConType(StampMillContainer::new);

	public final IntDeferredRef progRef;
	public final IntDeferredRef timeRef;

	public StampMillContainer(int windowId, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE, windowId, playerInv, data);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		timeRef = new IntDeferredRef(te::getTimer, te.getLevel().isClientSide);
		addDataSlot(progRef);
		addDataSlot(timeRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 25, 36));
		addSlot(new OutputSlot(te, 1, 125, 36));
	}
}
