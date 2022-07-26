package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.fluid.FluidTankTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class FluidTankContainer extends MachineContainer<FluidTankTileEntity>{

	protected static final MenuType<FluidTankContainer> TYPE = CRContainers.createConType(FluidTankContainer::new);

	public FluidTankContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
	}

	@Override
	protected void addSlots(){
		Pair<Slot, Slot> flSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100,19, 100, 54, te, new int[] {0});
		addFluidManagerSlots(flSlots);
	}

	@Override
	protected int slotCount(){
		return 2;
	}
}
