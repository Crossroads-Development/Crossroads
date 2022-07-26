package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.fluid.SteamerTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class SteamerContainer extends MachineContainer<SteamerTileEntity>{

	protected static final MenuType<SteamerContainer> TYPE = CRContainers.createConType(SteamerContainer::new);

	public final IntDeferredRef cookProg;

	public SteamerContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE, id, playerInv, data);
		cookProg = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(cookProg);
	}

	@Override
	protected void addSlots(){
		// Input slot, ID 0
		addSlot(new StrictSlot(te, 0, 56, 35));

		// Output slot, ID 1
		addSlot(new OutputSlot(te, 1, 116, 35));

		//Fluid slots
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 9, 19, 9, 54, te, new int[] {0, 1});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return super.slotCount() + 2;
	}
}
