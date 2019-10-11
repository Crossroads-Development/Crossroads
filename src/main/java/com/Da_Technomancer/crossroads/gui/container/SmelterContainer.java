package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.heat.SmelterTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class SmelterContainer extends MachineContainer<SmelterTileEntity>{

	@ObjectHolder("smelter")
	private static ContainerType<SmelterContainer> type = null;

	public SmelterContainer(int id, PlayerInventory playerInv, PacketBuffer data){
		super(type, id, playerInv, data);
	}

	@Override
	protected void addSlots(){
		// Input slot, ID 0
		addSlot(new StrictSlot(te, 0, 56, 35));

		// Output slot, ID 1
		addSlot(new OutputSlot(te, 1, 116, 35));
	}
}
