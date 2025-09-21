package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.heat.IceboxTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class IceboxContainer extends MachineContainer<IceboxTileEntity>{
	
	public IntDeferredRef coolProg;

	public IceboxContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(CRContainers.ICEBOX_CONTAINER.get(), id, playerInv, buf);

		coolProg = new IntDeferredRef(te::getCoolProg, te.getLevel().isClientSide);
		addDataSlot(coolProg);
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
