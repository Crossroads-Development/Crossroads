package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.tileentities.heat.FluidCoolingChamberTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

public class FluidCoolerContainer extends MachineContainer<FluidCoolingChamberTileEntity>{

	@ObjectHolder("fluid_cooler")
	private static ContainerType<FluidCoolerContainer> type = null;

	public FluidCoolerContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id, playerInv, buf);
	}

	@Override
	protected void addSlots(){
		addSlot(new OutputSlot(te, 0, 71, 32));
		Pair<Slot, Slot> slots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0});
		addSlot(slots.getLeft());
		addSlot(slots.getRight());
	}

	@Override
	protected int slotCount(){
		return 3;
	}
}
