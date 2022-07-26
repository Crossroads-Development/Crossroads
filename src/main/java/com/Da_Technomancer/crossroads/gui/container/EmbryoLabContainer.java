package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.EmbryoLabTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class EmbryoLabContainer extends MachineContainer<EmbryoLabTileEntity>{

	protected static final MenuType<EmbryoLabContainer> TYPE = CRContainers.createConType(EmbryoLabContainer::new);

	public EmbryoLabContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
	}

	@Override
	protected int[] getInvStart(){
		return new int[] {8, 164};
	}

	@Override
	protected void addSlots(){
		addSlot(new OutputSlot(te, 0, 152, 6));
	}
}
