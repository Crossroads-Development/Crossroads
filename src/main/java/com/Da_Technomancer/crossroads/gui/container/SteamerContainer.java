package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.fluid.SteamerTileEntity;
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
public class SteamerContainer extends MachineContainer<SteamerTileEntity>{

	@ObjectHolder("steamer")
	private static MenuType<SteamerContainer> type = null;

	public final IntDeferredRef cookProg;

	public SteamerContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(type, id, playerInv, data);
		cookProg = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(cookProg);
	}

	@Override
	protected void addSlots(){
		// Input slot, ID 0
		addSlot(new StrictSlot(te, 0, 56, 35));

		// Output slot, ID 1
		addSlot(new OutputSlot(te, 1, 116, 35));

		//Fluid slots
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 9, 19, 9, 54, te, new int[] {0, 1});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return super.slotCount() + 2;
	}
}
