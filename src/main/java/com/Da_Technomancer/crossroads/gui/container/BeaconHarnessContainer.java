package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeaconHarnessContainer extends Container{

	@ObjectHolder("beacon_harness")
	private static ContainerType<BeaconHarnessContainer> type = null;

	public final IntDeferredRef cycleRef;
	public final BeaconHarnessTileEntity te;

	public BeaconHarnessContainer(int id, PlayerInventory playerInv, PacketBuffer data){
		super(type, id);

		BlockPos pos = data.readBlockPos();
		TileEntity rawTE = playerInv.player.level.getBlockEntity(pos);
		if(rawTE instanceof BeaconHarnessTileEntity){
			te = (BeaconHarnessTileEntity) rawTE;
		}else{
			//Should never happen
			te = null;
			Crossroads.logger.error("Null/Incorrect TileEntity passed to BeaconHarnessContainer!");
		}

		if(te == null){
			cycleRef = new IntDeferredRef(() -> 0, false);
		}else{
			cycleRef = new IntDeferredRef(te::getCycles, te.getLevel().isClientSide);
		}
		addDataSlot(cycleRef);
	}

	@Override
	public boolean stillValid(PlayerEntity playerIn){
		return te.stillValid(playerIn);
	}
}
