package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.HydroponicsTroughTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

@ObjectHolder(Crossroads.MODID)
public class HydroponicsTroughContainer extends MachineContainer<HydroponicsTroughTileEntity>{

	@ObjectHolder("hydroponics_trough")
	private static ContainerType<HydroponicsTroughContainer> type = null;

	public HydroponicsTroughContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id, playerInv, buf);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 62, 16));//Input
		addSlot(new OutputSlot(te, 1, 62, 52));//Output
		addSlot(new OutputSlot(te, 2, 80, 52));//Output
		addSlot(new OutputSlot(te, 3, 98, 52));//Output
		addSlot(new OutputSlot(te, 4, 116, 52));//Output
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 8, 17, 8, 52, te, new int[] {0});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return 7;
	}
}
