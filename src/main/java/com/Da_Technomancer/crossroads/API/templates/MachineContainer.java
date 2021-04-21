package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import org.apache.commons.lang3.tuple.Pair;

public abstract class MachineContainer<U extends InventoryTE> extends TileEntityContainer<U>{

	public final IntDeferredRef heatRef;
	public final IntDeferredRef rotRef;
	public final IntDeferredRef[][] fluidManagerRefs;//Outer array is each fluid manager, inner array is size 2 {id, qty}

	//The passed PacketBuffer must have the blockpos as the first encoded datum
	public MachineContainer(ContainerType<? extends MachineContainer> type, int windowId, PlayerInventory playerInv, PacketBuffer data){
		super(type, windowId, playerInv, data);

		boolean remote = te.getLevel().isClientSide;
		//Track rotary info for UI
		if(te.useRotary()){
			rotRef = new IntDeferredRef(te::getUISpeed, remote);
			addDataSlot(rotRef);
		}else{
			rotRef = null;
		}
		//Track heat info for UI
		if(te.useHeat()){
			heatRef = new IntDeferredRef(te::getUITemp, remote);
			addDataSlot(heatRef);
		}else{
			heatRef = null;
		}
		//Track fluid info for UI
		fluidManagerRefs = new IntDeferredRef[te.fluidManagers.length][2];
		for(int i = 0; i < te.fluidManagers.length; i++){
			//Generate 2 int references for each fluid manager, track those references, and store them in an array
			FluidSlotManager manager = te.fluidManagers[i];
			fluidManagerRefs[i][0] = new IntDeferredRef(manager::getFluidId, remote);
			fluidManagerRefs[i][1] = new IntDeferredRef(manager::getFluidQty, remote);
			addDataSlot(fluidManagerRefs[i][0]);
			addDataSlot(fluidManagerRefs[i][1]);
		}
	}

	/**
	 * Helper method to add fluid manager slots and link the input slot to all fluid manager
	 * @param fluidSlots The fluid manager slot pair returned by FluidManager::createFluidSlots, in order output, input
	 */
	protected void addFluidManagerSlots(Pair<Slot, Slot> fluidSlots){
		addSlot(fluidSlots.getLeft());
		Slot inputSlot = fluidSlots.getRight();
		addSlot(inputSlot);
		for(FluidSlotManager manager : te.fluidManagers){
			manager.linkSlot(inputSlot);
		}
	}

	@Override
	protected int slotCount(){
		return te.inventory.length;
	}

	@Override
	public void removed(PlayerEntity playerIn){
		super.removed(playerIn);

		if(!te.getLevel().isClientSide){
			if(playerIn.isAlive() && !(playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity) playerIn).hasDisconnected())){
				for(Slot s : slots){
					if(s.container instanceof FluidSlotManager.FakeInventory){
						playerIn.inventory.placeItemBackInInventory(te.getLevel(), s.getItem());
					}
				}
			}else{
				for(Slot s : slots){
					if(s.container instanceof FluidSlotManager.FakeInventory){
						playerIn.drop(s.getItem(), false);
					}
				}
			}
		}
	}
}
