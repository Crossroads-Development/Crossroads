package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.EmbryoLabTileEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class EmbryoLabContainer extends MachineContainer<EmbryoLabTileEntity>{

	@ObjectHolder("embryo_lab")
	private static MenuType<EmbryoLabContainer> type = null;

	public EmbryoLabContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(type, id, playerInv, buf);
	}

	@Override
	protected int[] getInvStart(){
		return new int[] {8, 164};
	}

	@Override
	protected void addSlots(){
		addSlot(new OutputSlot(te, 0, 152, 6));
	}
}
