package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.CultivatorVatTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

@ObjectHolder(Crossroads.MODID)
public class CultivatorVatContainer extends MachineContainer<CultivatorVatTileEntity>{

	@ObjectHolder("cultivator_vat")
	private static ContainerType<CultivatorVatContainer> type = null;

	public final IntDeferredRef progressRef;

	public CultivatorVatContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id, playerInv, buf);
		progressRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progressRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 5, 22));//Target
		addSlot(new StrictSlot(te, 1, 5, 32));//Input 1
		addSlot(new StrictSlot(te, 2, 25, 32));//Input 2
		addSlot(new OutputSlot(te, 3, 45, 32));//Output
		Pair<Slot, Slot> fluidSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0});
		addFluidManagerSlots(fluidSlots);
	}

	@Override
	protected int slotCount(){
		return 6;
	}
}
