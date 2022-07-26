package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.fluid.SteamBoilerTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class SteamBoilerContainer extends MachineContainer<SteamBoilerTileEntity>{

	protected static final MenuType<SteamBoilerContainer> TYPE = CRContainers.createConType(SteamBoilerContainer::new);

	public SteamBoilerContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
	}

	@Override
	protected void addSlots(){
		addSlot(new OutputSlot(te, 0, 40, 54));//Salt
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0, 1});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return 3;
	}
}
