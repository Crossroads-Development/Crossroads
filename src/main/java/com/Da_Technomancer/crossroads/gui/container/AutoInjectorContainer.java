package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.api.templates.MachineContainer;
import com.Da_Technomancer.crossroads.blocks.witchcraft.AutoInjectorTileEntity;
import com.Da_Technomancer.essentials.api.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class AutoInjectorContainer extends MachineContainer<AutoInjectorTileEntity>{

	protected static final MenuType<AutoInjectorContainer> TYPE = CRContainers.createConType(AutoInjectorContainer::new);

	public final IntDeferredRef effectRef;
	public final IntDeferredRef intensityRef;
	public final IntDeferredRef durationRef;
	public final IntDeferredRef doseRef;

	public AutoInjectorContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(TYPE, id, playerInv, buf);

		effectRef = new IntDeferredRef(te::getStoredEffectIndex, te.getLevel().isClientSide);
		intensityRef = new IntDeferredRef(te::getIntensity, te.getLevel().isClientSide);
		durationRef = new IntDeferredRef(te::getDuration, te.getLevel().isClientSide);
		doseRef = new IntDeferredRef(te::getDurationSetting, te.getLevel().isClientSide);
		addDataSlot(effectRef);
		addDataSlot(intensityRef);
		addDataSlot(durationRef);
		addDataSlot(doseRef);
	}

	@Override
	protected void addSlots(){
		addSlot(new StrictSlot(te, 0, 116, 15));
		addSlot(new OutputSlot(te, 1, 116, 50));
	}
}
