package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.heat.FireboxTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ObjectHolder;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer.StrictSlot;

@ObjectHolder(Crossroads.MODID)
public class FireboxContainer extends MachineContainer<FireboxTileEntity>{

	@ObjectHolder("firebox")
	private static MenuType<FireboxContainer> type = null;

	public IntDeferredRef burnProg;

	public FireboxContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(type, id, playerInv, data);
		burnProg = new IntDeferredRef(te::getBurnProg, te.getLevel().isClientSide);
		addDataSlot(burnProg);
	}

	@Override
	protected void addSlots(){
		// Fuel slot, ID 0
		addSlot(new StrictSlot(te, 0, 80, 23));
	}

	@Override
	public int[] getInvStart(){
		return new int[] {8, 54};
	}
}
