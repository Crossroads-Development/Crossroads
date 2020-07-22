package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.fluid.OreCleanserTileEntity;
import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.commons.lang3.tuple.Pair;

@ObjectHolder(Crossroads.MODID)
public class OreCleanserContainer extends MachineContainer<OreCleanserTileEntity>{

	@ObjectHolder("ore_cleanser")
	private static ContainerType<OreCleanserContainer> type = null;

	public final IntDeferredRef progRef;

	public OreCleanserContainer(int id, PlayerInventory playerInv, PacketBuffer data){
		super(type, id, playerInv, data);
		progRef = new IntDeferredRef(te::getProgress, te.getWorld().isRemote);
		trackInt(progRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 26, 53));//Gravel
		addSlot(new OutputSlot(te, 1, 44, 53));//Clumps
		Pair<Slot, Slot> fSlots = FluidSlotManager.createFluidSlots(new FluidSlotManager.FakeInventory(this), 0, 98, 18, 98, 53, te, new int[] {0, 1});
		addFluidManagerSlots(fSlots);
	}

	@Override
	protected int slotCount(){
		return 4;
	}
}
