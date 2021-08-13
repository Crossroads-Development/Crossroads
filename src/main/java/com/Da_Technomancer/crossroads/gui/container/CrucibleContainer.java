package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatingCrucibleTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

@ObjectHolder(Crossroads.MODID)
public class CrucibleContainer extends MachineContainer<HeatingCrucibleTileEntity>{

	@ObjectHolder("crucible")
	private static ContainerType<CrucibleContainer> type = null;

	public final IntDeferredRef progRef;

	public CrucibleContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id, playerInv, buf);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progRef);
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
