package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.HydroponicsTroughTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer.OutputSlot;
import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer.StrictSlot;

@ObjectHolder(Crossroads.MODID)
public class HydroponicsTroughContainer extends MachineContainer<HydroponicsTroughTileEntity>{

	@ObjectHolder("hydroponics_trough")
	private static MenuType<HydroponicsTroughContainer> type = null;

	public final IntDeferredRef progRef;

	public HydroponicsTroughContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(type, id, playerInv, buf);
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
