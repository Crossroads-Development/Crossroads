package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.ColdStorageTileEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ObjectHolder;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer.StrictSlot;

@ObjectHolder(Crossroads.MODID)
public class ColdStorageContainer extends MachineContainer<ColdStorageTileEntity>{

	@ObjectHolder("cold_storage")
	private static MenuType<ColdStorageContainer> type = null;

	public ColdStorageContainer(int id, Inventory playerInv, FriendlyByteBuf data){
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
