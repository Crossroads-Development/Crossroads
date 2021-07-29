package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.BloodCentrifugeTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BloodCentrifugeContainer extends MachineContainer<BloodCentrifugeTileEntity>{

	@ObjectHolder("blood_centrifuge")
	private static ContainerType<BloodCentrifugeContainer> type = null;

	public IntReferenceHolder progRef;

	public BloodCentrifugeContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id, playerInv, buf);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progRef);
	}

	@Override
	protected void addSlots(){
		// input 0
		addSlot(new StrictSlot(te, 0, 25, 18));
		// input 1
		addSlot(new StrictSlot(te, 1, 25, 36));
		// output 2
		addSlot(new OutputSlot(te, 2, 125, 18));
		// output 3
		addSlot(new OutputSlot(te, 3, 125, 36));
	}
}
