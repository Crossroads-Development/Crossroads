package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.BrewingVatTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BrewingVatContainer extends MachineContainer<BrewingVatTileEntity>{

	@ObjectHolder("brewing_vat")
	private static ContainerType<BrewingVatContainer> type = null;

	public final IntDeferredRef craftProgress;

	public BrewingVatContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id, playerInv, buf);
		craftProgress = new IntDeferredRef(te::getProgess, te.getLevel().isClientSide);
		addDataSlot(craftProgress);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 44, 50));//Ingredient input
		addSlot(new StrictSlot(te, 1, 8, 14));//Potion input
		addSlot(new StrictSlot(te, 2, 8, 32));//Potion input
		addSlot(new StrictSlot(te, 3, 8, 50));//Potion input
		addSlot(new OutputSlot(te, 4, 80, 14));//Potion output
		addSlot(new OutputSlot(te, 5, 80, 32));//Potion output
		addSlot(new OutputSlot(te, 6, 80, 50));//Potion output
	}
}
