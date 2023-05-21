package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.fluid.RadiatorTileEntity;
import com.Da_Technomancer.essentials.api.FluidSlotManager;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.apache.commons.lang3.tuple.Pair;

public class RadiatorContainer extends MachineContainer<RadiatorTileEntity>{

	protected static final MenuType<RadiatorContainer> TYPE = CRContainers.createConType(RadiatorContainer::new);

	public final IntDeferredRef mode;

	public RadiatorContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);
		mode = new IntDeferredRef(te::getMode, te.getLevel().isClientSide);
		addDataSlot(mode);
		if(te.getLevel().isClientSide){
			mode.set(buf.readByte());
		}
	}

	@Override
	protected void addSlots(){
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0, 1});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return 2;
	}
}
