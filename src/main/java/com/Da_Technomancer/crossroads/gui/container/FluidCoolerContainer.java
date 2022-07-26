package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.heat.FluidCoolingChamberTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class FluidCoolerContainer extends MachineContainer<FluidCoolingChamberTileEntity>{

	protected static final MenuType<FluidCoolerContainer> TYPE = CRContainers.createConType(FluidCoolerContainer::new);

	public FluidCoolerContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
	}

	@Override
	protected void addSlots(){
		addSlot(new OutputSlot(te, 0, 71, 32));
		Pair<Slot, Slot> slots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0});
		addFluidManagerSlots(slots);
	}

	@Override
	protected int slotCount(){
		return 3;
	}
}
