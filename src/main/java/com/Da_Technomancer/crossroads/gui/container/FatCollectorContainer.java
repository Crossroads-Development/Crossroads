package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.fluid.FatCollectorTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class FatCollectorContainer extends MachineContainer<FatCollectorTileEntity>{

	protected static final MenuType<FatCollectorContainer> TYPE = CRContainers.createConType(FatCollectorContainer::new);

	public FatCollectorContainer(int id, Inventory inv, FriendlyByteBuf data){
		super(TYPE, id, inv, data);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 25, 32));
		Pair<Slot, Slot> fluidSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0});
		addFluidManagerSlots(fluidSlots);
	}

	@Override
	protected int slotCount(){
		return 3;
	}
}
