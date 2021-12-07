package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentFilterTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class ReagentFilterContainer extends TileEntityContainer<ReagentFilterTileEntity>{

	@ObjectHolder("reagent_filter")
	private static MenuType<ReagentFilterContainer> type = null;

	public ReagentFilterContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
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
