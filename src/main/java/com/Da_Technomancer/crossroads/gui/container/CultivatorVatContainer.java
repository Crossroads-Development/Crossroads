package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.CultivatorVatTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class CultivatorVatContainer extends MachineContainer<CultivatorVatTileEntity>{

	protected static final MenuType<CultivatorVatContainer> TYPE = CRContainers.createConType(CultivatorVatContainer::new);

	public final IntDeferredRef progressRef;

	public CultivatorVatContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
		progressRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progressRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 98, 36));//Target
		addSlot(new StrictSlot(te, 1, 62, 18));//Input 1
		addSlot(new StrictSlot(te, 2, 62, 54));//Input 2
		addSlot(new OutputSlot(te, 3, 134, 36));//Output
		Pair<Slot, Slot> fluidSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 8, 19, 8, 54, te, new int[] {0});
		addFluidManagerSlots(fluidSlots);
	}

	@Override
	protected int slotCount(){
		return 6;
	}
}
