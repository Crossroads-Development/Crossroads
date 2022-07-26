package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.technomancy.CopshowiumCreationChamberTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class CopshowiumMakerContainer extends MachineContainer<CopshowiumCreationChamberTileEntity>{

	protected static final MenuType<CopshowiumMakerContainer> TYPE = CRContainers.createConType(CopshowiumMakerContainer::new);

	public CopshowiumMakerContainer(int windowId, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE, windowId, playerInv, data);
	}

	@Override
	protected void addSlots(){
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0, 1});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return 2;
	}
}
