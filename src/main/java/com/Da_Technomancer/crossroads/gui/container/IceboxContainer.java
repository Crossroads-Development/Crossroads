package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.heat.Icebox;
import com.Da_Technomancer.crossroads.tileentities.heat.IceboxTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class IceboxContainer extends MachineContainer<IceboxTileEntity>{

	@ObjectHolder("icebox")
	private static ContainerType<IceboxContainer> type = null;

	public IceboxContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id, playerInv, buf);
		trackInt(te.coolProg);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlot(new StrictSlot(te, 0, 80, 23));
	}

	@Override
	public int[] getInvStart(){
		return new int[] {8, 54};
	}
}
