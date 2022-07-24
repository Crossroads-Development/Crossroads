package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.HydroponicsTroughTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Supplier;

public class HydroponicsTroughContainer extends MachineContainer<HydroponicsTroughTileEntity>{

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("hydroponics_trough", ForgeRegistries.Keys.MENU_TYPES);

	public final IntDeferredRef progRef;

	public HydroponicsTroughContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE_SPL.get(), id, playerInv, buf);
		progRef = new IntDeferredRef(te::getProgressBar, te.getLevel().isClientSide);
		addDataSlot(progRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 62, 16));//Input
		addSlot(new OutputSlot(te, 1, 62, 52));//Output
		addSlot(new OutputSlot(te, 2, 80, 52));//Output
		addSlot(new OutputSlot(te, 3, 98, 52));//Output
		addSlot(new OutputSlot(te, 4, 116, 52));//Output
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 8, 17, 8, 52, te, new int[] {0});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return 7;
	}
}
