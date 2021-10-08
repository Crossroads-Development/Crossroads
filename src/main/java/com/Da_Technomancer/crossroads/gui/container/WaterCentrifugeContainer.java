package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.fluid.WaterCentrifugeTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer.OutputSlot;

@ObjectHolder(Crossroads.MODID)
public class WaterCentrifugeContainer extends MachineContainer<WaterCentrifugeTileEntity>{

	@ObjectHolder("water_centrifuge")
	private static MenuType<WaterCentrifugeContainer> type = null;

	public WaterCentrifugeContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(type, id, playerInv, data);
	}

	@Override
	protected void addSlots(){
		addSlot(new OutputSlot(te, 0, 40, 54));//Salt
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 100, 19, 100, 54, te, new int[] {0, 1});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return 3;
	}
}
