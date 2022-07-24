package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.StasisStorageTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class StasisStorageContainer extends MachineContainer<StasisStorageTileEntity>{

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("stasis_storage", ForgeRegistries.Keys.MENU_TYPES);

	public StasisStorageContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE_SPL.get(), id, playerInv, data);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 80, 18));
	}
}
