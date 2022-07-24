package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.IncubatorTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class IncubatorContainer extends MachineContainer<IncubatorTileEntity>{

	private static final Supplier<MenuType<?>> TYPE_SPL = MiscUtil.getCRRegistryObject("incubator", ForgeRegistries.Keys.MENU_TYPES);

	public final IntDeferredRef progressRef;
	public final IntDeferredRef targetRef;

	public IncubatorContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE_SPL.get(), id, playerInv, buf);
		progressRef = new IntDeferredRef(te::getProgress, te.getLevel().isClientSide);
		addDataSlot(progressRef);
		targetRef = new IntDeferredRef(te::getTargetTemp, te.getLevel().isClientSide);
		addDataSlot(targetRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 26, 23));//Input 1
		addSlot(new StrictSlot(te, 1, 26, 41));//Input 2
		addSlot(new OutputSlot(te, 2, 98, 32));//Output
	}
}
