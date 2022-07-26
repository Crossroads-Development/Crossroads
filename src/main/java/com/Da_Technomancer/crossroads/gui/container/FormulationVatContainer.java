package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.FormulationVatTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class FormulationVatContainer extends MachineContainer<FormulationVatTileEntity>{

	protected static final MenuType<FormulationVatContainer> TYPE = CRContainers.createConType(FormulationVatContainer::new);

	public final IntDeferredRef craftProgress;

	public FormulationVatContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
		craftProgress = new IntDeferredRef(te::getProgess, te.getLevel().isClientSide);
		addDataSlot(craftProgress);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 44, 50));//Input
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 116, 15, 116, 50, te, new int[] {0, 1});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return 3;
	}
}
