package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.StasisStorageTileEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ObjectHolder;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer.StrictSlot;

@ObjectHolder(Crossroads.MODID)
public class StasisStorageContainer extends MachineContainer<StasisStorageTileEntity>{

	@ObjectHolder("stasis_storage")
	private static MenuType<StasisStorageContainer> type = null;

	public StasisStorageContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(type, id, playerInv, data);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 80, 18));
	}
}
