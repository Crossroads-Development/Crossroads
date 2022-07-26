package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.rotary.BlastFurnaceTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class BlastFurnaceContainer extends MachineContainer<BlastFurnaceTileEntity>{

	protected static final MenuType<BlastFurnaceContainer> TYPE = CRContainers.createConType(BlastFurnaceContainer::new);

	public final IntDeferredRef carbRef;
	public final IntDeferredRef progRef;

	public BlastFurnaceContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
		carbRef = new IntDeferredRef(te::getCarbon, te.getLevel().isClientSide);
		addDataSlot(carbRef);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 8, 35));//Gravel/Clumps
		addSlot(new StrictSlot(te, 1, 29, 20));//Carbon
		addSlot(new OutputSlot(te, 2, 44, 53));//Slag
		Pair<Slot, Slot> fluidSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 98, 18, 98, 53, te, new int[] {0});
		addFluidManagerSlots(fluidSlots);
	}

	@Override
	protected int slotCount(){
		return 5;
	}
}
