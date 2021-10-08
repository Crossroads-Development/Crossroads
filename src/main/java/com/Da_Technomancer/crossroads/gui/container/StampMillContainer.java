package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ObjectHolder;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer.OutputSlot;
import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer.StrictSlot;

@ObjectHolder(Crossroads.MODID)
public class StampMillContainer extends MachineContainer<StampMillTileEntity>{

	@ObjectHolder("stamp_mill")
	private static MenuType<StampMillContainer> type = null;

	public final IntDeferredRef progRef;
	public final IntDeferredRef timeRef;

	public StampMillContainer(int windowId, Inventory playerInv, FriendlyByteBuf data){
		super(type, windowId, playerInv, data);
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
