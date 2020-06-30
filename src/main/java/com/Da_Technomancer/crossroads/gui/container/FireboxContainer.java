package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.heat.FireboxTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class FireboxContainer extends MachineContainer<FireboxTileEntity>{

	@ObjectHolder("firebox")
	private static ContainerType<FireboxContainer> type = null;

	public IntDeferredRef burnProg;

	public FireboxContainer(int id, PlayerInventory playerInv, PacketBuffer data){
		super(type, id, playerInv, data);
		burnProg = new IntDeferredRef(te::getBurnProg, te.getWorld().isRemote);
		trackInt(burnProg);
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
