package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.EmbryoLabTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class EmbryoLabContainer extends MachineContainer<EmbryoLabTileEntity>{

	@ObjectHolder("embryo_lab")
	private static ContainerType<EmbryoLabContainer> type = null;

	public EmbryoLabContainer(int id, PlayerInventory playerInv, PacketBuffer buf){
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
