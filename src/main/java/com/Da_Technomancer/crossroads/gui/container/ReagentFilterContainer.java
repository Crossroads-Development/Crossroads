package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentFilterTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class ReagentFilterContainer extends TileEntityContainer<ReagentFilterTileEntity>{

	@ObjectHolder("reagent_filter")
	private static ContainerType<ReagentFilterContainer> type = null;

	public ReagentFilterContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id, playerInv, buf);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlot(new StrictSlot(te, 0, 80, 53));
	}

	@Override
	protected int slotCount(){
		return 1;
	}
}
