package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.heat.IceboxTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ObjectHolder;

import com.Da_Technomancer.crossroads.API.templates.TileEntityContainer.StrictSlot;

@ObjectHolder(Crossroads.MODID)
public class IceboxContainer extends MachineContainer<IceboxTileEntity>{

	@ObjectHolder("icebox")
	private static MenuType<IceboxContainer> type = null;

	public IntDeferredRef coolProg;

	public IceboxContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(type, id, playerInv, buf);

		coolProg = new IntDeferredRef(te::getCoolProg, te.getLevel().isClientSide);
		addDataSlot(coolProg);
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
