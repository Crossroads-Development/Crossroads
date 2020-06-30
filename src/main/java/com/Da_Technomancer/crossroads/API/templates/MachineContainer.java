package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.essentials.gui.container.FluidSlotManager;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public abstract class MachineContainer<U extends InventoryTE> extends TileEntityContainer<U>{

	public final IntDeferredRef heatRef;
	public final IntDeferredRef rotRef;
	public final IntDeferredRef[][] fluidManagerRefs;//Outer array is each fluid manager, inner array is size 2 {id, qty}

	//The passed PacketBuffer must have the blockpos as the first encoded datum
	@SuppressWarnings("unchecked")
	public MachineContainer(ContainerType<? extends MachineContainer> type, int windowId, PlayerInventory playerInv, PacketBuffer data){
		super(type, windowId, playerInv, data);

		boolean remote = te.getWorld().isRemote;
		//Track rotary info for UI
		if(te.useRotary()){
			rotRef = new IntDeferredRef(te::getUISpeed, remote);
			trackInt(rotRef);
		}else{
			rotRef = null;
		}
		//Track heat info for UI
		if(te.useHeat()){
			heatRef = new IntDeferredRef(te::getUITemp, remote);
			trackInt(heatRef);
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
			trackInt(fluidManagerRefs[i][0]);
			trackInt(fluidManagerRefs[i][1]);
		}

		addSlots();
		int[] invStart = getInvStart();

		//Hotbar
		for(int x = 0; x < 9; x++){
			addSlot(new Slot(playerInv, x, invStart[0] + x * 18, invStart[1] + 58));
		}

		//Crossroads player inv
		for(int y = 0; y < 3; y++){
			for(int x = 0; x < 9; x++){
				addSlot(new Slot(playerInv, x + y * 9 + 9, invStart[0] + x * 18, invStart[1] + y * 18));
			}
		}
	}

	@Override
	protected int slotCount(){
		return te.inventory.length;
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn){
		super.onContainerClosed(playerIn);

		if(!te.getWorld().isRemote){
			if(playerIn.isAlive() && !(playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity) playerIn).hasDisconnected())){
				for(Slot s : inventorySlots){
					if(s.inventory instanceof FluidSlotManager.FakeInventory){
						playerIn.inventory.placeItemBackInInventory(te.getWorld(), s.getStack());
					}
				}
			}else{
				for(Slot s : inventorySlots){
					if(s.inventory instanceof FluidSlotManager.FakeInventory){
						playerIn.dropItem(s.getStack(), false);
					}
				}
			}
		}
	}
}
