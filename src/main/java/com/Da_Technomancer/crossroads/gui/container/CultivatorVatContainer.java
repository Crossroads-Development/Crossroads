package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.CultivatorVatTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

@ObjectHolder(Crossroads.MODID)
public class CultivatorVatContainer extends MachineContainer<CultivatorVatTileEntity>{

	@ObjectHolder("cultivator_vat")
	private static MenuType<CultivatorVatContainer> type = null;

	public final IntDeferredRef progressRef;

	public CultivatorVatContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(type, id, playerInv, buf);
		progressRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progressRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 98, 36));//Target
		addSlot(new StrictSlot(te, 1, 62, 18));//Input 1
		addSlot(new StrictSlot(te, 2, 62, 54));//Input 2
		addSlot(new OutputSlot(te, 3, 134, 36));//Output
		Pair<Slot, Slot> fluidSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 8, 19, 8, 54, te, new int[] {0});
		addFluidManagerSlots(fluidSlots);
	}

	@Override
	protected int slotCount(){
		return 6;
	}
}
