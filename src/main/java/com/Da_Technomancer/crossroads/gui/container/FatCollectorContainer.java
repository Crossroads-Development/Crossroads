package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.fluid.FatCollectorTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

@ObjectHolder(Crossroads.MODID)
public class FatCollectorContainer extends MachineContainer<FatCollectorTileEntity>{

	@ObjectHolder("fat_collector")
	private static ContainerType<FatCollectorContainer> type = null;

	public FatCollectorContainer(int id, PlayerInventory inv, PacketBuffer data){
		super(type, id, inv, data);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 25, 32));
		Pair<Slot, Slot> fluidSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0});
		addSlot(fluidSlots.getLeft());
		addSlot(fluidSlots.getRight());
	}

	@Override
	protected int slotCount(){
		return 3;
	}
}
