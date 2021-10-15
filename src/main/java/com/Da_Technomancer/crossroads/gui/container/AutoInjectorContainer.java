package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.templates.MachineContainer;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.AutoInjectorTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class AutoInjectorContainer extends MachineContainer<AutoInjectorTileEntity>{

	@ObjectHolder("auto_injector")
	private static MenuType<AutoInjectorContainer> type = null;

	public final IntDeferredRef effectRef;
	public final IntDeferredRef intensityRef;
	public final IntDeferredRef durationRef;
	public final IntDeferredRef doseRef;

	public AutoInjectorContainer(int id, Inventory playerInv, FriendlyByteBuf buf){
		super(type, id, playerInv, buf);

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
