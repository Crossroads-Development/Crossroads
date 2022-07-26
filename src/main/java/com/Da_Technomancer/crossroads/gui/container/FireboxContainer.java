package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.heat.FireboxTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class FireboxContainer extends MachineContainer<FireboxTileEntity>{

	protected static final MenuType<FireboxContainer> TYPE = CRContainers.createConType(FireboxContainer::new);
	
	public IntDeferredRef burnProg;

	public FireboxContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE, id, playerInv, data);
		burnProg = new IntDeferredRef(te::getBurnProg, te.getLevel().isClientSide);
		addDataSlot(burnProg);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlot(new StrictSlot(te, 0, 80, 23));
	}

	@Override
	public int[] getInvStart(){
		return new int[] {8, 54};
	}
}
