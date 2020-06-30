package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamExtractorTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeamExtractorContainer extends TileEntityContainer<BeamExtractorTileEntity>{

	@ObjectHolder("beam_extractor")
	private static ContainerType<BeamExtractorContainer> type = null;

	public final IntDeferredRef progRef;

	public BeamExtractorContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
		super(type, id, playerInv, buf);

		progRef = new IntDeferredRef(te::getProgress, te.getWorld().isRemote);
		trackInt(progRef);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlot(new StrictSlot(te, 0, 80, 47));
	}

	@Override
	protected int slotCount(){
		return 1;
	}
}
