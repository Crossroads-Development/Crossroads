package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import com.Da_Technomancer.essentials.gui.container.IntDeferredRef;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeaconHarnessContainer extends AbstractContainerMenu{

	@ObjectHolder("beacon_harness")
	private static MenuType<BeaconHarnessContainer> type = null;

	public final IntDeferredRef cycleRef;
	public final BeaconHarnessTileEntity te;

	public BeaconHarnessContainer(int id, Inventory playerInv, FriendlyByteBuf data){
		super(type, id);

		BlockPos pos = data.readBlockPos();
		BlockEntity rawTE = playerInv.player.level.getBlockEntity(pos);
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
	public boolean stillValid(Player playerIn){
		return te.stillValid(playerIn);
	}
}
