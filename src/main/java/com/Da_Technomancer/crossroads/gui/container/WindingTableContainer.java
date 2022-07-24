package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.rotary.WindingTableTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class WindingTableContainer extends MachineContainer<WindingTableTileEntity>{

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("winding_table", ForgeRegistries.Keys.MENU_TYPES);

	public final IntDeferredRef progRef;

	public WindingTableContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE_SPL.get(), id, playerInv, buf);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 80, 47));
	}
}
