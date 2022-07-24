package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.EmbryoLabTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class EmbryoLabContainer extends MachineContainer<EmbryoLabTileEntity>{

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("embryo_lab", ForgeRegistries.Keys.MENU_TYPES);

	public EmbryoLabContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE_SPL.get(), id, playerInv, buf);
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
