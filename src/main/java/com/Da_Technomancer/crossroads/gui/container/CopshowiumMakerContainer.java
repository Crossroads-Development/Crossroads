package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CopshowiumCreationChamberTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

@ObjectHolder(Crossroads.MODID)
public class CopshowiumMakerContainer extends MachineContainer<CopshowiumCreationChamberTileEntity>{

	@ObjectHolder("copshowium_maker")
	private static ContainerType<CopshowiumMakerContainer> type = null;

	public CopshowiumMakerContainer(int windowId, PlayerInventory playerInv, PacketBuffer data){
		super(type, windowId, playerInv, data);
	}

	@Override
	protected void addSlots(){
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0, 1});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return 2;
	}
}
