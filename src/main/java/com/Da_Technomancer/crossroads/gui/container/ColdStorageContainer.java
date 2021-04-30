package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.ColdStorageTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class ColdStorageContainer extends MachineContainer<ColdStorageTileEntity>{

	@ObjectHolder("cold_storage")
	private static ContainerType<ColdStorageContainer> type = null;

	public ColdStorageContainer(int id, PlayerInventory playerInv, PacketBuffer data){
		super(type, id, playerInv, data);
	}

	@Override
	protected void addSlots(){
		for(int row = 0; row < te.getContainerSize() / 9; row++){
			for(int column = 0; column < 9; column++){
				addSlot(new StrictSlot(te, row * 9 + column, 8 + column * 18, 18 + row * 18));
			}
		}
	}
}
