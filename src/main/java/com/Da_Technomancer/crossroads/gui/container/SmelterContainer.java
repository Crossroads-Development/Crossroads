package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.heat.SmelterTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ObjectHolder;



@ObjectHolder(Crossroads.MODID)
public class SmelterContainer extends MachineContainer<SmelterTileEntity>{

	@ObjectHolder("smelter")
	private static MenuType<SmelterContainer> type = null;

	public final IntDeferredRef cookProg;

	public SmelterContainer(int id, Inventory playerInv, FriendlyByteBuf data){
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
	}
}
