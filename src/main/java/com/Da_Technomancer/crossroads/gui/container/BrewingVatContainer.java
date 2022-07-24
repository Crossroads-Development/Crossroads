package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.BrewingVatTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BrewingVatContainer extends MachineContainer<BrewingVatTileEntity>{

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("brewing_vat", ForgeRegistries.Keys.MENU_TYPES);

	public final IntDeferredRef craftProgress;

	public BrewingVatContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE_SPL.get(), id, playerInv, buf);
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
