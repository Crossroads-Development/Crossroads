package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.rotary.StampMillTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class StampMillContainer extends MachineContainer<StampMillTileEntity>{

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("stamp_mill", ForgeRegistries.Keys.MENU_TYPES);

	public final IntDeferredRef progRef;
	public final IntDeferredRef timeRef;

	public StampMillContainer(int windowId, Inventory playerInv, FriendlyByteBuf data){
		super(TYPE_SPL.get(), windowId, playerInv, data);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		timeRef = new IntDeferredRef(te::getTimer, te.getLevel().isClientSide);
		addDataSlot(progRef);
		addDataSlot(timeRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 25, 36));
		addSlot(new OutputSlot(te, 1, 125, 36));
	}
}
