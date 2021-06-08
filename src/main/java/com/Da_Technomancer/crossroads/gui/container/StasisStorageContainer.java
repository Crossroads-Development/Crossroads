package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.StasisStorageTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class StasisStorageContainer extends MachineContainer<StasisStorageTileEntity>{

	@ObjectHolder("stasis_storage")
	private static ContainerType<StasisStorageContainer> type = null;

	public StasisStorageContainer(int id, PlayerInventory playerInv, PacketBuffer data){
		super(type, id, playerInv, data);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 80, 18));
	}
}
