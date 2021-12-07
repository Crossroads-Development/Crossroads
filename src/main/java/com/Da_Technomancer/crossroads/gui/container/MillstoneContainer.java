package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.rotary.MillstoneTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class MillstoneContainer extends MachineContainer<MillstoneTileEntity>{

	@ObjectHolder("millstone")
	private static MenuType<MillstoneContainer> type = null;

	public DataSlot progRef;

	public MillstoneContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(type, id, playerInv, buf);
		progRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progRef);
	}

	@Override
	protected void addSlots(){
		// input 0
		addSlot(new StrictSlot(te, 0, 80, 17));

		// output 1-3
		for(int x = 0; x < 3; x++){
			addSlot(new OutputSlot(te, 1 + x, 62 + (x * 18), 53));
		}
	}
}
