package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class StampMillContainer extends MachineContainer<StampMillTileEntity>{

	@ObjectHolder("stamp_mill")
	private static ContainerType<StampMillContainer> type = null;

	public StampMillContainer(int windowId, PlayerInventory playerInv, PacketBuffer data){
		super(type, windowId, playerInv, data);
		trackInt(te.progRef);
		trackInt(te.timeRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 25, 36));
		addSlot(new OutputSlot(te, 1, 125, 36));
	}
}
